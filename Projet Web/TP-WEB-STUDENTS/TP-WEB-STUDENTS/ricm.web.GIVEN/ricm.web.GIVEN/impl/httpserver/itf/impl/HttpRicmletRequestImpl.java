package httpserver.itf.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;

import httpserver.itf.HttpResponse;
import httpserver.itf.HttpRicmlet;
import httpserver.itf.HttpRicmletRequest;
import httpserver.itf.HttpRicmletResponse;
import httpserver.itf.HttpSession;

public class HttpRicmletRequestImpl extends HttpRicmletRequest {

	HashMap<String, String> cookies = new HashMap<String, String>();

	private Session s = null;

	public HttpRicmletRequestImpl(HttpServer hs, String method, String ressname, BufferedReader br) throws IOException {
		super(hs, method, ressname);

		// Parsing cookies
		String str;
		String[] c;
		String[] splitcookies;
		do {
			c = null;
			str = br.readLine();
			if (str.startsWith("Cookie: ")) {
				c = str.split(" ");
				for (int i = 1; i < c.length; i++) {
					if (c[i].endsWith(";")) {
						c[i] = c[i].substring(0, c[i].length() - 1);
					}
					splitcookies = c[i].split("=");
					cookies.put(splitcookies[0], splitcookies[1]);
				}
			}
		} while (!str.contentEquals(""));

		// Getting session
		String sessionID = cookies.get("session-id");
		if (sessionID == null) {
			s = new Session();
			cookies.put("session-id", s.getId());
			hs.sessions.put(s.getId(), s);
		} else {
			synchronized (hs.sessions) { // to avoid conflicts with the sessions aging thread
				s = hs.sessions.get(sessionID);
				if (s == null) {
					s = new Session();
					cookies.put("session-id", s.getId());
					hs.sessions.put(s.getId(), s);
				} else {
					s.resetTTL();
				}
			}
		}

	}

	@Override
	public HttpSession getSession() {
		return s;
	}

	@Override
	public String getArg(String name) {
		if (name == null) {
			return null;
		}
		String folder = m_hs.getFolder().getName();
		m_ressname = folder + m_ressname;

		String[] url = m_ressname.split("\\?"); // Keep the second part
		
		if(url.length!=2) {
			return null;
		}

		String[] args = url[1].split("&");
		for (int i = 0; i < args.length; i++) {
			String[] argument = args[i].split("=");
			if (argument[0].contentEquals(name)) {
				return argument[1];
			}
		}
		return null;
	}

	@Override
	public String getCookie(String name) {
		return cookies.get(name);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void process(HttpResponse resp) throws Exception {

		String folder = m_hs.getFolder().getName();
		m_ressname = folder + m_ressname;

		String[] url = m_ressname.split("\\?");
		
		String[] base = url[0].split("/");
		HttpRicmlet ricmlet = m_hs.activeRicmlets.get(base[2] + "." + base[3]);
		if (ricmlet == null) {
			ricmlet = ((HttpRicmlet) Class.forName(base[2] + "." + base[3]).newInstance());
			m_hs.activeRicmlets.put(base[2] + "." + base[3], ricmlet);
		}
		ricmlet.doGet(this, (HttpRicmletResponse) resp);
	}

}
