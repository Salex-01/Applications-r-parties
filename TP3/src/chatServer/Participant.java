package chatServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Participant extends UnicastRemoteObject implements IParticipant {
	protected Participant() throws RemoteException {
		super();
	}

	private static final long serialVersionUID = 1L;

	static int DEFAULT_PORT = 1099;

	String name;

	Map<String, IChatRoom> rooms = new HashMap<String, IChatRoom>();

	boolean connect(String address, int port, String crName)
			throws AccessException, RemoteException, NotBoundException {
		Registry r = LocateRegistry.getRegistry(address, port);
		IChatRoom iCR = (IChatRoom) r.lookup(crName);
		if (iCR == null) {
			System.err.println(crName + " was not found on " + address + ":" + port);
			return false;
		}
		iCR.connect(this);
		rooms.put(crName, iCR);
		return true;
	}

	boolean leave(String crName) throws RemoteException {
		IChatRoom iCR = rooms.get(crName);
		if (iCR == null) {
			System.err.println("You are not connected to room " + crName);
			return false;
		} else {
			iCR.leave(this);
			rooms.remove(crName);
			return true;
		}
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
		IChatRoom cr = rooms.get(roomName);
		if (cr == null) {
			System.err.println("You are not connected to " + roomName);
		} else {
			cr.say(this, message);
			System.out.println("Sent");
		}
	}

	void who(String roomName) throws RemoteException {
		IChatRoom cr = rooms.get(roomName);
		if (cr == null) {
			System.err.println("You are not connected to " + roomName);
		} else {
			String[] res = cr.who();
			System.out.println("Members connected to " + roomName + " :");
			for (int i = 0; i < res.length; i++) {
				System.out.println(res[i]);
			}
		}
	}

	@SuppressWarnings("unchecked")
	void run() throws NotBoundException, IOException {
		System.out.println("What's your name ?");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		name = reader.readLine();
		String[] params;
		String line;
		while (true) {
			try {
				line = reader.readLine();
				params = line.split(" ");
				switch (params[0]) {
				case "say":
					if (params.length < 3) {
						System.err.println("synthax : /say <room name> <message>");
					} else {
						send(line.substring(5 + params[1].length()), params[1]);
					}
					break;
				case "connect":
					if (params.length == 3) {
						if (connect(params[1], DEFAULT_PORT, params[2])) {
							System.out.println("Connected to " + params[2]);
						}
					} else if (params.length == 4) {
						if (connect(params[1], Integer.parseInt(params[3]), params[2])) {
							System.out.println("Connected to " + params[2]);
						}
					} else {
						System.err.println("synthax : /connect <server address> <room name> [<server port>]");
					}
					break;
				case "leave":
					if (params.length != 2) {
						System.err.println("synthax : /leave <room name>");
					} else {
						if (leave(params[1])) {
							System.out.println("Left " + params[1]);
						}
					}
					break;
				case "who":
					if (params.length != 2) {
						System.err.println("synthax : /who <room name>");
					} else {
						who(params[1]);
					}
					break;
				case "stop":
					Entry<String, IChatRoom>[] ccr = (Entry<String, IChatRoom>[]) rooms.entrySet().toArray();
					for (int i = 0; i < ccr.length; i++) {
						ccr[i].getValue().leave(this);
					}
					System.exit(0);
					break;
				default:
					System.err.println("Unknown command. Use : say, connect, leave, who or stop");	
				}
			} catch (RemoteException e) {
				System.err.println("There seems to be an issue with the connection to the server");
			}
		}
	}

}
