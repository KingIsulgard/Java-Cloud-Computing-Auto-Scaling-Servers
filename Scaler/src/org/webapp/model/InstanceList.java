package org.webapp.model;

import java.io.IOException;
import java.util.*;

import org.webapp.controllers.Scaler;

import com.amazonaws.services.ec2.model.Instance;

/*
 * Deze klasse houdt een lijst bij van alle draaiende instances en dewelke aan het opstarten zijn.
 */
public class InstanceList {
	private static int instanceId = 1;
	private static List<Instance> instances;
	private static Scaler scaler;
	private static int expectedMachines;

	protected InstanceList() {
		// Exists only to defeat instantiation.
	}

	public static int getNextInstanceId() {
		return instanceId++;
	}
	
	public static List<Instance> getInstances() {
		if(instances == null) {
			instances = new ArrayList<Instance>();
		}
		
		return instances;
	}
	
	public static Scaler getScaler() {
		if(scaler == null) {
			try {
				scaler = new Scaler();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return scaler;
	}
	
	public static void setExpectedMachines(int amount) {
		expectedMachines = amount;
	}
	
	public static void addExpectedMachine() {
		expectedMachines = expectedMachines + 1;
	}
	
	public static void removeExpectedMachine() {
		expectedMachines = expectedMachines - 1;
	}
	
	public static int getExpectedMachines() {
		return expectedMachines;
	}
}
