package de.piratenpartei.id;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JToolBar;
import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JList;

public class GUI {

	private JFrame frame;
	private VAOV v;
	private char[] pass;
	private char[] accountName;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
		
		this.queryAccount();
		this.cryptInit();
		
		try {
			v = new VAOV(accountName, pass);
			overwrite(pass);
		} catch(IOException e){
			 throw new RuntimeException(e);
		}
	}

	private void cryptInit() {
		// TODO Auto-generated method stub
		
	}

	private void queryAccount() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JToolBar toolBar = new JToolBar();
		frame.getContentPane().add(toolBar, BorderLayout.NORTH);
	}

}
