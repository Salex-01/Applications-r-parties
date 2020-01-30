package basicFileServer;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;

public class Client extends Thread {
	Socket socServer; // Socket Serveur

	public Client(String serverHost, int serverPort) {
		try {
			this.socServer = new Socket(serverHost, serverPort); // Connexion au serveur
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		LinkedList<String> fileNames = new LinkedList<String>();	// Fichiers à récupérer
		byte[][] content = null;				// Contenu des fichiers
		String[] path = null;
		String savePath;
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String tmp;
		BufferedOutputStream buffOS;
		try {
			System.out.println("Entrez les noms des fichiers à télécharger séparés par des retours à la ligne\n\\ pour terminer");
			tmp = reader.readLine();
			while (!tmp.startsWith("\\")){	// Taper les noms des fichiers (Enter entre chaque nom) et \ pour terminer
				fileNames.add(tmp);
				tmp = reader.readLine();
			}
			System.out.println("\nOù souhaitez-vous enregistrer ces fichiers ? (Assurez-vous que cet emplacement est accessible en écriture)");
			savePath = reader.readLine();
			new ObjectOutputStream(socServer.getOutputStream()).writeObject(fileNames);
			content = (byte[][]) new ObjectInputStream(socServer.getInputStream()).readObject();
			for(int i = 0; i<fileNames.size(); i++) {
				path = fileNames.get(i).split("/");
				File tmpFile = new File(savePath+"/"+path[path.length-1]);
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
