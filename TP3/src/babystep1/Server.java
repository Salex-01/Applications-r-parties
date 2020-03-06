package babystep1;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {

	static IPrinter printer;

	public static void main(String[] args) throws RemoteException, AlreadyBoundException {
		printer = new Printer();
		Registry r = LocateRegistry.createRegistry(1099);
		r.bind("printer", printer);
	}
}
