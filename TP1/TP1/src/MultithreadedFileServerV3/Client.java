package MultithreadedFileServerV3;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.LinkedList;

public class Client extends Thread {
	Socket socServer; // Socket Serveur

	public Client(String serverHost, int serverPort) throws IOException {
		this.socServer = new Socket(serverHost, serverPort); // Connexion au serveur
	}

	@Override
	public void run() {
		LinkedList<String> fileNames = new LinkedList<String>(); // Fichiers à récupérer
		String[] path = null;
		String savePath;
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String tmp;
		BufferedOutputStream buffOS;
		long nPacks;
		int lastLen;
		try {
			System.out.println(
					"Entrez les noms des fichiers à télécharger séparés par des retours à la ligne\n\\ pour terminer");
			tmp = reader.readLine();
			while (!tmp.startsWith("\\")) { // Taper les noms des fichiers (Enter entre chaque nom) et \ pour terminer
				fileNames.add(tmp);
				tmp = reader.readLine();
			}
			System.out.println(
					"\nOù souhaitez-vous enregistrer ces fichiers ? (Assurez-vous que cet emplacement est accessible en écriture)");
			savePath = reader.readLine();
			DataOutputStream out = new DataOutputStream(socServer.getOutputStream());
			for (int i = 0; i < fileNames.size(); i++) {
				out.writeUTF(fileNames.get(i));
			}
			DataInputStream in = new DataInputStream(socServer.getInputStream());
			for (int i = 0; i < fileNames.size(); i++) {
				path = fileNames.get(i).split("/");
				File tmpFile = new File(savePath + "/" + path[path.length - 1]);
				tmpFile.createNewFile();
				buffOS = new BufferedOutputStream(new FileOutputStream(tmpFile));
				nPacks = in.readLong();
				lastLen = in.readInt();
				for (int j = 0; j < nPacks - 1; j++) {
					buffOS.write(in.readNBytes(512));
				}
				if (lastLen > 0) {
					buffOS.write(in.readNBytes((int) lastLen));
				}
				buffOS.flush();
				buffOS.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
