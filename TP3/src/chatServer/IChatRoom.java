package chatServer;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IChatRoom extends Remote {

	/*
	 * allows a new participant to connect to a chat room
	 */
	void connect(IParticipant p) throws RemoteException;

	/*
	 * allows a participant to leave a chat room
	 */
	void leave(String name) throws RemoteException;

	/*
	 * allows a participant to send a message to a given chat room
	 */
	void say(IParticipant p, String message) throws RemoteException;

	/*
	 * allows a participant to get the names of all participants currently connected
	 * to a chat room
	 */
	String[] who() throws RemoteException;

	/*
	 * returns the name of this ChatRoom
	 */
	String name() throws RemoteException;
}
