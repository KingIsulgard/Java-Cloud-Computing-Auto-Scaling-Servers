package org.client;

import java.io.Serializable;
import java.util.concurrent.Callable;

import sun.util.logging.resources.logging;

/*
 * Dit is een callable object dat bedoelt is om een taak te bevatten dat naar de cloud gestuurd kan worden. Deze kan draaien 
 * in een aparte thread maar ik heb toch voor het callable concept gekozen omdat je dan het resultaat kan opvragen. Callables 
 * draaien net zoals runnables in een eigen thread.
 */
public class SimpleRunnable implements Callable<String>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String text;
	
	public SimpleRunnable(String text) {
		this.text = text;
	}
	
	public String call() {
		for(int i = 0; i < 1000000000; i++) {
			Math.log(i);
		}
		
		// Wait for 10 seconds
		try {
			Thread.sleep(10000);
		} catch(InterruptedException ex) {
			Thread.currentThread().interrupt();
		}

		return ("Finished thread with name " + text);
    }
}