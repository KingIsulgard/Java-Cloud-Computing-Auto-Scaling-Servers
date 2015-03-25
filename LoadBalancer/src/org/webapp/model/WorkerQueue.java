package org.webapp.model;

import java.util.LinkedList;
import java.util.Queue;

/*
 * Deze klasse bevat een queue met alle DNS addressen van de workers die zich hebben ingeschreven voor het ontvangen van taken.
 */
public class WorkerQueue {
	private Queue<String> workers;
	
	public WorkerQueue() {
		workers = new LinkedList<String>();
	}
	
	public void subscribeWorker(String publicDNS) {
		Queue<String> tempQueue = new LinkedList<String>();
		boolean inQueue = false;
		
		while(workers.size() > 0) {
			String worker = workers.poll();
			
			if(worker.equals(publicDNS)) {
				inQueue = true;
			}
			
			tempQueue.add(worker);
		}
		
		workers = tempQueue;
		
		if(!inQueue) {
			workers.add(publicDNS);
		}
	}
	
	public void unsubscribeWorker(String publicDNS) {
		Queue<String> tempQueue = new LinkedList<String>();
		boolean inQueue = false;
		
		while(workers.size() > 0) {
			String worker = workers.poll();
			
			if(!worker.equals(publicDNS)) {
				tempQueue.add(worker);
			}
		}
		
		workers = tempQueue;
	}
	
	public String getNextWorker() {
		return workers.poll();
	}
}