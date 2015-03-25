package org.webapp.listeners;

import javax.servlet.ServletContextEvent;

import org.webapp.controllers.LoadManager;
import org.webapp.model.LoadSingleton;
import org.webapp.model.Subscribing;

/*
 * Deze listener start op bij het opstarten van de server waarbij deze dan de LoadThread zal opstarten.
 */
public class StartListener implements javax.servlet.ServletContextListener {
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		Subscribing.unsubscribe();
		
		LoadManager loadManager = LoadSingleton.getInstance();
		loadManager.getAverageUsage();
	}
}