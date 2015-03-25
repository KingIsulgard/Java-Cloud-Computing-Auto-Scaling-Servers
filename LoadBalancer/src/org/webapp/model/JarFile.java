package org.webapp.model;

/*
 * Deze klasse is een dataklasse en houdt de data bij van een taak. In deze klasse zit een link naar de jarfile, een 
 * geserializeerde string van het callable object en een resultkey die gebruikt wordt om het resultaat op te vragen.
 */
public class JarFile {
	private String urlString;
	private String objectString;
	private Integer resultKey;
	
	public JarFile(String urlString, String objectString, Integer resultKey) {
		this.urlString = urlString;
		this.objectString = objectString;
		this.resultKey = resultKey;
	}
	
	public String getUrlString() {
		return this.urlString;
	}
	
	public String getObjectString() {
		return this.objectString;
	}
	
	public Integer getResultKey() {
		return this.resultKey;
	}
}
