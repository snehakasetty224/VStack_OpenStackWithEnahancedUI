package com.vstack.openstack.services;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vstack.beans.InstanceUsage;
import com.vstack.beans.OpenstackConnection;
import com.vstack.services.VStackException;
import com.vstack.services.VStackUtils;

public class InstanceUsageService implements OpenstackAPI{
	
	private static Logger logger = Logger.getLogger("InstanceUsageService");
	private String authToken = null;
	private String computeUrl = "";
	
	public InstanceUsageService(String server, String authToken) {
		// TODO Auto-generated constructor stub
		this.authToken = authToken;
		this.computeUrl = "http://" + server + NOVA_COMPUTE;
	}
	/**
	 * Get Flavors
	 * 
	 * @throws Exception
	 */
	public List<InstanceUsage> getInstanceUsageByProject(OpenstackConnection Connection) throws  VStackException {
		List<InstanceUsage> insanceUsageList = new ArrayList<InstanceUsage>();
		 
		AuthService service = new AuthService();
		Map<String, String> projUrl = service.getOpenstackProjects(Connection, authToken);
		try { 
			String response = getInstanceUsage(projUrl.get("admin"));
			
			JSONObject jsonObj = new JSONObject(response);
			JSONObject jsonObj1 = jsonObj.getJSONObject("tenant_usage");
			JSONArray jsonToken = jsonObj1.getJSONArray("server_usages");
			int i = 0;
			
			while (i < jsonToken.length()) {
				InstanceUsage instanceUsage = new InstanceUsage();
				String name = jsonToken.getJSONObject(i).getString("name");
				instanceUsage.setName(name);
				
				String local_gb = jsonToken.getJSONObject(i).getString("local_gb");
				instanceUsage.setLocal_gb(local_gb);
				
				String flavor = jsonToken.getJSONObject(i).getString("flavor");
				instanceUsage.setFlavor(flavor);
				
				String vcpus = jsonToken.getJSONObject(i).getString("vcpus");
				instanceUsage.setVcpus(vcpus);
				
				String state = jsonToken.getJSONObject(i).getString("state");
				instanceUsage.setState(state);
				
				String memory_mb = jsonToken.getJSONObject(i).getString("memory_mb");
				instanceUsage.setMemory_mb(memory_mb);
				
				String started_at = jsonToken.getJSONObject(i).getString("started_at");
				instanceUsage.setStarted_at(started_at);
				
				String ended_at = jsonToken.getJSONObject(i).getString("ended_at");
				instanceUsage.setEnded_at(ended_at);
				
				String uptime = jsonToken.getJSONObject(i).getString("uptime");
				//instanceUsage.setUptime(uptime);
				double dbtAmt = Double.parseDouble(uptime);

				double bill = calculateUsage(state, dbtAmt, flavor, vcpus, memory_mb);
				instanceUsage.setUptime(bill);
				
				insanceUsageList.add(instanceUsage);
				i++;
			}
		} catch (Exception ex) {
			logger.fatal(ex.getMessage());
			VStackUtils.returnExceptionTrace(ex);
			throw new VStackException(ex);
		}
		return insanceUsageList;
	}
	
		
	private double calculateUsage(String state, double uptime, String flavor, String vcpus, String mem) { 
		double val = 0.0;
		double flavoramt = 0.10;
		double memamt = 0.10;
		double cpuamt = 0.10;
		
		if(state.equals("active")) {
			if(flavor.equals("m1.tiny")){
				//free
			} if(flavor.equals("Pan Pizza")){
				flavoramt = flavoramt + 0.10;
			} if(flavor.equals("Medium Pizza")){
				flavoramt = flavoramt + 0.50;
			}
			
			if(vcpus.equals("1")){
				//free
			} if(vcpus.equals("2")){
				cpuamt = cpuamt + 0.10;
			} if(vcpus.equals("3")){
				cpuamt = cpuamt + 0.50;
			}
			
			if(mem.equals("1024")){
				//free
			} if(mem.equals("2048")){
				memamt = memamt + 0.50;
			} 
		}
		val = uptime/3600 + memamt + cpuamt + flavoramt;
		DecimalFormat df = new DecimalFormat("#.##");      
		val = Double.valueOf(df.format(val));
		return val;
	}
	public String getInstanceUsage(String projectUri) {
		 
		String json = "";
		try {
			String url = computeUrl + "/os-simple-tenant-usage/" + projectUri;
			logger.info("Get all instance usage REST Url :" + url);
			
			json = VStackUtils.executeHttpGetRequest(authToken, url);
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}
}
