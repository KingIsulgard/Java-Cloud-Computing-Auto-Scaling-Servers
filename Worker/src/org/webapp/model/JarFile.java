package org.webapp.model;

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
