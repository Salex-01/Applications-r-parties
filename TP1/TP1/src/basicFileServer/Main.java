package basicFileServer;

public class Main {
	static Client client;
	static Server server;

	public static void main(String[] args) {
		server= new Server(11799);
		server.start();
		
		client=new Client(server.getIP(),11799);
		client.start();
	}
}
