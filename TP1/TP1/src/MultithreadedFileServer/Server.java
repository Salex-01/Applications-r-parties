package MultithreadedFileServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class Server extends Thread {
	ServerSocket s = null;
	public Server(int port) {
		try {
			s = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public InetAddress getIP() {
		return s.getInetAddress();
	}

	@Override
	public void run() {
		while (true) {
			try {
				new ExecServer(s.accept()).start();;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
