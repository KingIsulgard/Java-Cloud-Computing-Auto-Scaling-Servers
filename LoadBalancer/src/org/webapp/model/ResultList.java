package org.webapp.model;

import java.util.*;

/*
 * Deze klasse bevat een hashmap met alle afgeleverde resultaten. De resultaten worden opgezocht via hun resultkey.
 */
public class ResultList {
	private HashMap<Integer, String> results;
	
	public ResultList() {
		results = new HashMap<Integer, String>();
	}
	
	public void addResult(Integer key, String result) {
		results.put(key, result);
	}
	
	public String getResult(Integer key) {
		return results.remove(key);
	}
}