package org.webapp.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.webapp.gui.GUI;
import org.webapp.model.InstanceList;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairResult;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusRequest;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusResult;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStatus;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.KeyPair;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesResult;

/*
 * Deze klasse dient voor het opstarten en terminaten van EC2 instances. 
 * Ook kunnen er keychains en securitygroups mee aangemaakt worden.
 */
public class InstanceManager {
	private AmazonEC2Client amazonEC2Client;
	public GUI frame;
	
	public InstanceManager(String endpoint) throws IOException {
		// Create and initialize AWS credentials
		AWSCredentials credentials = 
				new PropertiesCredentials(
						AwsConsoleApp.class.getResourceAsStream("AwsCredentials.properties"));

		// Create new client using credentials
		amazonEC2Client = new AmazonEC2Client(credentials);

		// Set endpoint
		amazonEC2Client.setEndpoint(endpoint);
	}
	
	/*
	 * Aanmaken van een securitygroep met al de rechten die nodig zijn om met de instances te kunnen werken.
	 */
	public void createSecurityGroup() throws Exception {
		// Create security group
		CreateSecurityGroupRequest createSecurityGroupRequest = new CreateSecurityGroupRequest();

		createSecurityGroupRequest.withGroupName("GillesGeneratedSecurityGroup")
			.withDescription("Dynamisch aangemaakt security group");

		// Authorize secutiy group
		IpPermission sshPermission = new IpPermission();

		sshPermission.withIpRanges("0.0.0.0/0")
		            .withIpProtocol("tcp")
		            .withFromPort(22)
		            .withToPort(22);

		IpPermission httpPermission = new IpPermission();

		httpPermission.withIpRanges("0.0.0.0/0")
		            .withIpProtocol("tcp")
		            .withFromPort(80)
		            .withToPort(80);

		IpPermission httpsPermission = new IpPermission();

		httpsPermission.withIpRanges("0.0.0.0/0")
		            .withIpProtocol("tcp")
		            .withFromPort(443)
		            .withToPort(443);

		IpPermission httpePermission = new IpPermission();

		httpePermission.withIpRanges("0.0.0.0/0")
		            .withIpProtocol("tcp")
		            .withFromPort(8080)
		            .withToPort(8080);

		AuthorizeSecurityGroupIngressRequest authorizeSecurityGroupIngressRequest =
				new AuthorizeSecurityGroupIngressRequest();

		authorizeSecurityGroupIngressRequest.withGroupName("GillesGeneratedSecurityGroup")
		                                    .withIpPermissions(sshPermission, httpPermission, httpsPermission, httpePermission);

		amazonEC2Client.authorizeSecurityGroupIngress(authorizeSecurityGroupIngressRequest);
	}
	
	/*
	 * Aanmaken van een keypair voor authenticatie.
	 */
	public String createKeyPair() throws Exception {
		// Create keypair
		CreateKeyPairRequest createKeyPairRequest = new CreateKeyPairRequest();	
		createKeyPairRequest.withKeyName("GillesGeneratedKeyPair");

		CreateKeyPairResult createKeyPairResult = 
				amazonEC2Client.createKeyPair(createKeyPairRequest);

		KeyPair keyPair = new KeyPair();
		keyPair = createKeyPairResult.getKeyPair();	
		
		return keyPair.getKeyMaterial();
	}
	
	/*
	 * Opstarten van een EC2 instance met de naam instanceName en gebaseerd op het image met id imageId
	 */
	public Instance createInstance(String imageId, String instanceName) throws Exception {
		// Run the instance
		RunInstancesRequest runInstancesRequest = 
				new RunInstancesRequest();

		runInstancesRequest.withImageId(imageId)
		.withInstanceType("t1.micro")
		.withMinCount(1)
		.withMaxCount(1)
		.withKeyName("GillesGeneratedKeyPair")
		.withSecurityGroups("GillesNetCentric");
		RunInstancesResult runInstancesResult = 
				amazonEC2Client.runInstances(runInstancesRequest);

		// Get the instance
		Instance instance = runInstancesResult.getReservation().getInstances().get(0);

		frame.addConsole("Creating a new instance with image " + imageId);
		
		// Wait of the instance to be started up (why? you can only get public dns of running instance)
		DescribeInstanceStatusRequest describeInstanceRequest = new DescribeInstanceStatusRequest().withInstanceIds(instance.getInstanceId());
		DescribeInstanceStatusResult describeInstanceResult = amazonEC2Client.describeInstanceStatus(describeInstanceRequest);
		List<InstanceStatus> state = describeInstanceResult.getInstanceStatuses();
		while (state.size() < 1) {
			// Wait for 10 second
			try {
			    Thread.sleep(10000);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
						
			// Do nothing, just wait, have thread sleep if needed
			describeInstanceResult = amazonEC2Client.describeInstanceStatus(describeInstanceRequest);
			state = describeInstanceResult.getInstanceStatuses();
		}

		// Write status
		String status = state.get(0).getInstanceState().getName();
		frame.addConsole("State of instance is now '" + status + "'");

		// Find newly created instance public DNS
		String instanceId = null;
		DescribeInstancesResult result = amazonEC2Client.describeInstances();
		
		// Loop through all instances and check which instance matches the one we are looking for
		DescribeInstancesResult r = amazonEC2Client.describeInstances();
		Iterator<Reservation> ir= r.getReservations().iterator();
		while(ir.hasNext()){
			Reservation reservations = ir.next();
			List<Instance> instances = reservations.getInstances();
			
			for(Instance tempInstance : instances) {
				if(instance.getInstanceId().equals(tempInstance.getInstanceId())) {
					frame.addConsole("Instance has public DNS: " + tempInstance.getPublicDnsName());
					instance = tempInstance;
				}
			}
		}
		
		// Create tags
		LinkedList tags = new LinkedList<Tag>();
		tags.add(new Tag("Name", instanceName));
		tags.add(new Tag("Owner", "Gilles Lesire"));
		
		// Name the instance
		CreateTagsRequest createTagsRequest = new CreateTagsRequest();
		createTagsRequest.withResources(instance.getInstanceId()).withTags(tags);
		amazonEC2Client.createTags(createTagsRequest);
		
		return instance;
	}
	
	/*
	 * Beëindigen van de instance met id instanceId.
	 */
	public void terminateInstance(String instanceId) throws AmazonServiceException,Exception {
    	TerminateInstancesRequest rq = new TerminateInstancesRequest();
    	rq.getInstanceIds().add(instanceId);
    	// the method returns when you move from "your previous state" to terminating and not when the machine is actually terminated. 
    	//You have the same problems if you use asynchronous call too
    	TerminateInstancesResult rsp = amazonEC2Client.terminateInstances(rq);
    	
    	frame.addConsole("Instance with ID " + instanceId + " terminated: "+ rsp.toString());
    }
}