package ricm.channels.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

import ricm.channels.IChannel;
import ricm.channels.IChannelListener;

public class Channel implements IChannel {

	SelectableChannel sc;
	private IChannelListener ICL = null;

	SocketChannel socket;

	LinkedList<ByteBuffer> toSend = new LinkedList<ByteBuffer>();

	boolean closed = false;
	Broker broker;

	private Reader read;
	private Writer write;

	public Channel(SelectableChannel channel, Broker b) {
		sc = channel;
		broker = b;
		write = new Writer((SocketChannel) channel);
		read = new Reader((SocketChannel) channel);
	}

	@Override
	public void setListener(IChannelListener l) {
		ICL = l;
	}

	@Override
	public void send(byte[] bytes, int offset, int count) throws IOException {
		ByteBuffer b = ByteBuffer.wrap(bytes, offset, count);
		toSend.addLast(b);
	}

	@Override
	public void send(byte[] bytes) throws IOException {
		ByteBuffer b = ByteBuffer.wrap(bytes, 0, bytes.length);
		toSend.addLast(b);
	}

	@Override
	public void close() {
		closed = true;
		ICL.closed(this, null);
	}

	@Override
	public boolean closed() {
		return closed;
	}

	void handleRead(SelectionKey key) throws IOException {
		assert (broker.scKey == key);
		assert (sc == key.channel());

		try {
			byte[] result = read.execReader();
			if (result != null) {
				ICL.received(this, result);
			}
		} catch (IOException e) {
			System.out.println("Fermeture du serveur/Erreur IO");
			System.exit(-1);
		}

	}

	void handleWrite(SelectionKey key) throws IOException {
		assert (broker.scKey != key);
		assert (sc != key.channel());

		if ((!write.set) && (toSend.size() > 0)) {
			write.setMessage(toSend.removeFirst().array());
		}

		try {
			if (write.set) {
				if (write.execWriter()) {
					if (toSend.size() > 0) {
						write.setMessage(toSend.removeFirst().array());
					} else {
						key.interestOps(SelectionKey.OP_READ);
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Client déconnecté/Erreur IO");
		}
	}

}
