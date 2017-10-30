package com.vstack.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.InetAddress;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.vstack.beans.OpenstackConnection;

/**
 * Utility Class for the Aegis Toolkit
 */

public class VStackUtils {

	private static ServletContext servletContext = null;

	private static String contextPath = null;

	/**
	 * File Separator
	 */
	public static String FS = System.getProperty("file.separator");

	/**
	 * Temporary Directory for putting Logs and Scripts
	 */
	public static String SYSTEM_TMP_DIR = System.getProperty("java.io.tmpdir");

	/**
	 * Log Directory for putting Logs and Scripts
	 */
	public static String LOG_DIR = SYSTEM_TMP_DIR + FS + "logs";

	/**
	 * Temporary Directory for Charts
	 */
	public static String CHARTS_DIR = "charts";

	/**
	 * Throw exception
	 * 
	 * @param ex
	 * @return
	 */
	public static String returnExceptionTrace(Exception ex) {
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		return writer.toString();
	}

	/**
	 * Return the ServletContext from the Controllers
	 * 
	 * @return
	 */
	public static ServletContext getServletContext() {
		return servletContext;
	}
	
	/**
	 * Check  connection
	 * @param connection
	 * @return
	 * @throws IOException
	 */
	public static boolean isServerRachable(OpenstackConnection connection) throws IOException {

		// Check for empty values
		/**/

		 
		InetAddress address = InetAddress.getByName(connection.getServer());
		boolean ifreachable = address.isReachable(1000);;
		return ifreachable;
		
	}
	/**
	 * Sets the ServletContext
	 * 
	 * @param servletContext
	 */
	public static void setServletContext(ServletContext servletContext) {
		VStackUtils.servletContext = servletContext;
	}

	/**
	 * Get the servlet context PAth
	 * 
	 * @return
	 */
	public static String getContextPath() {
		return contextPath;
	}

	/**
	 * Set the servlet context path
	 * 
	 * @param contextPath
	 */
	public static void setContextPath(String contextPath) {
		VStackUtils.contextPath = contextPath;
	}

	// Author: Tran.Pham
	// Create date: Oct 1st, 2016
	// Desc: execute the http call to url
	public static String executeHttpPostRequest(String token, String url, String body)
			throws IOException, ClientProtocolException, JSONException {

		HttpPost Req = new HttpPost(url);
		Req.addHeader("Content-Type", "application/json");
		Req.addHeader("accept", "application/json");
		if (!token.isEmpty())
			Req.addHeader("X-Auth-Token", token);
		if (!body.isEmpty()) {
			HttpEntity entity = new ByteArrayEntity(body.getBytes());
			Req.setEntity(entity);
		}

		return executeHttpRequest(Req);
	}

	// Author: Tran.Pham
	// Create date: Oct 1st, 2016
	// Desc: execute the http call to url
	public static String executeHttpGetRequest(String token, String url)
			throws IOException, ClientProtocolException, JSONException {

		HttpGet Req = new HttpGet(url);
		Req.addHeader("Content-Type", "application/json");
		Req.addHeader("accept", "application/json");
		if (!token.isEmpty())
			Req.addHeader("X-Auth-Token", token);

		return executeHttpRequest(Req);
	}


	//Function to Create a Floating IP
	public static String floatingip(OpenstackConnection conn, String instancename,String projectId,String projectname,String userdomain,String username,String password) throws Exception{
		Session session = createSession(conn);
	    String floatingips = runCommand(conn, "export OS_PROJECT_ID="+projectId+";export OS_PROJECT_NAME=\""+projectname+"\";export OS_USER_DOMAIN_NAME=\""+userdomain+"\";export OS_USERNAME=\""+username+"\";export OS_PASSWORD=\""+password+"\";nova floating-ip-create provider | grep -Eo '[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}'", session);
		deleteSession(session);
		return floatingips.replace("\r", "").replace("\n","");
	}

