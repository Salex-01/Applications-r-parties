package ricm.distsys.nio.babystep3;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Writer {

	enum State {
		LGMESS, WRITEMESS
	};

	State etat;

	SocketChannel socket;

	int bytesWrite;

	ByteBuffer dataBuff;
	ByteBuffer lenBuff = ByteBuffer.allocate(4);

	public boolean set = false;

	public Writer(SocketChannel socket1) {
		etat = State.LGMESS;
		socket = socket1;
		bytesWrite = 0;
	}

	public boolean execWriter() throws IOException {
		switch (etat) {
		case LGMESS:
			socket.write(lenBuff);
			if (lenBuff.remaining() == 0) {
				etat = State.WRITEMESS;
			} else {
				return false;
			}
		case WRITEMESS:
			socket.write(dataBuff);
			if (dataBuff.remaining() == 0) {
				etat = State.LGMESS;
				set = false;
				return true;
			}
			break;
		default:
		}
		return false;
	}

	public void setMessage(byte[] data) {
		dataBuff = ByteBuffer.wrap(data, 0, data.length);
		lenBuff.position(0);
		lenBuff.putInt(data.length);
		lenBuff.rewind();
		set = true;
	}
}
