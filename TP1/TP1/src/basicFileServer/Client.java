package basicFileServer;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;

public class Client extends Thread {
	Socket socServer; // Socket Serveur

	LinkedList<String> fileNames = null;	// Fichiers à récupérer
	byte[][] content = null;				// Contenu des fichiers

	public Client(InetAddress serverHost, int serverPort) {
		try {
			this.socServer = new Socket(serverHost, serverPort); // Connexion au serveur
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String tmp;
		BufferedOutputStream buffOS;
		try {
			tmp = reader.readLine();
			while (!tmp.startsWith("\\")){	// Taper les noms des fichiers (Enter entre chaque nom) et \ pour terminer
				fileNames.add(tmp);
			}
			new ObjectOutputStream(socServer.getOutputStream()).writeObject(fileNames);
			content = (byte[][]) new ObjectInputStream(socServer.getInputStream()).readObject();
			for(int i = 0; i<fileNames.size(); i++) {
				File tmpFile = new File("/home/AR/TP1/"+fileNames.get(i));
				tmpFile.createNewFile();
				buffOS = new BufferedOutputStream(new FileOutputStream(tmpFile));
				buffOS.write(content[i]);
				buffOS.flush();
				buffOS.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
