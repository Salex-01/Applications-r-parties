package httpserver.itf;

/*
 * Extends the HttpResponse interface to provide response information for ricmlets. 
 */
public interface HttpRicmletResponse extends HttpResponse {

	/*
	 * Set-up a cookie for the client browser to memorize
	 */
	public void setCookie(String name, String value);
	
	/*
	 * Set-up a session-id value for the client browser to memorize
	 */
	public void setSession(String id);

}
