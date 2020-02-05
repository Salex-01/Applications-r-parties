package MultithreadedFileServerV3;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class Server extends Thread {
	ServerSocket s = null;

	int Nworks = 3;	//Nombre de Threads
	ExecServer[] workers = new ExecServer[Nworks];	//Tableau de Threads
	Semaphore ready = new Semaphore(Nworks);	//Semaphore s'assurer que les threads ont accès à la mémoire un par un

	public Server(int port) throws IOException {	//Constructeur
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
				ready.acquire();	//Attend qu'un thread soit prêt
				while (!workers[i].isReady()) {	//Recherche un thread libre
					i++;
					if (i >= Nworks) {
						i = 0;
					}
				}
				workers[i].affect(s2); //Affecte la tache au thread
				workers[i].go.release(); //Signale au worker qu'il peut commencer à travailler
				s2 = null;
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
