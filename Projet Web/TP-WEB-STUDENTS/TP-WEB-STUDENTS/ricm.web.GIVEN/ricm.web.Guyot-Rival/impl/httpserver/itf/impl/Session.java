package httpserver.itf.impl;

import java.util.HashMap;

import httpserver.itf.HttpSession;

public class Session implements HttpSession {

	
	/** Time to live in seconds **/
	private static long ttl = 5;

	
	static private int currID = 0;
	private String id;
	private HashMap<String, Object> elements = new HashMap<String, Object>();
	private long timeleft = ttl;

	public Session() {
		id = Integer.toString(currID);
		currID++;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Object getValue(String key) {
		return elements.get(key);
	}

	@Override
	public void setValue(String key, Object value) {
		elements.put(key, value);
	}

	public synchronized void resetTTL() {
		timeleft = ttl;
	}

	public synchronized boolean age() {
		timeleft--;
		return (timeleft == 0);
	}

}
