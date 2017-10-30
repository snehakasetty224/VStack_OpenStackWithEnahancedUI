package com.vstack.beans;

public class InstanceUsage {
	 //"instance_id": "0687e799-792b-40b4-b5eb-dfcd9164623c",
	private double uptime;
	private String started_at; //": "2016-11-23T07:32:44.000000",
	private String ended_at;   //": "2016-11-29T05:02:24.000000",
	private String memory_mb;  //": 512,
	private String state;      //": "terminated",
	private String vcpus;	   //: 1,
	private String flavor;		//": "m1.tiny",
	private String local_gb;	//": 1,
	private String name; 		//": "instance1"
	

	public double getUptime() {
		return uptime;
	}
	public void setUptime(double uptime) {
		this.uptime = uptime;
	}
	public String getStarted_at() {
		return started_at;
	}
	public void setStarted_at(String started_at) {
		this.started_at = started_at;
	}
	public String getEnded_at() {
		return ended_at;
	}
	public void setEnded_at(String ended_at) {
		this.ended_at = ended_at;
	}
	public String getMemory_mb() {
		return memory_mb;
	}
	public void setMemory_mb(String memory_mb) {
		this.memory_mb = memory_mb;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getVcpus() {
		return vcpus;
	}
	public void setVcpus(String vcpus) {
		this.vcpus = vcpus;
	}
	public String getFlavor() {
		return flavor;
	}
	public void setFlavor(String flavor) {
		this.flavor = flavor;
	}
	public String getLocal_gb() {
		return local_gb;
	}
	public void setLocal_gb(String local_gb) {
		this.local_gb = local_gb;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
