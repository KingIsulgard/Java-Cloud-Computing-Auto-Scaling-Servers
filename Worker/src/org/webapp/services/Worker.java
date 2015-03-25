package org.webapp.services;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.rmi.RemoteException;
import java.util.concurrent.*;

import org.apache.axis2.AxisFault;
import org.apache.commons.codec.DecoderException;
import org.webapp.controllers.*;
import org.webapp.model.*;

/*
 * Dit is de klasse die services bevat om te communiceren met de worker.
 */
public class Worker {
	public void setAddresses(String loadBalancer, String myAddress) {
		System.out.println("Setting loadbalancer: " + loadBalancer);
		System.out.println("Setting own address: " + myAddress);

		AddressData.setLoadBalancer(loadBalancer);
		AddressData.setMyAddress(myAddress);
	}
	
	/*
	 * Geeft de huidige werklast voor de werker terug.
	 */
	public double getWorkLoad() {
		return LoadSingleton.getInstance().getAverageUsage();
	}
	
	/*
	 * Deze zal er voor zorgen dat de werker zijn draaiende taken stopzet, terugstuurt naar de load balancer en zijn threadlist leegmaakt.
	 * Deze functie wordt uitgevoerd vlak voordat de worker wordt afgezet.
	 */
	public boolean ditchThreads() {
		try {
			System.out.println("Send all jar files back to load balancer");
			
			// Avoid new thread from coming in
			Subscribing.subscribe();
			
			// Unsubscribe to workbalancer
			LoadBalancerStub loadStub = new LoadBalancerStub("http://" + AddressData.getLoadBalancer() + ":8080/LoadBalancer/services/LoadBalancer.LoadBalancerHttpEndpoint/");
			
			// Creating the request
			org.webapp.services.LoadBalancerStub.UnsubscribeWorker request = new org.webapp.services.LoadBalancerStub.UnsubscribeWorker();
			request.setWorker(AddressData.getMyAddress());

			// Invoking the service
			loadStub.unsubscribeWorker(request);
			
			if(ThreadSingleton.getInstance().getThreads().size() > 0) {
				for(JarFile jarFile : ThreadSingleton.getInstance().getThreads()) {
					// Creating the request
					org.webapp.services.LoadBalancerStub.RePutJarFile reputrequest = new org.webapp.services.LoadBalancerStub.RePutJarFile();
					reputrequest.setObjectString(jarFile.getObjectString());
					reputrequest.setUrlString(jarFile.getUrlString());
					reputrequest.setResultKey(jarFile.getResultKey());

					// Invoking the service
					loadStub.rePutJarFile(reputrequest);
					
					// Remove jar file
					ThreadSingleton.getInstance().removeThread(jarFile);
				}
			}
			
		} catch (AxisFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}
	
	/*
	 * Voer een taak uit met de volgende informative, url van de jarfile en de geserializeerde objectstring. 
	 * Alsook de key die gebruikt moet worden voor het resultaat terug te geven aan de load balancer.
	 */
	public String executeJar(String urlString, String objectString, Integer resultKey) {
		System.out.println("Received new jar file.");
		
		JarFile jarFile = new JarFile(urlString, objectString, resultKey);
		
		// Unsubscribed
		Subscribing.unsubscribe();
		
		System.out.println("Clear subscribed.");
		
		// Add to current list
		ThreadSingleton.getInstance().addThread(jarFile);
		
		System.out.println("Add thread to current running threads list");
		
		// Run task in seperate thread
		RunThread thread = new RunThread(jarFile);
        new Thread(thread).start();

		return "Deploying thread on server.";
	}
}
