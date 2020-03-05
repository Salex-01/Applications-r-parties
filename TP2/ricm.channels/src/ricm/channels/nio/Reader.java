package ricm.channels.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Reader {

	enum State {
		LGMESS, READMESS
	};

	State etat;

	ByteBuffer lenBuf = ByteBuffer.allocate(4);

	SocketChannel socket;
	SelectionKey key;
	int bytesToRead;
	int bytesRead;

	public Reader(SocketChannel socket1) {
		etat = State.LGMESS;
		socket = socket1;
		bytesToRead = 0;
		bytesRead = 0;
		lenBuf.rewind();
	}

	public String execReader() throws IOException {
		switch (etat) {
		case LGMESS:
			int res = socket.read(lenBuf);
			if (res == -1) {
				socket.close();
				return null;
			}
			bytesRead += res;
			if (bytesRead == 4) {
				lenBuf.rewind();
				bytesToRead = lenBuf.getInt();
				etat = State.READMESS;
				res = 0;
				bytesRead = 0;
			} else {
				return null;
			}
		case READMESS:
			ByteBuffer dataBuf = ByteBuffer.allocate(bytesToRead);
			res = socket.read(dataBuf);
			if (res == -1) {
				socket.close();
				return null;
			}
			bytesRead += res;
			if (bytesRead == bytesToRead) {
				bytesRead = 0;
				bytesToRead = 0;
				lenBuf.rewind();
				dataBuf.rewind();
				String msg = new String(dataBuf.array(), Charset.forName("UTF-8"));
				etat = State.LGMESS;
				return msg;
			}
		default:
		}
		return null;
	}
}
