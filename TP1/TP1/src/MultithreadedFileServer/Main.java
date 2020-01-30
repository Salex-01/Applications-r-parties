package MultithreadedFileServer;

public class Main {
	static Client client;
	static Client client2;
	static Server server;

	public static void main(String[] args) {
		server= new Server(11799);
		server.start();
		
		client=new Client(server.getIP(),11799);
		client.start();
		System.out.println("Premier client établie");
		
		client2=new Client(server.getIP(),11799);
		client2.start();
		System.out.println("Deuxieme client établie");
	}
}
