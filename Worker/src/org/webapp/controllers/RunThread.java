package org.webapp.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.codec.DecoderException;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.webapp.model.AddressData;
import org.webapp.model.JarFile;
import org.webapp.model.ThreadSingleton;
import org.webapp.services.LoadBalancerStub;

/*
 * Deze klasse zal een bepaalde taak draaien in een aparte thread. De taak wordt meegegeven via een JarFile object.
 */
public class RunThread implements Runnable {
	private JarFile jarFile;
	
	public RunThread(JarFile jarFile) {
		this.jarFile = jarFile;
	}
	
    public void run() {
		try {
			URL url = new URL(jarFile.getUrlString());
			URLClassLoader classLoader = new URLClassLoader(new URL[] {url});

			Callable<String> simpleRunnable = (Callable<String>) SerializeController.deserialize(classLoader, jarFile.getObjectString());

			ExecutorService executor = Executors.newCachedThreadPool();
			Future<String> result = executor.submit(simpleRunnable);
			
			String str = result.get();
			executor.shutdown();
			
			// Log
			System.out.println("Sending result of jar file to load balancer.");
			
			// Send result to load balancer
			LoadBalancerStub loadStub = new LoadBalancerStub("http://" + AddressData.getLoadBalancer() + ":8080/LoadBalancer/services/LoadBalancer.LoadBalancerHttpEndpoint/");
			
			// Creating the request
			org.webapp.services.LoadBalancerStub.PutResult request = new org.webapp.services.LoadBalancerStub.PutResult();
			request.setKey(jarFile.getResultKey());
			request.setResult(str);

			// Invoking the service
			loadStub.putResult(request);
			
			// Remove jarFile from list
			ThreadSingleton.getInstance().removeThread(jarFile);
			
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}