package MultithreadedFileServer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;

public class ExecServeur extends Thread{
	Socket socket=null;
	ObjectInputStream in = null;
	ObjectOutputStream out = null;
	LinkedList<String> names;
	byte[][] content = null;
	
	public ExecServeur(Socket s) {
		socket=s;
	}
	
	public void run() {
		BufferedInputStream buffIS;
		File f;
		while (true) {
			try {
				in = new ObjectInputStream(socket.getInputStream());
				out = new ObjectOutputStream(socket.getOutputStream());
				names = (LinkedList<String>) in.readObject();
				content = new byte[names.size()][];
				for (int i = 0; i < names.size(); i++) {
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
