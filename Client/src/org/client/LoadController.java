package org.client;

import java.util.*;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

/*
 * Deze klasse dient voor het berekenen van de work load op de lokale computer. Deze wordt momenteel niet meer 
 * gebruikt omdat alle taken vanaf nu in de cloud gedraaid worden en niets meer lokaal.
 */
public class LoadController implements Runnable {
	private ArrayList<Double> lastData;
	
	public LoadController() {
		this.lastData = new ArrayList<Double>();
	}
	
    public void run() {
	    try {
	    	while(true) {
	    		long millis = System.currentTimeMillis();
	    		
	    		// If more data than one minute, remove old data
	    		while(lastData.size() >= 60) {
	    			lastData.remove(0);
	    		}
	    		
    			// Calculate CPU usage
	    		Sigar sigar = new Sigar();
	    		double per = sigar.getCpuPerc().getCombined();
	    		
	    		// Add new CPU usage data
    			lastData.add(per);
	    		
	    		// Wait 1 second
				Thread.sleep(1000 - millis % 1000);
    		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public double getAverageUsage() {
    	System.out.println(lastData);
    	if(lastData.size() > 0) {
	    	double total = 0.0;
	    	
	    	for(double data : lastData) {
	    		total += data;
	    	}
	    	
	    	return total / lastData.size();
    	} else {
    		// Calculate CPU usage
    		Sigar sigar = new Sigar();
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