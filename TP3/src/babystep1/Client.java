package babystep1;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
	
	public static void main(String[] args) throws RemoteException, NotBoundException {
		Registry r = LocateRegistry.getRegistry("localhost", 1099);
		IPrinter p = (IPrinter) r.lookup("printer");
		p.printLine("Coucou");
	}
	
}
