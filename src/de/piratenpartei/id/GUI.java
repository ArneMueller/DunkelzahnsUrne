package de.piratenpartei.id;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JToolBar;
import java.awt.BorderLayout;
import java.io.IOException;
import java.security.KeyStore;

import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.JLabel;
import javax.swing.JInternalFrame;
import java.awt.FlowLayout;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JButton;

public class GUI {

	private JFrame frame;
	private VAOV v;
	private char[] accountName;
	private KeyStore keystore;
	private PrivateAccount account;

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
		
		this.cryptInit();
		this.vaovInit();
	}

	private void cryptInit() {
		this.account = this.queryAccount();
		//Helper.initKeyStore(password)
	}

	private void vaovInit(){
		try {
			v = new VAOV(this.account);
		} catch(IOException e){
			 throw new RuntimeException(e);
		}
		
	}
	
	private PrivateAccount queryAccount() {
		return null;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JToolBar toolBar = new JToolBar();
		frame.getContentPane().add(toolBar, BorderLayout.NORTH);
		
		JTree tree = new JTree();
		frame.getContentPane().add(tree, BorderLayout.WEST);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		JLabel lblInitextlabel = new JLabel("IniTextLabel");
		panel.add(lblInitextlabel);
		
		JPanel panel_1 = new JPanel();
		panel.add(panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));

		JButton btnYes = new JButton("Yes");
		panel_1.add(btnYes);
		
		JButton btnNo = new JButton("No");
		panel_1.add(btnNo);
	}

}
