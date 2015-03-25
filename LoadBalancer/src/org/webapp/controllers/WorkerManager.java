package org.webapp.controllers;

import org.webapp.model.WorkerQueue;

/*
 * WorkerManager.java
 * Deze klasse houdt de singleton instance bij van WorkerQueue.
 */
public class WorkerManager {
	private static WorkerQueue instance = null;

	protected WorkerManager() {
		// Exists only to defeat instantiation.
	}
	
	/*
	 * Geeft de instance van WorkerQueue terug.
	 */
	public static WorkerQueue getInstance() {
		if(instance == null) {
			instance = new WorkerQueue();
		}

		return instance;
	}
}
