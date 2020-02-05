package MultithreadedFileServerV3;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class ExecServer extends Thread {
	private Socket socket = null;
	DataInputStream in = null;
	DataOutputStream out = null;
	LinkedList<String> names = null;
	byte[] content = null;
	private boolean ready = true; // Disponible ou non
	Semaphore go = new Semaphore(0); // Semaphore attend le signal du serveur pour effectuer son code
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

	@Override
	public void run() {
		BufferedInputStream buffIS = null;
		File f;
		int nFiles;
		long len;
		long nPack;
		while (true) {
			f = null;
			try {
				try {
					go.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
				nFiles = in.readInt();
				for (int i = 0; i < nFiles; i++) {
					f = new File(in.readUTF());
					len = f.length();
					nPack = len / 512;
					if (len % 512 != 0) {
						nPack++;
					}
					out.writeLong(nPack);
					out.writeInt((int) (len % 512));
					buffIS = new BufferedInputStream(new FileInputStream(f));
					for (int j = 0; j < nPack; j++) {
						content = buffIS.readNBytes(512);
						out.write(content);
					}
					buffIS.close();
					buffIS = null;
					f = null;
				}
				socket.close(); // Ferme le socket
				socket = null;
				ready = true;
			} catch (IOException e) { // Traite les exeptions
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				System.out.println("Format de requête incorrect");
			} finally {
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (NullPointerException e1) {
				}
				try {
					buffIS.close();
				} catch (IOException e1) {
				} catch (NullPointerException e1) {
				}
				parent.ready.release(); // Libere le semaphore du serveur
			}
		}
	}
}