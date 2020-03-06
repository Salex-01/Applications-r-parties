package babystep1;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Printer extends UnicastRemoteObject implements IPrinter {
	private static final long serialVersionUID = 1L;

	protected Printer() throws RemoteException {
		super();
	}

	@Override
	public void printLine(String s) throws RemoteException {
		System.out.println(s);
	}

}
