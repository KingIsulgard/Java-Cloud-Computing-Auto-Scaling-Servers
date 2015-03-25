package org.webapp.model;

import java.util.*;

/*
 * Deze klasse bevat een queue met alle uit te voeren taken op de load balancer. De taken zijn JarFile objecten.
 */
public class ThreadQueue {
	private Queue<JarFile> queue;
	
	public ThreadQueue() {
		this.queue = new LinkedList<JarFile>();
	}
	
	public void addThread(JarFile jarFile) {
		this.queue.add(jarFile);
	}
	
	public void reAddThread(JarFile jarFile) {
		Queue<JarFile> temp = new LinkedList<JarFile>();
		temp.add(jarFile);
		
		while(queue.size() > 0) {
			temp.add(queue.poll());
		}
		
		queue = temp;
	}
	
	public JarFile getNextThread() {
		return queue.poll();
	}
	
	public int getSize() {
		return queue.size();
	}
}