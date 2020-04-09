package httpserver.itf.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.StringTokenizer;

import httpserver.itf.HttpRequest;
import httpserver.itf.HttpResponse;
import httpserver.itf.HttpRicmlet;

/**
 * Basic Http Server Implementation
 * 
 * Only manages static requests The url for a static ressource is of the form:
 * "http//host:port/<path>/<ressource name>" For example, try accessing the
 * following urls from your brower, knowing that the current Eclipse project
 * contains a FILES directory: http://localhost:<port>/FILES/
 * http://localhost:<port>/FILES/voile.jpg ...
 */
public class HttpServer {

	private int m_port;
	private File m_folder;
	private ServerSocket m_ssoc;

	HashMap<String, Session> sessions = new HashMap<String, Session>();
	HashMap<String, HttpRicmlet> activeRicmlets = new HashMap<String, HttpRicmlet>();

	protected HttpServer(int port, String folderName) {
		m_port = port;
		if (!folderName.endsWith(File.separator))
			folderName = folderName + File.separator;
		m_folder = new File(folderName);
		try {
			m_ssoc = new ServerSocket(m_port);
			System.out.println("HttpServer started on port " + m_port);
			System.out.println("HttpServer folderName " + folderName);
		} catch (IOException e) {
			System.out.println("HttpServer Exception:" + e);
			System.exit(1);
		}
	}

	public File getFolder() {
		return m_folder;
	}

	public HttpRicmlet getInstance(String clsname)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, MalformedURLException {
		throw new Error("No Support for Ricmlets");
	}

	protected void loop() {
		try {
			while (true) {
				Socket soc = m_ssoc.accept();
				(new HttpWorker(this, soc)).start();
			}
		} catch (IOException e) {
			System.out.println("HttpServer Exception, skipping request");
			e.printStackTrace();
		}
	}

	/*
	 * Reads a request on the given input stream and returns the corresponding
	 * HttpRequestImpl object
	 */
	public HttpRequest getRequest(BufferedReader br) throws IOException {
		String line = null;
		HttpRequest request = null;
		line = br.readLine();
		if(line==null) {
			System.err.println("Received a null request. Skipping.");
			throw new IOException("Null request");
		}
		String[] tokens = line.split(" ");
		String method = tokens[0].toUpperCase();
		String ressname = tokens[1];
		if (method.equals("GET")) {
			if (ressname.contains("ricmlets")) {
				request = new HttpRicmletRequestImpl(this, method, ressname, br);
			} else {
				request = new HttpStaticRequest(this, method, ressname);
			}
		} else {
			request = new UnknownRequest(this, method, ressname);
		}
		return request;
	}

	/*
	 * Returns an HttpResponse object corresponding the the given HttpRequest object
	 */
	public HttpResponse getResponse(HttpRequest req, PrintStream ps) {
		if (req instanceof HttpStaticRequest) {
			return new HttpResponseImpl(this, req, ps);
		} else {
			return new HttpRicmletResponseImpl(this, req, ps);
		}
	}

	public static void main(String[] args) {
		int port = 0;
		if (args.length != 2) {
			System.out.println("Usage: java Server <port-number> <file folder>");
		} else {
			port = Integer.parseInt(args[0]);
			String foldername = args[1];
			HttpServer hs = new HttpServer(port, foldername);
			AgingThread at = new AgingThread();
			at.setSessions(hs.sessions);
			at.start();
			hs.loop();
		}
	}

}
