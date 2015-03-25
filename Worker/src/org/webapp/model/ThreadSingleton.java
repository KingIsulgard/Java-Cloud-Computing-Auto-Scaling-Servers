package org.webapp.model;

import org.webapp.model.*;

public class ThreadSingleton {
	private static ThreadList instance = null;
	   
   protected ThreadSingleton() {
      // Exists only to defeat instantiation.
   }
   
   public static ThreadList getInstance() {
      if(instance == null) {
         instance = new ThreadList();
      }
      
      return instance;
   }
}