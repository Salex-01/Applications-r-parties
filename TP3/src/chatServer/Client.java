package chatServer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {

	public static void main(String[] args) throws RemoteException, NotBoundException {

		Participant p = new Participant();
		p.run();
	}
}