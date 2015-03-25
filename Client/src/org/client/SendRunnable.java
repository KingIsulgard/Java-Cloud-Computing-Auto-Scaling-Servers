package org.client;

import java.io.IOException;
import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;
import org.webapp.services.LoadBalancerStub;

/*
 * Aparte threadklasse voor het versturen van callables naar de load balancer.
 */
public class SendRunnable implements Runnable {
	private String address = null;
	private SimpleRunnable simpleRunnable = null;

	public SendRunnable(SimpleRunnable simpleRunnable, String address) {
		this.simpleRunnable = simpleRunnable;
		this.address = address;
	}

	public void run() {
		try {
			// Subscribe to workbalancer
			LoadBalancerStub loadStub = new LoadBalancerStub("http://" + this.address + ":8080/LoadBalancer/services/LoadBalancer.LoadBalancerHttpEndpoint/");

			// Creating the request
			org.webapp.services.LoadBalancerStub.PutRunnable request = new org.webapp.services.LoadBalancerStub.PutRunnable();
			request.setUrlString(new String("http://businessgame.be/netcentric/classes.jar"));
			request.setObjectString(SerializeController.serialize(this.simpleRunnable));

			// Invoking the service
			org.webapp.services.LoadBalancerStub.PutRunnableResponse response = loadStub.putRunnable(request);
			int key = response.get_return();

			System.out.println("Waiting for result with key " + key);

			String result = null;

			while(result == null) {
				System.out.println("Polling for result with key " + key);
				
				// Creating the request
				org.webapp.services.LoadBalancerStub.GetResult requestResult = new org.webapp.services.LoadBalancerStub.GetResult();
				requestResult.setKey(key);

				// Invoking the service
				org.webapp.services.LoadBalancerStub.GetResultResponse responseResult = loadStub.getResult(requestResult);
				result = (String) responseResult.get_return();

				// Wait for 60 seconds
				try {
					Thread.sleep(60000);
				} catch(InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
			}

			System.out.println("Result is '" + result + "'");
		} catch (AxisFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}