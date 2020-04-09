package httpserver.itf.impl;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import httpserver.itf.HttpRequest;
import httpserver.itf.HttpRicmletResponse;

public class HttpRicmletResponseImpl extends HttpResponseImpl implements HttpRicmletResponse {

	LinkedList<String> cookiesSet = new LinkedList<String>();

	boolean overrideSession = false;
	String sessionValue = null;

	protected HttpRicmletResponseImpl(HttpServer hs, HttpRequest req, PrintStream ps) {
		super(hs, req, ps);
	}

	//////////////////////////////////////////////////
	/**
	 * DO NOT USE setCookie("session-id",value) AND setSession(value) AT THE SAME
	 * TIME
	 **/

	@Override
	public void setCookie(String name, String value) {
		cookiesSet.add(name);
		m_ps.println("Set-Cookie: " + name + "=" + value);
	}

	@Override
	public void setSession(String id) {
		overrideSession = true;
		sessionValue = id;
	}

	//////////////////////////////////////////////////

	@Override
	public PrintStream beginBody() {
		Iterator<Entry<String, String>> i = ((HttpRicmletRequestImpl) (this.m_req)).cookies.entrySet().iterator();
		Entry<String, String> e;
		while (i.hasNext()) {
			e = i.next();
			if (e.getKey().contentEquals("session-id") && overrideSession) {
				setCookie("session-id", sessionValue);
			} else {
				if (!cookiesSet.contains(e.getKey())) {
					setCookie(e.getKey(), e.getValue());
				}
			}
		}
		m_ps.println();
		return m_ps;
	}

}