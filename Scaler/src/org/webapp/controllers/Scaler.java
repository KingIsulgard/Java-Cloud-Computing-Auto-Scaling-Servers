package org.webapp.controllers;
import java.io.IOException;
import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;
import org.webapp.gui.GUI;
import org.webapp.model.InstanceList;
import org.webapp.services.LoadBalancerStub;
import org.webapp.services.WorkerStub;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.model.Instance;

/*
 * Deze klasse dient voor het managen van de instances. Voor het aanmaken en terminaten van de workers en load balancer. 
 * Deze is gescheiden van de RunScaler klasse voor redundantie te vermijden en beter overzicht te behouden. Het beheren 
 * van de load balancer en een worker instance is namelijk redelijk lineair.
 */
public class Scaler {
	private String loadBalancerAddress;
	String endpoint;
	InstanceManager instanceManager;
	private GUI frame;
	
	public Scaler() throws IOException {
		endpoint = "ec2.eu-west-1.amazonaws.com";
		instanceManager = new InstanceManager(endpoint);
	}
	
	/*
	 * GUI Frame meegeven voor het kunnen schrijven naar de console
	 */
	public void setFrame(GUI frame) {
		this.frame = frame;
		instanceManager.frame = frame;
	}
	
	/*
	 * Aanmaken van de securitygroep
	 */
	public void createSecurityGroup() throws Exception {
		// Create security group
		instanceManager.createSecurityGroup();
	}
	
	/*
	 * Aanmaken van de key pair
	 */
	public void createKeyPair() throws Exception {
		// Create keypair
		instanceManager.createKeyPair();
	}
	
	/*
	 * Aanmaken van de load balancer
	 */
	public void createLoadBalancer() throws Exception {
		// Create loadbalancer
		frame.addConsole("Creating loadbalancer.");
		frame.setLoadBalancer("Starting up");
		Instance loadBalancer = instanceManager.createInstance("ami-1e6f8769", "Gilles Generated Load Balancer");
		
		frame.setLoadBalancer("Created");
		
		// Address of the loadbalancer
		loadBalancerAddress = loadBalancer.getPublicDnsName();
		
		frame.setLoadBalancer("Running, DNS: " + loadBalancerAddress);
		
		// Wait for 1 minute till tomcat has started
		try {
		    Thread.sleep(60000);
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
	}
	
	/*
	 * Opstarten van een worker.
	 */
	public Instance createWorker() throws Exception {
		// Create worker instance
		frame.addConsole("Creating worker.");
		Instance worker = instanceManager.createInstance("ami-ce6f87b9", "Gilles Generated Worker" + InstanceList.getNextInstanceId());
		
		// Wait for 1 minute till tomcat has started
		try {
		    Thread.sleep(90000);
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
		
		frame.addConsole("Sending address of load balancer to worker.");
		
		// Tell worker where loadbalancer is and what his own address is
		WorkerStub workerStub = new WorkerStub("http://" + worker.getPublicDnsName() + ":8080/Worker/services/Worker.WorkerHttpSoap11Endpoint/");
		
		// Creating the request
		org.webapp.services.WorkerStub.SetAddresses request = new org.webapp.services.WorkerStub.SetAddresses();
		request.setLoadBalancer(loadBalancerAddress);
		request.setMyAddress(worker.getPublicDnsName());
		
		// Invoking the service
		workerStub.setAddresses(request);
		
		frame.addConsole("Worker ready.");
		
		return worker;
	}
	
	/*
	 * De workload van een bepaalde instance opvragen.
	 */
	public double getInstanceWorkLoad(Instance instance) throws RemoteException {
		// Tell worker where loadbalancer is
		WorkerStub instanceStub = new WorkerStub("http://" + instance.getPublicDnsName() + ":8080/Worker/services/Worker.WorkerHttpSoap11Endpoint/");
		
		// Creating the request
		org.webapp.services.WorkerStub.GetWorkLoad request = new org.webapp.services.WorkerStub.GetWorkLoad();
		
		// Invoking the service
		org.webapp.services.WorkerStub.GetWorkLoadResponse response = instanceStub.getWorkLoad(request);
		return response.get_return();
	}
	
	/*
	 * De lengte van de queue in de load balancer opvragen.
	 */
	public int getQueueLength() throws RemoteException {
		// Tell worker where loadbalancer is
		LoadBalancerStub instanceStub = new LoadBalancerStub("http://" + loadBalancerAddress + ":8080/LoadBalancer/services/LoadBalancer.LoadBalancerHttpEndpoint/");
		
		// Creating the request
		org.webapp.services.LoadBalancerStub.QueueSize request = new org.webapp.services.LoadBalancerStub.QueueSize();
		
		// Invoking the service
		org.webapp.services.LoadBalancerStub.QueueSizeResponse response = instanceStub.queueSize(request);
		return response.get_return();
	}
	
	/*
	 * Afsluiten van een worker
	 */
	public void terminateWorker(Instance worker) throws AmazonServiceException, Exception {
		// Tell worker to send his current threads back to the load balancer
		WorkerStub workerStub = new WorkerStub("http://" + worker.getPublicDnsName() + ":8080/Worker/services/Worker.WorkerHttpSoap11Endpoint/");
		
		// Creating the request
		org.webapp.services.WorkerStub.DitchThreads request = new org.webapp.services.WorkerStub.DitchThreads();
		
		// Invoking the service
		org.webapp.services.WorkerStub.DitchThreadsResponse response = workerStub.ditchThreads(request);

		// Wait till finished
		boolean antw = response.get_return();
				
		// Terminate the instance
		instanceManager.terminateInstance(worker.getInstanceId());
	}
}
