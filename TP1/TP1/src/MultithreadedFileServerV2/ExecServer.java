package MultithreadedFileServerV2;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class ExecServer extends Thread {
	private Socket socket = null;
	ObjectInputStream in = null;
	ObjectOutputStream out = null;
	LinkedList<String> names = null;
	byte[][] content = null;
	private boolean ready = true;
	Semaphore go = new Semaphore(0);
	Server parent;

	public ExecServer(Server s) {
		parent = s;
	}

	public boolean isReady() {
		return ready;
	}

	public void affect(Socket s) {
		socket = s;
		ready = false;
	}

	@SuppressWarnings("unchecked")
	public void run() {
		BufferedInputStream buffIS = null;
		File f;
		Object o;
		while (true) {
			f = null;
			o = null;
			try {
				try {
					go.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				in = new ObjectInputStream(socket.getInputStream());
				out = new ObjectOutputStream(socket.getOutputStream());
				o = in.readObject();
				if (o instanceof LinkedList<?>) {
					names = (LinkedList<String>) o;
				} else {
					throw new IllegalArgumentException();
				}
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
				socket.close();
				socket = null;
				ready = true;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				System.out.println("Format de requête incorrect");
			} finally {
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}catch(NullPointerException e1) {}
				try {
					buffIS.close();
				} catch (IOException e1) {
				} catch (NullPointerException e1) {
				}
				parent.ready.release();
			}
		}
	}
}
