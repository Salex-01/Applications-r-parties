package baby_step;

import java.io.*;
import java.net.*;

public class Server extends Thread {
	ServerSocket s = null;
	Socket s2 = null;
	InputStream in = null;
	OutputStream out = null;
	String output = null;
	String ServerName = "Serveur test";

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

	@Override
	public void run() {
		while (true) {
			try {
				s2 = s.accept();
				in = s2.getInputStream();
				out = s2.getOutputStream();
				output = "Hello ".concat(new String(in.readNBytes((int) in.readNBytes(1)[0])));
				out.write(output.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
