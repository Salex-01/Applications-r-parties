package httpserver.itf.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

public class AgingThread extends Thread {

	HashMap<String, Session> sessions;

	LinkedList<String> toRemoveList = new LinkedList<String>();
	
	public void setSessions(HashMap<String, Session> s) {
		sessions = s;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			synchronized (sessions) {	// to avoid conflicts with the main thread accessing the HashMap
				Iterator<Entry<String, Session>> i = sessions.entrySet().iterator();
				while (i.hasNext()) {
					Entry<String, Session> e = i.next();
					Session s = e.getValue();
					if (s.age()) {
						toRemoveList.add(e.getKey());
					}
				}
				Iterator<String> j = toRemoveList.iterator();
				while(j.hasNext()) {
					sessions.remove(j.next());
				}
			}
		}
	}

}
