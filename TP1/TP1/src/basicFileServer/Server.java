package basicFileServer;

import java.io.*;
import java.net.*;
import java.util.LinkedList;

public class Server extends Thread {
	ServerSocket s = null;
	Socket s2 = null;
	ObjectInputStream in = null;
	ObjectOutputStream out = null;
	LinkedList<String> names;
	byte[][] content = null;

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
		BufferedInputStream buffIS;
		File f;
		while (true) {
			try {
				s2 = s.accept();
				in = new ObjectInputStream(s2.getInputStream());
				out = new ObjectOutputStream(s2.getOutputStream());
				names = (LinkedList<String>) in.readObject();
				content = new byte[names.size()][];
				for(int i = 0; i<names.size();i++) {
					f = new File(names.get(i));
					buffIS = new BufferedInputStream(new FileInputStream(f));
					content[i] = buffIS.readAllBytes();
					buffIS.close();
					buffIS = null;
					f = null;
				}
				out.writeObject(content);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
