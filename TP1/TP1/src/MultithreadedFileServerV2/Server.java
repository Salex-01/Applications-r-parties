package MultithreadedFileServerV2;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class Server extends Thread {
	ServerSocket s = null;

	int Nworks = 3;
	ExecServer[] workers = new ExecServer[Nworks];
	Semaphore ready = new Semaphore(Nworks);

	public Server(int port) throws IOException {
		s = new ServerSocket(port);
		for (int i = 0; i < workers.length; i++) {
			workers[i] = new ExecServer(this);
			workers[i].start();
		}
	}

	public InetAddress getIP() {
		return s.getInetAddress();
	}

	@Override
	public void run() {
		int i = 0;
		Socket s2 = null;
		while (true) {
			try {
				s2 = s.accept();
				ready.acquire();
				while (!workers[i].isReady()) {
					i++;
					if (i >= Nworks) {
						i = 0;
					}
				}
				workers[i].affect(s2);
				workers[i].go.release();
				s2 = null;
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
