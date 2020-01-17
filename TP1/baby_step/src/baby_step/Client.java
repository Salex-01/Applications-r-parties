package baby_step;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends Thread{
	
	byte lg_Nom; //Longueur du nom
	String nom;	// Nom du client
	Socket socServer; // Socket Serveur
	Socket socClient; // Socket Client

	public Client(String Nom, InetAddress serverHost, int serverPort) {
		this.nom=Nom;
		this.lg_Nom= (byte) Nom.length();
		
		//Création socket vers
		try {
			this.socServer=new Socket(serverHost,serverPort); //Connexion au serveur
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.socClient=new Socket();
		
		
	}
	
	@Override
	public void run () {
		try {
			socClient.bind(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			socServer.getOutputStream().write(lg_Nom);
			socServer.getOutputStream().write(nom.getBytes());;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			System.out.println(new String(socServer.getInputStream().readNBytes(lg_Nom+6)));
		}catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
}
