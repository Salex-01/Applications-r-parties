package chatServer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Random;

public class Client {

	public static void main(String[] args) throws RemoteException, NotBoundException {

		Participant p = new Participant("Participant" + new Random().nextInt() % 1000);
		p.connect("localhost", 1099, "ChatRoom1");
		p.send("Coucou", "ChatRoom1");
	}
}