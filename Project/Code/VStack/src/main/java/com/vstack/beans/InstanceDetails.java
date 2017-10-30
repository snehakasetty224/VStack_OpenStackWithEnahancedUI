package com.vstack.beans;

import java.util.HashMap;
import java.util.Map;

public class InstanceDetails {
	private String instanceName;
	private String flavor;
	private String image;
	private String status;
	private String updated_date;
	private String hostId;
	private Map<String, String> addresses = new HashMap<String, String>();
	private String security_groups;
	private String created_date;
	public String getInstanceName() {
		return instanceName;
	}
	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}
	public String getFlavor() {
		return flavor;
	}
	public void setFlavor(String flavor) {
		this.flavor = flavor;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUpdated_date() {
		return updated_date;
	}
	public void setUpdated_date(String updated_date) {
		this.updated_date = updated_date;
	}
	public String getHostId() {
		return hostId;
	}
	public void setHostId(String hostId) {
		this.hostId = hostId;
	}
	public Map<String, String> getAddresses() {
		return addresses;
	}
	public void setAddresses(Map<String, String> addresses) {
		this.addresses = addresses;
	}
	public String getSecurity_groups() {
		return security_groups;
	}
	public void setSecurity_groups(String security_groups) {
		this.security_groups = security_groups;
	}
	public String getCreated_date() {
		return created_date;
	}
	public void setCreated_date(String created_date) {
		this.created_date = created_date;
	}
	
	

}
