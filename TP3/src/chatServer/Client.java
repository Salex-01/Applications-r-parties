package chatServer;

import java.io.IOException;
import java.rmi.NotBoundException;

public class Client {

	public static void main(String[] args) throws NotBoundException, IOException {
		Participant p = new Participant();
		p.run();
	}
}