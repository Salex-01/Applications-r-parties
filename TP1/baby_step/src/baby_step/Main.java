package baby_step;

public class Main {
	static Client client;
	static Server server;

	public static void main(String[] args) {
		server= new Server(11799);
		
		server.start();
		
		client=new Client("test",server.getIP(),11799);
		client.start();
		
	}
}
