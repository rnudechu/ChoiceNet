
/**
 * @author Rob
 * Based on CardLayoutDemo.java on http://download.oracle.com/javase/tutorial/uiswing/examples/layout/CardLayoutDemoProject/src/layout/CardLayoutDemo.java
 * Similar to ServerDriver ... just a GUI Representation allows for sensorVisualization
 *
 */
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class ProviderGUI implements ActionListener {
	static Server server;
	static int serverCount = 0;
	ChoiceNetLibrary cnLibrary = ChoiceNetLibrary.getInstance();

	boolean testOn = false; 

	JPanel cards; //a panel that uses CardLayout
	static String activeCard;
	final static String HOMEPANEL = "Card with Home Panel";
	final static String RENDEZVOUSPANEL = "Card with Rendezvous Panel";
	final static String SETTINGSPANEL = "Card with Settings Panel";
	final static String SENDCONSIDERATIONPANEL = "Card with Send Consideration Panel";
	final static String PURCHASEPANEL = "Card with Purchase Panel";
	final static String SEARCHMARKETPLACEPANEL = "Card with Marketplace Panel";
	final static String PLANNERPANEL = "Card with Planner Panel";
	final static String USEPLANEPANEL = "Card with Access Use Plane Panel";

	public static int deviceDatabaseSize = 0;

	static JFrame providerServer;
	static JPanel menuPanel, rendezvousPanel, considerationPanel, purchasePanel, settingsPanel, marketplacePanel, plannerPanel, usePlanePanel;
	private JButton btnRendezvousMenu, btnRendezvousRequest,
	btnSendConsiderationMenu, btnSendConsiderationRequest, btnKnownEntities,
	btnSendListingMenu, btnSendListingRequest, btnListingAdvertisementBrowse, btnShowTokenId, 
	btnClearPlanner,searchBtnPlanner,
	btnSendUsePlane, btnStoredUsePlane, btnSearchMarketplace, btnAccessUsePlane,
	btnHomeMenuRendezvous, btnHomeMenuListing, btnHomeMenuConsideration, btnHomeMenuUsePlane, btnHomeMenuPlanner;
	private JTextField txtListingIPAddr, txtListingFileLocation, txtListingTokenID, txtListingTarget;
	private JTextField txtConsiderationServiceName, txtConsiderationTarget, txtConsiderationMethod, txtConsiderationValue, txtConsiderationIPAddr, txtConsiderationAddrType;
	private JTextField txtRendezvousIPAddr, txtRendezvousTarget;
	private JTextField serverIPAddressTxtFldPlanner, txtLocationSourceTypePlanner, textFieldLocationSourcePlanner, txtLocationDestinationTypePlanner, 
	textFieldLocationDestinationPlanner, txtFormatSourceTypePlanner, textFieldFormatSourcePlanner, txtFormatDestinationTypePlanner, 
	textFieldFormatDestinationPlanner, txtCostMethodPlanner, textFieldCostPlanner;
	private JFileChooser fc =  new JFileChooser();
	private static JTextArea textAreaRendezvous, textAreaConsideration, textAreaListing, textAreaMktpl, textAreaPlanner, textAreaUsePlane;

	TokenManager tokenMgr = TokenManager.getInstance();
	DiscoveredEntitiesManager dEMgr = DiscoveredEntitiesManager.getInstance();
	private JCheckBox chckbxTurnTestData;
	private JTextField txtRendezvousAddrType;
	private JTextField txtListingAddrType;
	private static JLabel lblRendezvousMessage, lblConsiderationMessage, lblListingMessage, lblUsePlaneMessage;
	private JTextField txtUsePlaneGWType, txtUsePlaneGWAddr, txtUsePlaneClientType, txtUsePlaneClientAddr, txtUsePlaneToken;


	private JLabel lblServerIpAddress;
	private JTextField serverIPAddressTxtFldMktpl;
	private JLabel lblClientDirections;
	private JLabel lblSource;
	private JLabel lblDestination;
	private JTextField textFieldLocationSourceMktpl;
	private JTextField textFieldLocationDestinationMktpl;
	private JLabel lblBitsPerSecond;
	private JTextField textFieldFormatSourceMktpl, textFieldFormatDestinationMktpl, textFieldCostMktpl, textFieldAdvID;
	JButton searchBtn;
	private JButton btnHomeMenuSearchMarketplace;
	private JLabel lblSearchTheMarketplace;
	private JLabel lblSourceValue;
	private JLabel lblValue;
	private JTextField txtLocationSourceTypeMktpl;
	private JLabel lblType;
	private JLabel lblType_1;
	private JTextField txtLocationDestinationTypeMktpl;
	private JTextField txtFormatSourceTypeMktpl;
	private JTextField txtFormatDestinationTypeMktpl;
	private JTextField txtCostMethodMktpl;
	private JButton btnUsePlannerService;
	private JLabel lblMarketplacenotifier;

	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event dispatch thread.
	 */
	private static void createAndShowGUI() {
		//Create and set up the window.
		providerServer = new JFrame("Loading ...");
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
		ProviderGUI gui = new ProviderGUI();
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
		JPanel panelHome = createProviderHomeMenuPanel();
		if(Server.runningMode.equals("UserMarketplaceGUI"))
		{
			panelHome = createMarketplaceMenuPanel();
			providerServer.setTitle("Marketplace GUI");
		}
		if(Server.runningMode.equals("SuperUserGUI"))
		{
			panelHome = createSuperUserMenuPanel();
			providerServer.setTitle("Super User GUI");
		}
		if(Server.runningMode.equals("ClientGUI"))
		{
			panelHome = createCustomerHomeMenuPanel();
			providerServer.setTitle("Customer GUI");
		}
		if(Server.runningMode.equals("PlannerGUI"))
		{
			panelHome = createPlannerMenuPanel();
			providerServer.setTitle("Planner GUI");
		}
		if(Server.runningMode.equals("ProviderGUI"))
		{
			panelHome = createProviderHomeMenuPanel();
			providerServer.setTitle("Provider GUI");
		}
		//Create the panel that contains the "cards".
		cards = new JPanel(new CardLayout());
		cards.add(panelHome, HOMEPANEL);
		pane.setPreferredSize(new Dimension(650, 380));
		pane.add(cards, BorderLayout.CENTER);

		rendezvousPanel = createRendezvousPanel();
		cards.add(rendezvousPanel, RENDEZVOUSPANEL);

		considerationPanel = createSendConsiderationPanel();
		cards.add(considerationPanel, SENDCONSIDERATIONPANEL);

		purchasePanel = createPurchasePanel();
		cards.add(purchasePanel, PURCHASEPANEL);

		marketplacePanel = createMarketplacePanel();
		cards.add(marketplacePanel, SEARCHMARKETPLACEPANEL);

		plannerPanel = createPlannerPanel();
		cards.add(plannerPanel, PLANNERPANEL);

		usePlanePanel = createUsePlanePanel();
		cards.add(usePlanePanel, USEPLANEPANEL);

		// load test case
		showTestData();
	}

	// change card by button selection

	public void actionPerformed(ActionEvent e) {
		// Change Panels 
		if(e.getSource() == btnRendezvousMenu)
		{
			activeCard = "Rendezvous";
			actionStateChanged(RENDEZVOUSPANEL);
		}
		if(e.getSource() == btnSendConsiderationMenu)
		{
			activeCard = "Consideration";
			actionStateChanged(SENDCONSIDERATIONPANEL);
		}
		if(e.getSource() == btnSendListingMenu)
		{
			activeCard = "Listing";
			actionStateChanged(PURCHASEPANEL);
		}
		if(e.getSource() == btnSearchMarketplace)
		{
			activeCard = "Marketplace";
			actionStateChanged(SEARCHMARKETPLACEPANEL);
		}
		if(e.getSource() == btnUsePlannerService)
		{
			activeCard = "Planner";
			actionStateChanged(PLANNERPANEL);
		}
		if(e.getSource() == btnAccessUsePlane)
		{
			activeCard = "UsePlane";
			actionStateChanged(USEPLANEPANEL);
		}
		if(e.getSource() == btnHomeMenuRendezvous || e.getSource() == btnHomeMenuConsideration || e.getSource() == btnHomeMenuListing || e.getSource() == btnHomeMenuUsePlane
				|| e.getSource() == btnHomeMenuSearchMarketplace || e.getSource() ==  btnHomeMenuPlanner)
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

		String[] addr;
		String ipAddr, portStr, target, message;
		int port = -1;
		boolean success = true;
		// Rendezvous Panel
		if(e.getSource() == btnRendezvousRequest)
		{
			ipAddr = txtRendezvousIPAddr.getText();
			addr = ipAddr.split(":");
			if(addr.length == 2)
			{
				ipAddr = addr[0];
				portStr = addr[1];
				try
				{
					port = Integer.parseInt(portStr);
				}
				catch(Exception error)
				{
					Logger.log("Invalid port supplied to Send Consideration form");
					success = false;
				}
				target = txtRendezvousTarget.getText();

				if(success)
				{
					server.sendRendevouzMessage(target,ipAddr,port);
					updateTextArea();
				}
			}

		}

		if(e.getSource() == btnSendConsiderationRequest)
		{

			// Check the form
			ipAddr = txtConsiderationIPAddr.getText();
			addr = ipAddr.split(":");
			if(addr.length == 2)
			{
				ipAddr = addr[0];
				portStr = addr[1];
				try
				{
					port = Integer.parseInt(portStr);
				}
				catch(Exception error)
				{
					Logger.log("Invalid port supplied to Send Consideration form");
					success = false;
				}
				target = txtConsiderationTarget.getText();
				String exchangeMethod = txtConsiderationMethod.getText();
				addr = exchangeMethod.split(":");
				String exchangeType = addr[0];
				String exchangeValue = addr[1];
				System.out.println(exchangeValue+" not being used");
				String exchangeAmount = txtConsiderationValue.getText();
				String serviceName = txtConsiderationServiceName.getText();
				if(success)
				{
					server.transferConsiderationMessage(serviceName, target, exchangeType, exchangeAmount, ipAddr, port);
					updateTextArea();
				}
			}

		}
		if(e.getSource() == btnKnownEntities)
		{
			message = dEMgr.printDiscoveredEntities();
			textAreaConsideration.setText(message);
		}
		// Send Listing Request
		if(e.getSource() ==  btnListingAdvertisementBrowse)
		{
			int returnVal = fc.showOpenDialog(purchasePanel);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				txtListingFileLocation.setText(file.getAbsolutePath());
			}
		}
		if(e.getSource() == btnSendListingRequest)
		{

			// Check the form
			ipAddr = txtListingIPAddr.getText();
			addr = ipAddr.split(":");
			if(addr.length == 2)
			{
				ipAddr = addr[0];
				portStr = addr[1];
				try
				{
					port = Integer.parseInt(portStr);
				}
				catch(Exception error)
				{
					Logger.log("Invalid port supplied to Request to List form");
					success = false;
				}
				target = txtListingTarget.getText();
				String tokenID = txtListingTokenID.getText();
				String fileName = txtListingFileLocation.getText();
				if(success)
				{
					server.transferListingMessage(fileName, target, tokenID, ipAddr, port);
					updateTextArea();
				}
			}
		}
		if(e.getSource() == btnShowTokenId)
		{

			message = tokenMgr.printAvailableTokens();
			textAreaListing.setText(message);
		}
		// Search Marketplace
		if( e.getSource() == searchBtn)
		{
			String marketplaceAddr = serverIPAddressTxtFldMktpl.getText().toString();
			String sourceLoc = textFieldLocationSourceMktpl.getText().toString();
			String srcLocType = txtLocationSourceTypeMktpl.getText().toString();
			String destinationLoc = textFieldLocationDestinationMktpl.getText().toString();
			String dstLocType = txtLocationDestinationTypeMktpl.getText().toString();
			String cost = textFieldCostMktpl.getText().toString();
			String sourceFormat = textFieldFormatSourceMktpl.getText().toString();
			String srcFormatType = txtFormatSourceTypeMktpl.getText().toString();
			String destinationFormat = textFieldFormatDestinationMktpl.getText().toString();
			String dstFormatType = txtFormatDestinationTypeMktpl.getText().toString();
			String adID = textFieldAdvID.getText().toString();

			if(!marketplaceAddr.isEmpty() && marketplaceAddr.contains(":"))
			{
				String[] srcLocArr = sourceLoc.split(",");
				String[] dstLocArr = destinationLoc.split(",");
				String[] srcFormatArr = sourceFormat.split(",");
				String[] dstFormatArr = destinationFormat.split(",");
				String[] srcLocTypeArr = srcLocType.split(",");
				String[] dstLocTypeArr = dstLocType.split(",");
				String[] srcFormatTypeArr = srcFormatType.split(",");
				String[] dstFormatTypeArr = dstFormatType.split(",");
				if(srcLocArr.length==srcLocTypeArr.length && dstLocArr.length == dstLocTypeArr.length &&
						srcFormatArr.length == srcFormatTypeArr.length && dstFormatArr.length == dstFormatTypeArr.length)
				{
					lblMarketplacenotifier.setText("");
					server.sendMarketplaceQuery(marketplaceAddr, sourceLoc, destinationLoc, sourceFormat, destinationFormat, srcLocType, dstLocType, srcFormatType, dstFormatType, cost, adID);
				}
				else
				{
					message = "<html>Error: Type should have same number of commas as their corresponding Value</html>";
					lblMarketplacenotifier.setText(message);
				}
			}
		}
		// Planner Panel
		if(e.getSource() == btnClearPlanner)
		{
			// clear the form
			textFieldLocationSourcePlanner.setText("");
			txtLocationSourceTypePlanner.setText("");
			textFieldLocationDestinationPlanner.setText("");
			txtLocationDestinationTypePlanner.setText("");
			txtCostMethodPlanner.setText("");
			textFieldCostPlanner.setText("");
			textFieldFormatSourcePlanner.setText("");
			txtFormatSourceTypePlanner.setText("");
			textFieldFormatDestinationPlanner.setText("");
			txtFormatDestinationTypePlanner.setText("");
		}
		if(e.getSource() == searchBtnPlanner)
		{
			// search the form
			ServiceRequirement svcReq;
			String marketplaceAddr = serverIPAddressTxtFldPlanner.getText().toString();
			String sourceLoc = textFieldLocationSourcePlanner.getText().toString();
			String sourceLocType = txtLocationSourceTypePlanner.getText().toString();
			String destinationLoc = textFieldLocationDestinationPlanner.getText().toString();
			String destinationLocType = txtLocationDestinationTypePlanner.getText().toString();
			String method = txtCostMethodPlanner.getText().toString();
			String amount = textFieldCostPlanner.getText().toString();
			String sourceFormat = textFieldFormatSourcePlanner.getText().toString();
			String sourceFormatType = txtFormatSourceTypePlanner.getText().toString();
			String destinationFormat = textFieldFormatDestinationPlanner.getText().toString();
			String destinationFormatType = txtFormatDestinationTypePlanner.getText().toString();
			Cost svcCost = new Cost(method, amount);
			svcReq = new ServiceRequirement(sourceLoc, sourceLocType, destinationLoc, destinationLocType, sourceFormat, sourceFormatType, destinationFormat, destinationFormatType, svcCost);

			if(!marketplaceAddr.isEmpty() && marketplaceAddr.contains(":"))
			{
				server.sendPlannerRequest(marketplaceAddr, svcReq);
			}
		}
		// Use Plane Panel
		if(e.getSource() == btnSendUsePlane)
		{
			String gwType = txtUsePlaneGWType.getText();
			String gwAddr = txtUsePlaneGWAddr.getText();
			String clientType = txtUsePlaneClientType.getText();
			String clientAddr = txtUsePlaneClientAddr.getText();
			String token = txtUsePlaneToken.getText();
			if(gwType.equals("TCPv4") || gwType.equals("UDPv4"))
			{
				addr = gwAddr.split(":");
				ipAddr = addr[0];
				portStr = addr[1];
				try
				{
					port = Integer.parseInt(portStr);
				}
				catch(Exception error)
				{
					Logger.log("Invalid port supplied to Access Use Plane form");
					success = false;
				}
				if(success)
				{
					server.fireUsePlaneSignaling(gwType, gwAddr, clientType, clientAddr, token, ipAddr, port);
				}
			}
			else
			{
				message ="Unknown Address type supplied for the ChoiceNet Gateway: "+gwType+"\n"; 
				Server.systemMessage = message;
				Logger.log(message);
			}
		}

		if(e.getSource() == btnStoredUsePlane)
		{

		}
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
	public JPanel createProviderHomeMenuPanel ()
	{
		activeCard = "Menu";
		JPanel providerHomeMenuPanel = new JPanel();
		providerHomeMenuPanel.setLayout(new FormLayout(new ColumnSpec[] {
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

		btnRendezvousMenu = new JButton("Rendezvous Menu");
		btnRendezvousMenu.setFont(new Font("Dialog", Font.BOLD, 12));
		btnRendezvousMenu.addActionListener(this);
		providerHomeMenuPanel.add(btnRendezvousMenu, "3, 9");

		JLabel lblMainMenu = new JLabel("Main Menu");
		lblMainMenu.setFont(new Font("Dialog", Font.BOLD, 12));
		providerHomeMenuPanel.add(lblMainMenu, "3, 3, 7, 1, center, default");

		//		btnSettings = new JButton("Settings");
		//		btnSettings.setFont(new Font("Dialog", Font.BOLD, 12));
		//		btnSettings.addActionListener(this);
		//		providerHomeMenuPanel.add(btnSettings, "9, 3, right, default");,

		btnSendConsiderationMenu = new JButton("Send Consideration");
		btnSendConsiderationMenu.setFont(new Font("Dialog", Font.BOLD, 12));
		btnSendConsiderationMenu.addActionListener(this);
		providerHomeMenuPanel.add(btnSendConsiderationMenu, "7, 9");

		btnSendListingMenu = new JButton("Send Purchase Request");
		btnSendListingMenu.setFont(new Font("Dialog", Font.BOLD, 12));
		btnSendListingMenu.addActionListener(this);
		providerHomeMenuPanel.add(btnSendListingMenu, "9, 9, center, default");

		chckbxTurnTestData = new JCheckBox("Turn Test Data On");
		chckbxTurnTestData.addActionListener(this);

		providerHomeMenuPanel.add(chckbxTurnTestData, "3, 17");

		return providerHomeMenuPanel;
	}

	/**
	 *  Returns a JPanel of the Login screen
	 * @return JPanel
	 */
	public JPanel createCustomerHomeMenuPanel ()
	{
		activeCard = "Menu";
		JPanel customerHomeMenuPanel = new JPanel();
		customerHomeMenuPanel.setLayout(new FormLayout(new ColumnSpec[] {
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

		btnRendezvousMenu = new JButton("Rendezvous Menu");
		btnRendezvousMenu.setFont(new Font("Dialog", Font.BOLD, 12));
		btnRendezvousMenu.addActionListener(this);
		customerHomeMenuPanel.add(btnRendezvousMenu, "3, 9");

		JLabel lblMainMenu = new JLabel("Main Menu");
		lblMainMenu.setFont(new Font("Dialog", Font.BOLD, 12));
		customerHomeMenuPanel.add(lblMainMenu, "3, 3, 7, 1, center, default");

		//		btnSettings = new JButton("Settings");
		//		btnSettings.setFont(new Font("Dialog", Font.BOLD, 12));
		//		btnSettings.addActionListener(this);
		//		customerHomeMenuPanel.add(btnSettings, "9, 3, right, default");,

		btnSendConsiderationMenu = new JButton("Send Consideration");
		btnSendConsiderationMenu.setFont(new Font("Dialog", Font.BOLD, 12));
		btnSendConsiderationMenu.addActionListener(this);
		customerHomeMenuPanel.add(btnSendConsiderationMenu, "7, 9");

		btnSendListingMenu = new JButton("Send Purchase Request");
		btnSendListingMenu.setFont(new Font("Dialog", Font.BOLD, 12));
		btnSendListingMenu.addActionListener(this);
		customerHomeMenuPanel.add(btnSendListingMenu, "9, 9, center, default");

		chckbxTurnTestData = new JCheckBox("Turn Test Data On");
		chckbxTurnTestData.addActionListener(this);

		btnSearchMarketplace = new JButton("Search Marketplace");
		btnSearchMarketplace.setFont(new Font("Dialog", Font.BOLD, 12));
		customerHomeMenuPanel.add(btnSearchMarketplace, "3, 13");
		btnSearchMarketplace.addActionListener(this);


		btnUsePlannerService = new JButton("Use Planner Service");
		btnUsePlannerService.setFont(new Font("Dialog", Font.BOLD, 12));
		btnUsePlannerService.addActionListener(this);
		customerHomeMenuPanel.add(btnUsePlannerService, "7, 13");

		customerHomeMenuPanel.add(chckbxTurnTestData, "3, 17");

		return customerHomeMenuPanel;
	}

	/**
	 *  Returns a JPanel of the Login screen
	 * @return JPanel
	 */
	public JPanel createPlannerMenuPanel ()
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

		btnRendezvousMenu = new JButton("Rendezvous Menu");
		btnRendezvousMenu.setFont(new Font("Dialog", Font.BOLD, 12));
		btnRendezvousMenu.addActionListener(this);
		menuPanel.add(btnRendezvousMenu, "3, 9");

		JLabel lblMainMenu = new JLabel("Main Menu");
		lblMainMenu.setFont(new Font("Dialog", Font.BOLD, 12));
		menuPanel.add(lblMainMenu, "3, 3, 7, 1, center, default");

		//		btnSettings = new JButton("Settings");
		//		btnSettings.setFont(new Font("Dialog", Font.BOLD, 12));
		//		btnSettings.addActionListener(this);
		//		menuPanel.add(btnSettings, "9, 3, right, default");,

		chckbxTurnTestData = new JCheckBox("Turn Test Data On");
		chckbxTurnTestData.addActionListener(this);

		btnUsePlannerService = new JButton("Use Planner Service");
		btnUsePlannerService.setFont(new Font("Dialog", Font.BOLD, 12));
		btnUsePlannerService.addActionListener(this);
		menuPanel.add(btnUsePlannerService, "7, 9");
		menuPanel.add(chckbxTurnTestData, "3, 17");

		return menuPanel;
	}

	/**
	 *  Returns a JPanel of the Login screen
	 * @return JPanel
	 */
	public JPanel createMarketplaceMenuPanel ()
	{
		activeCard = "Menu";
		JPanel marketplaceHomeMenuPanel = new JPanel();
		marketplaceHomeMenuPanel.setLayout(new FormLayout(new ColumnSpec[] {
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

		btnRendezvousMenu = new JButton("Rendezvous Menu");
		btnRendezvousMenu.setFont(new Font("Dialog", Font.BOLD, 12));
		btnRendezvousMenu.addActionListener(this);
		marketplaceHomeMenuPanel.add(btnRendezvousMenu, "3, 9");

		JLabel lblMainMenu = new JLabel("Main Menu");
		lblMainMenu.setFont(new Font("Dialog", Font.BOLD, 12));
		marketplaceHomeMenuPanel.add(lblMainMenu, "3, 3, 7, 1, center, default");

		chckbxTurnTestData = new JCheckBox("Turn Test Data On");
		chckbxTurnTestData.addActionListener(this);

		//		btnSettings = new JButton("Settings");
		//		btnSettings.setFont(new Font("Dialog", Font.BOLD, 12));
		//		btnSettings.addActionListener(this);
		//		marketplaceHomeMenuPanel.add(btnSettings, "9, 3, right, default");,

		btnSearchMarketplace = new JButton("Search Marketplace");
		btnSearchMarketplace.setFont(new Font("Dialog", Font.BOLD, 12));
		btnSearchMarketplace.addActionListener(this);
		marketplaceHomeMenuPanel.add(btnSearchMarketplace, "7, 9, center, default");


		marketplaceHomeMenuPanel.add(chckbxTurnTestData, "3, 17");

		return marketplaceHomeMenuPanel;
	}

	/**
	 *  Returns a JPanel of the Login screen
	 * @return JPanel
	 */
	public JPanel createSuperUserMenuPanel ()
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

		btnRendezvousMenu = new JButton("Rendezvous Menu");
		btnRendezvousMenu.setFont(new Font("Dialog", Font.BOLD, 12));
		btnRendezvousMenu.addActionListener(this);
		menuPanel.add(btnRendezvousMenu, "3, 9");

		JLabel lblMainMenu = new JLabel("Main Menu");
		lblMainMenu.setFont(new Font("Dialog", Font.BOLD, 12));
		menuPanel.add(lblMainMenu, "3, 3, 7, 1, center, default");

		//		btnSettings = new JButton("Settings");
		//		btnSettings.setFont(new Font("Dialog", Font.BOLD, 12));
		//		btnSettings.addActionListener(this);
		//		menuPanel.add(btnSettings, "9, 3, right, default");,

		btnSendConsiderationMenu = new JButton("Send Consideration");
		btnSendConsiderationMenu.setFont(new Font("Dialog", Font.BOLD, 12));
		btnSendConsiderationMenu.addActionListener(this);
		menuPanel.add(btnSendConsiderationMenu, "7, 9");

		btnSendListingMenu = new JButton("Send Purchase Request");
		btnSendListingMenu.setFont(new Font("Dialog", Font.BOLD, 12));
		btnSendListingMenu.addActionListener(this);
		menuPanel.add(btnSendListingMenu, "9, 9, center, default");

		chckbxTurnTestData = new JCheckBox("Turn Test Data On");
		chckbxTurnTestData.addActionListener(this);

		btnSearchMarketplace = new JButton("Search Marketplace");
		btnSearchMarketplace.setFont(new Font("Dialog", Font.BOLD, 12));
		menuPanel.add(btnSearchMarketplace, "3, 13");
		btnSearchMarketplace.addActionListener(this);

		btnAccessUsePlane = new JButton("Access Use Plane");
		btnAccessUsePlane.setEnabled(false);
		btnAccessUsePlane.addActionListener(this);

		btnUsePlannerService = new JButton("Use Planner Service");
		btnUsePlannerService.setFont(new Font("Dialog", Font.BOLD, 12));
		btnUsePlannerService.addActionListener(this);
		menuPanel.add(btnUsePlannerService, "7, 13");
		btnAccessUsePlane.setFont(new Font("Dialog", Font.BOLD, 12));

		menuPanel.add(btnAccessUsePlane, "9, 13, center, default");
		menuPanel.add(chckbxTurnTestData, "3, 17");

		return menuPanel;
	}

	/**
	 *  Returns a JPanel of the Login screen
	 * @return JPanel
	 */
	public JPanel createPurchasePanel ()
	{

		JPanel purchasePanel = new JPanel();
		purchasePanel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("6px"),
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
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
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
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

		JLabel title = new JLabel("Trigger: Purchase Request");
		title.setFont(new Font("Dialog", Font.BOLD, 12));
		purchasePanel.add(title, "4, 3, 3, 1, center, default");

		btnHomeMenuListing = new JButton("Back to Main Menu");
		btnHomeMenuListing.setFont(new Font("Dialog", Font.BOLD, 12));
		btnHomeMenuListing.addActionListener(this);
		purchasePanel.add(btnHomeMenuListing, "8, 3, right, default");

		btnSendListingRequest = new JButton("Send Listing Request");
		btnSendListingRequest.setFont(new Font("Dialog", Font.BOLD, 12));
		btnSendListingRequest.addActionListener(this);

		lblListingMessage = new JLabel("");
		lblListingMessage.setFont(new Font("Dialog", Font.BOLD, 12));
		lblListingMessage.setForeground(Color.BLUE);
		purchasePanel.add(lblListingMessage, "4, 5, 5, 1");

		JLabel lblMarketplaceEconomyPlane = new JLabel("Marketplace Economy Plane Agent Address Type");
		lblMarketplaceEconomyPlane.setFont(new Font("Dialog", Font.BOLD, 12));
		purchasePanel.add(lblMarketplaceEconomyPlane, "4, 9");

		txtListingAddrType = new JTextField();
		purchasePanel.add(txtListingAddrType, "6, 9, fill, default");
		txtListingAddrType.setColumns(10);

		JLabel lblMarketplaceIpAddress = new JLabel("Marketplace Economy Plane Agent Address");
		lblMarketplaceIpAddress.setFont(new Font("Dialog", Font.BOLD, 12));
		purchasePanel.add(lblMarketplaceIpAddress, "4, 11");

		txtListingIPAddr = new JTextField();
		purchasePanel.add(txtListingIPAddr, "6, 11, fill, default");
		txtListingIPAddr.setColumns(10);

		JLabel lblListingServiceTarget = new JLabel("Listing Service Target");
		lblListingServiceTarget.setFont(new Font("Dialog", Font.BOLD, 12));
		purchasePanel.add(lblListingServiceTarget, "4, 13");

		txtListingTarget = new JTextField();
		purchasePanel.add(txtListingTarget, "6, 13, fill, default");
		txtListingTarget.setColumns(10);
		JLabel lblRendezvousTarget = new JLabel("Token ID");
		lblRendezvousTarget.setFont(new Font("Dialog", Font.BOLD, 12));
		purchasePanel.add(lblRendezvousTarget, "4, 15");
		txtListingTokenID = new JTextField();
		purchasePanel.add(txtListingTokenID, "6, 15, fill, default");
		txtListingTokenID.setColumns(10);

		JLabel lblServiceAdvertisementLocation = new JLabel("Advertisement Specification");
		lblServiceAdvertisementLocation.setFont(new Font("Dialog", Font.BOLD, 12));
		purchasePanel.add(lblServiceAdvertisementLocation, "4, 17");

		txtListingFileLocation = new JTextField();
		txtListingFileLocation.setEditable(false);
		purchasePanel.add(txtListingFileLocation, "6, 17, fill, default");
		txtListingFileLocation.setColumns(10);

		btnListingAdvertisementBrowse = new JButton("Browse");
		btnListingAdvertisementBrowse.setFont(new Font("Dialog", Font.BOLD, 12));
		btnListingAdvertisementBrowse.addActionListener(this);
		purchasePanel.add(btnListingAdvertisementBrowse, "8, 17, left, default");
		purchasePanel.add(btnSendListingRequest, "4, 21, center, default");

		btnShowTokenId = new JButton("Show Token ID");
		btnShowTokenId.addActionListener(this);
		btnShowTokenId.setFont(new Font("Dialog", Font.BOLD, 12));
		purchasePanel.add(btnShowTokenId, "6, 21");

		textAreaListing = new JTextArea();
		textAreaListing.setLineWrap(true);
		textAreaListing.setWrapStyleWord(true);
		JScrollPane scroll = new JScrollPane (textAreaListing);
		purchasePanel.add(scroll, "4, 23, 5, 7, fill, fill");

		return purchasePanel;
	}

	/**
	 *  Returns a JPanel of the Login screen
	 * @return JPanel
	 */
	public JPanel createRendezvousPanel ()
	{
		JPanel rendezvousPanel = new JPanel();
		rendezvousPanel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("6px"),
				FormFactory.DEFAULT_COLSPEC,
				ColumnSpec.decode("min:grow"),
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
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("26px"),
				FormFactory.UNRELATED_GAP_ROWSPEC,
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

		JLabel title = new JLabel("Trigger: Rendezvous");
		title.setFont(new Font("Dialog", Font.BOLD, 12));
		rendezvousPanel.add(title, "3, 3, 5, 1, center, default");

		btnHomeMenuRendezvous = new JButton("Back to Main Menu");
		btnHomeMenuRendezvous.setFont(new Font("Dialog", Font.BOLD, 12));
		btnHomeMenuRendezvous.addActionListener(this);
		rendezvousPanel.add(btnHomeMenuRendezvous, "9, 3");

		btnRendezvousRequest = new JButton("Send Rendezvous");
		btnRendezvousRequest.setFont(new Font("Dialog", Font.BOLD, 12));
		btnRendezvousRequest.addActionListener(this);

		lblRendezvousMessage = new JLabel("");
		lblRendezvousMessage.setForeground(Color.BLUE);
		lblRendezvousMessage.setFont(new Font("Dialog", Font.BOLD, 12));
		rendezvousPanel.add(lblRendezvousMessage, "3, 7, 7, 1");

		JLabel lblEntityEconomyPlane = new JLabel("Entity Economy Plane Agent Address Type");
		lblEntityEconomyPlane.setFont(new Font("Dialog", Font.BOLD, 12));
		rendezvousPanel.add(lblEntityEconomyPlane, "3, 11");

		txtRendezvousAddrType = new JTextField();
		rendezvousPanel.add(txtRendezvousAddrType, "7, 11, fill, default");
		txtRendezvousAddrType.setColumns(10);

		JLabel lblIpAddress = new JLabel("Entity Economy Plane Agent Address\n");
		lblIpAddress.setFont(new Font("Dialog", Font.BOLD, 12));
		rendezvousPanel.add(lblIpAddress, "3, 13");

		txtRendezvousIPAddr = new JTextField();
		rendezvousPanel.add(txtRendezvousIPAddr, "7, 13, fill, default");
		txtRendezvousIPAddr.setColumns(10);

		JLabel lblRendezvousTarget = new JLabel("Rendezvous Target's Provider Type\n");
		lblRendezvousTarget.setFont(new Font("Dialog", Font.BOLD, 12));
		rendezvousPanel.add(lblRendezvousTarget, "3, 15");
		txtRendezvousTarget = new JTextField();
		rendezvousPanel.add(txtRendezvousTarget, "7, 15, fill, default");
		txtRendezvousTarget.setColumns(10);
		rendezvousPanel.add(btnRendezvousRequest, "3, 19, center, default");

		textAreaRendezvous = new JTextArea();
		textAreaRendezvous.setLineWrap(true);
		textAreaRendezvous.setWrapStyleWord(true);
		JScrollPane scroll = new JScrollPane (textAreaRendezvous);
		rendezvousPanel.add(scroll, "3, 21, 7, 9, fill, fill");

		return rendezvousPanel;
	}

	/**
	 *  Returns a JPanel of the Login screen
	 * @return JPanel
	 */
	public JPanel createSendConsiderationPanel ()
	{

		JPanel considerationPanel = new JPanel();
		considerationPanel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("6px"),
				FormFactory.DEFAULT_COLSPEC,
				ColumnSpec.decode("min:grow"),
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
				FormFactory.NARROW_LINE_GAP_ROWSPEC,
				RowSpec.decode("24px"),
				FormFactory.NARROW_LINE_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.UNRELATED_GAP_ROWSPEC,
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
				RowSpec.decode("bottom:default"),}));
		JLabel title = new JLabel("Trigger: Send Consideration");
		title.setFont(new Font("Dialog", Font.BOLD, 12));
		considerationPanel.add(title, "3, 3, 3, 1, center, default");

		btnHomeMenuConsideration = new JButton("Back to Main Menu");
		btnHomeMenuConsideration.setFont(new Font("Dialog", Font.BOLD, 12));
		btnHomeMenuConsideration.addActionListener(this);
		considerationPanel.add(btnHomeMenuConsideration, "7, 3");

		btnSendConsiderationRequest = new JButton("Send Consideration");
		btnSendConsiderationRequest.setFont(new Font("Dialog", Font.BOLD, 12));
		btnSendConsiderationRequest.addActionListener(this);

		lblConsiderationMessage = new JLabel("");
		lblConsiderationMessage.setForeground(Color.BLUE);
		lblConsiderationMessage.setFont(new Font("Dialog", Font.BOLD, 12));
		considerationPanel.add(lblConsiderationMessage, "3, 5, 5, 1");

		JLabel lblProviderEconomyPlane = new JLabel("Provider Economy Plane Agent Address Type");
		lblProviderEconomyPlane.setFont(new Font("Dialog", Font.BOLD, 12));
		considerationPanel.add(lblProviderEconomyPlane, "3, 9");

		txtConsiderationAddrType = new JTextField();
		considerationPanel.add(txtConsiderationAddrType, "5, 9, fill, default");
		txtConsiderationAddrType.setColumns(10);

		JLabel lblProviderIpAddress = new JLabel("Provider Economy Plane Agent Address");
		lblProviderIpAddress.setFont(new Font("Dialog", Font.BOLD, 12));
		considerationPanel.add(lblProviderIpAddress, "3, 11");

		txtConsiderationIPAddr = new JTextField();
		considerationPanel.add(txtConsiderationIPAddr, "5, 11, fill, default");
		txtConsiderationIPAddr.setColumns(10);

		JLabel lblConsiderationTarget = new JLabel("Consideration Target");
		lblConsiderationTarget.setFont(new Font("Dialog", Font.BOLD, 12));
		considerationPanel.add(lblConsiderationTarget, "3, 13");
		txtConsiderationTarget = new JTextField();
		considerationPanel.add(txtConsiderationTarget, "5, 13, fill, default");
		txtConsiderationTarget.setColumns(10);

		JLabel lblServiceAdvertisementName = new JLabel("Service Name");
		lblServiceAdvertisementName.setFont(new Font("Dialog", Font.BOLD, 12));
		considerationPanel.add(lblServiceAdvertisementName, "3, 15");
		txtConsiderationServiceName = new JTextField();
		considerationPanel.add(txtConsiderationServiceName, "5, 15, fill, default");
		txtConsiderationServiceName.setColumns(10);

		JLabel lblConsiderationMethod = new JLabel("Consideration Method");
		lblConsiderationMethod.setFont(new Font("Dialog", Font.BOLD, 12));
		considerationPanel.add(lblConsiderationMethod, "3, 17");

		txtConsiderationMethod = new JTextField();
		considerationPanel.add(txtConsiderationMethod, "5, 17, fill, default");
		txtConsiderationMethod.setColumns(10);

		JLabel lblConsiderationValue = new JLabel("Consideration Amount");
		lblConsiderationValue.setFont(new Font("Dialog", Font.BOLD, 12));
		considerationPanel.add(lblConsiderationValue, "3, 19");

		txtConsiderationValue = new JTextField();
		considerationPanel.add(txtConsiderationValue, "5, 19, fill, default");
		txtConsiderationValue.setColumns(10);
		considerationPanel.add(btnSendConsiderationRequest, "3, 21, center, default");

		btnKnownEntities = new JButton("Known Entities");
		btnKnownEntities.addActionListener(this);
		btnKnownEntities.setFont(new Font("Dialog", Font.BOLD, 12));
		considerationPanel.add(btnKnownEntities, "5, 21, center, default");

		textAreaConsideration = new JTextArea();
		textAreaConsideration.setLineWrap(true);
		textAreaConsideration.setWrapStyleWord(true);
		JScrollPane scroll = new JScrollPane (textAreaConsideration);
		considerationPanel.add(scroll, "3, 23, 5, 7, fill, fill");

		return considerationPanel;
	}

	/**
	 *  Returns a JPanel of the Login screen
	 * @return JPanel
	 */
	public JPanel createMarketplacePanel ()
	{
		JPanel marketplacePanel = new JPanel();
		marketplacePanel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("6px"),
				FormFactory.DEFAULT_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(62dlu;default):grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.MIN_COLSPEC,},
				new RowSpec[] {
				FormFactory.NARROW_LINE_GAP_ROWSPEC,
				FormFactory.NARROW_LINE_GAP_ROWSPEC,
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
				RowSpec.decode("20px"),
				FormFactory.NARROW_LINE_GAP_ROWSPEC,
				RowSpec.decode("20px"),
				FormFactory.NARROW_LINE_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.NARROW_LINE_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.NARROW_LINE_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.MIN_ROWSPEC,}));

		lblSearchTheMarketplace = new JLabel("Search the Marketplace");
		lblSearchTheMarketplace.setFont(new Font("Dialog", Font.BOLD, 12));
		marketplacePanel.add(lblSearchTheMarketplace, "3, 3, 9, 1, center, default");

		btnHomeMenuSearchMarketplace = new JButton("Main Menu\n");
		btnHomeMenuSearchMarketplace.setFont(new Font("Dialog", Font.BOLD, 12));
		btnHomeMenuSearchMarketplace.addActionListener(this);
		marketplacePanel.add(btnHomeMenuSearchMarketplace, "13, 3, right, default");

		lblServerIpAddress = new JLabel("Marketplace Address:");
		lblServerIpAddress.setFont(new Font("Dialog", Font.BOLD, 12));
		marketplacePanel.add(lblServerIpAddress, "3, 5, left, default");

		serverIPAddressTxtFldMktpl = new JTextField(Server.marketplaceRESTAPI);
		serverIPAddressTxtFldMktpl.setForeground(Color.BLACK);
		marketplacePanel.add(serverIPAddressTxtFldMktpl, "5, 5, 7, 1, fill, default");
		serverIPAddressTxtFldMktpl.setColumns(10);

		lblClientDirections = new JLabel("<html>Only support Marketplace Address in the form {IPv4 address}:{UDP port}</html>");
		lblClientDirections.setFont(new Font("Dialog", Font.BOLD, 12));
		lblClientDirections.setForeground(Color.BLUE);
		marketplacePanel.add(lblClientDirections, "3, 7, 11, 1, center, center");

		lblMarketplacenotifier = new JLabel("");
		lblMarketplacenotifier.setForeground(Color.RED);
		marketplacePanel.add(lblMarketplacenotifier, "3, 9, 11, 1");

		lblSource = new JLabel("Source");
		lblSource.setFont(new Font("Dialog", Font.BOLD, 12));
		marketplacePanel.add(lblSource, "5, 11, 3, 1, center, default");

		lblDestination = new JLabel("Destination");
		lblDestination.setFont(new Font("Dialog", Font.BOLD, 12));
		marketplacePanel.add(lblDestination, "9, 11, 3, 1, center, default");

		lblType = new JLabel("Type");
		lblType.setFont(new Font("Dialog", Font.BOLD, 12));
		marketplacePanel.add(lblType, "5, 13, center, default");

		lblSourceValue = new JLabel("Value");
		lblSourceValue.setFont(new Font("Dialog", Font.BOLD, 12));
		marketplacePanel.add(lblSourceValue, "7, 13, center, default");

		lblType_1 = new JLabel("Type");
		lblType_1.setFont(new Font("Dialog", Font.BOLD, 12));
		marketplacePanel.add(lblType_1, "9, 13, center, default");

		lblValue = new JLabel("Value");
		lblValue.setFont(new Font("Dialog", Font.BOLD, 12));
		marketplacePanel.add(lblValue, "11, 13, center, default");
		JLabel lblLocation = new JLabel("Location");
		lblLocation.setFont(new Font("Dialog", Font.BOLD, 12));
		marketplacePanel.add(lblLocation, "3, 15, left, default");

		txtLocationSourceTypeMktpl = new JTextField();
		marketplacePanel.add(txtLocationSourceTypeMktpl, "5, 15, fill, default");
		txtLocationSourceTypeMktpl.setColumns(10);

		textFieldLocationSourceMktpl = new JTextField();
		marketplacePanel.add(textFieldLocationSourceMktpl, "7, 15, fill, default");
		textFieldLocationSourceMktpl.setColumns(10);

		txtLocationDestinationTypeMktpl = new JTextField();
		marketplacePanel.add(txtLocationDestinationTypeMktpl, "9, 15, fill, default");
		txtLocationDestinationTypeMktpl.setColumns(10);

		textFieldLocationDestinationMktpl = new JTextField();
		marketplacePanel.add(textFieldLocationDestinationMktpl, "11, 15, fill, default");
		textFieldLocationDestinationMktpl.setColumns(10);
		JLabel lblFormat = new JLabel("Format");
		lblFormat.setFont(new Font("Dialog", Font.BOLD, 12));
		marketplacePanel.add(lblFormat, "3, 17, left, default");

		txtFormatSourceTypeMktpl = new JTextField();
		marketplacePanel.add(txtFormatSourceTypeMktpl, "5, 17, fill, default");
		txtFormatSourceTypeMktpl.setColumns(10);
		textFieldFormatSourceMktpl = new JTextField();
		marketplacePanel.add(textFieldFormatSourceMktpl, "7, 17, fill, default");
		textFieldFormatSourceMktpl.setColumns(10);

		txtFormatDestinationTypeMktpl = new JTextField();
		marketplacePanel.add(txtFormatDestinationTypeMktpl, "9, 17, fill, default");
		txtFormatDestinationTypeMktpl.setColumns(10);
		textFieldFormatDestinationMktpl = new JTextField();
		marketplacePanel.add(textFieldFormatDestinationMktpl, "11, 17, fill, default");
		textFieldFormatDestinationMktpl.setColumns(10);

		lblBitsPerSecond = new JLabel("Cost");
		lblBitsPerSecond.setFont(new Font("Dialog", Font.BOLD, 12));
		marketplacePanel.add(lblBitsPerSecond, "3, 19, left, default");

		searchBtn = new JButton("Search"); 
		searchBtn.setFont(new Font("Dialog", Font.BOLD, 12));
		searchBtn.addActionListener(this);

		txtCostMethodMktpl = new JTextField();
		marketplacePanel.add(txtCostMethodMktpl, "5, 19, fill, default");
		txtCostMethodMktpl.setColumns(10);

		textFieldCostMktpl = new JTextField();
		marketplacePanel.add(textFieldCostMktpl, "7, 19, fill, default");
		textFieldCostMktpl.setColumns(10);
		JLabel lblAdvertisementId_1 = new JLabel("Advertisement ID");
		lblAdvertisementId_1.setFont(new Font("Dialog", Font.BOLD, 12));
		marketplacePanel.add(lblAdvertisementId_1, "3, 21, left, default");
		textFieldAdvID = new JTextField();
		marketplacePanel.add(textFieldAdvID, "5, 21, 3, 1, fill, default");
		textFieldAdvID.setColumns(10);

		marketplacePanel.add(searchBtn, "3, 23, fill, center");

		textAreaMktpl = new JTextArea();
		textAreaMktpl.setLineWrap(true);
		textAreaMktpl.setWrapStyleWord(true);
		textAreaMktpl.setEditable(false);
		JScrollPane scroll = new JScrollPane(textAreaMktpl);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		marketplacePanel.add(scroll, "3, 25, 11, 5, fill, fill");

		return marketplacePanel;
	}

	/**
	 *  Returns a JPanel of the Login screen
	 * @return JPanel
	 */
	public JPanel createPlannerPanel ()
	{
		JPanel plannerPanel = new JPanel();
		plannerPanel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("6px"),
				FormFactory.DEFAULT_COLSPEC,
				ColumnSpec.decode("max(59dlu;default):grow"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("max(41dlu;default):grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.MIN_COLSPEC,},
				new RowSpec[] {
				FormFactory.NARROW_LINE_GAP_ROWSPEC,
				FormFactory.NARROW_LINE_GAP_ROWSPEC,
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
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.NARROW_LINE_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.MIN_ROWSPEC,}));

		lblSearchTheMarketplace = new JLabel("Trigger Planner Service");
		lblSearchTheMarketplace.setFont(new Font("Dialog", Font.BOLD, 12));
		plannerPanel.add(lblSearchTheMarketplace, "3, 3, 9, 1, center, default");

		btnHomeMenuPlanner = new JButton("Main Menu\n");
		btnHomeMenuPlanner.setFont(new Font("Dialog", Font.BOLD, 12));
		btnHomeMenuPlanner.addActionListener(this);
		plannerPanel.add(btnHomeMenuPlanner, "13, 3, right, default");

		lblServerIpAddress = new JLabel("<html>Planner Address:</html>");
		lblServerIpAddress.setFont(new Font("Dialog", Font.BOLD, 12));
		plannerPanel.add(lblServerIpAddress, "3, 5, left, default");

		serverIPAddressTxtFldPlanner = new JTextField(Server.marketplaceRESTAPI);
		serverIPAddressTxtFldPlanner.setForeground(Color.BLACK);
		plannerPanel.add(serverIPAddressTxtFldPlanner, "5, 5, 7, 1, fill, default");
		serverIPAddressTxtFldPlanner.setColumns(10);

		lblClientDirections = new JLabel("<html><center>Only supports Planner Address in the form {IPv4 address}:{UDP port}</center><br>\n</html>");
		lblClientDirections.setFont(new Font("Dialog", Font.BOLD, 12));
		lblClientDirections.setForeground(Color.BLUE);
		plannerPanel.add(lblClientDirections, "3, 7, 11, 1, center, center");

		lblSource = new JLabel("Source");
		lblSource.setFont(new Font("Dialog", Font.BOLD, 12));
		plannerPanel.add(lblSource, "5, 9, 3, 1, center, default");

		lblDestination = new JLabel("Destination");
		lblDestination.setFont(new Font("Dialog", Font.BOLD, 12));
		plannerPanel.add(lblDestination, "9, 9, 3, 1, center, default");

		lblType = new JLabel("Type");
		lblType.setFont(new Font("Dialog", Font.BOLD, 12));
		plannerPanel.add(lblType, "5, 11, center, default");

		lblSourceValue = new JLabel("Value");
		lblSourceValue.setFont(new Font("Dialog", Font.BOLD, 12));
		plannerPanel.add(lblSourceValue, "7, 11, center, default");

		lblType_1 = new JLabel("Type");
		lblType_1.setFont(new Font("Dialog", Font.BOLD, 12));
		plannerPanel.add(lblType_1, "9, 11, center, default");

		lblValue = new JLabel("Value");
		lblValue.setFont(new Font("Dialog", Font.BOLD, 12));
		plannerPanel.add(lblValue, "11, 11, center, default");
		JLabel lblLocation = new JLabel("Location");
		lblLocation.setFont(new Font("Dialog", Font.BOLD, 12));
		plannerPanel.add(lblLocation, "3, 13, left, default");

		txtLocationSourceTypePlanner = new JTextField();
		plannerPanel.add(txtLocationSourceTypePlanner, "5, 13, fill, default");
		txtLocationSourceTypePlanner.setColumns(10);

		textFieldLocationSourcePlanner = new JTextField();
		plannerPanel.add(textFieldLocationSourcePlanner, "7, 13, fill, default");
		textFieldLocationSourcePlanner.setColumns(10);

		txtLocationDestinationTypePlanner = new JTextField();
		plannerPanel.add(txtLocationDestinationTypePlanner, "9, 13, fill, default");
		txtLocationDestinationTypePlanner.setColumns(10);

		textFieldLocationDestinationPlanner = new JTextField();
		plannerPanel.add(textFieldLocationDestinationPlanner, "11, 13, fill, default");
		textFieldLocationDestinationPlanner.setColumns(10);
		JLabel lblFormat = new JLabel("Format");
		lblFormat.setFont(new Font("Dialog", Font.BOLD, 12));
		plannerPanel.add(lblFormat, "3, 15, left, default");

		txtFormatSourceTypePlanner = new JTextField();
		plannerPanel.add(txtFormatSourceTypePlanner, "5, 15, fill, default");
		txtFormatSourceTypePlanner.setColumns(10);
		textFieldFormatSourcePlanner = new JTextField();
		plannerPanel.add(textFieldFormatSourcePlanner, "7, 15, fill, default");
		textFieldFormatSourcePlanner.setColumns(10);

		txtFormatDestinationTypePlanner = new JTextField();
		plannerPanel.add(txtFormatDestinationTypePlanner, "9, 15, fill, default");
		txtFormatDestinationTypePlanner.setColumns(10);
		textFieldFormatDestinationPlanner = new JTextField();
		plannerPanel.add(textFieldFormatDestinationPlanner, "11, 15, fill, default");
		textFieldFormatDestinationPlanner.setColumns(10);

		lblBitsPerSecond = new JLabel("Cost");
		lblBitsPerSecond.setFont(new Font("Dialog", Font.BOLD, 12));
		plannerPanel.add(lblBitsPerSecond, "3, 17, left, default");

		txtCostMethodPlanner = new JTextField();
		plannerPanel.add(txtCostMethodPlanner, "5, 17, fill, default");
		txtCostMethodPlanner.setColumns(10);

		textFieldCostPlanner = new JTextField();
		plannerPanel.add(textFieldCostPlanner, "7, 17, fill, default");
		textFieldCostPlanner.setColumns(10);

		searchBtnPlanner = new JButton("Search"); 
		searchBtnPlanner.setFont(new Font("Dialog", Font.BOLD, 12));
		searchBtnPlanner.addActionListener(this);

		plannerPanel.add(searchBtnPlanner, "3, 21, fill, center");

		btnClearPlanner = new JButton("Clear the form");
		btnClearPlanner.setFont(new Font("Dialog", Font.BOLD, 12));
		btnClearPlanner.addActionListener(this);
		plannerPanel.add(btnClearPlanner, "5, 21, 3, 1");

		textAreaPlanner = new JTextArea();
		textAreaPlanner.setLineWrap(true);
		textAreaPlanner.setWrapStyleWord(true);
		textAreaPlanner.setEditable(false);
		JScrollPane scroll = new JScrollPane(textAreaPlanner);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		plannerPanel.add(scroll, "3, 23, 11, 5, fill, fill");

		return plannerPanel;
	}

	/**
	 *  Returns a JPanel of the Login screen
	 * @return JPanel
	 */
	public JPanel createUsePlanePanel ()
	{
		JPanel usePlanePanel = new JPanel();
		usePlanePanel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("6px"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("min:grow"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				FormFactory.MIN_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(74dlu;pref):grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(30dlu;default):grow"),
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
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("26px"),
				FormFactory.UNRELATED_GAP_ROWSPEC,
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

		JLabel title = new JLabel("Trigger: Access Use Plane");
		title.setFont(new Font("Dialog", Font.BOLD, 12));
		usePlanePanel.add(title, "4, 3, 3, 1, center, default");

		btnHomeMenuUsePlane = new JButton("Back to Main Menu");
		btnHomeMenuUsePlane.setFont(new Font("Dialog", Font.BOLD, 12));
		btnHomeMenuUsePlane.addActionListener(this);
		usePlanePanel.add(btnHomeMenuUsePlane, "8, 3, 3, 1, right, default");

		lblUsePlaneMessage = new JLabel("");
		lblUsePlaneMessage.setForeground(Color.BLUE);
		lblUsePlaneMessage.setFont(new Font("Dialog", Font.BOLD, 12));
		usePlanePanel.add(lblUsePlaneMessage, "4, 5, 7, 1");

		JLabel lblChoicenetGatewayUse = new JLabel("ChoiceNet Gateway Use Plane Agent Type");
		usePlanePanel.add(lblChoicenetGatewayUse, "4, 7");

		txtUsePlaneGWType = new JTextField();
		usePlanePanel.add(txtUsePlaneGWType, "8, 7, fill, default");
		txtUsePlaneGWType.setColumns(10);

		JLabel lblChoicenetGatewayUse_1 = new JLabel("ChoiceNet Gateway Use Plane Agent Address");
		usePlanePanel.add(lblChoicenetGatewayUse_1, "4, 9");

		txtUsePlaneGWAddr = new JTextField();
		usePlanePanel.add(txtUsePlaneGWAddr, "8, 9, fill, default");
		txtUsePlaneGWAddr.setColumns(10);

		JLabel lblChuserAddressType = new JLabel("Chuser Use Plane Agent Type");
		usePlanePanel.add(lblChuserAddressType, "4, 11");

		txtUsePlaneClientType = new JTextField();
		usePlanePanel.add(txtUsePlaneClientType, "8, 11, fill, default");
		txtUsePlaneClientType.setColumns(10);

		JLabel lblChuserUsePlane = new JLabel("Chuser Use Plane Agent Address");
		usePlanePanel.add(lblChuserUsePlane, "4, 13");

		txtUsePlaneClientAddr = new JTextField();
		usePlanePanel.add(txtUsePlaneClientAddr, "8, 13, fill, default");
		txtUsePlaneClientAddr.setColumns(10);

		JLabel lblToken = new JLabel("Token");
		usePlanePanel.add(lblToken, "4, 15");

		txtUsePlaneToken = new JTextField();
		usePlanePanel.add(txtUsePlaneToken, "8, 15, fill, default");
		txtUsePlaneToken.setColumns(10);

		btnSendUsePlane = new JButton("Send Use Plane Signal");
		btnSendUsePlane.addActionListener(this);
		usePlanePanel.add(btnSendUsePlane, "4, 19, center, default");

		btnStoredUsePlane = new JButton("Stored Use Plane Information");
		btnStoredUsePlane.addActionListener(this);
		usePlanePanel.add(btnStoredUsePlane, "8, 19, center, default");

		textAreaUsePlane = new JTextArea();
		textAreaUsePlane.setLineWrap(true);
		textAreaUsePlane.setWrapStyleWord(true);
		JScrollPane scroll = new JScrollPane (textAreaUsePlane);
		usePlanePanel.add(scroll, "4, 21, 7, 9, fill, fill");

		return usePlanePanel;
	}

	public void showTestData ()
	{
		if(testOn)
		{
			String ipAddr = "127.0.0.1:4040";
			String addrType = "UDPv4";
			String target = "ABC Marketplace";
			String targetService = "Marketplace";
			String targetServiceName = "Advertisement Listing";
			String cMethod = "BitCoin:123456";
			String cValue = "USD 200";
			String fileNameAd = "/Users/rudechuk/service.xml";
			fileNameAd = "/Users/rudechuk/Documents/CSC/Research/JUNO/workspace/ChoiceNetArchitecture/test.xml";
			String srcLocationType = "IPv4,IPv4";
			String dstLocationType = "IPv4";
			String srcLocation = "A,B";
			String dstLocation = "B";


			txtRendezvousIPAddr.setText(ipAddr);
			txtRendezvousAddrType.setText(addrType);
			txtRendezvousTarget.setText(targetService);

			txtConsiderationAddrType.setText(addrType);
			txtConsiderationIPAddr.setText(ipAddr);
			txtConsiderationTarget.setText(target);
			txtConsiderationServiceName.setText(targetServiceName);
			txtConsiderationMethod.setText(cMethod);
			txtConsiderationValue.setText(cValue);

			txtListingAddrType.setText(addrType);
			txtListingIPAddr.setText(ipAddr);
			txtListingFileLocation.setText(fileNameAd);
			txtListingTarget.setText(target);

			serverIPAddressTxtFldPlanner.setText(ipAddr);

			txtLocationSourceTypeMktpl.setText(srcLocationType);
			txtLocationDestinationTypeMktpl.setText(dstLocationType);
			textFieldLocationSourceMktpl.setText(srcLocation);
			textFieldLocationDestinationMktpl.setText(dstLocation);
		}
		else
		{
			txtRendezvousAddrType.setText("");
			txtRendezvousIPAddr.setText("");
			txtRendezvousTarget.setText("");

			txtConsiderationAddrType.setText("");
			txtConsiderationIPAddr.setText("");
			txtConsiderationTarget.setText("");
			txtConsiderationServiceName.setText("");
			txtConsiderationMethod.setText("");
			txtConsiderationValue.setText("");

			txtListingAddrType.setText("");
			txtListingIPAddr.setText("");
			txtListingFileLocation.setText("");
			txtListingTarget.setText("");

			serverIPAddressTxtFldPlanner.setText("");

			txtLocationSourceTypeMktpl.setText("");
			txtLocationDestinationTypeMktpl.setText("");
			textFieldLocationSourceMktpl.setText("");
			textFieldLocationDestinationMktpl.setText("");
		}
	}
	public static void updateTextArea()
	{
		String message = Logger.display(0);
		System.out.println(activeCard);
		if(activeCard != null)
		{
			lblRendezvousMessage.setText("");
			lblConsiderationMessage.setText("");
			lblListingMessage.setText("");

			if(activeCard.equals("Menu"))
			{
				// Nothing
			}
			if(activeCard.equals("Rendezvous"))
			{
				textAreaRendezvous.setText(message);
				lblRendezvousMessage.setText(Server.systemMessage);
			}
			if(activeCard.equals("Consideration"))
			{
				textAreaConsideration.setText(message);
				System.out.println("Consideration: "+Server.systemMessage);
				lblConsiderationMessage.setText(Server.systemMessage);
			}
			if(activeCard.equals("Listing"))
			{
				textAreaListing.setText(message);
				lblListingMessage.setText(Server.systemMessage);
			}
			if(activeCard.equals("Marketplace"))
			{
				textAreaMktpl.setText(Server.systemMessage);
			}
			if(activeCard.equals("Planner"))
			{
				textAreaPlanner.setText(Server.systemMessage);
			}
		}
	}

	public static void main(String[] args) {
		/* Use an appropriate Look and Feel */
		// load the config

		String argument = "";
		if(args.length == 1)
		{
			argument = args[0];
		}
		if(argument.isEmpty() || argument == "")
		{
			System.out.println("\n\nYou may provide a configuration file to the providerGUI.jar as an argument");
			System.out.println("\nExample: java -jar providerGUI.jar <configName>");
			System.out.println("\n\nNo config file supplied, using default Transport Provider configuration.");
			server = new Server ("transport.properties");
		}
		else
		{
			server = new Server (argument);
		}
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
