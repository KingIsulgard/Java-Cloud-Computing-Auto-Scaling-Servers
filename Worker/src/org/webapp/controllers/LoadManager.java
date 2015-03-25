package org.webapp.controllers;

import java.util.*;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.webapp.model.AddressData;
import org.webapp.model.Subscribing;
import org.webapp.model.ThreadSingleton;
import org.webapp.services.LoadBalancerStub;

/*
 * De loadmanager draait in een aparte thread en gaat elke 5 seconden de load van de worker berekenen en op 
 * deze manier een gemiddelde van de laatste minuut berekenen.
 */
public class LoadManager implements Runnable {
	private ArrayList<Double> lastData;
	private Sigar sigar;
	
	public LoadManager() {
		this.lastData = new ArrayList<Double>();
		this.sigar = new Sigar();
	}
	
    public void run() {
	    try {
	    	while(true) {
	    		long millis = System.currentTimeMillis();
	    		
	    		// If more data than one minute, remove old data
	    		while(lastData.size() >= 12) {
	    			lastData.remove(0);
	    		}
	    		
    			// Calculate CPU usage
	    		double per = sigar.getCpuPerc().getCombined();
	    		
	    		// Add new CPU usage data
    			lastData.add(per);
    			
    			System.out.println("Checking if I can subscribe with this load.");
    			
    			// Subscribe if average is below 0.4
    			if(getAverageUsage() < 0.4 && !Subscribing.isSubscribed() && AddressData.getLoadBalancer() != null && ThreadSingleton.getInstance().getThreads().size() < 3) {
					// Mark as already subscribed to avoid unnecessairy server calls
    				Subscribing.subscribe();
    				
    				System.out.println("Worker subscribed to load balancer for work");
    				
    				// Subscribe to workbalancer
    				LoadBalancerStub loadStub = new LoadBalancerStub("http://" + AddressData.getLoadBalancer() + ":8080/LoadBalancer/services/LoadBalancer.LoadBalancerHttpEndpoint/");
					
					// Creating the request
					org.webapp.services.LoadBalancerStub.SubscribeWorker request = new org.webapp.services.LoadBalancerStub.SubscribeWorker();
					request.setWorker(AddressData.getMyAddress());

					// Invoking the service
					loadStub.subscribeWorker(request);
    			}
	    		
	    		// Sleep for 5 seconds
                Thread.sleep(5 * 1000);
    		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /*
     * Met deze functie kan je de gemiddelde work load van de laatste minuut opvragen
     */
    public double getAverageUsage() {
    	if(lastData.size() > 0) {
	    	double total = 0.0;
	    	
	    	for(double data : lastData) {
	    		total += data;
	    	}
	    	
    		System.out.println("Current average load (last minute): " + total / lastData.size());
	    	
	    	return total / lastData.size();
    	} else {
    		// Calculate CPU usage
    		double per = 0;
			try {
				per = sigar.getCpuPerc().getCombined();
			} catch (SigarException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    		return per;
    	}
    }
}