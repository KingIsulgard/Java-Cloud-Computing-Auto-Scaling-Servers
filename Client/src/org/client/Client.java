package org.client;

import org.webapp.services.*;

/*
 * Dit is de main klasse van het project. Deze zal de taken naar de load balancer sturen gebaseerd op het DNS adres van de load balancer.
 */
public class Client {
	public static void main(String[] args) throws Exception {
		SimpleRunnable simpleRunnable = new SimpleRunnable("Dit is een callable thread.");
        
		// Address van de work balancer
		String address = "ec2-54-195-73-84.eu-west-1.compute.amazonaws.com";
		int amountTasks = 1;
		
		for(int i = 0; i < amountTasks; i++) {
			SendRunnable sendRunnable = new SendRunnable(simpleRunnable, address);
			new Thread(sendRunnable).start();
		}
	}
}