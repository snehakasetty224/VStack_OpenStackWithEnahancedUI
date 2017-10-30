package com.vstack.controller;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vstack.beans.ComputeInstance;
import com.vstack.beans.InstanceDetails;
import com.vstack.beans.InstanceUsage;
import com.vstack.beans.OpenstackConnection;
import com.vstack.openstack.services.AuthService;
import com.vstack.openstack.services.FlavorService;
import com.vstack.openstack.services.InstanceService;
import com.vstack.openstack.services.InstanceUsageService;
import com.vstack.openstack.services.OpenStackApiService;
import com.vstack.openstack.services.OpenstackAPI;
import com.vstack.services.Bootstrap;
import com.vstack.services.VStackUtils;

@Controller
public class ComputeController implements OpenstackAPI {

	@Autowired
	ServletContext context;

	@Autowired
	Bootstrap bootstrap;

	@Autowired
	AuthService authentication;

	@Autowired
	OpenstackConnection connection;
	
	private static Logger logger = Logger.getLogger("ComputeController");

	/**
	 * Get Flavors
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/getFlavor", method = RequestMethod.GET)
	public @ResponseBody String getFlavor(HttpServletResponse response) {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		logger.info("\n--------- Getting Flavor Data ---------");

		try {
			
			FlavorService flavorAPI = new FlavorService();
			Map<String, String> flavorList = flavorAPI.getFlavors(connection.getServer(), authentication.getAuthToken());
			
			return ow.writeValueAsString(flavorList);

		} catch (Exception ex) {
			logger.fatal(ex.getMessage());
			logger.fatal(VStackUtils.returnExceptionTrace(ex));
			return null;
		}
	}
	
	/**
	 * Get Instances
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/getInstances", method = RequestMethod.GET)
	public @ResponseBody String getInstances(HttpServletResponse response) {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		logger.info("\n--------- Getting Instance List ---------");

		try {
			
			InstanceService instanceService = new InstanceService(connection, authentication.getAuthToken());
			Map<String, String> instanceList = instanceService.getInstances();
			
			return ow.writeValueAsString(instanceList);

		} catch (Exception ex) {
			logger.fatal(ex.getMessage());
			logger.fatal(VStackUtils.returnExceptionTrace(ex));
			return null;
		}
	}
	
	/**
	 * Get Instances
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/getNetworks", method = RequestMethod.GET)
	public @ResponseBody String getNetworks(HttpServletResponse response) {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		logger.info("\n--------- Getting Network List ---------");

		try {
			
			InstanceService instanceService = new InstanceService(connection, authentication.getAuthToken());
			Map<String, String> nwlist = instanceService.getNetworks();
			
			return ow.writeValueAsString(nwlist);

		} catch (Exception ex) {
			logger.fatal(ex.getMessage());
			logger.fatal(VStackUtils.returnExceptionTrace(ex));
			return null;
		}
	}
	
	/**
	 * Get Instance
	 * @param instance
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/getInstance", method = RequestMethod.POST)
	public void getInstance(@RequestBody String instance, HttpServletResponse response) throws IOException {
		
		if (connection != null) {
			try {

				logger.info("------ Get Instance ------");
				InstanceService instanceService = new InstanceService(connection, authentication.getAuthToken());
				InstanceDetails details = instanceService.getInstanceByName(instance);

				ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
				

				VStackUtils.handleResponse(response, ow.writeValueAsString(details));
			}  catch (Exception ex) {
				logger.fatal(ex.getMessage());
				logger.fatal(VStackUtils.returnExceptionTrace(ex));
				VStackUtils.handleRuntimeException(ex, response, VStackUtils.returnExceptionTrace(ex));
			}
		} else {
			VStackUtils.handleRuntimeError(response, "Sorry, an error has occurred. Connection not added.");
		}
	}
	
	 /* Get Images
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/getImages", method = RequestMethod.GET)
	public @ResponseBody String getImages(HttpServletResponse response) {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		logger.info("\n--------- Getting Image Data ---------");

		try {
			
			InstanceService imageService = new InstanceService(connection, authentication.getAuthToken());
			Map<String, String> imageList = imageService.getImages();
			
			return ow.writeValueAsString(imageList);

		} catch (Exception ex) {
			logger.fatal(ex.getMessage());
			logger.fatal(VStackUtils.returnExceptionTrace(ex));
			
			return null;
		}
	}

	/**
	 * Launch Instance
	 * @param instance
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/launchInstance", method = RequestMethod.POST)
	public void launchInstance(@RequestBody ComputeInstance instance, HttpServletResponse response) throws IOException {
		
		if (connection != null) {
			try {

				logger.info("------ Launch Instance ------");
				InstanceService instanceService = new InstanceService(connection, authentication.getAuthToken());
				
				AuthService service = new AuthService();
				Map<String, String> projects = service.getOpenstackProjects(connection, authentication.getAuthToken());
				
				Map<String, String> instanceId = instanceService.launchInstance(instance, projects.get("admin"));
				InstanceDetails instanceDetail = instanceService.getInstanceByName(instance.getInstanceName());
				
				VStackUtils.handleResponse(response, "Instance Created. Status :" + instanceDetail.getStatus());
			}  catch (Exception ex) {
				logger.fatal(ex.getMessage());
				logger.fatal(VStackUtils.returnExceptionTrace(ex));
				VStackUtils.handleResponse(response, "Failed to create instance " + instance.getInstanceName());
			}
		} else {
			VStackUtils.handleRuntimeError(response, "Sorry, an error has occurred. Connection not added.");
		}
	}

	/**
	 * Stop Instance
	 * @param instance
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/stopInstance", method = RequestMethod.POST)
	public void stopInstance(@RequestBody String instance, HttpServletResponse response) throws IOException {
		
		if (connection != null) {
			try {

				logger.info("------ stop Instance ------");
				InstanceService instanceService = new InstanceService(connection, authentication.getAuthToken());
				 Map<String, String> instances= instanceService.getInstances();
				
				OpenStackApiService apiService = new OpenStackApiService(connection.getServer(), authentication.getAuthToken());
				apiService.stopInstance(instances.get(instance));
				
				VStackUtils.handleResponse(response, instance + " Instance Stopped.");
			} catch (Exception ex) {
				logger.fatal(ex.getMessage());
				logger.fatal(VStackUtils.returnExceptionTrace(ex));
				VStackUtils.handleRuntimeException(ex, response, "Failed to stop instance. ");
			}
		} else {
			VStackUtils.handleRuntimeError(response, "Sorry, an error has occurred. Connection not added.");
		}
	}
	
	/**
	 * Resume Instance
	 * @param instance
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/resumeInstance", method = RequestMethod.POST)
	public void resumeInstance(@RequestBody String instance, HttpServletResponse response) throws IOException {
		
		if (connection != null) {
			try {

				logger.info("------ Resume Instance ------");
				InstanceService instanceService = new InstanceService(connection, authentication.getAuthToken());
				 Map<String, String> instances= instanceService.getInstances();
				
				OpenStackApiService apiService = new OpenStackApiService(connection.getServer(), authentication.getAuthToken());
				apiService.startInstance(instances.get(instance));
				
				VStackUtils.handleResponse(response, "Instance Started.");
			} catch (Exception ex) {
				logger.fatal(ex.getMessage());
				logger.fatal(VStackUtils.returnExceptionTrace(ex));
				VStackUtils.handleRuntimeException(ex, response, "Failed to start instance " + instance);
			}
		} else {
			VStackUtils.handleRuntimeError(response, "Sorry, an error has occurred. Connection not added.");
		}
	}
	/**
	 * Delete Instance
	 * @param instance
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/deleteInstance", method = RequestMethod.POST)
	public void deleteInstance(@RequestBody String instance, HttpServletResponse response) throws IOException {
		
		if (connection != null) {
			try {

				logger.info("------ Delete Instance ------");
				InstanceService instanceService = new InstanceService(connection, authentication.getAuthToken());
				 Map<String, String> instances= instanceService.getInstances();
				
				OpenStackApiService apiService = new OpenStackApiService(connection.getServer(), authentication.getAuthToken());
				 apiService.deleteInstance(instances.get(instance));
				
				VStackUtils.handleResponse(response, "Instance Deleted.");
			} catch (Exception ex) {
				logger.fatal(ex.getMessage());
				logger.fatal(VStackUtils.returnExceptionTrace(ex));
				VStackUtils.handleResponse(response, instance + " instance deleted successfully.");
			}
		} else {
			VStackUtils.handleRuntimeError(response, "Sorry, an error has occurred. Connection not added.");
		}
	}
	
	/**
	 * Get Projects
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/getInstanceUsage", method = RequestMethod.GET)
	public @ResponseBody String getInstanceUsage(HttpServletResponse response) {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		logger.info("\n--------- Getting Instance Usage Data ---------");

		try {
			
			InstanceUsageService usageService = new InstanceUsageService(connection.getServer(), authentication.getAuthToken());
			List<InstanceUsage> usage = usageService.getInstanceUsageByProject(connection);
			return ow.writeValueAsString(usage);

		} catch (Exception ex) {
			logger.fatal(ex.getMessage());
			logger.fatal(VStackUtils.returnExceptionTrace(ex));
			return null;
		}
	}
	
	
	/**
	 * Get Projects
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/getProject", method = RequestMethod.GET)
	public @ResponseBody String getProject(HttpServletResponse response) {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		logger.info("\n--------- Getting Project Data ---------");

		try {
			
			AuthService service = new AuthService();
			Map<String, String> projects = service.getOpenstackProjects(connection, authentication.getAuthToken());
			return ow.writeValueAsString(projects);

		} catch (Exception ex) {
			logger.fatal(ex.getMessage());
			logger.fatal(VStackUtils.returnExceptionTrace(ex));
			return null;
		}
	}
	
}
