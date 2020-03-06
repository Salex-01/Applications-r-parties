package babystep1;

import java.rmi.RemoteException;

public interface IPrinter extends java.rmi.Remote {

	public void printLine(String s) throws RemoteException;
}
