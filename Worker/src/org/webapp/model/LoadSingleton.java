package org.webapp.model;
import java.util.*;

import org.webapp.controllers.LoadManager;

/*
 * Beheert de instance van LoadManager. Je moet altijd met dezelfde instance van loadmanager werken.
 */
public class LoadSingleton {
   private static LoadManager instance = null;
   
   protected LoadSingleton() {
      // Exists only to defeat instantiation.
   }
   
   public static LoadManager getInstance() {
      if(instance == null) {
         instance = new LoadManager();
         new Thread(instance).start();
      }
      
      return instance;
   }
}