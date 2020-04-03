package httpserver.itf.impl;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import httpserver.itf.HttpResponse;
import httpserver.itf.HttpRicmlet;
import httpserver.itf.HttpRicmletRequest;
import httpserver.itf.HttpRicmletResponse;
import httpserver.itf.HttpSession;

public class HttpRicmletRequestImpl extends HttpRicmletRequest {

	public HttpRicmletRequestImpl(HttpServer hs, String method, String ressname) throws IOException {
		super(hs, method, ressname);
	}

	@Override
	public HttpSession getSession() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getArg(String name) {
		if(name==null) {
			return null;
		}
		String folder = m_hs.getFolder().getName();
		m_ressname = folder + m_ressname;

		String[] url=m_ressname.split("\\?"); //Keep the second part
		
		String [] url1 = url[1].split("&");
		for(int i=0;i<url1.length;i++) {
			if(url1[i].contains(name)){
				String[] argument=url1[i].split("=");
				return argument[1];
			}
		}
		return null;
	}

	@Override
	public String getCookie(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void process(HttpResponse resp) throws Exception {
		
		String folder = m_hs.getFolder().getName();
		m_ressname = folder + m_ressname;

		String[] url=m_ressname.split("\\?");
		StringTokenizer url1 = new StringTokenizer (url[0],"/");
		url1.nextToken();
		String NameRicmlet1=url1.nextToken();
		String NameRicmlet2=url1.nextToken();
		String NameRicmlet3=url1.nextToken();

		HttpRicmlet Classe=((HttpRicmlet) Class.forName(NameRicmlet2+"."+NameRicmlet3).newInstance());
		Classe.doGet(this,(HttpRicmletResponse) resp);
		System.out.println("C bon");
	}

}
