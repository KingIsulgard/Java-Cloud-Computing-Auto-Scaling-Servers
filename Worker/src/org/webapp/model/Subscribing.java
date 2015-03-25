package org.webapp.model;

/*
 * Houdt de huidge subscriber staat bij van de worker. Deze dient om te onthouden of de worker reeds is 
 * ingeschreven of niet voor een nieuwe taak om te vermijden dat hij zich meermaals tegelijk gaat 
 * inschrijven bij de load balancer.
 */
public class Subscribing {
	private static boolean subscribed = false;

	protected Subscribing() {
		unsubscribe();
	}

	public static void subscribe() {
		subscribed = true;
		System.out.println("Subscribed");
	}

	public static void unsubscribe() {
		subscribed = false;
		System.out.println("Unsubscribed");
	}

	public static boolean isSubscribed() {
		return subscribed;
	}
}