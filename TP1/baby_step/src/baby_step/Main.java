package baby_step;

public class Main {
	Client client;
	Server server;

	public static void main(String[] args) {
		server= new Server(11799);
		
		server.start();
		client=new Client(test,server,11799);
		
	}
}
