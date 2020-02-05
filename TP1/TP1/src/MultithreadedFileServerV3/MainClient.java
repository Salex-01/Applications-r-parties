package MultithreadedFileServerV3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainClient {

	public static void main(String[] args) {
		System.out.println("Entrez l'adresse du serveur");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try {
			new Client(reader.readLine(),11799).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
