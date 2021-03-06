
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
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
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

public class ChoiceNetSpeakerGUI implements ActionListener {
	static Server server;
	static int serverCount = 0;
	ChoiceNetLibrary cnLibrary = ChoiceNetLibrary.getInstance();
	OpenFlowFirewallMessageManager openFlowFirewallMsgLibrary = OpenFlowFirewallMessageManager.getInstance();

	boolean testOn = false; 

	JPanel cards; //a panel that uses CardLayout
	static String activeCard;
	final static String HOMEPANEL = "Card with Home Panel";
	final static String RENDEZVOUSPANEL = "Card with Rendezvous Panel";
	final static String SETTINGSPANEL = "Card with Settings Panel";
	final static String SENDCONSIDERATIONPANEL = "Card with Send Consideration Panel";
	final static String LISTINGPANEL = "Card with Listing Panel";
	final static String SEARCHMARKETPLACEPANEL = "Card with Marketplace Panel";
	final static String PLANNERPANEL = "Card with Planner Panel";
	final static String PROVISIONPANEL = "Card with Access Provisioning Panel";
	final static String PROVISIONMENUPANEL = "Card with Provisioning Menu Panel";
	final static String PAYMENTPANEL = "Card with Payment Panel";

	public static int deviceDatabaseSize = 0;

	static JFrame providerServer;
	static JPanel menuPanel, rendezvousPanel, considerationPanel, listingPanel, provisionMenuPanel, settingsPanel, marketplacePanel, plannerPanel, paymentPanel, provisionPanel;
	private JButton btnRendezvousMenu, btnRendezvousRequest,btnRendezvousKnownEntities,
	btnSendConsiderationMenu, btnSendConsiderationRequest, btnKnownEntities,
	btnProvisionMenu, btnSendListingRequest, btnListingAdvertisementBrowse, btnShowTokenId, 
	btnClearPlanner,searchBtnPlanner,
	btnSendProvision, btnStoredProvision, btnSearchMarketplace, btnAccessProvision,
	btnHomeMenuRendezvous, btnProvisionMenuListing, btnHomeMenuConsideration, btnProvisionMenuProvision, btnHomeMenuPlanner, btnHomeMenuProvisionMenu,
	btnPopulateMarketPl;
	private JTextField txtListingIPAddr, txtListingFileLocation, txtListingTokenID, txtListingTarget;
	private JTextField txtConsiderationServiceName, txtConsiderationTarget, txtConsiderationMethod, txtConsiderationValue, txtConsiderationIPAddr, txtConsiderationAddrType;
	private JTextField txtRendezvousIPAddr, txtRendezvousTarget;
	private JTextField serverIPAddressTxtFldPlanner, txtLocationSourceTypePlanner, textFieldLocationSourcePlanner, txtLocationDestinationTypePlanner, 
	textFieldLocationDestinationPlanner, txtFormatSourceTypePlanner, textFieldFormatSourcePlanner, txtFormatDestinationTypePlanner, 
	textFieldFormatDestinationPlanner, txtCostMethodPlanner, textFieldCostPlanner;
	private JFileChooser fc =  new JFileChooser();
	private static JTextArea textAreaRendezvous, textAreaConsideration, textAreaListing, textAreaMktpl, textAreaPlanner, textAreaProvision;
	ButtonGroup group;

	TokenManager tokenMgr = TokenManager.getInstance();
	DiscoveredEntitiesManager dEMgr = DiscoveredEntitiesManager.getInstance();
	private JCheckBox chckbxTurnTestData;
	private JTextField txtRendezvousAddrType;
	private JTextField txtListingAddrType;
	private static JLabel lblRendezvousMessage, lblConsiderationMessage, lblListingMessage, lblProvisionMessage;
	private JTextField txtProvisionGWType, txtProvisionGWAddr, txtProvisionTrafficProp, txtProvisionToken;


	private JLabel lblServerIpAddress, lblClientDirections,  lblSource, lblDestination, lblBitsPerSecond;
	private JTextField textFieldFormatSourceMktpl, textFieldFormatDestinationMktpl, textFieldCostMktpl, textFieldAdvID;
	JButton searchBtn;
	private JButton btnHomeMenuSearchMarketplace;
	private JLabel lblSearchTheMarketplace;
	private JLabel lblSourceValue;
	private JLabel lblValue;
	private JTextField txtLocationSourceTypeMktpl;
	private JLabel lblType;
	private JLabel lblType_1;
	private JTextField serverIPAddressTxtFldMktpl, textFieldLocationSourceMktpl, textFieldLocationDestinationMktpl, txtLocationDestinationTypeMktpl, 
	txtFormatSourceTypeMktpl, txtFormatDestinationTypeMktpl, txtCostMethodMktpl, textFieldProviderIDMktpl;
	private JButton btnUsePlannerService;
	private JLabel lblMarketplacenotifier;
	private JRadioButton rdbtnBitcoin;
	private JRadioButton rdbtnPayPal;
	private JRadioButton rdbtnCreditCard;
	private JLabel lblAccount;
	private JLabel lblPaymentMethod;
	private JTextField accountTextField;
	private JTextField amountTextField;
	private JLabel lblAmount;
	private JLabel lblUsd;
	private static JTextArea textAreaPayment;
	private JTextField paymentServiceNameTextField;
	private JLabel lblServiceName;
	private JTextField paymentURLTextField;
	private JLabel lblPaymentUrl;
	private JButton btnMakePayment;
	private JButton btnPaymentHistory;
	private JButton btnBackToSendConsideration;
	private JButton btnGoPaymentPanel;
	private JButton btnProvisionBrowse;
	private JButton btnProvisionShowTokenId;
	private JButton btnProvisionListing;
	private JButton btnProvisionService;
	private JLabel lblAdvertise;
	private JLabel lblRequestAccessWithin;
	private JLabel lblProviderId;

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
		ChoiceNetSpeakerGUI gui = new ChoiceNetSpeakerGUI();
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
		panelHome = createCustomerHomeMenuPanel();
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
//		if(Server.runningMode.equals("PlannerGUI"))
//		{
//			panelHome = createPlannerMenuPanel();
//			providerServer.setTitle("Planner GUI");
//		}
		if(Server.runningMode.equals("ProviderGUI"))
		{
			panelHome = createProviderHomeMenuPanel();
			providerServer.setTitle("Provider GUI");
		}
		//Create the panel that contains the "cards".
		cards = new JPanel(new CardLayout());
		cards.add(panelHome, HOMEPANEL);
		pane.setPreferredSize(new Dimension(700, 380));
		pane.add(cards, BorderLayout.CENTER);

