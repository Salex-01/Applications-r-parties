package myRicmlets;

import java.io.IOException;
import java.io.PrintStream;

import httpserver.itf.HttpRicmletRequest;
import httpserver.itf.HttpRicmletResponse;

public class MyFirstCookieRicmlet implements httpserver.itf.HttpRicmlet {

	@Override
	public void doGet(HttpRicmletRequest req, HttpRicmletResponse resp) throws IOException {

		String c = req.getCookie("MyFirstCookie");
		int v;
		if (c != null) {
			v = Integer.parseInt(c) + 1;
		} else {
			v = 1;
		}
		resp.setReplyOk();
		resp.setContentType("text/html");
		resp.setCookie("MyFirstCookie", Integer.toString(v));
		PrintStream ps = resp.beginBody();
		ps.println("<HTML><HEAD><TITLE> Ricmlet processing </TITLE></HEAD>");
		ps.println("<BODY><H4> Cookie value : " + v + "</H4></BODY></HTML>");
		ps.println();
	}
}
