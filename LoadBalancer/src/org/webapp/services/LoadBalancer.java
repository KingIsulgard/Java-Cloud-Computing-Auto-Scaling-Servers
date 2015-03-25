package org.webapp.services;

import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;
import org.webapp.controllers.*;
import org.webapp.model.*;

/*
 * LoadBalancer.java
 * Deze klasse is de webservice van de load balancer. Via deze webservice kunnen de clients en de workers communiceren met de load balancer voor 
 * het doorgeven van taken of aanvragen van taken, het opvragen van resultaten of het plaatsten er van. En ook opvragen hoeveel taken en workers 
 * er in de queues staan.
 */
public class LoadBalancer {
	/*
	 * Voor het opvragen van een resultaat, deze geeft false terug als het resultaat nog niet ter beschikking is.
	 */
	public String getResult(Integer key) {
		System.out.println("A client requested his result.");
		
		ResultList resultList = ResultManager.getInstance();
		return resultList.getResult(key);
	}
	
	/*
	 * Voor het plaatsen van een resultaat. Het resultaat wordt opgeslagen onder de 
	 * meegegeven key waaronder deze later kan opgevraagd worden door een client.
	 */
	public void putResult(Integer key, String result) {
		System.out.println("A worker added a result.");
		
		ResultList resultList = ResultManager.getInstance();
		resultList.addResult(key, result);
	}
	
	/*
	 * Als er reeds een worker in de WorkerQueue staat dan zal hij deze taak daar onmiddellijk naartoe sturen in het ander geval zal 
	 * hij deze taak toevoegen aan de queue. De functie geeft een integer terug. Deze integer is de key waarmee de client zijn resultaat 
	 * kan opvragen.
	 */
	public Integer putRunnable(String urlString, String objectString) {
		System.out.println("A client added a jar file to the queue.");
		
		try {
			// Create new jar file object
			Integer key = ResultManager.getResultCount();
			org.webapp.model.JarFile jarFile = new JarFile(urlString, objectString, key);

			// Check if there is a subscriber
			String worker = WorkerManager.getInstance().getNextWorker();

			// If there is a subscribed worker waiting, send it to him
			if(worker != null) {
				BalanceController balanceController = new BalanceController();
				balanceController.sendJarFileToWorker(jarFile, worker);
				
				System.out.println("The jar file has been sent to a subscribed worker.");

			} else { // Else add thread to queue
				QueueManager.getInstance().addThread(jarFile);
				
				System.out.println("The jar file has been added to the queue");
			}

			return key;
			
		} catch (AxisFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return -1;
	}
	
	/*
	 * Deze functie plaatst taken terug vanvoor in de queue. Dit zijn taken die al eens opgevraagd zijn geweest maar door een worker die 
	 * wordt afgesloten teruggeplaatst worden. Deze komen dus vooraan in de queue omdat ze normaal al aan het draaien moeten zijn.
	 */
	public void rePutJarFile(String urlString, String objectString, Integer resultKey) {
		JarFile jarFile = new JarFile(urlString, objectString, resultKey);
		
		ThreadQueue threadQueue = QueueManager.getInstance();
		threadQueue.reAddThread(jarFile);
	}
	
	/*
	 * Als er reeds taken in de ThreadQueue staan stuurt hij meteen een taak terug naar de worker. Anders voegt hij deze worker toe 
	 * aan de WorkerQueue.
	 */
	public void subscribeWorker(String worker) {
		try {
			// If there is a task waiting, send task to subscriber
			JarFile jarFile = QueueManager.getInstance().getNextThread();
			if(jarFile != null) {
				BalanceController balanceController = new BalanceController();
				balanceController.sendJarFileToWorker(jarFile, worker);
				
				System.out.println("The worker has been immediately assigned a jar file from the queue.");
				
			} else { // Else add worker to queue
				WorkerManager.getInstance().subscribeWorker(worker);
				
				System.out.println("The worker has been added to the subscribers queue. No more need to contact load balancer, load balancer will contact you for work.");
			}
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * Deze functie gaat een worker uit de WorkerQueue halen als hij daar in staat. Dit wordt 
	 * gebruikt als een workergeterminate wordt om te vermijden dat de load balancer later 
	 * een taak gaat proberen te sturen naar een instance die niet meer runt.
	 */
	public void unsubscribeWorker(String worker) {
		WorkerManager.getInstance().unsubscribeWorker(worker);
		
		System.out.println("The worker has been removed from the queue.");
	}
	
	/*
	 * Geeft de lengte van de ThreadQueue terug. (Het aantal taken in de queue).
	 */
	public int queueSize() {
		return QueueManager.getInstance().getSize();
	}
}
