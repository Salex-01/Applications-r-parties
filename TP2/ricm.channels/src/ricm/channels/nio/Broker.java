package ricm.channels.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ricm.channels.IBrokerListener;

public class Broker implements ricm.channels.IBroker {

	private Selector selector;
	private IBrokerListener IBL = null;

	SelectionKey scKey;
	SocketChannel sc;

	private ServerSocketChannel ssc;
	private SelectionKey sscKey;

	Map<SocketChannel, IOGroup> groups = new HashMap<SocketChannel, IOGroup>();

	Map<SelectableChannel, Channel> channels = new HashMap<SelectableChannel, Channel>();

	public Broker() throws IOException {
		selector = SelectorProvider.provider().openSelector();
	}

	@Override
	public void setListener(IBrokerListener l) {
		IBL = l;
	}

	@Override
	public boolean connect(String host, int port) {
		try {
			// create a new non-blocking server socket channel
			sc = SocketChannel.open();
			sc.configureBlocking(false);

			// register an connect interested in order to get a
			// connect event, when the connection will be established
			scKey = sc.register(selector, SelectionKey.OP_CONNECT);

			// request a connection to the given server and port
			InetAddress addr = InetAddress.getByName(host);
			sc.connect(new InetSocketAddress(addr, port));
			return true;
		} catch (IOException e) {
			IBL.refused(host, port);
			return false;
		}
	}

	@Override
	public boolean accept(int port) {
		try {
			// create a new non-blocking server socket channel
			ssc = ServerSocketChannel.open();
			ssc.configureBlocking(false);

			// bind the server socket to the given address and port
			InetSocketAddress isa = new InetSocketAddress(port);
			ssc.socket().bind(isa);

			// be notified when connection requests arrive
			sscKey = ssc.register(selector, SelectionKey.OP_ACCEPT);
			return true;
		} catch (IOException e) {
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
				if (key.isValid()) {
					if (key.isAcceptable()) {
						SelectableChannel sc1 = ssc.accept();
						sc1.configureBlocking(false);
						scKey = sc1.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
						Channel c = new Channel(sc1, this);
						channels.put(sc1, c); // Channel associated with its own SelectableChannel
						IBL.accepted(c);
					} else if (key.isReadable()) {
						channels.get(key.channel()).handleRead(key);
					} else if (key.isWritable()) {
						channels.get(key.channel()).handleWrite(key);
					} else if (key.isConnectable()) {
						sc.finishConnect();
						key.interestOps(SelectionKey.OP_WRITE | SelectionKey.OP_READ);
						Channel c = new Channel(sc, this);
						channels.put(sc, c);
						IBL.connected(c);
					}
				}
				// remove the key from the selected-key set
				selectedKeys.remove();
			}
		}
	}

}
