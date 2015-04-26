/**
 * Proxy to ServerThread operations 
 * @author Robinson Udechukwu
 */

// Based on: http://www.cs.uic.edu/~troy/spring05/cs450/sockets/UDPServer.java

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Scanner;

import com.google.gson.Gson;


public class Server {
	private static int DEFAULT_SERVER_PORT = 4445;
	static int DEFAULT_CLIENT_PORT = 4000;
	Properties prop = new Properties();
	DatagramSocket serverSocket;
	byte[] receiveData = new byte[8192]; 
	static String providerAddress = "127.0.0.1";
	static String marketplaceRESTAPI = "";
	static String CONFIG_FILE = "server.properties";
	static String myName = "Unknown";
	static String myType = "Unknown";
	static String providerType = "Unknown";
	static String purchaseStatus = "None";
	static String purchaseStatusMessage = "None";
	static String acceptedConsideration = "None";
	static String availableConsideration = "None";
	static String runningMode = "Unknown";
	static String systemMessage;
	TransactionManager transcactionMgr = TransactionManager.getInstance();
	CouchDBOperations couchDBsocket = CouchDBOperations.getInstance();
	PurchaseManager purchaseMgr = PurchaseManager.getInstance();
	ChoiceNetLibrary cnLibrary = ChoiceNetLibrary.getInstance();
	TokenManager tokenMgr = TokenManager.getInstance();
	AdvertisementManager adMgr = AdvertisementManager.getInstance();

	public Server ()
	{
		initServer();
	}
	public Server (String newConfigFile)
	{
		CONFIG_FILE = newConfigFile;
		initServer();
	}

	public void initServer()
	{
		try 
		{
			prop.load(new FileInputStream(CONFIG_FILE));
			try
			{
				int portAddress = Integer.parseInt(prop.getProperty("myPortAddress"));
				DEFAULT_SERVER_PORT = portAddress;
			}
			catch (NumberFormatException e)
			{
				System.out.println("ERROR: Supplied server port address ("+prop.getProperty("myPortAddress")+") failed. System will use default "+DEFAULT_SERVER_PORT);
			}

			String clientIPAddress = prop.getProperty("providerAddress");
			providerAddress = clientIPAddress;
			marketplaceRESTAPI = prop.getProperty("marketplaceRESTAPI");
			String configmyName  = prop.getProperty("myName");
			if(!configmyName.isEmpty())
			{
				myName = configmyName; 
			}
			// providerType
			configmyName  = prop.getProperty("providerType");
			if(!configmyName.isEmpty())
			{
				providerType = configmyName; 
			}
			// myType
			myType = prop.getProperty("myType");

			// nodeAcceptableConsideration
			acceptedConsideration  = prop.getProperty("acceptedConsideration");

			// nodeAvailableConsideration
			availableConsideration  = prop.getProperty("availableConsideration");

			runningMode =  prop.getProperty("mode");
			if(providerType.equals("Planner"))
			{
				System.out.println("HERE WE ARE");
				//				Process p = Runtime.getRuntime().exec("java HelloWorld");
				//				p.waitFor();
				//				StringBuffer sb = new StringBuffer(); 
				//			    BufferedReader reader = 
				//			         new BufferedReader(new InputStreamReader(p.getInputStream()));
				//			 
				//			    String line = "";			
				//			    while ((line = reader.readLine())!= null) {
				//				sb.append(line + "\n");
				//			    }
				HelloWorld helloWorld = new HelloWorld();
			}

			serverSocket = new DatagramSocket(DEFAULT_SERVER_PORT);
			serverSocket.setReuseAddress(false);
			Runtime.getRuntime().addShutdownHook(new ServerShutdown());
		} catch (SocketException e) {
			System.out.println("Port "+DEFAULT_SERVER_PORT+" is occupied.");
			System.exit(0);
		} catch (FileNotFoundException e) {
			System.err.println(CONFIG_FILE+" not found, using default values!");
		} catch (IOException e) {
			e.printStackTrace();
			//		} catch (InterruptedException e) {
			//			// TODO Auto-generated catch block
			//			e.printStackTrace();
		}
	}

