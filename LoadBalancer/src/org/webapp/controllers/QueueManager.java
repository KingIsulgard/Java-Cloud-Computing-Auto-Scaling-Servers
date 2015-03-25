package org.webapp.controllers;

import org.webapp.model.*;

/*
 * QueueManager.java
 * Deze klasse dient voor een singleton te maken van de klasse ThreadQueue.java
 * 
 */
public class QueueManager {
	private static ThreadQueue instance = null;

	protected QueueManager() {
		// Exists only to defeat instantiation.
	}
	
	/*
	 * Geeft de singelton instance van ThreadQueue terug
	 */
	public static ThreadQueue getInstance() {
		if(instance == null) {
			instance = new ThreadQueue();
		}

		return instance;
	}
}