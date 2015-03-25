package org.webapp.model;

/*
 * Deze klasse houdt in een singelton data bij over de adres van de worker zelf en het adres van de load balancer.
 */
public class AddressData {
	private static String loadBalancer = null;
	private static String myAddress = null;

	protected AddressData() {
		// Exists only to defeat instantiation.
	}

	public static void setLoadBalancer(String address) {
		loadBalancer = address;
	}

	public static String getLoadBalancer() {
		return loadBalancer;
	}

	public static void setMyAddress(String address) {
		myAddress = address;
	}

	public static String getMyAddress() {
		return myAddress;
	}
}