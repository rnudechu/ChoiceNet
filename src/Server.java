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


	public String sendPlannerRequest(String marketplaceAddr, ServiceRequirement svcReq) {
		String[] parsedContent;

		// parse the marketplace address
		parsedContent = marketplaceAddr.split(":");
		String marketplaceAddress = parsedContent[0];
		int marketplacePort = Integer.parseInt(parsedContent[1]);

		Gson gson = new Gson();
		String json =  gson.toJson(svcReq);
		System.out.println(json);

		ChoiceNetMessageField data =  new ChoiceNetMessageField("Planner Service Request", json, "");
		ChoiceNetMessageField payload[] = {data};
		// send the payload
		Packet packet = new Packet(PacketType.PLANNER_REQUEST,myName,"",myType, providerType,payload);
		System.out.println(packet);
		new ServerThread(serverSocket, marketplaceAddress, marketplacePort).sendRequest(packet);

		return "";
	}

	public String sendMarketplaceQuery(String marketplaceAddr, String sourceLoc, String destinationLoc, String sourceFormat, String destinationFormat, String sourceLocType, 
			String destinationLocType, String sourceFormatType, String destinationFormatType, String cost, String adID) {
		String[] parsedContent;
		// parse the marketplace address
		parsedContent = marketplaceAddr.split(":");
		String marketplaceAddress = parsedContent[0];
		int marketplacePort = Integer.parseInt(parsedContent[1]);

		// create search parameter for each valid field

		Packet packet;
		ArrayList<ChoiceNetMessageField> list = new ArrayList<ChoiceNetMessageField>();
		// determine the query property by content submitted in the location/format source/destination
		ChoiceNetMessageField searchedContent;
		// determine the query property by content submitted in the location/format source/destination
		// check if any of the query parameters contain a comma if so multiple searches need to be performed
		String[] srcLocArr = sourceLoc.split(",");
		String[] dstLocArr = destinationLoc.split(",");
		String[] srcFormatArr = sourceFormat.split(",");
		String[] dstFormatArr = destinationFormat.split(",");

		int i = 0;
		int j = 0;
		int k = 0;
		int l = 0;
		boolean operate;
		// loop through the possible combinations between this four fields
		while(i<srcLocArr.length)
		{
			operate = false;
			searchedContent = determineQueryParameter(srcLocArr[i],dstLocArr[j],srcFormatArr[k],dstFormatArr[l],sourceLocType,destinationLocType,sourceFormatType,destinationFormatType);
			if(searchedContent!=null)
			{
				list.add(searchedContent);
			}
			if(j<dstLocArr.length-1 && k == srcFormatArr.length-1 && l == dstFormatArr.length-1 && operate==false)
			{
				operate = true;
				j++;
				k = 0;
				l = 0;
			}
			if(k<srcFormatArr.length-1 && l == dstFormatArr.length-1  && operate==false)
			{
				operate = true;
				k++;
				l = 0;
			}
			if(l<dstFormatArr.length-1  && operate==false)
			{
				operate = true;
				l++;
			}
			if(j == dstLocArr.length-1 && k == srcFormatArr.length-1 && l == dstFormatArr.length-1  && operate==false)
			{

				i++;
				j = 0;
				k = 0;
				l = 0;
			}
		}

		// decode the source field
		if(!cost.isEmpty())
		{
			searchedContent = new ChoiceNetMessageField(""+QueryType.COST, "[\""+cost+"\"]", "");
			list.add(searchedContent);
		}
		// decode the advertisement ID field
		if(!adID.isEmpty())
		{
			searchedContent = new ChoiceNetMessageField(""+QueryType.ADVERTISEMENT_ID, "[\""+adID+"\"]", "");
			list.add(searchedContent);
		}
		System.out.println(sourceLoc);
		// store the content of the query within the packet
		ChoiceNetMessageField dataPayload[] = new ChoiceNetMessageField[list.size()];
		for(i=0;i<list.size();i++)
		{
			dataPayload[i] = list.get(i);
		}
		ChoiceNetMessageField data = new ChoiceNetMessageField("Search Parameter", dataPayload, "");
		ChoiceNetMessageField payload[] = {data};
		// send the payload
		packet = new Packet(PacketType.MARKETPLACE_QUERY,myName,"",myType, providerType,payload);
		System.out.println(packet);
		new ServerThread(serverSocket, marketplaceAddress, marketplacePort).sendRequest(packet);

		return null;
	}


	/**
	 * 
	 * @param sourceLoc
	 * @param destinationLoc
	 * @param sourceFormat
	 * @param destinationFormat
	 * @return
	 */
	public ChoiceNetMessageField determineQueryParameter(String sourceLoc, String destinationLoc, String sourceFormat, String destinationFormat, 
			String sourceLocType, String destinationLocType, String sourceFormatType, String destinationFormatType)
	{
		// 1: (1,2,3,4)
		// 2: (1,2,3)
		// 3: (1,2,4)
		// 4: (1,3,4)
		// 5: (2,3,4)
		// 6: (1,2)
		// 7: (1,3)
		// 8: (1,4)
		// 9: (2,3)
		// 10: (2,4)
		// 11: (3,4)
		// 12: (1)
		// 13: (2)
		// 14: (3)
		// 15: (4)
		ChoiceNetMessageField searchedContent = null;
		String data = "";
		if(!sourceLoc.isEmpty() && !destinationLoc.isEmpty() && !sourceFormat.isEmpty() && !destinationFormat.isEmpty())
		{
			data = "[\""+sourceLoc+"\",\""+destinationLoc+"\",\""+sourceFormat+"\",\""+destinationFormat+"\"]";
			searchedContent = new ChoiceNetMessageField(""+QueryType.LOCATION_SRC_DST_FORMAT_SRC_DST, data, "");
			return searchedContent;
		}
		// Triplets
		if(!sourceLoc.isEmpty() && !destinationLoc.isEmpty() && !sourceFormat.isEmpty())
		{
			data = "[\""+sourceLoc+"\",\""+destinationLoc+"\",\""+sourceFormat+"\"]";
			searchedContent = new ChoiceNetMessageField(""+QueryType.LOCATION_SRC_DST_FORMAT_SRC, data, "");
			return searchedContent;
		}
		if(!sourceLoc.isEmpty() && !destinationLoc.isEmpty() && !destinationFormat.isEmpty())
		{
			data = "[\""+sourceLoc+"\",\""+destinationLoc+"\",\""+destinationFormat+"\"]";
			searchedContent = new ChoiceNetMessageField(""+QueryType.LOCATION_SRC_DST_FORMAT_DST, data, "");
			return searchedContent;
		}
		if(!sourceLoc.isEmpty() && !sourceFormat.isEmpty() && !destinationFormat.isEmpty())
		{
			data = "[\""+sourceLoc+"\",\""+sourceFormat+"\",\""+destinationFormat+"\"]";
			searchedContent = new ChoiceNetMessageField(""+QueryType.LOCATION_SRC_FORMAT_SRC_DST, data, "");
			return searchedContent;
		}
		if(!destinationLoc.isEmpty() && !sourceFormat.isEmpty() && !destinationFormat.isEmpty())
		{
			data = "[\""+destinationLoc+"\",\""+sourceFormat+"\",\""+destinationFormat+"\"]";
			searchedContent = new ChoiceNetMessageField(""+QueryType.LOCATION_DST_FORMAT_SRC_DST, data, "");
			return searchedContent;
		}
		// Pair Case
		if(!sourceLoc.isEmpty() && !destinationLoc.isEmpty())
		{
			data = "[\""+sourceLoc+"\",\""+destinationLoc+"\"]";
			searchedContent = new ChoiceNetMessageField(""+QueryType.LOCATION_SRC_DST, data, "");
			return searchedContent;
		}
		if(!sourceLoc.isEmpty() && !sourceFormat.isEmpty())
		{
			data = "[\""+sourceLoc+"\",\""+sourceLocType+"\",\""+sourceFormat+"\",\""+sourceFormatType+"\"]";
			searchedContent = new ChoiceNetMessageField(""+QueryType.LOCATION_SRC_FORMAT_SRC, data, "");
			return searchedContent;
		}
		if(!sourceLoc.isEmpty() && !destinationFormat.isEmpty())
		{
			data = "[\""+sourceLoc+"\",\""+destinationFormat+"\"]";
			searchedContent = new ChoiceNetMessageField(""+QueryType.LOCATION_SRC_FORMAT_DST, data, "");
			return searchedContent;
		}
		if(destinationLoc.isEmpty() && !sourceFormat.isEmpty())
		{
			data = "[\""+destinationLoc+"\",\""+sourceFormat+"\"]";
			searchedContent = new ChoiceNetMessageField(""+QueryType.LOCATION_DST_FORMAT_SRC, data, "");
			return searchedContent;
		}
		if(!destinationLoc.isEmpty() &&  !destinationFormat.isEmpty())
		{
			data = "[\""+destinationLoc+"\",\""+destinationFormat+"\"]";
			searchedContent = new ChoiceNetMessageField(""+QueryType.LOCATION_DST_FORMAT_DST, data, "");
			return searchedContent;
		}
		if(!sourceFormat.isEmpty() && !destinationFormat.isEmpty())
		{
			data = "[\""+sourceFormat+"\",\""+destinationFormat+"\"]";
			searchedContent = new ChoiceNetMessageField(""+QueryType.FORMAT_SRC_DST, data, "");
			return searchedContent;
		}
		// Single case
		if(!sourceLoc.isEmpty())
		{
			data = "[\""+sourceLoc+"\",\""+sourceLocType+"\"]";
			searchedContent = new ChoiceNetMessageField(""+QueryType.LOCATION_SRC, data, "");
			return searchedContent;
		}
		if(!destinationLoc.isEmpty())
		{
			data = "[\""+destinationLoc+"\",\""+destinationLocType+"\"]";
			searchedContent = new ChoiceNetMessageField(""+QueryType.LOCATION_DST, data, "");
			return searchedContent;
		}
		if(!sourceFormat.isEmpty())
		{
			data = "[\""+sourceFormat+"\",\""+sourceFormatType+"\"]";
			searchedContent = new ChoiceNetMessageField(""+QueryType.FORMAT_SRC, data, "");
			return searchedContent;
		}
		if(!destinationFormat.isEmpty())
		{
			data = "[\""+destinationFormat+"\",\""+destinationFormatType+"\"]";
			searchedContent = new ChoiceNetMessageField(""+QueryType.FORMAT_DST, data, "");
			return searchedContent;
		}

		return searchedContent;
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