	//Function to Assign the Created Floating IP to an Instance
	public static Boolean assignFloatingIP(OpenstackConnection conn, String instancename,String projectId,String projectname,String userdomain,String username,String password) throws Exception{
		String floatingIP = floatingip( conn, instancename,projectId,projectname,userdomain,username,password);
		
	    Session session = createSession(conn);
		String res  = runCommand(conn, "export OS_PROJECT_ID="+projectId+";export OS_PROJECT_NAME=\""+projectname+"\";export OS_USER_DOMAIN_NAME=\""+userdomain+"\";export OS_USERNAME=\""+username+"\";export OS_PASSWORD=\""+password+"\";nova floating-ip-associate "+instancename+" \""+floatingIP+"\"", session);
		deleteSession(session);
		return true;
	}
	/**
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String executeHttpRequest(HttpUriRequest request) throws IOException, ClientProtocolException {
		 
		HttpClient httpClient1 = HttpClientBuilder.create().build();
		HttpResponse response1 = httpClient1.execute(request);
		int statusCode = response1.getStatusLine().getStatusCode();
		switch (statusCode) {
		case 200:
			break;
		// Auth-Token-Response
		case 201: {
			if(response1.containsHeader("X-Subject-Token")) {
				String xToken = response1.getFirstHeader("X-Subject-Token").getValue();
				return xToken;
			} else{
				break;
			}
		}
		// POST /Servers response
		case 202: {
			break;
		}
		case 300:
			throw new RuntimeException("Multiple version of API detected : " + statusCode);
		default:
			throw new RuntimeException("Failed : HTTP error code : " + statusCode);
		}
		;

		BufferedReader br = new BufferedReader(new InputStreamReader((response1.getEntity().getContent())));

		String jsonData = "";
		String line;
		while ((line = br.readLine()) != null)
			jsonData += line + "\n";

		httpClient1.getConnectionManager().shutdown();

		return jsonData;
	}

	/**
	 * This  function creates a channel and does an exec in the connected machine
	 * Reads the output of the command and returns the output as a string
	 * @param command1
	 * @param session
	 * @throws JSchException
	 * @throws IOException
	 */
	public static String runCommand(OpenstackConnection conn, String command1, Session session)
			throws JSchException, IOException {
		String result = null;
		//command1 = "source /data/admin-openrc.sh;"+command1;
		command1 = "export OS_AUTH_URL=\"http://" + conn.getServer() + ":5000/v3\";"+command1;
		Channel channel=session.openChannel("exec");
		((ChannelExec) channel).setPty(true);
		((ChannelExec)channel).setCommand(command1);
		channel.setInputStream(null);
		((ChannelExec)channel).setErrStream(System.err);

		InputStream in=channel.getInputStream();
		channel.connect();
		byte[] tmp=new byte[1024];
		while(true){
			while(in.available()>0){
				int i=in.read(tmp, 0, 1024);
				if(i<0)break;
				result = new String(tmp, 0, i);
			}
			if(channel.isClosed()){
				System.out.println("exit-status: "+channel.getExitStatus());
				break;
			}
			try{Thread.sleep(1000);}catch(Exception ee){}
		}
		channel.disconnect();
		System.out.println(result);
		return result;
	  }


	// Function to create a session to SSH into system 
	public static Session createSession(OpenstackConnection conn) throws Exception{
		String host= conn.getServer();
		String user="osbash";
		String password="osbash";
		int port = 2230;
		java.util.Properties config = new java.util.Properties(); 
		config.put("StrictHostKeyChecking", "no");
		JSch jsch = new JSch();
		//creating the session to the computer
		Session session=jsch.getSession(user, host, port);
		session.setPassword(password);
		session.setConfig(config);
		session.connect();
		System.out.println("Connected");
		return session;
	}
	
	//Deleteing a Session
	public static void deleteSession(Session session){
		session.disconnect();
		System.out.println("Dis-connected");
	}
	
	/**
	 * Handle RuntimeException
	 * 
	 * @param ex
	 * @param response
	 * @param message
	 * @throws IOException
	 */
	public static void handleRuntimeException(Exception ex, HttpServletResponse response, String message)
			throws IOException {
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		response.getWriter().write(message + " " + ex.getMessage());
		response.flushBuffer();
	}

	/**
	 * Handle Runtime Error
	 * 
	 * @param ex
	 * @param response
	 * @param message
	 * @throws IOException
	 */
	public static void handleRuntimeError(HttpServletResponse response, String message) throws IOException {
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		if (message != null) {
			response.getWriter().write(message);
		} else {
			response.getWriter().write("");
		}
		response.flushBuffer();
	}

	/**
	 * Handle Runtime Error
	 * 
	 * @param ex
	 * @param response
	 * @param message
	 * @throws IOException
	 */
	public static void handleResponse(HttpServletResponse response, String message) throws IOException {
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().write(message);
		response.flushBuffer();
	}

}
