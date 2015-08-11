
/**
 * @author Rob
 * Based on CardLayoutDemo.java on http://download.oracle.com/javase/tutorial/uiswing/examples/layout/CardLayoutDemoProject/src/layout/CardLayoutDemo.java
 *
 */
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class AdminGUI implements ActionListener {
	static Server server = new Server ("admin.properties");
	static int serverCount = 0;
	boolean testOn = false; 

	JPanel cards; //a panel that uses CardLayout
	static String activeCard;
	final static String HOMEPANEL = "Card with Home Panel";
	final static String RENDEZVOUSPANEL = "Card with Rendezvous Panel";
	final static String SETTINGSPANEL = "Card with Settings Panel";
	final static String SENDCONSIDERATIONPANEL = "Card with Send Consideration Panel";
	final static String LISTINGPANEL = "Card with Listing Panel";

	public static int deviceDatabaseSize = 0;

	static JFrame providerServer;
	static JPanel menuPanel, rendezvousPanel, considerationPanel, listingPanel, settingsPanel;
	private JButton btnSettings, btnCreateMarketplace, btnHomeMenuSettings;

	private JTextField txtSettingsMktAddr;
	private static JTextArea textAreaSettings;

	TokenManager tokenMgr = TokenManager.getInstance();
	DiscoveredEntitiesManager dEMgr = DiscoveredEntitiesManager.getInstance();
	private JCheckBox chckbxTurnTestData;

	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event dispatch thread.
	 */
	private static void createAndShowGUI() {
		//Create and set up the window.
		providerServer = new JFrame("CouchDB-ChoiceNet Admin Tool");
		providerServer.setBounds(200, 100, 263, 326);
		providerServer.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		// Source https://tips4java.wordpress.com/2009/05/01/closing-an-application/
		providerServer.addWindowListener( new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				JFrame frame = (JFrame)e.getSource();

				int result = JOptionPane.showConfirmDialog(
						frame,
						"Are you sure you want to exit the application?",
						"Exit Application",
						JOptionPane.YES_NO_OPTION);

				if (result == JOptionPane.YES_OPTION)
				{
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				}
			}
		});

		//Create and set up the content pane.
		AdminGUI gui = new AdminGUI();
		gui.addComponentToPane(providerServer.getContentPane());

		//Display the window.
		providerServer.pack();
		providerServer.setVisible(true);

	}

	/**
	 * Creates all panes for the different user views.
	 * @param pane
	 */
	public void addComponentToPane(Container pane ) {
		JPanel panelHome = createMenuPanel();
		//Create the panel that contains the "cards".
		cards = new JPanel(new CardLayout());
		cards.add(panelHome, HOMEPANEL);
		pane.setPreferredSize(new Dimension(650, 380));
		pane.add(cards, BorderLayout.CENTER);

		settingsPanel = createSettingsPanel();
		cards.add(settingsPanel, SETTINGSPANEL);


		// load test case
		showTestData();
	}

	// change card by button selection

	public void actionPerformed(ActionEvent e) {
		// Change Panels 
		if(e.getSource() == btnSettings)
		{
			activeCard = "Settings";
			actionStateChanged(SETTINGSPANEL);
		}
		if(e.getSource() == btnHomeMenuSettings)
		{
			actionStateChanged(HOMEPANEL);
		}
		// Home Panel
		if(e.getSource() == chckbxTurnTestData)
		{
			if(chckbxTurnTestData.isSelected())
			{
				testOn = true;
			}
			else
			{
				testOn = false;
			}
			showTestData();
		}

		// Settings 
		if(e.getSource() == btnCreateMarketplace)
		{
			String marketplaceAddr = txtSettingsMktAddr.getText();
			server.createMarketplaceDatabase(marketplaceAddr);
			//server.createRangeDatabase(rangeHelperAddr);
		}
		
		updateTextArea();
	}

	/**
	 * Changes the card in view based on name provided
	 * @param cardName
	 */
	public void actionStateChanged(String cardName) {
		CardLayout cl = (CardLayout)(cards.getLayout());
		cl.show(cards, cardName);
	}

	/**
	 *  Returns a JPanel of the Login screen
	 * @return JPanel
	 */
	public JPanel createMenuPanel ()
	{
		activeCard = "Menu";
		JPanel menuPanel = new JPanel();
		menuPanel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("6px"),
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.MIN_COLSPEC,
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				FormFactory.MIN_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(74dlu;pref):grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(62dlu;default):grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,},
				new RowSpec[] {
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("20px"),
				FormFactory.NARROW_LINE_GAP_ROWSPEC,
				RowSpec.decode("20px"),
				FormFactory.NARROW_LINE_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.NARROW_LINE_GAP_ROWSPEC,
				RowSpec.decode("24px"),
				FormFactory.UNRELATED_GAP_ROWSPEC,
				RowSpec.decode("26px"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("bottom:default"),}));



		JLabel lblMainMenu = new JLabel("Main Menu");
		lblMainMenu.setFont(new Font("Dialog", Font.BOLD, 12));
		menuPanel.add(lblMainMenu, "3, 3, 7, 1, center, default");

		btnSettings = new JButton("Settings");
		btnSettings.setFont(new Font("Dialog", Font.BOLD, 12));
		btnSettings.addActionListener(this);
		menuPanel.add(btnSettings, "9, 3, right, default");

		chckbxTurnTestData = new JCheckBox("Turn Test Data On");
		chckbxTurnTestData.setFont(new Font("Dialog", Font.BOLD, 12));
		chckbxTurnTestData.addActionListener(this);
		menuPanel.add(chckbxTurnTestData, "3, 17");

		return menuPanel;
	}

	/**
	 *  Returns a JPanel of the Login screen
	 * @return JPanel
	 */
	public JPanel createSettingsPanel ()
	{
		JPanel settingsPanel = new JPanel();
		settingsPanel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("6px"),
				FormFactory.DEFAULT_COLSPEC,
				ColumnSpec.decode("max(52dlu;min)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(141dlu;pref):grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(62dlu;default):grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("20px"),
				FormFactory.NARROW_LINE_GAP_ROWSPEC,
				RowSpec.decode("20px"),
				FormFactory.NARROW_LINE_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.NARROW_LINE_GAP_ROWSPEC,
				RowSpec.decode("24px"),
				FormFactory.UNRELATED_GAP_ROWSPEC,
				RowSpec.decode("26px"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("bottom:default"),}));

		JLabel title = new JLabel("Settings Menu");
		title.setFont(new Font("Dialog", Font.BOLD, 12));
		settingsPanel.add(title, "3, 3, 3, 1, center, default");

		btnHomeMenuSettings = new JButton("Back to Home Panel");
		btnHomeMenuSettings.setFont(new Font("Dialog", Font.BOLD, 12));
		btnHomeMenuSettings.addActionListener(this);
		settingsPanel.add(btnHomeMenuSettings, "9, 3");

		JLabel lblIpAddress = new JLabel("Marketplace Address");
		lblIpAddress.setFont(new Font("Dialog", Font.BOLD, 12));
		settingsPanel.add(lblIpAddress, "3, 9");

		txtSettingsMktAddr = new JTextField();
		txtSettingsMktAddr.setText(Server.marketplaceRESTAPI);
		settingsPanel.add(txtSettingsMktAddr, "5, 9, fill, default");
		txtSettingsMktAddr.setColumns(10);

		btnCreateMarketplace = new JButton("Create Marketplace");
		btnCreateMarketplace.setFont(new Font("Dialog", Font.BOLD, 12));
		btnCreateMarketplace.addActionListener(this);
		settingsPanel.add(btnCreateMarketplace, "9, 9");

		textAreaSettings = new JTextArea();
		textAreaSettings.setLineWrap(true);
		textAreaSettings.setWrapStyleWord(true);
		JScrollPane scroll = new JScrollPane (textAreaSettings);
		settingsPanel.add(scroll, "3, 21, 7, 9, fill, fill");

		return settingsPanel;
	}

	public void showTestData ()
	{
		if(testOn)
		{
			txtSettingsMktAddr.setText(Server.marketplaceRESTAPI);
			System.out.println("Test Data On");
		}
		else
		{
			txtSettingsMktAddr.setText("");
			System.out.println("Test Data Off");
		}
	}
	
	public void updateTextArea()
	{
		String message = Logger.display(0);
		System.out.println(activeCard);
		if(activeCard != null)
		{
			if(activeCard.equals("Menu"))
			{
				// Nothing
			}
			if(activeCard.equals("Settings"))
			{
				textAreaSettings.setText(message);
			}
		}
	}

	public static void main(String[] args) {
		/* Use an appropriate Look and Feel */

		try {
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		/* Turn off metal's use of bold fonts */
		UIManager.put("swing.boldMetal", Boolean.FALSE);

		//Schedule a job for the event dispatch thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});

		int loopCount = 0;
		while(true)
		{
			// update the display text area
			//			updateTextArea();
			try {
				server.startServer();
			} catch (Exception e) {
				System.out.println("Server is already running at the given port");
			}
			System.out.println("Should see something here");

			loopCount++; // do not delete this count or suffer my wrath
			System.out.println("Loop count "+loopCount);
		}


	}

}