	public void startServer() throws Exception 
	{
		System.out.println ("Waiting for datagram packet on port "+DEFAULT_SERVER_PORT);
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length); 
		new ServerThread(serverSocket, providerAddress, receivePacket).start();
	}

	public void closeServer() throws Exception 
	{
		serverSocket.close();
	}


	public String sendPlannerRequest(String marketplaceAddr, String sourceLoc, String destinationLoc, String sourceFormat, String destinationFormat, String sourceLocType, 
			String destinationLocType, String sourceFormatType, String destinationFormatType, String cost, String cMethod, String adID) {
		String[] parsedContent;

		// parse the marketplace address
		parsedContent = marketplaceAddr.split(":");
		String marketplaceAddress = parsedContent[0];
		int marketplacePort = Integer.parseInt(parsedContent[1]);
		ChoiceNetMessageField[] dataPayload = createGeneralRequestPayload( sourceLoc,  destinationLoc,  sourceFormat,  destinationFormat,  sourceLocType, 
				 destinationLocType,  sourceFormatType,  destinationFormatType,  cost,  cMethod, adID);
		ChoiceNetMessageField data =  new ChoiceNetMessageField("Service Requirement", dataPayload, "");
		ChoiceNetMessageField payload[] = {data};
		// send the payload
		Packet packet = new Packet(PacketType.PLANNER_REQUEST,myName,"",myType, providerType,payload);
		System.out.println(packet);
		new ServerThread(serverSocket, marketplaceAddress, marketplacePort).sendRequest(packet);

		return "";
	}

	public String sendMarketplaceQuery(String marketplaceAddr, String sourceLoc, String destinationLoc, String sourceFormat, String destinationFormat, String sourceLocType, 
			String destinationLocType, String sourceFormatType, String destinationFormatType, String cost, String cMethod, String adID) {
		String[] parsedContent;
		// parse the marketplace address
		parsedContent = marketplaceAddr.split(":");
		String marketplaceAddress = parsedContent[0];
		int marketplacePort = Integer.parseInt(parsedContent[1]);

		// create search parameter for each valid field
		Packet packet;
		ChoiceNetMessageField[] dataPayload = createGeneralRequestPayload( sourceLoc,  destinationLoc,  sourceFormat,  destinationFormat,  sourceLocType, 
				 destinationLocType,  sourceFormatType,  destinationFormatType,  cost,  cMethod, adID);
		ChoiceNetMessageField data = new ChoiceNetMessageField("Search Parameter", dataPayload, "");
		ChoiceNetMessageField payload[] = {data};
		// send the payload
		packet = new Packet(PacketType.MARKETPLACE_QUERY,myName,"",myType, providerType,payload);
		System.out.println(packet);
		new ServerThread(serverSocket, marketplaceAddress, marketplacePort).sendRequest(packet);

		return null;
	}

	public ChoiceNetMessageField[] createGeneralRequestPayload(String sourceLoc, String destinationLoc, String sourceFormat, String destinationFormat, String sourceLocType, 
			String destinationLocType, String sourceFormatType, String destinationFormatType, String cost, String cMethod, String adID)
	{
		ArrayList<ChoiceNetMessageField> list = new ArrayList<ChoiceNetMessageField>();
		// determine the query property by content submitted in the location/format source/destination
		ChoiceNetMessageField searchedContent;
		if(!sourceLoc.isEmpty())
		{
			searchedContent = new ChoiceNetMessageField(""+RequestType.LOCATION_SRC, sourceLoc, "");
			list.add(searchedContent);
		}
		if(!destinationLoc.isEmpty())
		{
			searchedContent = new ChoiceNetMessageField(""+RequestType.LOCATION_DST, destinationLoc, "");
			list.add(searchedContent);
		}
		if(!sourceFormat.isEmpty())
		{
			searchedContent = new ChoiceNetMessageField(""+RequestType.FORMAT_SRC, sourceFormat, "");
			list.add(searchedContent);
		}
		if(!destinationFormat.isEmpty())
		{
			searchedContent = new ChoiceNetMessageField(""+RequestType.FORMAT_DST, destinationFormat, "");
			list.add(searchedContent);
		}
		
		if(!sourceLocType.isEmpty())
		{
			searchedContent = new ChoiceNetMessageField(""+RequestType.LOCATION_SRC_TYPE, sourceLocType, "");
			list.add(searchedContent);
		}
		if(!destinationLocType.isEmpty())
		{
			searchedContent = new ChoiceNetMessageField(""+RequestType.LOCATION_DST_TYPE, destinationLocType, "");
			list.add(searchedContent);
		}
		if(!sourceFormatType.isEmpty())
		{
			searchedContent = new ChoiceNetMessageField(""+RequestType.FORMAT_SRC_TYPE, sourceFormatType, "");
			list.add(searchedContent);
		}
		if(!destinationFormatType.isEmpty())
		{
			searchedContent = new ChoiceNetMessageField(""+RequestType.FORMAT_DST_TYPE, destinationFormatType, "");
			list.add(searchedContent);
		}

		// decode the cost type field
		if(!cMethod.isEmpty())
		{
			searchedContent = new ChoiceNetMessageField(""+RequestType.COST_TYPE, cMethod, "");
			list.add(searchedContent);
		}
		if(!cost.isEmpty())
		{
			searchedContent = new ChoiceNetMessageField(""+RequestType.COST, cost, "");
			list.add(searchedContent);
		}
		// decode the advertisement ID field
		if(!adID.isEmpty())
		{
			searchedContent = new ChoiceNetMessageField(""+RequestType.ADVERTISEMENT_ID, adID, "");
			list.add(searchedContent);
		}
		System.out.println(sourceLoc);
		// store the content of the query within the packet
		ChoiceNetMessageField dataPayload[] = new ChoiceNetMessageField[list.size()];
		for(int i=0;i<list.size();i++)
		{
			dataPayload[i] = list.get(i);
		}
		return dataPayload;
	}
	

	public void createMarketplaceDatabase(String marketplaceAddr)
	{
		Scanner sc;
		String response = "";
		// load couchdb view
		String content = "";
		try {
			sc = new Scanner(new FileReader("marketplace_view.json"));
			while (sc.hasNextLine()) {
				content += sc.nextLine();
			}
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		response = couchDBsocket.getRestInterface(marketplaceAddr);
		Logger.log("Requested CouchDB Database: "+marketplaceAddr);
		if(response.equals("404"))
		{
			// insert the database
			couchDBsocket.putRestInterface(marketplaceAddr,"");
			Logger.log("Insert CouchDB Database");
			//server.loadCouchDBView("/_design/marketplace", content);
			// load the View
			couchDBsocket.postRestInterface(marketplaceAddr,content);
			Logger.log("Load CouchDB View");
		}
		// Install a default listing service
		Advertisement ad1 = createListingServiceAdvertisement();
		adMgr.flush();
		adMgr.addAdvertisement(ad1);

		// Load the marketplace with advertisements
		for(Advertisement myAd : adMgr.getSingleInstance())
		{
			couchDBsocket.postRestInterface(marketplaceAddr, myAd);
			Logger.log("Included the following advertisement into CouchDB: \n"+myAd);
		}
	}
	
	public void createRangeDatabase(String marketplaceAddr)
	{
		String response = "";
		// load couchdb view
		response = couchDBsocket.getRestInterface(marketplaceAddr);
		Logger.log("Requested CouchDB Database: "+marketplaceAddr);
		if(response.equals("404"))
		{
			// insert the database
			couchDBsocket.putRestInterface(marketplaceAddr,"");
			Logger.log("Insert CouchDB Database");
		}
	}
	
	

	/**
	 * Default Listing Service
	 * @return
	 */
	public Advertisement createListingServiceAdvertisement()
	{
		Service listingService  = new Service("Advertisement Listing", "Listing", "List Service");
		long moreTime = 600000*2;// 10 minutes * 2
		int port = Integer.parseInt(getLocalIpAddress("Port"));
		Advertisement myAd = new Advertisement("Free", 0, myName, listingService, providerAddress, port, "UDPv4", System.currentTimeMillis()+moreTime, "UDPv4", providerAddress+":"+port);
		return myAd;
	}

	// this is a hack ... and informs the marketplace rather than the advertiser about 
	// advertisement purchase ... it should be the other way around. Requiring advertisement management
	// from the advertising provider
	public void sendMarketplacePurchase (String purchaseID)
	{
		System.out.println("\nPurchase Button selected");
		String query = "/_design/marketplace/_view/by_id?key=\""+purchaseID+"\"";
		String url = marketplaceRESTAPI+query;
		String response = couchDBsocket.getRestInterface(url);
		CouchDBResponse cResponse = CouchDBResponse.parseJson(response);
		Packet packet;
		String id = myName;
		//String data[] = {"Purchase_ID", purchaseID};

		ChoiceNetMessageField data = new ChoiceNetMessageField("Purchase_ID", purchaseID, "");
		ChoiceNetMessageField payload[] = {data};
		String advertiserAddress= "";
		int advertiserPort = -1;
		try
		{
			AdvertisementDisplay selectedAdv = cResponse.getRows().getFirst().getValue();
			advertiserAddress = selectedAdv.getAdvertiserAddress();
			advertiserPort = selectedAdv.getAdvertiserPortAddress();
			System.out.println(advertiserAddress+" "+advertiserPort);
			// send the payload
			//packet = new Packet(PacketType.USE_ATTEMPT,id,"",providerType);
			packet = new Packet(PacketType.USE_ATTEMPT,id,"",myType, providerType,payload);
			System.out.println(packet);
			new ServerThread(serverSocket, advertiserAddress, advertiserPort).sendRequest(packet);
		}
		catch(NoSuchElementException e)
		{
			System.out.println("No advertisement matches the requested product: "+purchaseID);
			String newData[] = {"Status", "Failed", "Reason", ""};
			String reason = "no advertisement matches the requested product: "+purchaseID;
			newData[3] = reason;
			data = new ChoiceNetMessageField("NACK Type", "Use Attempt", "");
			ChoiceNetMessageField data1 = new ChoiceNetMessageField("Operation Code", 1, "");
			ChoiceNetMessageField data2 = new ChoiceNetMessageField("Reason", reason, "");
			payload[0] = data;
			payload[1] = data1;
			payload[2] = data2;
			packet = new Packet(PacketType.NACK_USE_ATTEMPT,id,"",myType, providerType,payload);
			//			new ServerThread(serverSocket, "127.0.0.1", DEFAULT_SERVER_PORT).updatePurchaseStatus(packet, PacketType.USE_ATTEMPT);
		}
	}

	public void sendMarketplacePurgePurchase (String token)
	{
		System.out.println("\nRelease Resources Button selected");

		Packet packet;
		String id = myName;
		//		int seqNum = seqMgr.createAndSaveSequenceNumber();
		//String data[] = {"Purchase_ID", "", "Token", ""};
		ChoiceNetMessageField data = new ChoiceNetMessageField("Purchase_ID", "", "");
		ChoiceNetMessageField data1 = new ChoiceNetMessageField("Token", "", "");
		ChoiceNetMessageField payload[] = {data, data1};

		String reason = "";
		try
		{
			Purchase myPurchase = purchaseMgr.getPurchase(token);
			PurchaseType purchaseType = myPurchase.getStatus();
			String purchaseID = myPurchase.getPurchaseID();
			String advertiserAddress= myPurchase.getAdvertiserAddress();
			int advertiserPort = Integer.parseInt(myPurchase.getAdvertiserPortAddress());
			if(purchaseType == PurchaseType.ESTABLISHED)
			{

				data.setValue(purchaseID);
				data1.setValue(token);
				// send the payload
				packet = new Packet(PacketType.RELEASE_USE_ATTEMPT_RESOURCES,id,"",myType, providerType,payload);
				new ServerThread(serverSocket, advertiserAddress, advertiserPort).sendRequest(packet);
			}
			else
			{
				System.out.println("The purchase for this "+token+" has no associated flows");
				reason = "the purchase for this "+token+" has no associated flows";
				data = new ChoiceNetMessageField("NACK Type", "Use Attempt", "");
				data1 = new ChoiceNetMessageField("Operation Code", 2, "");
				ChoiceNetMessageField data2 = new ChoiceNetMessageField("Reason", reason, "");
				payload[0] = data;
				payload[1] = data1;
				payload[2] = data2;
				packet = new Packet(PacketType.NACK_RELEASE_USE_ATTEMPT_RESOURCES,id,"",myType, providerType,payload);
				//				new ServerThread(serverSocket, "127.0.0.1", DEFAULT_SERVER_PORT).updatePurchaseStatus(packet, PacketType.RELEASE_USE_ATTEMPT_RESOURCES);
			}
		}
		catch(NoSuchElementException e)
		{
			System.out.println("No purchase matches the requested token: "+token);
			reason = "no purchase matches the requested product: "+token;
			ChoiceNetMessageField data2 = new ChoiceNetMessageField("Reason", reason, "");
			data = new ChoiceNetMessageField("NACK Type", "Use Attempt", "");
			data1 = new ChoiceNetMessageField("Operation Code", 2, "");
			payload[0] = data;
			payload[1] = data1;
			payload[2] = data2;
			packet = new Packet(PacketType.NACK_RELEASE_USE_ATTEMPT_RESOURCES,id,"",myType, providerType,payload);
			//			new ServerThread(serverSocket, "127.0.0.1", DEFAULT_SERVER_PORT).updatePurchaseStatus(packet, PacketType.RELEASE_USE_ATTEMPT_RESOURCES);
		}
	}
	public String retrievePurchaseList()
	{
		String result = "ID\tStatus\tToken\n\n";
		for(Purchase myPurchase : purchaseMgr.getPurchase())
		{
			result += myPurchase.getPurchaseID()+"\t";
			result += myPurchase.getStatus()+"\t";
			result += myPurchase.getToken()+"\n";
		}

		return result;
	}

	public String retrieveActivePurchaseList()
	{
		String result = "ID\tStatus\tToken\n\n";
		for(Purchase myPurchase : purchaseMgr.getPurchase())
		{
			if(myPurchase.getStatus() == PurchaseType.ESTABLISHED)
			{
				result += myPurchase.getPurchaseID()+"\t";
				result += myPurchase.getStatus()+"\t";
				result += myPurchase.getToken()+"\n";
			}
		}

		return result;
	}

	/**
	 * Send Rendevouz Message to a specific IP Address and port
	 * @param target
	 * @param ipAddr
	 * @param port
	 */
	public void sendRendevouzMessage (String target, String ipAddr, int port)
	{
		ChoiceNetMessageField rendezvousTarget = new ChoiceNetMessageField("Rendezvous Target", target, "");
		ChoiceNetMessageField acceptedConsiderationFld = new ChoiceNetMessageField("Accepted Consideration", acceptedConsideration, "");
		ChoiceNetMessageField availableConsiderationFld = new ChoiceNetMessageField("Available Consideration", availableConsideration, "");
		ChoiceNetMessageField[] payload = {rendezvousTarget,acceptedConsiderationFld,availableConsiderationFld};
		Packet packet = new Packet(PacketType.RENDEZVOUS_REQUEST,myName,"",myType, providerType,payload);
		new ServerThread(serverSocket, ipAddr, port).sendRequest(packet);
	}


	/**
	 * Transfer Consideration Message to a specific IP Address and port
	 * @param target
	 * @param ipAddr
	 * @param port
	 */
	public void transferConsiderationMessage (String sName, String cTarget, 
			String exchangeMethod, String exchangeValue, String ipAddr, int port)
	{
		System.out.println("Discovered Service Name:"+sName);
		int transcactionNum = transcactionMgr.createAndSaveTransaction(cTarget, sName);
		ChoiceNetMessageField transactionNumber = new ChoiceNetMessageField("Transaction Number", transcactionNum, "");
		ChoiceNetMessageField considerationTarget = new ChoiceNetMessageField("Consideration Target", cTarget, "");
		ChoiceNetMessageField serviceName = new ChoiceNetMessageField("Service Name", sName, "");
		ChoiceNetMessageField considerationExchMethod = new ChoiceNetMessageField("Consideration Exchange Method", exchangeMethod, "");
		ChoiceNetMessageField considerationExchValue = new ChoiceNetMessageField("Consideration Exchange Value", exchangeValue, "");
		ChoiceNetMessageField[] payload = {transactionNumber,considerationTarget,serviceName,considerationExchMethod,considerationExchValue};
		Packet packet = new Packet(PacketType.TRANSFER_CONSIDERATION,myName,"",myType, providerType,payload);
		String pktXML = cnLibrary.createPacketXML(packet);
		new ServerThread(serverSocket, ipAddr, port).sendRequest(packet);
	}

	public boolean transferListingMessage (String fileName, String issuedBy, String tokenID, String ipAddr, int port)
	{
		// Create the Advertisement by parsing an XML file
		String advertisementXML = readFile(fileName);
		
		String message = "";
		if(advertisementXML != null)
		{
			// Extract the service name from the advertisement message to include in the token
			String myAdXML = "<![CDATA["+advertisementXML+"\n]]>";
			ChoiceNetMessageField advertisement = new ChoiceNetMessageField("Advertisement", myAdXML, "");
			int tID = Integer.parseInt(tokenID); 
			long creationTimeID = tokenMgr.getTokenCreationTime(tID);
			Token tempToken = TokenManager.getSingleToken(creationTimeID);
			System.out.println("==> "+System.currentTimeMillis());
			System.out.println("==> "+tempToken);
			if(tempToken!=null)
			{
				String issuedTo = tempToken.getIssuedTo();
				Long eTime = tempToken.getExpirationTime();
				String tokenType = tempToken.getServiceName();
				// check that service name matches with token's service name
				ChoiceNetMessageField token = cnLibrary.createToken(issuedTo, issuedBy,tokenType,eTime, false);
				ChoiceNetMessageField[] payload = {advertisement,token};
				// Save the Advertisement Attempt
				// NOTE: I am making the Expiration time dependent on the TOKEN
				ArrayList<Advertisement> submittedAds = cnLibrary.getAdvertisementsFromXML(fileName, "File");
				cnLibrary.storeAdvertisement(submittedAds);
//				Advertisement myAd = cnLibrary.extractAdvertisementContent(advertisement, eTime);
//				adMgr.addAdvertisement(myAd);

				Packet packet = new Packet(PacketType.LISTING_REQUEST,myName,"",myType, providerType,payload);
				new ServerThread(serverSocket, ipAddr, port).sendRequest(packet);
				return true;
			}
			else
			{
				message = "Token ID supplied does not match with any Token in the database";
			}
		}
		else
		{
			message = "Could not parse supplied advertisement specification";
		}

		Server.systemMessage = message;
		System.out.println("Here we are");
		System.out.println(message);
		return false;
	}

	// http://stackoverflow.com/a/326448
	private String readFile(String pathname) {
		String result = "";
		try
		{
			File file = new File(pathname);
			StringBuilder fileContents = new StringBuilder((int)file.length());
			Scanner scanner = new Scanner(file);
			String lineSeparator = System.getProperty("line.separator");

			try {
				while(scanner.hasNextLine()) {        
					fileContents.append(scanner.nextLine() + lineSeparator);
				}
				result = fileContents.toString();
			} finally {
				scanner.close();
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return result;
	}

	public void fireUsePlaneSignaling(String gwType, String gwAddr,	String clientType, String clientAddr, String myToken, String ipAddr, int port) 
	{
		ChoiceNetMessageField userInfo = new ChoiceNetMessageField(clientType, clientAddr, "");
		ChoiceNetMessageField token = new ChoiceNetMessageField("Token", myToken, "");
		ChoiceNetMessageField[] payload = {userInfo,token};
		Packet packet = new Packet(PacketType.USE_ATTEMPT ,myName,"",myType, providerType,payload);
		new ServerThread(serverSocket, ipAddr, port).sendRequest(packet);
	}

	public String getLocalIpAddress(String value) 
	{
		try {
			if(value.equals("IP"))
			{
				return InetAddress.getLocalHost().getHostAddress();
			}
			if(value.equals("Port"))
			{
				return Integer.toString(DEFAULT_SERVER_PORT);
			}
		} 
		catch (UnknownHostException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void printServerProperties ()
	{
		System.out.println("PORT: "+DEFAULT_SERVER_PORT);
		System.out.println("Provider Name: "+myName);
		System.out.println("Provider Type: "+myType);
		System.out.println("Provider Type: "+providerType);
		System.out.println("Accepted Consideration: "+acceptedConsideration);
		System.out.println("Available Consideration: "+availableConsideration);
		System.out.println();
	}

	//Runs when ctrl-c is pressed
	class ServerShutdown extends Thread {
		@Override
		public void run() {
			// Save database
			System.out.println();
			System.out.println("Server shutting down");
		}
	}

}
