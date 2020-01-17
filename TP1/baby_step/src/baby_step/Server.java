package baby_step;

import java.io.*;
import java.net.*;

public class Server {

	public static void main(String[] args) {
		ServerSocket s = null;
		try {
			s = new ServerSocket(11799);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Socket s2 = null;
		InputStream in = null;
		OutputStream out = null;
		String name = null;
		while (true) {
			try {
				s2 = s.accept();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				in = s2.getInputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				out = s2.getOutputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				name = new String(in.readNBytes((int) in.readNBytes(1)[0]));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			name = "Hello ".concat(name);
			try {
				out.write(name.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
