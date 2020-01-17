package baby_step;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	
	char lg_Nom; //Longueur du nom
	String nom;	// Nom du client
	Socket socServer; // Socket Serveur
	Socket socClient; // Socket Client

	public Client(String Nom, String serverHost, int serverPort) {
		this.nom=Nom;
		this.lg_Nom= (char)Nom.length();
		
		//Création socket vers
		try {
			this.socServer=new Socket(serverHost,serverPort);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.socClient=new Socket();
		
		try {
			socClient.bind(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			this.socClient.connect(socServer.getLocalSocketAddress());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	
}
