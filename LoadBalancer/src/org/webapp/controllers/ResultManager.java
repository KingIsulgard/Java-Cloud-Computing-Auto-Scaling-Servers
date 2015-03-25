package org.webapp.controllers;

import org.webapp.model.ResultList;

/*
 * ResultManager.java
 * Deze klasse houdt een singelton bij van de hashmap van resultaten. Met een key en een resultaat. 
 * Ook genereert deze klasse de key voor het volgende resultaat.
 */
public class ResultManager {
	private static ResultList instance = null;
	private static Integer resultCounter = 1;
	   
   protected ResultManager() {
      // Exists only to defeat instantiation.
   }
   
   /*
    * Genereert een key voor een resultaat.
    */
   public static Integer getResultCount() {
	   return resultCounter++;
   }
   
   /*
    * Geeft de instance van ResultList terug.
    */
   public static ResultList getInstance() {
      if(instance == null) {
         instance = new ResultList();
      }
      
      return instance;
   }
}