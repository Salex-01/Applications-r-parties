package httpserver.itf.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;

import httpserver.itf.HttpRequest;
import httpserver.itf.HttpResponse;

public class HttpStaticRequest extends HttpRequest {
	static final int BUF_SZ = 1024;
	static final String DEFAULT_FILE = "index.html";
	
	public HttpStaticRequest(HttpServer hs, String method, String ressname) throws IOException {
		super(hs, method, ressname);
	}
	
	public void process(HttpResponse resp) throws Exception {
		
		String folder=m_hs.getFolder().getName();	//Get name of the file
		m_ressname=folder+m_ressname;
		File file= new File(m_ressname);	//Create a file with the same URL
		
		if(file.exists()) {
			
			resp.setReplyOk();
			resp.setContentLength((int)file.length());
			resp.setContentType(getContentType(m_ressname));
			
			PrintStream ps = resp.beginBody();
			FileInputStream flux = new FileInputStream(file);
			
			ps.write(flux.readAllBytes());
			flux.close();
			
			ps.flush();
			ps.close();
			
		}
		else {
			resp.setReplyError(404, "File not Found");
		}
	}

}
