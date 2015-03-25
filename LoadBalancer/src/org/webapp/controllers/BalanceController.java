package org.webapp.controllers;

import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;
import org.webapp.services.WorkerStub;

/*
 * Deze classe dient voor het versturen van een taken naar workers
 */
public class BalanceController {
	public BalanceController() {
		
	}
	
	/*
	 * Versturen van een taak JarFile naar een worker met DNS adres workerDNS
	 */
	public void sendJarFileToWorker(org.webapp.model.JarFile jarFile, String workerDNS) throws RemoteException {
		// Send jarfile to worker
		WorkerStub instanceStub;

		instanceStub = new WorkerStub("http://" + workerDNS + ":8080/Worker/services/Worker.WorkerHttpSoap11Endpoint/");

		// Creating the request
		org.webapp.services.WorkerStub.ExecuteJar request = new org.webapp.services.WorkerStub.ExecuteJar();
		request.setObjectString(jarFile.getObjectString());
		request.setUrlString(jarFile.getUrlString());
		request.setResultKey(jarFile.getResultKey());

		// Invoking the service
		org.webapp.services.WorkerStub.ExecuteJarResponse response;
		response = instanceStub.executeJar(request);
		System.out.println(response.get_return());
	}
}
