package de.piratenpartei.id;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JToolBar;
import java.awt.BorderLayout;
import java.io.IOException;
import java.security.KeyStore;

import javax.swing.JList;

public class GUI {

	private JFrame frame;
	private VAOV v;
	private char[] accountName;
	private KeyStore keystore;

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
		
		Account a = this.queryAccount();
		this.cryptInit();
		
		try {
			v = new VAOV(a);
		} catch(IOException e){
			 throw new RuntimeException(e);
		}
	}

	private void cryptInit() {
		// TODO
		//Helper.initKeyStore(password)
		
	}

	private Account queryAccount() {
		return null;
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
