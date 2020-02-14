package ricm.distsys.nio.babystep2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class Writer {

	enum State {
		LGMESS, WRITEMESS
	};

	State etat;
	byte[] data;

	SocketChannel socket;
	SelectionKey key;

	int bytesWrite;

	ByteBuffer dataBuff;
	ByteBuffer lenBuff = ByteBuffer.allocate(4);
	
	public boolean set=false;

	public Writer(SocketChannel socket1, SelectionKey key1) {
		etat = State.LGMESS;
		socket = socket1;
		key = key1;
		bytesWrite = 0;
	}

	public void EtatWriter() throws IOException {
		switch (etat) {
		case LGMESS:
			socket.write(lenBuff);
			if (lenBuff.remaining() == 0) {
				etat = State.WRITEMESS;
				return;
			} else {
				return;
			}
		case WRITEMESS:
			socket.write(dataBuff);
			if (dataBuff.remaining() == 0) {
				etat = State.LGMESS;
				set = false;
				return;
			} else {
				return;
			}
		default:
		}
	}

	public void setMessage(byte[] data) {
		dataBuff = ByteBuffer.wrap(data, 0, data.length);
		lenBuff.position(0);
		lenBuff.putInt(data.length);
		set=true;
	}
}
