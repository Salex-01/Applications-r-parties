package MultithreadedFileServer;

public class MainServer {

	public static void main(String[] args) {
		new Server(11799).start();
	}
}
