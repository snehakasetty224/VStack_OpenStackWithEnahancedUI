package com.vstack.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jcraft.jsch.Session;
import com.vstack.beans.OpenstackConnection;
import com.vstack.openstack.services.AuthService;
import com.vstack.openstack.services.OpenstackAPI;
import com.vstack.services.Bootstrap;
import com.vstack.services.VStackException;
import com.vstack.services.VStackUtils;

@Controller
public class UserController implements OpenstackAPI {

	@Autowired
	ServletContext context;

	@Autowired
	Bootstrap bootstrap;

	@Autowired
	AuthService authentication;

	@Autowired
	OpenstackConnection connection;

	private static Logger logger = Logger.getLogger("UserController");

	@RequestMapping(value = "/fblogin", method = RequestMethod.POST)
	public void login(@RequestBody OpenstackConnection connection, HttpServletResponse response) throws IOException {
		String isUser = "false";
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		
		if (connection.getServer() != null) {
			try {

				logger.info("------ User Controller ------");
				
				if (VStackUtils.isServerRachable(connection)) {

					this.authentication.setAuthToken(connection);
					
					 
					this.connection.setServer(connection.getServer());

					if (connection.getUsername() != null) {

						this.connection.setUsername(connection.getUsername());
						this.connection.setPassword(connection.getPassword());
						
						Map<String, String> userList = getUsers(connection);
						if(!userList.containsKey(connection.getArg1())) {
							if(connection.getArg1() != null) {
								createUser(connection);
								isUser = "true";
								this.authentication.setAuthToken(connection);
								this.connection.setArg1(connection.getArg1());
								this.connection.setArg2(defaultPassword);
							}
						} else {
							//Assumption : User already created;
							isUser = "true";
						}
					} else {
						//Assumption : User already created;
						this.connection.setArg1(connection.getArg1());
						this.connection.setArg2(defaultPassword);
						this.authentication.setAuthToken(connection);
					}
					
					logger.info("User added");
					VStackUtils.handleResponse(response, ow.writeValueAsString(isUser));

				} else {
					VStackUtils.handleResponse(response, connection.getServer() + " - Openstack Server is not reachable. ");
				}
			} catch (Exception ex) {
				logger.fatal(ex.getMessage());
				logger.fatal(VStackUtils.returnExceptionTrace(ex));
				VStackUtils.handleResponse(response, ow.writeValueAsString("User Creation status : " + isUser + "Reason : " + ex.getMessage()));
			}

		} else {
			VStackUtils.handleResponse(response, ow.writeValueAsString(isUser));
		}
			
	}

	/**
	 * 
	 * @param conn
	 * @throws VStackException
	 */
	private void createUser(OpenstackConnection conn) throws VStackException {
 
		String url = "http://" + conn.getServer() + KEYSTONE_IDENTITY + USERS;
		String reqBody;

		try {
			 
			Map<String, String> domains = authentication.getOpenstackDomains(connection,
					this.authentication.getAuthToken());
			Map<String, String> projects = authentication.getOpenstackProjects(connection,
					this.authentication.getAuthToken());

			reqBody = String.format("{\"user\": {\"domain_id\":\"" + domains.get(domainName) + "\", "
					+ "\"default_project_id\": \"" + projects.get(projectName) + "\","
					+ "\"enabled\": true,\"name\":\"%s\", \"password\":\"%s\"}}", conn.getArg1(), defaultPassword);

			logger.info("Create User POST REST Url :" + url);
			logger.info("Create User Request Body :" + reqBody);
			
			String response = VStackUtils.executeHttpPostRequest(this.authentication.getAuthToken(), url, reqBody);

			System.out.println(response);

			//Assigning role to user
			boolean role = assignRole(conn.getArg1(), projects.get(projectName), projectName, domainName, conn.getUsername(), conn.getPassword() );
			if(role) {
				logger.info("Role assigned");
			} else {
				throw new VStackException("Role can not be assigned to user ");
			}
		} catch (Exception ex) {
			throw new VStackException(ex.getMessage());
		}

		
	}

	
	/**
	 *  
	 * @param conn
	 * @throws VStackException
	 */
	public Map<String, String> getUsers(OpenstackConnection conn) throws VStackException {

		String url = "http://" + conn.getServer() + KEYSTONE_IDENTITY + USERS;
		 
		Map<String, String> users = new HashMap<String, String>();
		try {
			conn.setArg2(defaultPassword);
			
			logger.info("Get User REST Url :" + url);
			
			String json = VStackUtils.executeHttpGetRequest(this.authentication.getAuthToken(), url);
			
			JSONObject jsonObj = new JSONObject(json);
			JSONArray jsonToken = jsonObj.getJSONArray("users");
			int i = 0;

			while (i < jsonToken.length()) {
				String user = jsonToken.getJSONObject(i).getString("name");
				String userId = jsonToken.getJSONObject(i).getString("id");
				users.put(user, userId);
				i++;
			}
		} catch (Exception ex) {
			throw new VStackException(ex.getMessage());
		}

		return users;

	}
	
	// Function to Assign Role to user
	private Boolean assignRole(String user, String projectId, String projectname, String userdomain, String username,
			String password) throws Exception {
		
		logger.info("Assigning Role to user :" + user);
		
		Session session = VStackUtils.createSession(connection);
		String cmd = "export OS_PROJECT_ID=" + projectId + ";export OS_PROJECT_NAME=\"" + projectname
						+ "\";export OS_USER_DOMAIN_NAME=\"" + userdomain + "\";export OS_USERNAME=\"" + username
						+ "\";export OS_PASSWORD=\"" + password
						+ "\";export OS_IDENTITY_API_VERSION=3;openstack role add --project " + projectname + " --user " + user + " admin";
				
		logger.info("Assigning Role to user command :" + cmd);
		
		VStackUtils.runCommand(connection, cmd,	session);
				
			
		VStackUtils.deleteSession(session);
		return true;
	}

}