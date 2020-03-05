package ricm.distsys.nio.babystep3;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * NIO elementary server RICM4 TP F. Boyer
 */

public class NioServer {

	public static int DEFAULT_SERVER_PORT = 8888;

	// The channel used to accept connections from server-side
	private ServerSocketChannel ssc;
	private SelectionKey sscKey;

	// Unblocking selector
	private Selector selector;

	private Map<SocketChannel, IOGroup> groups = new HashMap<SocketChannel, IOGroup>();

	/**
	 * NIO server initialization
	 * 
	 * @param the host address and port of the server
	 * @throws IOException
	 */
	public NioServer(int port) throws IOException {

		// create a new selector
		selector = SelectorProvider.provider().openSelector();

		// create a new non-blocking server socket channel
		ssc = ServerSocketChannel.open();
		ssc.configureBlocking(false);

		// bind the server socket to the given address and port
		InetAddress hostAddress;
		hostAddress = InetAddress.getByName("localhost");
		InetSocketAddress isa = new InetSocketAddress(hostAddress, port);
		ssc.socket().bind(isa);

		// be notified when connection requests arrive
		sscKey = ssc.register(selector, SelectionKey.OP_ACCEPT);
	}

	/**
	 * NIO mainloop Wait for selected events on registered channels Selected events
	 * for a given channel may be ACCEPT, CONNECT, READ, WRITE Selected events for a
	 * given channel may change over time
	 */
	public void loop() throws IOException {
		System.out.println("NioServer running");
		while (true) {
			selector.select();

			Iterator<?> selectedKeys = selector.selectedKeys().iterator();

			while (selectedKeys.hasNext()) {

				SelectionKey key = (SelectionKey) selectedKeys.next();
				if (key.isValid() && key.isAcceptable())
					handleAccept(key);
				if (key.isValid() && key.isReadable())
					handleRead(key);
				if (key.isValid() && key.isWritable())
					handleWrite(key);
				if (key.isValid() && key.isConnectable())
					handleConnect(key);
				selectedKeys.remove();
			}
		}
	}

	/**
	 * Accept a connection and make it non-blocking
	 * 
	 * @param the key of the channel on which a connection is requested
	 */
	private void handleAccept(SelectionKey key) throws IOException {
		assert (this.sscKey == key);
		assert (ssc == key.channel());
		SocketChannel sc;

		// do the actual accept on the server-socket channel
		sc = ssc.accept();
		sc.configureBlocking(false);

		groups.put(sc, new IOGroup(new Writer(sc), new Reader(sc)));

		// register the read interest for the new socket channel
		// in order to know when there are bytes to read
		sc.register(this.selector, SelectionKey.OP_READ);
	}

	/**
	 * Handle a connect, this should never happen
	 * 
	 * @param the key of the channel on which a connection is requested
	 * @throws Error since this should never happen
	 */
	private void handleConnect(SelectionKey key) throws IOException {
		throw new Error("Unexpected connect");
	}

	/**
	 * Handle incoming data event
	 * 
	 * @param the key of the channel on which the incoming data waits to be received
	 */
	private void handleRead(SelectionKey key) throws IOException {
		assert (sscKey != key);
		assert (ssc != key.channel());

		IOGroup io = groups.get(key.channel());

		try {
			String res = io.read.execReader();
			if (res != null) {
				System.out.println(res);
				io.write.setMessage(res.getBytes());
				key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
			}
		} catch (IOException e) {
			System.out.println("Erreur IO");
		}
	}

	/**
	 * Handle outgoing data event
	 * 
	 * @param the key of the channel on which data can be sent
	 */
	private void handleWrite(SelectionKey key) throws IOException {
		assert (sscKey != key);
		assert (ssc != key.channel());

		IOGroup io = groups.get(key.channel());

		try {
			if (io.write.set) {
				if (io.write.execWriter()) {
					key.interestOps(SelectionKey.OP_READ);
				}
			}
		} catch (IOException e) {
			System.out.println("Client déconnecté/Erreur IO");
		}

	}

	public static void main(String args[]) throws IOException {
		int serverPort = DEFAULT_SERVER_PORT;
		String arg;

		for (int i = 0; i < args.length; i++) {
			arg = args[i];
			if (arg.equals("-p")) {
				serverPort = new Integer(args[++i]).intValue();
			}
		}
		NioServer ns = new NioServer(serverPort);
		ns.loop();
	}

}
