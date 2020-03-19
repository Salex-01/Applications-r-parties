package chatServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class ChatRoom extends UnicastRemoteObject implements IChatRoom {
	private static final long serialVersionUID = 1L;

	String name;

	Map<String, IParticipant> clients = new HashMap<String, IParticipant>();

	public ChatRoom(String n) throws RemoteException {
		name = n;
	}

	public void connect(IParticipant p) throws RemoteException {
		if (clients.get(p.name()) != null) {
			return;
		}
		clients.put(p.name(), p);
	}

	public void leave(IParticipant p) throws RemoteException {
		clients.remove(p.name());
	}

	public void say(IParticipant p, String message) throws RemoteException {
		String n = p.name();
		if (clients.containsKey(n)) {
			Iterator<Entry<String, IParticipant>> i = clients.entrySet().iterator();
			while (i.hasNext()) {
				Entry<String, IParticipant> e = i.next();
				if (!e.getKey().contentEquals(n)) {
					e.getValue().receive(n, message);
				}
			}
		}
	}

	public String[] who() throws RemoteException {
		String[] res = new String[clients.size()];
		int j = 0;
		Iterator<Entry<String, IParticipant>> i = clients.entrySet().iterator();
		while (i.hasNext()) {
			res[j++] = i.next().getKey();
		}
		return res;
	}

	public String name() throws RemoteException {
		return name;
	}
}
