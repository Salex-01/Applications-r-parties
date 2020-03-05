package ricm.channels.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ricm.channels.IBrokerListener;

public class Broker implements ricm.channels.IBroker {

	private Selector selector;
	private IBrokerListener IBL = null;

	private SelectionKey scKey;
	private static SocketChannel sc;

	private ServerSocketChannel ssc;
	private SelectionKey sscKey;

	static Reader read;
	static Writer write;

	static byte[] digest;
	
	private Map<SocketChannel, IOGroup> groups = new HashMap<SocketChannel, IOGroup>();

	public Broker() throws IOException {
		selector = SelectorProvider.provider().openSelector();
	}

	@Override
	public void setListener(IBrokerListener l) {
		IBL = l;
	}

	@Override
	public boolean connect(String host, int port) throws IOException {
		// create a new selector
		selector = SelectorProvider.provider().openSelector();

		// create a new non-blocking server socket channel
		sc = SocketChannel.open();
		sc.configureBlocking(false);

		// register an connect interested in order to get a
		// connect event, when the connection will be established
		scKey = sc.register(selector, SelectionKey.OP_CONNECT);

		// request a connection to the given server and port
		InetAddress addr = InetAddress.getByName(host);
		return sc.connect(new InetSocketAddress(addr, port));
	}


	@Override
	public boolean accept(int port) throws IOException {
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
		
		
		//////////////////Je sais pas comment retourner un boolean dans ce cas ????\\\\\\\\\\\\\\\\\\\\\\\\
		if(sscKey.isAcceptable()) {
			return true;
		}
		else {
			return false;
		}

	}

	public void run() throws IOException {
		while (true) {
			selector.select();
			// get the keys for which an event occurred
			Iterator<?> selectedKeys = selector.selectedKeys().iterator();
			while (selectedKeys.hasNext()) {
				SelectionKey key = (SelectionKey) selectedKeys.next();
				// process key's events
				if (key.isValid() && key.isAcceptable())
					IBL.accepted(new Channel(key.channel()));
				if (key.isValid() && key.isReadable())
					handleRead(key);
				if (key.isValid() && key.isWritable())
					handleWrite(key);
				if (key.isValid() && key.isConnectable())
					IBL.connected(new Channel(key.channel()));
				// remove the key from the selected-key set
				selectedKeys.remove();
			}
		}
	}

	private void handleRead(SelectionKey key) throws IOException {
		assert (this.scKey == key);
		assert (sc == key.channel());

		try {
			String result = read.execReader();
			if (result != null) {
				assert (Arrays.equals(md5(result.getBytes()), digest));
				write.setMessage(result.getBytes());
				digest = md5(result.getBytes());
				key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
			}
		} catch (IOException e) {
			System.out.println("Fermeture du serveur/Erreur IO");
			System.exit(-1);
		}

	}

	private void handleWrite(SelectionKey key) throws IOException {
		assert (scKey != key);
		assert (sc != key.channel());

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

	public static byte[] md5(byte[] bytes) throws IOException {
		byte[] digest = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(bytes, 0, bytes.length);
			digest = md.digest();
		} catch (Exception ex) {
			throw new IOException(ex);
		}
		return digest;
	}

}
