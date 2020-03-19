package chatServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.AlreadyBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {

	static int DEFAULT_PORT = 1099;

	public static void main(String[] args) throws AlreadyBoundException, IOException {
		System.out.println("Server port ? (enter for default 1099)");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String port = reader.readLine();
		Registry r;
		if (port.contentEquals("")) {
			System.out.println("Using port 1099");
			r = LocateRegistry.createRegistry(DEFAULT_PORT);
		} else {
			System.out.println("Using port " + port);
			r = LocateRegistry.createRegistry(Integer.parseInt(port));
		}
		r.bind("def", new ChatRoom("Default"));
		String line;
		while (true) {
			line = reader.readLine();
			String[] s = line.split(" ");
			if (s.length != 2 || (!s[0].contentEquals("add") && (!s[0].contentEquals("who")))) {
				System.err.println("add <new room name>\nOR\nwho <existing room name>");
			} else {
				switch (s[0]) {
				case "add":
					r.bind(s[1], new ChatRoom(s[1]));
					break;
				case "who":
					try {
						String[] res = ((ChatRoom) r.lookup(s[1])).who();
						if (res.length == 0) {
							System.out.println(s[1] + " is empty");
						}else {
							System.out.println("Members connected to " + s[1] + " :");
							for (int i = 0; i < res.length; i++) {
								System.out.println(res[i]);
							}
						}
					} catch (Exception e) {
						System.err.println("Unable to communicate with " + s[1]);
					}
					break;
				}
			}
		}
	}

}