		rendezvousPanel = createRendezvousPanel();
		cards.add(rendezvousPanel, RENDEZVOUSPANEL);

		considerationPanel = createSendConsiderationPanel();
		cards.add(considerationPanel, SENDCONSIDERATIONPANEL);

		// TODO: Here
		provisionMenuPanel = createPurchasePanel();
		cards.add(provisionMenuPanel, PROVISIONMENUPANEL);

		listingPanel = createListingPanel();
		cards.add(listingPanel, LISTINGPANEL);

		marketplacePanel = createMarketplacePanel();
		cards.add(marketplacePanel, SEARCHMARKETPLACEPANEL);

		plannerPanel = createPlannerPanel();
		cards.add(plannerPanel, PLANNERPANEL);

		provisionPanel = createProvisionPanel();
		cards.add(provisionPanel, PROVISIONPANEL);

		paymentPanel = createPaymentPanel();
		cards.add(paymentPanel, PAYMENTPANEL);

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
		if(e.getSource() == btnSendConsiderationMenu || e.getSource() == btnBackToSendConsideration)
		{
			activeCard = "Consideration";
			actionStateChanged(SENDCONSIDERATIONPANEL);
		}
		if(e.getSource() == btnProvisionMenu || e.getSource() == btnProvisionMenuListing || e.getSource() == btnProvisionMenuProvision)
		{
			activeCard = "Provision";
			actionStateChanged(PROVISIONMENUPANEL);
		}
		if(e.getSource() == btnProvisionListing)
		{
			activeCard = "Listing";
			actionStateChanged(LISTINGPANEL);
		}
		if(e.getSource() == btnProvisionService)
		{
			activeCard = "Provision";
			actionStateChanged(PROVISIONPANEL);
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
		if(e.getSource() == btnGoPaymentPanel)
		{
			activeCard = "Payment";
			actionStateChanged(PAYMENTPANEL);
		}
		if(e.getSource() == btnAccessProvision)
		{
			activeCard = "Provision";
			actionStateChanged(PROVISIONPANEL);
		}
		if(e.getSource() == btnHomeMenuRendezvous || e.getSource() == btnHomeMenuConsideration || e.getSource() == btnHomeMenuSearchMarketplace || e.getSource() ==  btnHomeMenuPlanner
				|| e.getSource() == btnHomeMenuProvisionMenu)
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
		if(e.getSource() == btnPopulateMarketPl)
		{
			System.out.println("Populate Marketplace");
			populateMarketplace();
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
				}
			}

		}

		if(e.getSource() == btnRendezvousKnownEntities)
		{
			message = dEMgr.printDiscoveredEntities();
			textAreaRendezvous.setText(message);
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
				//				addr = exchangeMethod.split(":");
				//				String exchangeType = addr[0];
				//				String exchangeValue = addr[1];
				//				System.out.println(exchangeValue+" not being used");
				String exchangeAmount = txtConsiderationValue.getText();
				String serviceName = txtConsiderationServiceName.getText();
				if(success)
				{
					server.transferConsiderationMessage(serviceName, target, exchangeMethod, exchangeAmount, ipAddr, port);
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
			int returnVal = fc.showOpenDialog(listingPanel);

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
			String method = txtCostMethodMktpl.getText().toString();
			String sourceFormat = textFieldFormatSourceMktpl.getText().toString();
			String srcFormatType = txtFormatSourceTypeMktpl.getText().toString();
			String destinationFormat = textFieldFormatDestinationMktpl.getText().toString();
			String dstFormatType = txtFormatDestinationTypeMktpl.getText().toString();
			String providerID = textFieldProviderIDMktpl.getText().toString();
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
					server.sendMarketplaceQuery(marketplaceAddr, sourceLoc, destinationLoc, sourceFormat, destinationFormat, srcLocType, dstLocType, srcFormatType, dstFormatType, cost, method, adID, providerID);
				}
				else
				{
					message = "<html><center>Error: Type should have same number of commas as their corresponding Value</center></html>";
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

			if(!marketplaceAddr.isEmpty() && marketplaceAddr.contains(":"))
			{
				server.sendPlannerRequest(marketplaceAddr, sourceLoc, destinationLoc, sourceFormat, destinationFormat, sourceLocType, destinationLocType, sourceFormatType, destinationFormatType, amount, method, "", "");
			}
		}
		// Payment Panel
		if(rdbtnPayPal.isSelected())
		{
			paymentServiceNameTextField.setEditable(false);
			//			paymentServiceNameTextField.setText("");
		}
		else
		{
			paymentServiceNameTextField.setEditable(true);
		}
		if(e.getSource() == btnMakePayment)
		{
			String url = paymentURLTextField.getText();
			String paymentMethod = getSelectedButtonText(group); 
			String account = accountTextField.getText();
			String amount = amountTextField.getText();
			String currency = "USD";
			String service = paymentServiceNameTextField.getText();
			String considerationConfirmation = server.makePayment(url, paymentMethod, account, amount, currency, service);
			message = considerationConfirmation;
			textAreaPayment.setText(message);
		}
		if(e.getSource() == btnPaymentHistory)
		{

			message = server.considerationMgr.printAvailableConsiderations();
			textAreaPayment.setText(message);
		}
		// Provisioning Panel
		if(e.getSource() ==  btnProvisionBrowse)
		{
			int returnVal = fc.showOpenDialog(provisionPanel);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				txtProvisionTrafficProp.setText(file.getAbsolutePath());
			}
		}
		if(e.getSource() == btnProvisionShowTokenId)
		{
			message = tokenMgr.printAvailableTokens();
			textAreaProvision.setText(message);
		}
		if(e.getSource() == btnSendProvision)
		{
			message = "";
			String gwType = txtProvisionGWType.getText();
			String gwAddr = txtProvisionGWAddr.getText();
			String trafficPropFile = txtProvisionTrafficProp.getText();
			String token = txtProvisionToken.getText();
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
					message = server.sendProvisionAttempt(trafficPropFile, token, ipAddr, port);
				}
			}
			else
			{
				message ="Unknown Address type supplied for the ChoiceNet Gateway: "+gwType+"\n"; 
				Server.systemMessage = message;
				Logger.log(message);
			}
			lblProvisionMessage.setText(message);
		}

		if(e.getSource() == btnStoredProvision)
		{
			message = openFlowFirewallMsgLibrary.printAvailableOpenFlowFirewallMessages();
			textAreaProvision.setText(message);
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

		btnSendConsiderationMenu = new JButton("Purchase Menu");
		btnSendConsiderationMenu.setFont(new Font("Dialog", Font.BOLD, 12));
		btnSendConsiderationMenu.addActionListener(this);
		providerHomeMenuPanel.add(btnSendConsiderationMenu, "7, 9");

		btnProvisionMenu = new JButton("Provisioning Menu");
		btnProvisionMenu.setFont(new Font("Dialog", Font.BOLD, 12));
		btnProvisionMenu.addActionListener(this);
		providerHomeMenuPanel.add(btnProvisionMenu, "9, 9");

		btnSearchMarketplace = new JButton("Search Marketplace");
		btnSearchMarketplace.setFont(new Font("Dialog", Font.BOLD, 12));
		providerHomeMenuPanel.add(btnSearchMarketplace, "3, 13");
		btnSearchMarketplace.addActionListener(this);

		chckbxTurnTestData = new JCheckBox("Turn Test Data On");
		chckbxTurnTestData.setFont(new Font("Dialog", Font.BOLD, 12));
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

		btnSendConsiderationMenu = new JButton("Purchase Menu");
		btnSendConsiderationMenu.setFont(new Font("Dialog", Font.BOLD, 12));
		btnSendConsiderationMenu.addActionListener(this);
		customerHomeMenuPanel.add(btnSendConsiderationMenu, "7, 9");

		btnProvisionMenu = new JButton("Provisioning Menu");
		btnProvisionMenu.setFont(new Font("Dialog", Font.BOLD, 12));
		btnProvisionMenu.addActionListener(this);
		customerHomeMenuPanel.add(btnProvisionMenu, "9, 9");

		chckbxTurnTestData = new JCheckBox("Turn Test Data On");
		chckbxTurnTestData.addActionListener(this);

		btnSearchMarketplace = new JButton("Search Marketplace");
		btnSearchMarketplace.setFont(new Font("Dialog", Font.BOLD, 12));
		customerHomeMenuPanel.add(btnSearchMarketplace, "3, 13");
		btnSearchMarketplace.addActionListener(this);

		//		btnAccessProvision = new JButton("Access Use Plane");
		//		btnAccessProvision.setEnabled(false);
		//		btnAccessProvision.addActionListener(this);
		//		btnAccessProvision.setFont(new Font("Dialog", Font.BOLD, 12));

		btnUsePlannerService = new JButton("Use Planner Service");
		btnUsePlannerService.setFont(new Font("Dialog", Font.BOLD, 12));
		btnUsePlannerService.addActionListener(this);
		customerHomeMenuPanel.add(btnUsePlannerService, "7, 13");
		//		customerHomeMenuPanel.add(btnAccessProvision, "9, 13, center, default");
		customerHomeMenuPanel.add(chckbxTurnTestData, "3, 17");

		return customerHomeMenuPanel;
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

		btnSendConsiderationMenu = new JButton("Purchase Menu");
		btnSendConsiderationMenu.setFont(new Font("Dialog", Font.BOLD, 12));
		btnSendConsiderationMenu.addActionListener(this);
		menuPanel.add(btnSendConsiderationMenu, "7, 9");

		btnProvisionMenu = new JButton("Provisioning Menu");
		btnProvisionMenu.setFont(new Font("Dialog", Font.BOLD, 12));
		btnProvisionMenu.addActionListener(this);
		menuPanel.add(btnProvisionMenu, "9, 9");

		chckbxTurnTestData = new JCheckBox("Turn Test Data On");
		chckbxTurnTestData.addActionListener(this);

		btnSearchMarketplace = new JButton("Search Marketplace");
		btnSearchMarketplace.setFont(new Font("Dialog", Font.BOLD, 12));
		menuPanel.add(btnSearchMarketplace, "3, 13");
		btnSearchMarketplace.addActionListener(this);

		//		btnAccessProvision = new JButton("Access Use Plane");
		//		btnAccessProvision.setEnabled(false);
		//		btnAccessProvision.addActionListener(this);
		//		btnAccessProvision.setFont(new Font("Dialog", Font.BOLD, 12));

		btnPopulateMarketPl = new JButton("Populate Marketplace");
		btnPopulateMarketPl.setFont(new Font("Dialog", Font.BOLD, 12));
		btnPopulateMarketPl.addActionListener(this);

		btnUsePlannerService = new JButton("Use Planner Service");
		btnUsePlannerService.setFont(new Font("Dialog", Font.BOLD, 12));
		btnUsePlannerService.addActionListener(this);
		menuPanel.add(btnUsePlannerService, "7, 13");


		//		menuPanel.add(btnAccessProvision, "9, 13, center, default");
		menuPanel.add(chckbxTurnTestData, "3, 17");
		menuPanel.add(btnPopulateMarketPl, "3, 19");

		return menuPanel;
	}

	/**
	 *  Returns a JPanel of the Login screen
	 * @return JPanel
	 */
	public JPanel createPurchasePanel ()
	{

		JPanel provisionMenuPanel = new JPanel();
		provisionMenuPanel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("6px"),
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
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

		JLabel title = new JLabel("Trigger: Provisioning Interaction");
		title.setFont(new Font("Dialog", Font.BOLD, 12));
		provisionMenuPanel.add(title, "4, 3, 5, 1, center, default");

		btnHomeMenuProvisionMenu = new JButton("Back to Main Menu");
		btnHomeMenuProvisionMenu.setFont(new Font("Dialog", Font.BOLD, 12));
		btnHomeMenuProvisionMenu.addActionListener(this);
		provisionMenuPanel.add(btnHomeMenuProvisionMenu, "10, 3, right, default");

		lblListingMessage = new JLabel("");
		lblListingMessage.setFont(new Font("Dialog", Font.BOLD, 12));
		lblListingMessage.setForeground(Color.BLUE);
		provisionMenuPanel.add(lblListingMessage, "4, 5, 7, 1");

		btnProvisionListing = new JButton("Advertise Service");
		btnProvisionListing.addActionListener(this);
		btnProvisionListing.setFont(new Font("Dialog", Font.BOLD, 12));

		btnProvisionService = new JButton("Activate Service");
		btnProvisionService.setFont(new Font("Dialog", Font.BOLD, 12));
		btnProvisionService.addActionListener(this);

		lblAdvertise = new JLabel("Advertise a service");
		lblAdvertise.setFont(new Font("Dialog", Font.BOLD, 12));
		// Only display if the mode is NOT ClientGUI
		if(!Server.runningMode.equals("ClientGUI"))
		{
			provisionMenuPanel.add(lblAdvertise, "8, 7, 4, 1");
			provisionMenuPanel.add(btnProvisionListing, "4, 7");
		}
		provisionMenuPanel.add(btnProvisionService, "4, 11");

		lblRequestAccessWithin = new JLabel("Request access within a service provider's use plane");
		lblRequestAccessWithin.setFont(new Font("Dialog", Font.BOLD, 12));
		provisionMenuPanel.add(lblRequestAccessWithin, "8, 11, 3, 1");

		return provisionMenuPanel;
	}

	/**
	 *  Returns a JPanel of the Login screen
	 * @return JPanel
	 */
	public JPanel createListingPanel ()
	{

		JPanel listingPanel = new JPanel();
		listingPanel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("6px"),
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
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

		JLabel title = new JLabel("Trigger: Listing Request");
		title.setFont(new Font("Dialog", Font.BOLD, 12));
		listingPanel.add(title, "4, 3, center, default");

		btnProvisionMenuListing = new JButton("Back to Provisioning Menu");
		btnProvisionMenuListing.setFont(new Font("Dialog", Font.BOLD, 12));
		btnProvisionMenuListing.addActionListener(this);
		listingPanel.add(btnProvisionMenuListing, "6, 3, 3, 1, right, default");

		btnSendListingRequest = new JButton("Send Listing Request");
		btnSendListingRequest.setFont(new Font("Dialog", Font.BOLD, 12));
		btnSendListingRequest.addActionListener(this);

		lblListingMessage = new JLabel("");
		lblListingMessage.setFont(new Font("Dialog", Font.BOLD, 12));
		lblListingMessage.setForeground(Color.BLUE);
		listingPanel.add(lblListingMessage, "4, 5, 5, 1");

		JLabel lblMarketplaceEconomyPlane = new JLabel("Marketplace Economy Plane Agent Address Type");
		lblMarketplaceEconomyPlane.setFont(new Font("Dialog", Font.BOLD, 12));
		listingPanel.add(lblMarketplaceEconomyPlane, "4, 9");

		txtListingAddrType = new JTextField();
		listingPanel.add(txtListingAddrType, "6, 9, fill, default");
		txtListingAddrType.setColumns(10);

		JLabel lblMarketplaceIpAddress = new JLabel("Marketplace Economy Plane Agent Address");
		lblMarketplaceIpAddress.setFont(new Font("Dialog", Font.BOLD, 12));
		listingPanel.add(lblMarketplaceIpAddress, "4, 11");

		txtListingIPAddr = new JTextField();
		listingPanel.add(txtListingIPAddr, "6, 11, fill, default");
		txtListingIPAddr.setColumns(10);

		JLabel lblListingServiceTarget = new JLabel("Listing Service Target");
		lblListingServiceTarget.setFont(new Font("Dialog", Font.BOLD, 12));
		listingPanel.add(lblListingServiceTarget, "4, 13");

		txtListingTarget = new JTextField();
		listingPanel.add(txtListingTarget, "6, 13, fill, default");
		txtListingTarget.setColumns(10);
		JLabel lblRendezvousTarget = new JLabel("Token ID");
		lblRendezvousTarget.setFont(new Font("Dialog", Font.BOLD, 12));
		listingPanel.add(lblRendezvousTarget, "4, 15");
		txtListingTokenID = new JTextField();
		listingPanel.add(txtListingTokenID, "6, 15, fill, default");
		txtListingTokenID.setColumns(10);

		JLabel lblServiceAdvertisementLocation = new JLabel("Advertisement Specification");
		lblServiceAdvertisementLocation.setFont(new Font("Dialog", Font.BOLD, 12));
		listingPanel.add(lblServiceAdvertisementLocation, "4, 17");

		txtListingFileLocation = new JTextField();
		txtListingFileLocation.setEditable(false);
		listingPanel.add(txtListingFileLocation, "6, 17, fill, default");
		txtListingFileLocation.setColumns(10);

		btnListingAdvertisementBrowse = new JButton("Browse");
		btnListingAdvertisementBrowse.setFont(new Font("Dialog", Font.BOLD, 12));
		btnListingAdvertisementBrowse.addActionListener(this);
		listingPanel.add(btnListingAdvertisementBrowse, "8, 17, left, default");
		listingPanel.add(btnSendListingRequest, "4, 21, center, default");

		btnShowTokenId = new JButton("Show Token ID");
		btnShowTokenId.addActionListener(this);
		btnShowTokenId.setFont(new Font("Dialog", Font.BOLD, 12));
		listingPanel.add(btnShowTokenId, "6, 21, center, default");

		textAreaListing = new JTextArea();
		textAreaListing.setLineWrap(true);
		textAreaListing.setWrapStyleWord(true);
		JScrollPane scroll = new JScrollPane (textAreaListing);
		listingPanel.add(scroll, "4, 23, 5, 7, fill, fill");

		return listingPanel;
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

		btnRendezvousKnownEntities = new JButton("Known Entities");
		btnRendezvousKnownEntities.addActionListener(this);
		btnRendezvousKnownEntities.setFont(new Font("Dialog", Font.BOLD, 12));
		rendezvousPanel.add(btnRendezvousKnownEntities, "5, 19, center, default");

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
		JLabel title = new JLabel("Trigger: Purchase Interaction");
		title.setFont(new Font("Dialog", Font.BOLD, 12));
		considerationPanel.add(title, "3, 3, 3, 1, center, default");

		btnHomeMenuConsideration = new JButton("Back to Main Menu");
		btnHomeMenuConsideration.setFont(new Font("Dialog", Font.BOLD, 12));
		btnHomeMenuConsideration.addActionListener(this);
		considerationPanel.add(btnHomeMenuConsideration, "7, 3");

		btnSendConsiderationRequest = new JButton("Request Token");
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
		considerationPanel.add(txtConsiderationAddrType, "5, 9, 3, 1, fill, default");
		txtConsiderationAddrType.setColumns(10);

		JLabel lblProviderIpAddress = new JLabel("Provider Economy Plane Agent Address");
		lblProviderIpAddress.setFont(new Font("Dialog", Font.BOLD, 12));
		considerationPanel.add(lblProviderIpAddress, "3, 11");

		txtConsiderationIPAddr = new JTextField();
		considerationPanel.add(txtConsiderationIPAddr, "5, 11, 3, 1, fill, default");
		txtConsiderationIPAddr.setColumns(10);

		JLabel lblConsiderationTarget = new JLabel("Consideration Target");
		lblConsiderationTarget.setFont(new Font("Dialog", Font.BOLD, 12));
		considerationPanel.add(lblConsiderationTarget, "3, 13");
		txtConsiderationTarget = new JTextField();
		considerationPanel.add(txtConsiderationTarget, "5, 13, 3, 1, fill, default");
		txtConsiderationTarget.setColumns(10);

		JLabel lblServiceAdvertisementName = new JLabel("Service Name");
		lblServiceAdvertisementName.setFont(new Font("Dialog", Font.BOLD, 12));
		considerationPanel.add(lblServiceAdvertisementName, "3, 15");
		txtConsiderationServiceName = new JTextField();
		considerationPanel.add(txtConsiderationServiceName, "5, 15, 3, 1, fill, default");
		txtConsiderationServiceName.setColumns(10);

		JLabel lblConsiderationMethod = new JLabel("Consideration Method");
		lblConsiderationMethod.setFont(new Font("Dialog", Font.BOLD, 12));
		considerationPanel.add(lblConsiderationMethod, "3, 17");

		txtConsiderationMethod = new JTextField();
		considerationPanel.add(txtConsiderationMethod, "5, 17, 3, 1, fill, default");
		txtConsiderationMethod.setColumns(10);

		JLabel lblConsiderationValue = new JLabel("Consideration Amount");
		lblConsiderationValue.setFont(new Font("Dialog", Font.BOLD, 12));
		considerationPanel.add(lblConsiderationValue, "3, 19");

		txtConsiderationValue = new JTextField();
		considerationPanel.add(txtConsiderationValue, "5, 19, 3, 1, fill, default");
		txtConsiderationValue.setColumns(10);
		considerationPanel.add(btnSendConsiderationRequest, "3, 21, center, default");

		btnKnownEntities = new JButton("Known Entities");
		btnKnownEntities.addActionListener(this);
		btnKnownEntities.setFont(new Font("Dialog", Font.BOLD, 12));
		considerationPanel.add(btnKnownEntities, "5, 21, center, default");

		btnGoPaymentPanel = new JButton("Make Payment");
		btnGoPaymentPanel.setFont(new Font("Dialog", Font.BOLD, 12));
		btnGoPaymentPanel.addActionListener(this);
		considerationPanel.add(btnGoPaymentPanel, "7, 21, center, default");

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
		
		lblProviderId = new JLabel("Provider ID");
		lblProviderId.setFont(new Font("Dialog", Font.BOLD, 12));
		marketplacePanel.add(lblProviderId, "3, 21, left, default");
		
		textFieldProviderIDMktpl = new JTextField();
		marketplacePanel.add(textFieldProviderIDMktpl, "5, 21, 3, 1, fill, default");
		textFieldProviderIDMktpl.setColumns(10);
		JLabel lblAdvertisementId_1 = new JLabel("Advertisement ID");
		lblAdvertisementId_1.setFont(new Font("Dialog", Font.BOLD, 12));
		marketplacePanel.add(lblAdvertisementId_1, "3, 23, left, default");
		textFieldAdvID = new JTextField();
		marketplacePanel.add(textFieldAdvID, "5, 23, 3, 1, fill, default");
		textFieldAdvID.setColumns(10);

		marketplacePanel.add(searchBtn, "3, 25, fill, center");

		textAreaMktpl = new JTextArea();
		textAreaMktpl.setLineWrap(true);
		textAreaMktpl.setWrapStyleWord(true);
		textAreaMktpl.setEditable(false);
		JScrollPane scroll = new JScrollPane(textAreaMktpl);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		marketplacePanel.add(scroll, "3, 27, 11, 5, fill, fill");

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
	public JPanel createPaymentPanel ()
	{
		JPanel paymentPanel = new JPanel();
		paymentPanel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("6px"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(35dlu;min):grow"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				FormFactory.MIN_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(66dlu;pref):grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(77dlu;default):grow"),
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
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
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
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("bottom:default"),}));

		JLabel title = new JLabel("Trigger: Make Payment Plane");
		title.setFont(new Font("Dialog", Font.BOLD, 12));
		paymentPanel.add(title, "4, 3, 8, 1, center, default");

		btnBackToSendConsideration = new JButton("Previous Panel");
		btnBackToSendConsideration.setFont(new Font("Dialog", Font.BOLD, 12));
		btnBackToSendConsideration.addActionListener(this);
		paymentPanel.add(btnBackToSendConsideration, "12, 3, right, default");

		lblPaymentUrl = new JLabel("Payment URL:");
		lblPaymentUrl.setFont(new Font("Dialog", Font.BOLD, 12));
		paymentPanel.add(lblPaymentUrl, "4, 7, left, default");

		paymentURLTextField = new JTextField( Server.purchasePortal);
		paymentPanel.add(paymentURLTextField, "6, 7, 7, 1, fill, default");
		paymentURLTextField.setColumns(10);

		lblPaymentMethod = new JLabel("Payment Method:");
		lblPaymentMethod.setFont(new Font("Dialog", Font.BOLD, 12));
		paymentPanel.add(lblPaymentMethod, "4, 11");

		rdbtnBitcoin = new JRadioButton("Coinbase: Testnet Bitcoin");// hardcoded the get to read Bitcoin rather than the name
		rdbtnBitcoin.setSelected(true);
		rdbtnBitcoin.setFont(new Font("Dialog", Font.BOLD, 12));
		rdbtnBitcoin.addActionListener(this);
		paymentPanel.add(rdbtnBitcoin, "6, 11");

		rdbtnCreditCard = new JRadioButton("Credit Card");
		rdbtnCreditCard.setEnabled(false);
		rdbtnCreditCard.setFont(new Font("Dialog", Font.BOLD, 12));
		rdbtnCreditCard.addActionListener(this);
		paymentPanel.add(rdbtnCreditCard, "10, 11");

		rdbtnPayPal = new JRadioButton("PayPal");
		rdbtnPayPal.setFont(new Font("Dialog", Font.BOLD, 12));
		rdbtnPayPal.addActionListener(this);
		paymentPanel.add(rdbtnPayPal, "12, 11");

		group = new ButtonGroup();
		group.add(rdbtnBitcoin);
		group.add(rdbtnCreditCard);
		group.add(rdbtnPayPal);

		lblAccount = new JLabel("Account:");
		lblAccount.setFont(new Font("Dialog", Font.BOLD, 12));
		paymentPanel.add(lblAccount, "4, 13, left, default");

		accountTextField = new JTextField();
		paymentPanel.add(accountTextField, "6, 13, 7, 1, fill, default");
		accountTextField.setColumns(10);

		lblAmount = new JLabel("Amount:");
		lblAmount.setFont(new Font("Dialog", Font.BOLD, 12));
		paymentPanel.add(lblAmount, "4, 15, left, default");

		amountTextField = new JTextField();
		paymentPanel.add(amountTextField, "6, 15, 3, 1, fill, default");
		amountTextField.setColumns(10);

		lblUsd = new JLabel("USD");
		lblUsd.setFont(new Font("Dialog", Font.BOLD, 12));
		paymentPanel.add(lblUsd, "10, 15, left, default");

		lblServiceName = new JLabel("Service Name:");
		lblServiceName.setFont(new Font("Dialog", Font.BOLD, 12));
		paymentPanel.add(lblServiceName, "4, 17, left, default");

		paymentServiceNameTextField = new JTextField();
		paymentPanel.add(paymentServiceNameTextField, "6, 17, 3, 1, fill, default");
		paymentServiceNameTextField.setColumns(10);

		btnMakePayment = new JButton("Make Payment");
		btnMakePayment.setFont(new Font("Dialog", Font.BOLD, 12));
		btnMakePayment.addActionListener(this);
		paymentPanel.add(btnMakePayment, "4, 19");

		btnPaymentHistory = new JButton("Payment History");
		btnPaymentHistory.setFont(new Font("Dialog", Font.BOLD, 12));
		btnPaymentHistory.addActionListener(this);
		paymentPanel.add(btnPaymentHistory, "6, 19");

		textAreaPayment = new JTextArea();

		textAreaPayment.setLineWrap(true);
		textAreaPayment.setWrapStyleWord(true);
		textAreaPayment.setEditable(false);
		JScrollPane scroll = new JScrollPane(textAreaPayment);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		paymentPanel.add(scroll, "4, 21, 9, 12, fill, fill");

		return paymentPanel;
	}


	/**
	 *  Returns a JPanel of the Login screen
	 * @return JPanel
	 */
	public JPanel createProvisionPanel ()
	{
		JPanel provisionPanel = new JPanel();
		provisionPanel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("6px"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("min:grow"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				FormFactory.MIN_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(93dlu;pref):grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(20dlu;default):grow"),
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

		JLabel title = new JLabel("Trigger: Access Provisioning");
		title.setFont(new Font("Dialog", Font.BOLD, 12));
		provisionPanel.add(title, "3, 3, 3, 1, center, default");

		btnProvisionMenuProvision = new JButton("Back to Provisioning Menu");
		btnProvisionMenuProvision.setFont(new Font("Dialog", Font.BOLD, 12));
		btnProvisionMenuProvision.addActionListener(this);
		provisionPanel.add(btnProvisionMenuProvision, "7, 3, 3, 1, right, default");

		lblProvisionMessage = new JLabel("");
		lblProvisionMessage.setForeground(Color.BLUE);
		lblProvisionMessage.setFont(new Font("Dialog", Font.BOLD, 12));
		provisionPanel.add(lblProvisionMessage, "3, 5, 7, 1");

		JLabel lblChoicenetGatewayUse = new JLabel("ChoiceNet Gateway Use Plane Agent Type");
		lblChoicenetGatewayUse.setFont(new Font("Dialog", Font.BOLD, 12));
		provisionPanel.add(lblChoicenetGatewayUse, "3, 7");

		txtProvisionGWType = new JTextField();
		provisionPanel.add(txtProvisionGWType, "7, 7, fill, default");
		txtProvisionGWType.setColumns(10);

		JLabel lblChoicenetGatewayUse_1 = new JLabel("ChoiceNet Gateway Use Plane Agent Address");
		lblChoicenetGatewayUse_1.setFont(new Font("Dialog", Font.BOLD, 12));
		provisionPanel.add(lblChoicenetGatewayUse_1, "3, 9");

		txtProvisionGWAddr = new JTextField();
		provisionPanel.add(txtProvisionGWAddr, "7, 9, fill, default");
		txtProvisionGWAddr.setColumns(10);

		JLabel lblToken = new JLabel("Token ID");
		lblToken.setFont(new Font("Dialog", Font.BOLD, 12));
		provisionPanel.add(lblToken, "3, 11");

		txtProvisionToken = new JTextField();
		provisionPanel.add(txtProvisionToken, "7, 11, fill, default");
		txtProvisionToken.setColumns(10);

		JLabel lblChuserProvision = new JLabel("Traffic Properties");
		lblChuserProvision.setFont(new Font("Dialog", Font.BOLD, 12));
		provisionPanel.add(lblChuserProvision, "3, 13");

		txtProvisionTrafficProp = new JTextField();
		txtProvisionTrafficProp.setEditable(false);
		provisionPanel.add(txtProvisionTrafficProp, "7, 13, fill, default");
		txtProvisionTrafficProp.setColumns(10);

		btnProvisionBrowse = new JButton("Browse");
		btnProvisionBrowse.setFont(new Font("Dialog", Font.BOLD, 12));
		btnProvisionBrowse.addActionListener(this);
		provisionPanel.add(btnProvisionBrowse, "9, 13, left, default");

		btnProvisionShowTokenId = new JButton("Show Token ID");
		btnProvisionShowTokenId.addActionListener(this);

		btnSendProvision = new JButton("Send Use Attempt");
		btnSendProvision.setFont(new Font("Dialog", Font.BOLD, 12));
		btnSendProvision.addActionListener(this);

		btnStoredProvision = new JButton("Stored Use Plane Information");
		btnStoredProvision.setFont(new Font("Dialog", Font.BOLD, 12));
		btnStoredProvision.addActionListener(this);
		provisionPanel.add(btnStoredProvision, "3, 19, center, default");
		provisionPanel.add(btnSendProvision, "7, 19, center, default");
		btnProvisionShowTokenId.setFont(new Font("Dialog", Font.BOLD, 12));
		provisionPanel.add(btnProvisionShowTokenId, "9, 19");

		textAreaProvision = new JTextArea();
		textAreaProvision.setLineWrap(true);
		textAreaProvision.setWrapStyleWord(true);
		JScrollPane scroll = new JScrollPane (textAreaProvision);
		provisionPanel.add(scroll, "3, 21, 7, 9, fill, fill");

		return provisionPanel;
	}

	public void showTestData ()
	{
		if(testOn)
		{
			String ipAddr = "127.0.0.1:4040";
			String addrType = "UDPv4";
			String target = "ABC Marketplace";
			String targetService = "Marketplace";
			String targetServiceName = "Listing Service";//"Advertisement Listing";
			String cMethod = "Bitcoin:555acf41a0c013059d00001b";
			String cValue = "20";
			String cUnit = "USD";
			String fileNameAd = "/Users/rudechuk/Documents/CSC/Research/JUNO/workspace/ChoiceNetArchitecture/test3.xml";
			String srcLocationType = "IPv4,IPv4";
			String dstLocationType = "IPv4";
			String srcLocation = "A,B";
			String dstLocation = "B";
			String reason = targetServiceName;
			String fileNameFirewall = "/Users/rudechuk/Documents/CSC/Research/JUNO/workspace/ChoiceNetArchitecture/firewallSettings.xml";

			txtRendezvousIPAddr.setText(ipAddr);
			txtRendezvousAddrType.setText(addrType);
			txtRendezvousTarget.setText(targetService);

			txtConsiderationAddrType.setText(addrType);
			txtConsiderationIPAddr.setText(ipAddr);
			txtConsiderationTarget.setText(target);
			txtConsiderationServiceName.setText(targetServiceName);
			txtConsiderationMethod.setText(cMethod);
			txtConsiderationValue.setText(cValue+" "+cUnit);

			txtListingAddrType.setText(addrType);
			txtListingIPAddr.setText(ipAddr);
			txtListingFileLocation.setText(fileNameAd);
			txtListingTarget.setText(target);

			serverIPAddressTxtFldMktpl.setText(ipAddr);
			txtLocationSourceTypeMktpl.setText(srcLocationType);
			txtLocationDestinationTypeMktpl.setText(dstLocationType);
			textFieldLocationSourceMktpl.setText(srcLocation);
			textFieldLocationDestinationMktpl.setText(dstLocation);

			ipAddr = "127.0.0.1:5050";
			srcLocationType = "IPv4";
			dstLocationType = "IPv4";
			srcLocation = "10.1.10.0/24";
			dstLocation = "10.3.1.0/24";
			serverIPAddressTxtFldPlanner.setText(ipAddr);
			txtLocationSourceTypePlanner.setText(srcLocationType);
			txtLocationDestinationTypePlanner.setText(dstLocationType);
			textFieldLocationSourcePlanner.setText(srcLocation);
			textFieldLocationDestinationPlanner.setText(dstLocation);

			paymentURLTextField.setText(Server.purchasePortal);
			accountTextField.setText("mknFpFW8x5pvLH8WSSLSKQRBrkPPiPPoxF");
			amountTextField.setText(cValue);
			paymentServiceNameTextField.setText(reason);

			txtProvisionGWAddr.setText(ipAddr);
			txtProvisionGWType.setText(addrType);
			txtProvisionTrafficProp.setText(fileNameFirewall);
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

			paymentURLTextField.setText("");
			accountTextField.setText("");
			amountTextField.setText("");
			paymentServiceNameTextField.setText("");

			txtProvisionGWAddr.setText("");
			txtProvisionGWType.setText("");
			txtProvisionTrafficProp.setText("");
		}
	}
	public static void updateTextArea(String systemMessage)
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
				lblRendezvousMessage.setText(systemMessage);
			}
			if(activeCard.equals("Consideration"))
			{
				textAreaConsideration.setText(message);
				lblConsiderationMessage.setText(systemMessage);
			}
			if(activeCard.equals("Listing"))
			{
				textAreaListing.setText(message);
				lblListingMessage.setText(systemMessage);
			}
			if(activeCard.equals("Marketplace"))
			{
				textAreaMktpl.setText(systemMessage);
			}
			if(activeCard.equals("Payment"))
			{
				textAreaPayment.setText(systemMessage);
			}
			if(activeCard.equals("Planner"))
			{
				textAreaPlanner.setText(systemMessage);
			}
			if(activeCard.equals("Provision"))
			{
				lblProvisionMessage.setText(systemMessage);
			}
		}
	}

	private void populateMarketplace()
	{
		boolean setting = testOn;
		testOn = true;
		showTestData();
		File folder = new File("generated/");
		String fileName;
		for (File fileEntry : folder.listFiles()) {
			fileName = fileEntry.getPath();
			btnSendConsiderationRequest.doClick();
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Token token = server.tokenMgr.getFirstTokenFromMapping();
			if(token != null)
			{
				String tokenID = ""+token.getId();
				txtListingTokenID.setText(tokenID);
				txtListingFileLocation.setText(fileName);
				btnSendListingRequest.doClick();
				System.out.println( "Filename: "+fileName+"\n"+
						"Token ID: "+tokenID);
			}
			else
			{
				System.out.println("Invalid token!");
			}
		}
		testOn = setting;
	}

	public String getSelectedButtonText(ButtonGroup buttonGroup) {
		for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
			AbstractButton button = buttons.nextElement();

			if (button.isSelected()) {
				if(button.getText().equals("Coinbase: Testnet Bitcoin"))
				{
					return "Bitcoin";
				}
				return button.getText();
			}
		}

		return null;
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
			System.out.println("\n\nYou may provide a configuration file to the choicenetSpeakerGUI.jar as an argument");
			System.out.println("\nExample: java -jar choicenetSpeakerGUI.jar <configName>");
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
		server.printServerProperties();
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
