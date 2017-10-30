package com.vstack.openstack.services;

public interface OpenstackAPI {

	public String API_VERSION2 = "v2";
	public String API_VERSION3 = "v3";
	
	public String projectName = "admin";
	public String domainName = "default";
	public String defaultPassword = "VStack123"; //Create user adds this password by default
	public String controllerUser = "osbash";
	public String controllerPassword = "osbash";
	
	//GET APIs
	public String AUTH_API =  "/auth/tokens";
	public String GET_PROJECT_API =  "/projects";
	public String GET_DOMAIN_API =  "/domains";
	public String GET_FLAVORS = "/flavors";
	public String GET_IMAGES = "/images";
	public String USERS ="/users";
	//POST APIs
	public String CREATE_PROJECT_API ="";
	public String CREATE_FLAVOR = "flavor";
	
	public static final String KEYSTONE_IDENTITY = ":5000/v3";
	public static final String NOVA_COMPUTE = ":8774/v2.1";
	public static final String GLANCE_IMAGE = ":9292/v3";
	public static final String NEUTRON_NETWORK = ":9696/v2.0";

}
