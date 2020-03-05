package ricm.channels.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;

import ricm.channels.IChannel;
import ricm.channels.IChannelListener;


public class Channel implements IChannel {
	
	SelectableChannel sc;
	private IChannelListener ICL =null;
	
	SocketChannel socket;
	
	ByteBuffer databuff;
	
	boolean closed=false;
	

	public Channel(SelectableChannel channel) {
		sc=channel;
	}
	@Override
	public void setListener(IChannelListener l) {
		ICL=l;

	}

	@Override
	public void send(byte[] bytes, int offset, int count) throws IOException{
		databuff.get(bytes, offset, count);
		socket.write(databuff);
	}

	@Override
	public void send(byte[] bytes) throws IOException {
		databuff.get(bytes);
		socket.write(databuff);

	}

	@Override
	public void close() {
		closed=true;
		ICL.closed(this,null);

	}

	@Override
	public boolean closed() {
		return closed;
	}

}
