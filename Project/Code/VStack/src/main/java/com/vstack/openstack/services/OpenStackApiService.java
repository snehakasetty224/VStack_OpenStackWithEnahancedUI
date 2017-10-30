package com.vstack.openstack.services;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.JSONException;

import com.vstack.services.IOpenStackAPIService;
import com.vstack.services.VStackUtils;

public class OpenStackApiService implements IOpenStackAPIService, OpenstackAPI {

	private String authToken = null;
	private String computeUrl,neutronURL = "";
	
	private static Logger logger = Logger.getLogger("OpenStackApiService");
	
	public OpenStackApiService(String server, String authToken) {
		// TODO Auto-generated constructor stub
		this.authToken = authToken;
		this.computeUrl = "http://" + server + NOVA_COMPUTE;
		this.neutronURL = "http://" + server + NEUTRON_NETWORK;
	}

	public String getInstance(String id) {
		// TODO Auto-generated method stub
		String json = "";
		try {
			String url =  computeUrl + "/servers/" + id;
			
			logger.info("Get instance REST Url :" + url);
			
			json = VStackUtils.executeHttpGetRequest(authToken, url);
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}
	
	@Override
	public String getInstanceList() {
		// TODO Auto-generated method stub
		String json = "";
		try {
			String url =  computeUrl + "/servers";
			logger.info("Get instanceList REST Url :" + url);
			
			json = VStackUtils.executeHttpGetRequest(authToken, url);
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}

	@Override
	public String getFlavorList() {
		// TODO Auto-generated method stub
		String json = "";
		try {
			String url = computeUrl + "/flavors";
			logger.info("Get flavors REST Url :" + url);
			
			json = VStackUtils.executeHttpGetRequest(authToken, url);
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}

	@Override
	public String getImageList() {
		// TODO Auto-generated method stub
		String json = "";
		try {
			String url = computeUrl + "/images";
			logger.info("Get images REST Url :" + url);
			
			json = VStackUtils.executeHttpGetRequest(authToken, url);
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}

	// The API URL is the same with get instance list,
	// however for create new instance it's post request
	@Override
	public String launchInstance(String jsonInput) {
		// TODO Auto-generated method stub
		String json = "";
		try {
			String url = computeUrl + "/servers";
			logger.info("Launch compute POST REST Url :" + url);
			logger.info("Launch compute Request body :" + jsonInput);
			
			json = VStackUtils.executeHttpPostRequest(authToken, url, jsonInput);
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}

	// Pauses a server. Changes its status to PAUSED.
	// Specify the pause action in the request body.
	// Policy defaults enable only users with the administrative role
	// or the owner of the server to perform this operation. Cloud providers can
	// change these permissions through the policy.json file.
	@Override
	public void pauseInstance(String server_id) {
		// TODO Auto-generated method stub
		String url = computeUrl + "/servers/" + server_id + "/action";
		String body = "{\"pause\": null}";
		try {
			VStackUtils.executeHttpPostRequest(authToken, url, body);
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void startInstance(String server_id) {
		// TODO Auto-generated method stub
		String url = computeUrl + "/servers/" + server_id + "/action";
		String body = "{\"os-start\": null}";
		try {
			logger.info("Start Instance POST REST Url :" + url);
			logger.info("Start Instance Request body :" + body);
			
			VStackUtils.executeHttpPostRequest(authToken, url, body);
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void stopInstance(String server_id) {
		// TODO Auto-generated method stub
		String url = computeUrl + "/servers/" + server_id + "/action";
		String body = "{\"os-stop\": null}";
		logger.info("Stop Instance POST REST Url :" + url);
		logger.info("Stop Instance Request body :" + body);
		
		try {
			VStackUtils.executeHttpPostRequest(authToken, url, body);
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void resumeInstance(String server_id) {

		String url = computeUrl + "/servers/" + server_id + "/action";
		String body = "{\"resume\": null}";
		try {
			VStackUtils.executeHttpPostRequest(authToken, url, body);
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void deleteInstance(String server_id) {
		// TODO Auto-generated method stub
		String url = computeUrl + "/servers/" + server_id + "/action";
		String body = "{\"forceDelete\": null}";
		
		logger.info("Delete Instance POST REST Url :" + url);
		logger.info("Delete Instance Request body :" + body);
		
		try {
			VStackUtils.executeHttpPostRequest(authToken, url, body);
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public String getNetworkList() {
		// TODO Auto-generated method stub
		String json = "";
		try {
			String url = neutronURL + "/networks";
			logger.info("Get Networks REST Url :" + url);
			
			json = VStackUtils.executeHttpGetRequest(authToken, url);
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}

}
