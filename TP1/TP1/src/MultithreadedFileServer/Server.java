package MultithreadedFileServer;

import java.io.BufferedInputStream;
import java.io.File;
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
		}
	}

	public InetAddress getIP() {
		return s.getInetAddress();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		while (true) {
			try {
				new ExecServeur(s.accept()).start();;
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
