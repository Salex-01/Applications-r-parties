package MultithreadedFileServerV3;

import java.io.IOException;

public class MainServer {

	public static void main(String[] args) throws IOException {
		new Server(11799).start();
	}
}
