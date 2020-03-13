package chatServer;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {

	public static void main(String[] args) throws RemoteException, AlreadyBoundException {
		Registry r = LocateRegistry.createRegistry(1099);
		r.bind("ChatRoom1", new ChatRoom("ChatRoom1"));
	}

}
