package org.webapp.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import javax.swing.JTextArea;
import javax.swing.JLabel;

import java.awt.GridLayout;

import javax.swing.SwingConstants;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/*
 * Deze klasse dient voor het generen van de grafische user interface.
 */
public class GUI extends JFrame {

	private JPanel contentPane;
	
	private JTextArea console;
	private JLabel elblJobs;
	private JLabel elblWorkers;
	private JLabel elblWorkersBooting;
	private JLabel elblWorkload;
	private JLabel elblMinimumWorkers;
	private JLabel lblLoadbalancer;

	/**
	 * Create the frame.
	 */
	public GUI() {
		setTitle("Scaler");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 723, 294);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		
		console = new JTextArea();
		console.setEditable(false);
		console.setText("Console:");
		
		JScrollPane scroll = new JScrollPane(console);
	    scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	    
		contentPane.add(scroll, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.WEST);
		panel.setLayout(new GridLayout(0, 2, 0, 0));
		
		JLabel lblTasks = new JLabel("Tasks in Queue");
		panel.add(lblTasks);
		
		elblJobs = new JLabel("0");
		elblJobs.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(elblJobs);
		
		JLabel lblActiveWorkers = new JLabel("Active workers");
		panel.add(lblActiveWorkers);
		
		elblWorkers = new JLabel("0");
		elblWorkers.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(elblWorkers);
		
		JLabel lblWorkersBooting = new JLabel("Workers starting up");
		panel.add(lblWorkersBooting);
		
		elblWorkersBooting = new JLabel("0");
		elblWorkersBooting.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(elblWorkersBooting);
		
		JLabel lblAverageWorkload = new JLabel("Average workload");
		panel.add(lblAverageWorkload);
		
		elblWorkload = new JLabel("100%");
		elblWorkload.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(elblWorkload);
		
		JLabel lblMinimumWorkers = new JLabel("Minimum amount of workers");
		panel.add(lblMinimumWorkers);
		
		elblMinimumWorkers = new JLabel("3");
		elblMinimumWorkers.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(elblMinimumWorkers);
		
		lblLoadbalancer = new JLabel("Loadbalancer: Undefined");
		contentPane.add(lblLoadbalancer, BorderLayout.SOUTH);
	}
	
	public void addConsole(String text) {
		this.console.append("\n" + text);
		console.setCaretPosition(console.getDocument().getLength());
	}
	
	public void setAmountJobs(int amount) {
		this.elblJobs.setText(new Integer(amount).toString());
	}
	
	public void setWorkers(int amount) {
		this.elblWorkers.setText(new Integer(amount).toString());
	}
	
	public void setWorkersBooting(int amount) {
		this.elblWorkersBooting.setText(new Integer(amount).toString());
	}
	
	public void setWorkLoad(double d) {
		this.elblWorkload.setText(new Integer((int) d).toString() + "%");
	}
	
	public void setLoadBalancer(String text) {
		this.lblLoadbalancer.setText("Loadbalancer: " + text);
	}
}
