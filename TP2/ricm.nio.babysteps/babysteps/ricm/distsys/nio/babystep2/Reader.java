package ricm.distsys.nio.babystep2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Reader {
	// Idee automate : Envoie lg du message puis envoie le message entier

	enum State {
		LGMESS, READMESS
	};

	State etat;
	byte[] data;

	ByteBuffer lenBuf = ByteBuffer.allocate(4);
	
	SocketChannel socket;
	SelectionKey key;
	int bytesToRead;
	int byteRead;

	public Reader(SocketChannel socket1, SelectionKey key1) {
		etat = State.LGMESS;
		socket = socket1;
		key = key1;
		bytesToRead = 0;
		byteRead = 0;
	}

	public String EtatReader() throws IOException {
		switch (etat) {
		case LGMESS:
			int res = socket.read(lenBuf);
			if (res == -1) {
				key.cancel();
				socket.close();
				return null;
			}
			byteRead += res;
			if (byteRead == 4) {
				lenBuf.rewind();
				bytesToRead = lenBuf.getInt();
				etat = State.READMESS;
				res=0;
				byteRead=0;
			} else {
				return null;
			}
		case READMESS:
			ByteBuffer dataBuf = ByteBuffer.allocate(bytesToRead);
			res = socket.read(dataBuf);
			if (res == -1) {
				key.cancel();
				socket.close();
				return null;
			}
			byteRead+=res;
			if(byteRead==bytesToRead){
				dataBuf.rewind();
				data=new byte[bytesToRead];
				dataBuf.get(data);
				String msg= new String(data, Charset.forName("UTF-8"));
				System.out.println(msg);
				etat=State.LGMESS;
				return msg;
			}
		default:
		}
		return null;
	}
}
