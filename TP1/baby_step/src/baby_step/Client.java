package baby_step;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends Thread {

	byte lg_Nom; // Longueur du nom
	String nom; // Nom du client
	Socket socServer; // Socket Serveur

	public Client(String Nom, InetAddress serverHost, int serverPort) {
		this.nom = Nom;
		this.lg_Nom = (byte) Nom.length();

		// Création socket vers
		try {
			this.socServer = new Socket(serverHost, serverPort); // Connexion au serveur
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override

	public void run() {
		try {
			socServer.getOutputStream().write(lg_Nom); // A modifie en un seul getOutputStream (variable)
			socServer.getOutputStream().write(nom.getBytes());
			System.out.println(new String(socServer.getInputStream().readNBytes(lg_Nom + 6)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
