package chatServer;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Participant extends UnicastRemoteObject implements IParticipant {
	private static final long serialVersionUID = 1L;

	String name;

	Map<String, IChatRoom> rooms = new HashMap<String, IChatRoom>();

	public Participant(String n) throws RemoteException {
		name = n;
	}

	void connect(String adress, int port, String crName) throws AccessException, RemoteException, NotBoundException {
		Registry r = LocateRegistry.getRegistry(adress, port);
		IChatRoom iCR = (IChatRoom) r.lookup(crName);
		iCR.connect(this);
		rooms.put(iCR.name(), iCR);
	}

	@Override
	public String name() throws RemoteException {
		return name;
	}

	@Override
	public void receive(String name, String msg) throws RemoteException {
		System.out.println(name + " : " + msg);
	}

	void send(String message, String roomName) throws RemoteException {
		Iterator<Entry<String, IChatRoom>> i = rooms.entrySet().iterator();
		while (i.hasNext()) {
			Entry<String, IChatRoom> e = i.next();
			if(e.getKey().contentEquals(roomName)) {
				e.getValue().say(this, message);
			}
		}
	}

}
