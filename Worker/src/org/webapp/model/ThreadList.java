package org.webapp.model;

import java.util.*;

/*
 * Houdt een lijst bij van alle momenteel draaiende taken.
 */
public class ThreadList {
	private ArrayList<JarFile> threads;
	
	public ThreadList() {
		this.threads = new ArrayList<JarFile>();
	}
	
	public void addThread(JarFile jarFile) {
		this.threads.add(jarFile);
		System.out.println("Adding jar file to list");
	}
	
	public void removeThread(JarFile jarFile) {
		this.threads.remove(jarFile);
		System.out.println("Removing jarvfile from list");
	}
	
	public ArrayList<JarFile> getThreads() {
		return this.threads;
	}
}
