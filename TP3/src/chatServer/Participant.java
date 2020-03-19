package chatServer;

import java.io.Console;
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
	protected Participant() throws RemoteException {
		super();
	}

	private static final long serialVersionUID = 1L;

	String name;

	Map<String, IChatRoom> rooms = new HashMap<String, IChatRoom>();

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
			if (e.getKey().contentEquals(roomName)) {
				e.getValue().say(this, message);
			}
		}
	}

	@SuppressWarnings("unchecked")
	void run() throws AccessException, RemoteException, NotBoundException {
		System.out.println("What's your name ?");
		Console c = System.console();
		name = c.readLine();
		String[] params;
		while(true){
				params = c.readLine().split(" ");
				switch(params[0]) {
				case "/say":
					
					break;
				case "/connect":
					
					break;
				case "/leave":
					
					break;
				case "/who":
					
					break;
				case "/stop":
					Entry<String,IChatRoom>[] ccr = (Entry<String, IChatRoom>[]) rooms.entrySet().toArray();
					for(int i = 0; i<ccr.length; i++) {
						ccr[i].getValue().leave(this);
					}
					System.exit(0);
				}
			
		}
	}

}
