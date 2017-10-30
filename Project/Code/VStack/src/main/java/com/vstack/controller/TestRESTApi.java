package com.vstack.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.vstack.beans.InstanceDetails;
import com.vstack.beans.InstanceUsage;
import com.vstack.beans.OpenstackConnection;
import com.vstack.openstack.services.AuthService;
import com.vstack.openstack.services.InstanceService;
import com.vstack.openstack.services.InstanceUsageService;

public class TestRESTApi {

	public static void main(String[] args) {
		try{
		
				OpenstackConnection connection = new OpenstackConnection();
				connection.setServer("10.0.0.11");
				connection.setUsername("admin");
				connection.setPassword("admin_user_secret");
				 
				AuthService service = new AuthService();
				service.setAuthToken(connection);
				String token = service.getAuthToken();
				
				/*NetworkService nw = new NetworkService(connection.getServer(), token);
				String nwuuid = nw.getNetworks("provider");
				
				ComputeInstance instance = new ComputeInstance();
				instance.setFlavor("m1.tiny");
				instance.setImage("cirros");
				instance.setInstanceName("vstack");
				
				InstanceService instanceService = new InstanceService(connection.getServer(), token);
				Map<String, String> instanceId = instanceService.launchInstance(instance, nwuuid);
				InstanceDetails instanceDetail = instanceService.getInstanceByName(instance.getInstanceName());
			
				AuthService authentication = new AuthService();
				Map<String, String> projects = authentication.getOpenstackProjects(connection, token);
			
				// call to assign a Floating IP to an instance
				//Boolean assigningfloatingipresult = assignFloatingIP(instance.getInstanceName(),projects.get("admin"),"admin","default","admin","admin_user_secret");
			
				
				
				assignRole(projects.get("admin"),"admin","default","admin","admin_user_secret");
*/
				InstanceService instanceService = new InstanceService(connection, token);
				InstanceDetails details = instanceService.getInstanceByName("intance3");

				InstanceUsageService usageService = new InstanceUsageService(connection.getServer(), token);
				List<InstanceUsage> usage = usageService.getInstanceUsageByProject(connection);
				for(InstanceUsage usage1 : usage) {
					System.out.println(usage1.getName());
					System.out.println(usage1.getMemory_mb());
					System.out.println(usage1.getUptime());
					System.out.println(usage1.getVcpus());
					System.out.println(usage1.getFlavor());
					
				}
				
			} catch(Exception ex) {
				System.out.println(ex.getMessage());
			}
	}
	

	//Function to Assign Role to user
	public static Boolean assignRole(String projectId,String projectname,String userdomain,String username,String password) throws Exception{
		Session session = createSession();
		String roleassigned = runCommand("export OS_PROJECT_ID="+projectId+";export OS_PROJECT_NAME=\""+projectname+"\";export OS_USER_DOMAIN_NAME=\""+userdomain+"\";export OS_USERNAME=\""+username+"\";export OS_PASSWORD=\""+password+"\";export OS_IDENTITY_API_VERSION=3;openstack role add --project admin --user vstack admin ", session);
		deleteSession(session);
		return true;
	}
	

/**
 * This  function creates a channel and does an exec in the connected machine
 * Reads the output of the command and returns the output as a string
 * @param command1
 * @param session
 * @throws JSchException
 * @throws IOException
 */
private static String runCommand(String command1, Session session)
		throws JSchException, IOException {
	String result = null;
	//command1 = "source /data/admin-openrc.sh;"+command1;
	command1 = "export OS_AUTH_URL=\"http://controller:5000/v3\";"+command1;
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
	private static Session createSession() throws Exception{
		String host="localhost";
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

	//Function to Create a Floating IP
	public static String floatingip(String instancename,String projectId,String projectname,String userdomain,String username,String password) throws Exception{
		Session session = createSession();
	    String floatingips = runCommand("export OS_PROJECT_ID="+projectId+";export OS_PROJECT_NAME=\""+projectname+"\";export OS_USER_DOMAIN_NAME=\""+userdomain+"\";export OS_USERNAME=\""+username+"\";export OS_PASSWORD=\""+password+"\";nova floating-ip-create provider | grep -Eo '[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}'", session);
		deleteSession(session);
		return floatingips.replace("\r", "").replace("\n","");
	}

	//Function to Assign the Created Floating IP to an Instance
	public static Boolean assignFloatingIP(String instancename,String projectId,String projectname,String userdomain,String username,String password) throws Exception{
		String floatingIP = floatingip(instancename,projectId,projectname,userdomain,username,password);
		
	    Session session = createSession();
		String res  = runCommand("export OS_PROJECT_ID="+projectId+";export OS_PROJECT_NAME=\""+projectname+"\";export OS_USER_DOMAIN_NAME=\""+userdomain+"\";export OS_USERNAME=\""+username+"\";export OS_PASSWORD=\""+password+"\";nova floating-ip-associate "+instancename+" \""+floatingIP+"\"", session);
		deleteSession(session);
		return true;
	}

	//Deleteing a Session
	private static void deleteSession(Session session){
		session.disconnect();
		System.out.println("Dis-connected");
	}
}