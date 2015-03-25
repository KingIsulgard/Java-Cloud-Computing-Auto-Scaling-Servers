package org.webapp.controllers;

import org.webapp.gui.GUI;
import org.webapp.model.InstanceList;

import com.amazonaws.services.ec2.model.Instance;

/*
 * Deze klasse is de main klasse. Deze bevat een eeuwige lus en zal dynamisch machines bij opstarten of 
 * afsluiten indien nodig. Ook zal het de load balancer opstarten in het begin. Verder zal het de 
 * GUI updaten met up to date informatie.
 */
public class RunScaler {
	public static void main(String[] args) throws Exception {
		GUI frame = new GUI();
		frame.setVisible(true);
		
		Scaler scaler = InstanceList.getScaler();
		scaler.setFrame(frame);
		
		InstanceList.setExpectedMachines(0);

		/*
		 * Only need to do this once

		// Create security group
		scaler.createSecurityGroup();

		// Create keypair
		scaler.createKeyPair();
		 */

		// Create balancer
		scaler.createLoadBalancer();

		while(true) {
			double lowestWorkload = 100;
			Instance lowestWorkingInstance = null;

			// Calculate average work load on machines
			double average = 0;
			for(Instance instance : InstanceList.getInstances()) {
				double workLoad = scaler.getInstanceWorkLoad(instance);

				// Remember this instance, it is the least active
				if(lowestWorkload > workLoad) {
					lowestWorkload = workLoad;
					lowestWorkingInstance = instance;
				}

				average += workLoad;
			}

			if(InstanceList.getInstances().size() > 0) {
				average = average / (InstanceList.getInstances().size() + InstanceList.getExpectedMachines());
			} else {
				average = 0.0;
			}

			int queueLength = scaler.getQueueLength();

			frame.setWorkLoad(average * 100);
			frame.setWorkers(InstanceList.getInstances().size());
			frame.setWorkersBooting(InstanceList.getExpectedMachines());
			frame.setAmountJobs(queueLength);

			// Start new machine if average workload is higher than 0.5 or there is no machine running yet
			if((average > 0.5 || (InstanceList.getInstances().size() + InstanceList.getExpectedMachines()) < 3 || queueLength > ((InstanceList.getInstances().size() + InstanceList.getExpectedMachines()) * 8)) && InstanceList.getExpectedMachines() < 4) {
				if(average > 0.5) {
					frame.addConsole("Starting new worker because average workload is over 0.5.");
				} else if(InstanceList.getInstances().size() == 0) {
					frame.addConsole("Starting new worker because there must be at least 1 worker running.");
				} else {
					frame.addConsole("Starting new worker because there are more than 8 times amount of jobs than workers.");
				}
				
				InstanceList.addExpectedMachine();

				Thread thread = new Thread(new Runnable()
				{
					public void run()
					{
						// Get scaler
						Scaler scaler = InstanceList.getScaler();
						
						try {
							// Create a worker
							Instance worker = scaler.createWorker();

							// Add to working list (need at least one)
							InstanceList.getInstances().add(worker);
							
							InstanceList.removeExpectedMachine();
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});

				// start the thread

				thread.start();
			}

			// Close all machines except one if avarage workload is lower than 0.2
			if(average < 0.2 && InstanceList.getInstances().size() > 3 && queueLength < (InstanceList.getInstances().size() * 3)) {
				frame.addConsole("Terminating a worker because the load is too low, all jobs will be readded to the queue.");

				// Terminate worker
				scaler.terminateWorker(lowestWorkingInstance);

				// Remove instance from list
				InstanceList.getInstances().remove(lowestWorkingInstance);
			}

			// Wait for 5 seconds
			try {
				Thread.sleep(5000);
			} catch(InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
	}
}
