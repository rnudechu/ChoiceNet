/**
 * @author Rob Udechukwu
 *  Handles the different stages of a client and sensor device interaction with the server.
 *  Performs all the Socket operations
 */


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

public class ServerThread extends Thread {
	private DatagramSocket socket = null;

	ObjectOutputStream outToClient;
	ObjectInputStream inFromClient;
	ByteArrayOutputStream bos;
	ByteArrayInputStream bis;
	DatagramPacket sendPacket;
	InetAddress clientIPAddress; // changes based on the recieved packet address
	InetAddress serverIPAddress;
	int clientPort = -1;
	String serverMACAddress;
	ChoiceNetMessageField [] data;
	byte[] sendData  = new byte[2048];
	DatagramPacket receivePacket;
	String result;
	String resultValue;
	String myName = Server.myName;
	String myType = Server.myType;
	String providerType = Server.providerType;
	String acceptedConsideration = Server.acceptedConsideration;
	String availableConsideration = Server.availableConsideration;
	String jsonSeparator = "\n\n";

	AdvertisementManager adMgr = AdvertisementManager.getInstance();
	TransactionManager transcactionMgr = TransactionManager.getInstance();
	TokenManager tokenMgr = TokenManager.getInstance();
	CouchDBOperations couchDBsocket = CouchDBOperations.getInstance();
	PurchaseManager purchaseMgr = PurchaseManager.getInstance();
	ChoiceNetLibrary cnLibrary = ChoiceNetLibrary.getInstance();
	DiscoveredEntitiesManager dEMgr = DiscoveredEntitiesManager.getInstance();
	ServiceManager serviceMgr = ServiceManager.getInstance();

	/**
	 * 
	 * @param socket: serverSocket
	 * @param clientBroadcastAddress: address given to the server for the network which the sensor nodes lie on
	 * @param packet: received packet
	 */
	public ServerThread(DatagramSocket socket, String clientBroadcastAddress, DatagramPacket packet) {
		super("ServerThread");

		Logger.activate();
		sendData = new byte[2048];
		this.socket = socket;
		this.receivePacket = packet;
		try {
			socket.setBroadcast(true);
			socket.setReuseAddress(false);
			socket.receive(receivePacket);

			clientIPAddress = receivePacket.getAddress(); 
			clientPort = receivePacket.getPort();
			System.out.println ("**** Client Info Begins****");
			System.out.println ("From: " + clientIPAddress + ":" + clientPort);
			System.out.println ("**** Client Info Ends ****");
		}
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}

	}
	public ServerThread(DatagramSocket socket, String ipAddress, int port) {
		sendData = new byte[2048];
		this.socket = socket;
		Logger.activate();
		try {
			bos = new ByteArrayOutputStream();
			outToClient = new ObjectOutputStream(bos);
			try {
				clientIPAddress = InetAddress.getByName(ipAddress);

			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			clientPort = port;
			System.out.println ("**** Client Info Begins****");
			System.out.println ("To: " + clientIPAddress + ":" + clientPort);
			System.out.println ("**** Client Info Ends ****");
		}
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}

	}
	/**
	 * 	Send
	 * This function simply takes in a Packet object to be sent across the network.  Its destination is encoded into the Packet object.
	 * This function will send the Packet out the relevant socket.
	 */
	private int send (Packet packet) {
		try 
		{
			//			outToClient.reset();
			String pktXML = cnLibrary.createPacketXML(packet);
			System.out.println("Sending XML:\n"+pktXML);
			outToClient.writeObject(pktXML);
			outToClient.flush();
			sendData = bos.toByteArray();
			System.out.println("Size of payload: "+sendData.length);
			sendPacket = new DatagramPacket(sendData, sendData.length, clientIPAddress, clientPort); 
			socket.send(sendPacket);
			Logger.log("Following information is being to sent to "+clientIPAddress+":"+clientPort+"\n"+packet.toString());
		} 
		catch (IOException e) {
			e.printStackTrace();
			Logger.log("Failed to send message!");
			System.exit(1);
		}

		return 0;
	}

	public void run() {
		if(socket!=null)
		{
			try
			{
				System.out.println("Server Thread begins");
				// should be removed for the client ... 
				bos = new ByteArrayOutputStream();
				outToClient = new ObjectOutputStream(bos);
				//outToClient.flush();
				//bis = new ByteArrayInputStream(receivePacket.getData());
				bis = new ByteArrayInputStream(receivePacket.getData(), receivePacket.getOffset(),receivePacket.getLength());
				inFromClient = new ObjectInputStream(new BufferedInputStream(bis));

				while(!socket.isClosed())
				{
					try
					{
						// I can convert it to a XML object at this step 
						// Depending on the packet type received the server attempts to process that packet 
						String pktXML = (String) inFromClient.readObject();
						System.out.println("XML received :\n"+pktXML);
						Packet packet = cnLibrary.convertXMLtoPacket(pktXML);

						if(packet != null)
						{
							PacketType packetType = packet.getActionCode();
							Logger.log("Received Packet Type is "+packetType);
							System.out.println("XML for "+packetType+":\n"+pktXML);
							// Received a Rendezvous Message
							if(packetType == PacketType.RENDEZVOUS_REQUEST)
							{
								checkRendezvousRequest(packet);
							}

							// Received a Rendezvous Message
							if(packetType == PacketType.RENDEZVOUS_RESPONSE)
							{
								checkRendezvousResponse(packet);
							}
							// Received a Transfer Consideration Message
							if(packetType == PacketType.TRANSFER_CONSIDERATION)
							{
								respondToTransferConsiderationRequest(packet);
							}
							// Received a Request to List Message
							if(packetType == PacketType.LISTING_REQUEST)
							{
								respondToListingRequest(packet);
							}
							// Received a ACK Consideration Message
							if(packetType == PacketType.ACK_AND_SEND_TOKEN)
							{
								respondToConsiderationACK(packet);
							}
							// Store Listing Confirmation
							if(packetType == PacketType.LISTING_CONFIRMATION)
							{
								storeListingConfirmation(packet);
							}
							// Store Listing Confirmation
							if(packetType == PacketType.NACK)
							{
								logNegativeAck(packet);
							}
							// Marketplace Query
							if(packetType == PacketType.MARKETPLACE_QUERY)
							{
								respondToMarketplaceQuery(packet);
							}
							// Marketplace Query
							if(packetType == PacketType.MARKETPLACE_RESPONSE)
							{
								respondToMarketplaceResponse(packet);
							}
							// Marketplace Query
							if(packetType == PacketType.PLANNER_REQUEST)
							{
								respondToPlannerRequest(packet);
							}
							// Marketplace Query
							if(packetType == PacketType.PLANNER_RESPONSE)
							{
								respondToPlannerResponse(packet);
							}


							// Received a purchase request
							if(packetType == PacketType.USE_ATTEMPT)
							{
								respondToUseAttempt(packet);
							}
							// Received a purchase request
							if(packetType == PacketType.USE_PLANE_SIGNAL)
							{
								respondToUsePlaneSignaling(packet);
							}
							// Received a purchase ACK
							if(packetType == PacketType.ACK_USE_ATTEMPT)
							{
								logUseAttemptAck(packet);
							}
							// Received a purchase NACK
							if(packetType == PacketType.NACK_USE_ATTEMPT)
							{
								//									updatePurchaseStatus(packet, PacketType.USE_ATTEMPT);
							}
							// Received a purchase request
							if(packetType == PacketType.RELEASE_USE_ATTEMPT_RESOURCES)
							{
								//									respondToPurgePurchaseRequest(packet);
							}
							// Received a purchase ACK
							if(packetType == PacketType.ACK_RELEASE_USE_ATTEMPT_RESOURCES)
							{
								//									updatePurchaseStatus(packet, PacketType.RELEASE_USE_ATTEMPT_RESOURCES);
							}
							// Received a purchase NACK
							if(packetType == PacketType.NACK_RELEASE_USE_ATTEMPT_RESOURCES)
							{
								//									updatePurchaseStatus(packet, PacketType.RELEASE_USE_ATTEMPT_RESOURCES);
							}
							if(packetType == PacketType.USE_ATTEMPT_STATUS)
							{
								System.out.println("Updating purchase status");
							}
						}
						else
						{
							//System.err.println("No packet received");
						}
						// prevent other clients from running this logger
						if(Server.runningMode.equals("ProviderGUI"))
						{
							ProviderGUI.updateTextArea(); // should be removed for the client
						}
					}
					catch (ClassNotFoundException e) {
						System.err.println("Class Not found Exception was thrown while reading the client socket");
					} 
					catch (SocketTimeoutException e) {
						System.err.println("Currently no object to read from new client socket!");
					}
					catch(ClassCastException e)
					{
						System.err.println(e.getMessage());
						//						String packet;
						//						try {
						//							packet = (String) inFromClient.readObject();
						//							System.out.println("String value="+packet);
						//						} catch (ClassNotFoundException e1) {
						//							e1.printStackTrace();
						//						}
					}
				}
			}
			catch (IOException e) {
				System.out.println("Packet has been properly processed.");
			}
		}
		else
		{
			// if socket is null; should close all streams 
			System.out.println("No socket is open!");
		}
	}
	/**
	 * Any to Planner Provider
	 * Respond To Planner Request
	 * @param packet
	 */
	private void respondToPlannerRequest(Packet packet) {
		// TODO Auto-generated method stub
		// Currently no planner code to supply here :(
		// Send response back for testing
		ChoiceNetMessageField[] payload = (ChoiceNetMessageField[]) packet.getMessageSpecific().getValue();
		ChoiceNetMessageField reqParameters = payload[0];
		System.out.println("Parameter: "+reqParameters);
		String content = (String) reqParameters.getValue();
		try {
			Process p = Runtime.getRuntime().exec("./localchoicenet");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Content: "+content);
		String message = "";
		boolean testing = true;
		if(testing)
		{
			message = "Dummy Response sent to client requesting planner service\n\nContent: "+content;
			ChoiceNetMessageField resultsField = new ChoiceNetMessageField("Results", message, "");
			ChoiceNetMessageField[] myPayload = {resultsField};
			packet = new Packet(PacketType.PLANNER_RESPONSE,myName,"",myType, providerType,myPayload);
		}
		else
		{
			message = "No Planner to handle request";
			ChoiceNetMessageField type = new ChoiceNetMessageField("NACK Type", "Planner Request", "");
			ChoiceNetMessageField opCode = new ChoiceNetMessageField("Operation Code", 1, "");
			ChoiceNetMessageField reason = new ChoiceNetMessageField("Reason", message, "");
			ChoiceNetMessageField[] myPayload = {type,opCode,reason};
			packet = new Packet(PacketType.NACK,myName,"",myType, providerType,myPayload);
		}
		System.out.println(message);

		send(packet);
	}
	/**
	 * Planner Provider to Any
	 * Respond To Planner Response
	 * @param packet
	 */
	private void respondToPlannerResponse(Packet packet) {
		// TODO Auto-generated method stub
		System.out.println("Recieved a Planner Respone");
		// Print the content to GUI
		ChoiceNetMessageField[] payload = (ChoiceNetMessageField[]) packet.getMessageSpecific().getValue();
		String results = (String) payload[0].getValue();
		String message = "";
		if(!Server.runningMode.equals("standalone"))
		{
			//			CouchDBResponse cResponse;
			//			AdvertisementDisplay myAd;
			//			cResponse = CouchDBResponse.parseJson(results);
			//			for(CouchDBContainer currentAdv : cResponse.getRows())
			//			{
			//				myAd = currentAdv.getValue();
			//				message += "ID: "+myAd.getId()+"\n";
			//				message += "\tDescription: "+myAd.getDescription()+"\n";
			//				message += "\tCost: "+myAd.getConsiderationMethod()+":"+myAd.getConsiderationValue()+"\n";
			//				message += "\tLocation Source : "+myAd.getSrcLocationAddrScheme()+":"+myAd.getSrcLocationAddrValue()+"\n";
			//				message += "\tLocation Destination: "+myAd.getDstLocationAddrScheme()+":"+myAd.getDstLocationAddrValue()+"\n";
			//				message += "\tFormat Source: "+myAd.getSrcFormatScheme()+":"+myAd.getSrcFormatValue()+"\n";
			//				message += "\tFormat Destination: "+myAd.getDstFormatScheme()+":"+myAd.getDstFormatValue()+"\n";
			//				message += "\n";
			//			}
			//			if(message.equals(""))
			//			{
			//				message = "No results were found";
			//			}
			Server.systemMessage = results;
			ProviderGUI.updateTextArea();
		}
	}

	/**
	 * Any to Marketplace Provider
	 * Respond To Marketplace Query
	 * @param packet
	 */
	private void respondToMarketplaceQuery(Packet packet) {
		// TODO Auto-generated method stub
		// parse the packet for queried content
		ChoiceNetMessageField[] payload = (ChoiceNetMessageField[]) packet.getMessageSpecific().getValue();
		ChoiceNetMessageField searchparameters = payload[0];
		payload = (ChoiceNetMessageField[]) searchparameters.getValue();
		
		System.out.println("payload length is "+payload.length);
		String attr, queryValue, adID, query, response = "";
		String queryField = "";
		boolean isRange = false;
		int i = 0;
		CouchDBResponse cResponse;

		Map<String, String> responseCollection = new HashMap<String, String>();
		for(ChoiceNetMessageField searchField : payload)
		{
			attr = searchField.getAttributeName();
			System.out.println("YO => "+attr);
			queryValue = (String) searchField.getValue();
			queryValue = queryValue.replaceAll(" ", "%20");

			if(attr.equals(QueryType.ADVERTISEMENT_ID.toString()))
			{
				queryField = "byID";
			}
			if(attr.equals(QueryType.COST.toString()))
			{
				isRange = true;
				queryField = "byCost?startkey=0&endkey=";
			}
			if(attr.equals("Service Type"))
			{
				queryField = "byServiceType";
			}
			// Single
			if(attr.equals(QueryType.LOCATION_SRC.toString()))
			{
				queryField = "bySrcLocation";
			}
			if(attr.equals(QueryType.LOCATION_DST.toString()))
			{
				queryField = "byDstLocation";
			}
			if(attr.equals(QueryType.FORMAT_SRC.toString()))
			{
				queryField = "bySrcFormat";
			}
			if(attr.equals(QueryType.FORMAT_DST.toString()))
			{
				queryField = "byDstFormat";
			}
			// Pair
			if(attr.equals(QueryType.LOCATION_SRC_DST.toString()))
			{
				queryField = "bySrcDstLocation";
			}
			if(attr.equals(QueryType.LOCATION_SRC_FORMAT_SRC.toString()))
			{
				queryField = "bySrcLocationSrcFormat";
			}
			if(attr.equals(QueryType.LOCATION_SRC_FORMAT_DST.toString()))
			{
				queryField = "bySrcLocationDstFormat";
			}
			if(attr.equals(QueryType.LOCATION_DST_FORMAT_SRC.toString()))
			{
				queryField = "byDstLocationSrcFormat";
			}
			if(attr.equals(QueryType.LOCATION_DST_FORMAT_DST.toString()))
			{
				queryField = "byDstLocationDstFormat";
			}
			if(attr.equals(QueryType.FORMAT_SRC_DST.toString()))
			{
				queryField = "bySrcDstFormat";
			}
			// Triplets
			if(attr.equals(QueryType.LOCATION_SRC_DST_FORMAT_SRC.toString()))
			{
				queryField = "bySrcDstLocationSrcFormat";
			}
			if(attr.equals(QueryType.LOCATION_SRC_DST_FORMAT_DST.toString()))
			{
				queryField = "bySrcDstLocationDstFormat";
			}
			if(attr.equals(QueryType.LOCATION_SRC_FORMAT_SRC_DST.toString()))
			{
				queryField = "bySrcLocationSrcDstFormat";
			}
			if(attr.equals(QueryType.LOCATION_DST_FORMAT_SRC_DST.toString()))
			{
				queryField = "byDstLocationSrcDstFormat";
			}

			if(attr.equals(QueryType.LOCATION_SRC_DST_FORMAT_SRC_DST.toString()))
			{
				queryField = "bySrcDstLocationSrcDstFormat";
			}

			System.out.println(attr.equals(QueryType.LOCATION_SRC.toString()));
			System.out.println(queryValue);
			// make the required calls per query content
			if(!isRange)
			{
				query = "/_design/marketplace/_view/"+queryField+"?key="+queryValue;
			}
			else
			{
				query = "/_design/marketplace/_view/"+queryField+queryValue;
			}
			// Retrieve CouchDB unique identifier for the document containing its entry to get its document ID, this will be used as an Advertisement ID
			String url = Server.marketplaceRESTAPI+query;
			Logger.log(url);
			response = couchDBsocket.getRestInterface(url);
			cResponse = CouchDBResponse.parseJson(response);
			for(CouchDBContainer currentAdv : cResponse.getRows())
			{
				adID = currentAdv.getId();
				System.out.println("This advertisement is " +adID+" "+currentAdv.getValue().toString());
				if(responseCollection.get(adID) == null)
				{
					responseCollection.put(adID, response);
				}
				else
				{
					System.out.println("Advertisement "+adID+" already retrieved");
				}
			}
			//			results[i] = response;
			//			i++;
		}
		// responseCollection has all unique keys; perform a search based on the keys supply the UNION of the responses as the query result
		String results = "";
		for(String item: responseCollection.keySet())
		{
			query = "/_design/marketplace/_view/byID?key=[\""+item+"\"]";
			String url = Server.marketplaceRESTAPI+query;
			Logger.log(url);
			response = couchDBsocket.getRestInterface(url);
			results += response+jsonSeparator;
//			i++;
		}

		// loop through unique response
		//String[] results = new String [responseCollection.size()];

		results = results.substring(0, results.length()-jsonSeparator.length());
		results = "<![CDATA["+results.toString()+"\n]]>";
		ChoiceNetMessageField resultsField = new ChoiceNetMessageField("Results", results, "");
		ChoiceNetMessageField[] myPayload = {resultsField};
		packet = new Packet(PacketType.MARKETPLACE_RESPONSE,myName,"",myType, providerType,myPayload);
		send(packet);
		// 
	}
	/**
	 * Marketplace Provider to Any
	 * Decode a Marketplace Response
	 * @param packet
	 */
	private void respondToMarketplaceResponse(Packet packet) {
		// TODO Auto-generated method stub
		// should alert the GUI of the content of the message
		Logger.log("Received Marketplace Response packet!");
		ChoiceNetMessageField[] payload = (ChoiceNetMessageField[]) packet.getMessageSpecific().getValue();
		String parsedResults = (String) payload[0].getValue();
		parsedResults = parsedResults.replace("<![CDATA[", "");
		parsedResults = parsedResults.replace("\n]]>", "");
		String[] results = parsedResults.split(jsonSeparator);
//		String[] results = (String[]) payload[0].getValue();
		
		String message = "";
		System.out.println(Server.runningMode);
		if(Server.runningMode.equals("standalone"))
		{
			System.out.println("CLI VERSION <<<<<<");
			for(String x: results)
			{
				message += x+"\n";
			}
			Server.systemMessage = message;
			Logger.saveToFile(message, "marketplace.response");
		}
		else
		{
			CouchDBResponse cResponse;
			AdvertisementDisplay myAd;
			for(String x: results)
			{
				cResponse = CouchDBResponse.parseJson(x);
				for(CouchDBContainer currentAdv : cResponse.getRows())
				{
					myAd = currentAdv.getValue();
					message += "ID: "+myAd.getId()+"\n";
					message += "\tDescription: "+myAd.getDescription()+"\n";
					message += "\tCost: "+myAd.getConsiderationMethod()+":"+myAd.getConsiderationValue()+"\n";
					message += "\tLocation Source : "+myAd.getSrcLocationAddrScheme()+":"+myAd.getSrcLocationAddrValue()+"\n";
					message += "\tLocation Destination: "+myAd.getDstLocationAddrScheme()+":"+myAd.getDstLocationAddrValue()+"\n";
					message += "\tFormat Source: "+myAd.getSrcFormatScheme()+":"+myAd.getSrcFormatValue()+"\n";
					message += "\tFormat Destination: "+myAd.getDstFormatScheme()+":"+myAd.getDstFormatValue()+"\n";
					message += "\n";
				}
			}
			if(message.equals(""))
			{
				message = "No results were found";
			}
			Server.systemMessage = message;
			ProviderGUI.updateTextArea();
		}
	}

	private void logUseAttemptAck(Packet packet) {
		// TODO Auto-generated method stub
		ChoiceNetMessageField[] payload = (ChoiceNetMessageField[]) packet.getMessageSpecific().getValue();
		String handleID = (String) payload[0].getValue();
		String usePlanePortal = (String) payload[1].getValue();
		String usePlanePlugIn = (String) payload[2].getValue();
		String message = "Handle ID: "+handleID+" Use Plane Portal: "+usePlanePortal+" Use Plane PlugIn: "+usePlanePlugIn;
		Server.systemMessage = message;
	}
	private void respondToUsePlaneSignaling(Packet packet) {
		// TODO Auto-generated method stub
		ChoiceNetMessageField[] payload = (ChoiceNetMessageField[]) packet.getMessageSpecific().getValue();
		ChoiceNetMessageField token = payload[0];
		// store the Token packet
		Token myToken = cnLibrary.extractTokenContent(token);
		long creationTime = System.currentTimeMillis();
		myToken.setCreationTime(creationTime);
		tokenMgr.addToken(creationTime, myToken);
		Logger.log("Token has been added to the system database.");
		Logger.log("Token contains:\n"+myToken+"\n==================");

	}
	private void logNegativeAck(Packet packet) {
		// TODO Auto-generated method stub
		Logger.log("Received Negative Acknowledgment packet!");
		ChoiceNetMessageField[] payload = (ChoiceNetMessageField[]) packet.getMessageSpecific().getValue();
		ChoiceNetMessageField nackType = payload[0];
		ChoiceNetMessageField opCode = payload[1];
		ChoiceNetMessageField reason = payload[2];
		String message = (String) nackType.getValue()+": Operation Code="+(Integer)opCode.getValue()+" due to: "+(String) reason.getValue();
		message = "Failed: "+(String) reason.getValue();
		Server.systemMessage = message;
		Logger.log(message);
	}
	/**
	 * Any to Any
	 * Respond To Rendezvous Request
	 * @param packet
	 */
	private void checkRendezvousRequest(Packet packet) {
		Logger.log("Checking Rendezvous Request packet!");
		ChoiceNetMessageField[] payload = (ChoiceNetMessageField[]) packet.getMessageSpecific().getValue();
		ChoiceNetMessageField rendezvousTarget = (ChoiceNetMessageField) payload[0];
		String targetedProvider = (String) rendezvousTarget.getValue();
		if(targetedProvider.equals(providerType))
		{
			String targetName = (String) packet.getOriginatorName().getValue();
			// Create Rendezvous Response packet
			ChoiceNetMessageField targetOriginatorName = new ChoiceNetMessageField("Target's Originator Name", targetName, "");
			ChoiceNetMessageField acceptedConsiderationFld = new ChoiceNetMessageField("Accepted Consideration", acceptedConsideration, "");
			ChoiceNetMessageField availableConsiderationFld = new ChoiceNetMessageField("Available Consideration", availableConsideration, "");
			ChoiceNetMessageField[] myPayload = {targetOriginatorName,acceptedConsiderationFld,availableConsiderationFld};
			packet = new Packet(PacketType.RENDEZVOUS_RESPONSE,myName,"",myType, providerType,myPayload);
			send(packet);
		}
		else
		{
			Logger.log("WARNING: "+targetedProvider+" does not match "+providerType);
		}
	}
	/**
	 * Any to Any
	 * Respond To Rendezvous Request
	 * @param packet
	 */
	private void checkRendezvousResponse(Packet packet) {
		Logger.log("Checking Rendezvous Response packet");
		// Record the Consideration Type 
		ChoiceNetMessageField[] payload = (ChoiceNetMessageField[]) packet.getMessageSpecific().getValue();
		String intendedEntityName = (String) payload[0].getValue();
		//String targetAcceptedConsideration = (String) payload[1].getValue();
		// check that this response is both intended for this entity node 
		// .... (Save for another function) AND consideration accepted matches this entity's available consideration
		if(intendedEntityName.equals(myName))
		{
			// save this entity's service, consideration options ...  
			// an intelligent agent will need this information to query the system AND send consideration
			String originatorName = (String) packet.getOriginatorName().getValue();
			String originatorType = (String) packet.getOriginatorProviderType().getValue();
			String acceptedConsideration = (String) payload[1].getValue();
			String availableConsideration = (String) payload[2].getValue();
			DiscoveredEntities entity = new DiscoveredEntities(originatorName, originatorType, clientIPAddress.toString(), clientPort,acceptedConsideration, availableConsideration);
			String id = clientIPAddress.toString()+":"+clientPort;
			dEMgr.addDiscoveredEntities(id, entity);
			String message = "Entity: "+originatorName+" has been included in the Known Entity list";
			Logger.log(message);
			// prevent other clients from running this logger
			if(Server.runningMode.equals("ProviderGUI"))
			{
				Server.systemMessage = message; // should be removed for the client
			}
		}
	}
	/**
	 * Any to Any (Provider)
	 * Respond To Transfer Consideration
	 * @param packet
	 */
	private void respondToTransferConsiderationRequest(Packet packet) {
		Logger.log("Respond To Transfer Consideration Request packet");
		ChoiceNetMessageField[] payload = (ChoiceNetMessageField[]) packet.getMessageSpecific().getValue();
		String intendedEntityName = (String) payload[1].getValue();
		String targetConsiderationMethod = (String) payload[3].getValue();
		// check that this response is both intended for this entity node 
		// .... (Save for another function) AND consideration method sent matches this entity's accepted consideration
		Packet newPacket;
		if(intendedEntityName.equals(myName))
		{
			String sName = (String) payload[2].getValue();
			System.out.println("Service name: "+sName);
			System.out.println("Does the service exist: "+serviceMgr.doesServiceExist(sName));
			// Check to see if service name matches a service you own
			// if it is a marketplace it should check CouchDB for services
			//if(targetConsiderationMethod.equals(acceptedConsideration) && serviceMgr.doesServiceExist(sName))
			if(targetConsiderationMethod.equals(acceptedConsideration) && adMgr.getAdvertisementByName(sName) != null)
			{
				// Internal DB operations to check that the service ID and the value equate
				// Assuming nothing is the value equate
				// Transaction Number 
				int tNumber = (Integer) payload[0].getValue();
				// Create Token

				long eTime = 5; // where eTime is measured in minutes
				String originatorName = (String) packet.getOriginatorName().getValue();
				// TODO: Change Token Service Name quantity to Token Type Qualifier, ex: Listing:5
				String tokenType = "UNKNOWN";
				if(myType.equals("Provider"))
				{
					if(providerType.equals("Marketplace"))
					{
						tokenType = "Listing:5";
					}
					if(providerType.equals("Planner"))
					{
						tokenType = "Planner:50";
					}
					if(providerType.equals("Transport"))
					{
						tokenType = "Service:500";
					}
				}
				ChoiceNetMessageField token = cnLibrary.createToken(originatorName, myName, tokenType, eTime, true);

				// Send ACK with Token
				ChoiceNetMessageField transactionNum = new ChoiceNetMessageField("Transaction Number", tNumber, "");
				// TODO: Empty gateway credentials: Marketplace should provide something here
				ChoiceNetMessageField gatewayCredentials = new ChoiceNetMessageField("ChoiceNet Gateway Credentials", "", "");
				ChoiceNetMessageField[] newPayload = {transactionNum,token,gatewayCredentials}; 
				InetAddress providerIPAddress = clientIPAddress;
				int providerPort = clientPort;
				// TODO: Note: Depending on the Provider Type more operations may be necessary
				if(myType.equals("Provider") && !providerType.equals("Marketplace"))
				{
					// Contact the service's ChoiceNet Gateway for given service advertisement
					Advertisement myAd = adMgr.getAdvertisementByName(sName);
					if(myAd != null)
					{
						String usePlaneAddrType = myAd.getUsePlaneType();
						String usePlaneAddr = myAd.getUsePlaneAddress();
						ChoiceNetMessageField gatewayAddrType = new ChoiceNetMessageField("Addressing Scheme", usePlaneAddrType, "");
						ChoiceNetMessageField gatewayAddr = new ChoiceNetMessageField("Addressing Value", usePlaneAddr, "");
						ChoiceNetMessageField[] info = {gatewayAddrType,gatewayAddr}; 
						gatewayCredentials = new ChoiceNetMessageField("ChoiceNet Gateway Credentials", info, "");
						newPayload[2] = gatewayCredentials;
						if(usePlaneAddrType.equals("TCPv4") || usePlaneAddrType.equals("UDPv4"))
						{
							String[] addr = usePlaneAddr.split(":");
							try {
								clientIPAddress = InetAddress.getByName(addr[0]);
								clientPort = Integer.parseInt(addr[1]);
								ChoiceNetMessageField[] signalingPayload = {token};
								newPacket = new Packet(PacketType.USE_PLANE_SIGNAL,myName,"",myType,providerType,signalingPayload);
								send(newPacket);
							} catch (UnknownHostException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (NumberFormatException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					else
					{
						Logger.log("Warning no ChoiceNet Gateway Credentials found for this service: "+sName+"\nNo advertisement recorded for that service.");
					}
				}
				clientIPAddress = providerIPAddress;
				clientPort = providerPort;
				newPacket = new Packet(PacketType.ACK_AND_SEND_TOKEN,myName,"",myType,providerType,newPayload);
			}
			else
			{
				ChoiceNetMessageField type, opCode, reason;
				String reasonVal;
				int opCodeVal;
				if(!targetConsiderationMethod.equals(acceptedConsideration))
				{
					reasonVal = "Consideration Method: {"+targetConsiderationMethod+"} is not accepted";
					opCodeVal = 1;
				}
				else
				{
					reasonVal = "Service name: {"+sName+"} is not listed in our database";
					opCodeVal = 2;
				}
				Logger.log(reasonVal);
				type = new ChoiceNetMessageField("NACK Type", "Transfer Consideration", "");
				opCode = new ChoiceNetMessageField("Operation Code", opCodeVal, "");
				reason = new ChoiceNetMessageField("Reason", reasonVal, "");
				ChoiceNetMessageField[] newPayload = {type,opCode,reason}; 
				newPacket = new Packet(PacketType.NACK,myName,"",myType,providerType,newPayload);
			}

		}
		else
		{
			String reasonVal = "Consideration Target: {"+intendedEntityName+"} does not match this entity's name";
			Logger.log(reasonVal);
			ChoiceNetMessageField type = new ChoiceNetMessageField("NACK Type", "Transfer Consideration", "");
			ChoiceNetMessageField opCode = new ChoiceNetMessageField("Operation Code", 3, "");
			ChoiceNetMessageField reason = new ChoiceNetMessageField("Reason", reasonVal, "");
			ChoiceNetMessageField[] newPayload = {type,opCode,reason}; 
			newPacket = new Packet(PacketType.NACK,myName,"",myType,providerType,newPayload);
		}
		send(newPacket);
	}

	private void respondToConsiderationACK(Packet packet)
	{
		Logger.log("Respond To Consideration ACK");//ACK AND SEND TOKEN
		ChoiceNetMessageField[] payload = (ChoiceNetMessageField[]) packet.getMessageSpecific().getValue();
		ChoiceNetMessageField transactionNum = payload[0];
		ChoiceNetMessageField token = payload[1];
		int tNum = (Integer) transactionNum.getValue();
		String key = Integer.toString(tNum);
		if(TransactionManager.getSingleTransaction(key) != null)
		{
			// store the Token packet
			Token myToken = cnLibrary.extractTokenContent(token);
			long creationTime = System.currentTimeMillis();
			myToken.setCreationTime(creationTime);
			tokenMgr.addToken(creationTime, myToken);
			Logger.log("Token has been added to the system database.");
			Logger.log("Token contains:\n"+myToken+"\n==================");
			// prevent other clients from running this logger
			if(Server.runningMode.equals("ProviderGUI"))
			{
				Server.systemMessage = "Token "+myToken.getId()+" has been added to the system database"; // should be removed for the client
			}
		}
		else
		{
			Logger.log("Acknowledged Consideration Message transaction number does not match with any of our recorded requests. Ignoring the Token supplied.");
		}
	}
	/**
	 * Provider (Any Service) to Provider (Marketplace)
	 * Respond To Listing Request
	 * @param packet
	 */
	private void respondToListingRequest(Packet packet) {
		Logger.log("Respond To Listing Request packet");
		ChoiceNetMessageField[] payload = (ChoiceNetMessageField[]) packet.getMessageSpecific().getValue();
		// parse the payload structure
		ChoiceNetMessageField advertisement = payload[0];
		ChoiceNetMessageField token = payload[1];
		// retrieve the message's intended entity name 
		payload = (ChoiceNetMessageField[]) token.getValue();
		String intendedEntityName = (String) payload[2].getValue(); // Issued By
		// check that this response is both intended for this entity node 
		if(intendedEntityName.equals(myName))
		{
			String adXML = (String) advertisement.getValue();
			String sName = (String) payload[3].getValue(); // Service Name
			Logger.log("The Token contains service name "+sName);
			// NOTE: I am making the Expiration time dependent on the TOKEN
			long expirationTime = (Long) payload[4].getValue(); // Expiration Time
			long currTime = System.currentTimeMillis();
			// Store advertisement in Advertisement Manager (Internal Database)
			adXML = adXML.replace("<![CDATA[", "");
			adXML = adXML.replace("]]>", "");
			
			ArrayList<Advertisement> submittedAds = cnLibrary.getAdvertisementsFromXML(adXML, "String");
			cnLibrary.storeAdvertisement(submittedAds);
			// check that the service type matches

			ArrayList<ChoiceNetMessageField> tPayload = new ArrayList<ChoiceNetMessageField>();
			for(Advertisement myAd: submittedAds)
			{
				if(currTime<expirationTime)
				{
					String adServiceName = myAd.getService().getName();

					String queryValue = adServiceName.replaceAll(" ", "%20");
					// Store in CouchDB
					couchDBsocket.postRestInterface(Server.marketplaceRESTAPI, myAd);
					Logger.log("Installed Advertisement into database");
					// Retrieve CouchDB unique identifier for the document containing its entry to get its document ID, this will be used as an Advertisement ID
					String query = "/_design/marketplace/_view/byServiceName?key=[\""+queryValue+"\"]";
					String url = Server.marketplaceRESTAPI+query;
					Logger.log("The advertisement has be sent to "+url);
					CouchDBResponse cResponse = couchDBsocket.retrieveCouchObject(url);
					String adID = cResponse.getRows().getLast().getId();
					myAd.setId(adID);
					myAd.setState("Advertised");
					// Respond back with an acknowledgment and user handle (advertisement ID)
					ChoiceNetMessageField advertisementID = new ChoiceNetMessageField("Advertisement ID", adID, "");
					ChoiceNetMessageField serviceName = new ChoiceNetMessageField("Service Name", adServiceName, "");
					ChoiceNetMessageField[] thisPayload = {advertisementID, serviceName};
					tPayload.add(new ChoiceNetMessageField("Advertised Service", thisPayload, ""));
				}
				else
				{
					myAd.setState("Expired");
					Logger.log("Listing Request: Supplied Token has expired.");
				}

			}
			int i = 0;
			ChoiceNetMessageField[] newPayload = new ChoiceNetMessageField[tPayload.size()];
			for(ChoiceNetMessageField msg : tPayload)
			{
				newPayload[i] = msg;
				i++;
			}
			Packet newPacket = new Packet(PacketType.LISTING_CONFIRMATION,myName,"",myType,providerType,newPayload);
			send(newPacket);
		}
		else
		{
			Logger.log("Listing Request: Entity name did not match. Intended for "+intendedEntityName);
		}

	}

	private void storeListingConfirmation(Packet packet) {
		// TODO Auto-generated method stub
		Logger.log("Received Listing Confirmation");
		ChoiceNetMessageField[] payload = (ChoiceNetMessageField[]) packet.getMessageSpecific().getValue();
		int size = payload.length;
		for(int i=0;i<size;i++)
		{
			ChoiceNetMessageField advertisedService = payload[i];
			ChoiceNetMessageField[] value = (ChoiceNetMessageField[]) payload[i].getValue();
			String sName = (String) value[1].getValue();
			String adID = (String) value[0].getValue();
			Logger.log("Advertisement ID: "+adID);
			Logger.log("Service name: "+sName);
			Advertisement myAd = adMgr.getAdvertisementByName(sName);
			myAd.setId(adID);
			myAd.setState("Advertised");
			Server.systemMessage = "<html>Success service "+sName+" is being listed with an <br>Advertisement ID: "+adID+"</html>";
		}
	}

	// The assumption here is that only the ChoiceNet Gateway will have to respond to this 
	// this functionality will need to be split into its own minimum system
	private void respondToUseAttempt(Packet packet) {
		// TODO: Finish this ... 
		Logger.log("Respond to Use Attempt");
		ChoiceNetMessageField[] payload = (ChoiceNetMessageField[]) packet.getMessageSpecific().getValue();
		ChoiceNetMessageField userAddrInfo = payload[0];
		ChoiceNetMessageField token = payload[1];
		String tokenID = (String) token.getValue(); // Issued By
		// check whether the token matches any received 
		int tID = Integer.parseInt(tokenID); 
		long creationTimeID = tokenMgr.getTokenCreationTime(tID);
		Token myToken = TokenManager.getSingleToken(creationTimeID);
		if(myToken != null)
		{

			if(myToken.getExpirationTime() >= System.currentTimeMillis())
			{
				// Valid Token
				try {
					String addrType = userAddrInfo.getAttributeName();
					String addrName = (String) userAddrInfo.getValue();
					String command = "";
					// Generate a random port to open
					Random generator = new Random();
					int low = 49152;
					int high = 65536;// include 65535 in the running by making max+1
					int generatedPort = generator.nextInt(high-low) + low;

					if(addrType.equals("TCPv4") || addrType.equals("UDPv4"))
					{
						// TODO: Enter the correct fire wall rule
						command = "stuff"+addrName;
					}
					// Use useAddrInfo to open context ... (open a port and translate traffic to another addr scheme)					
					Runtime.getRuntime().exec(command);
					// Send an ACK when everything is completed
					// hard coded 
					String portal = "rtsp://"+Server.providerAddress+":"+generatedPort+"/";
					String portalHelpURL = "http://www4.ncsu.edu/~rnudechu/files/GENI/EPB/VLC/help.html";
					String plugInHelpURL = "http://www.vlc.com";
					ChoiceNetMessageField handleID = new ChoiceNetMessageField("Handle ID", System.currentTimeMillis(), "");
					ChoiceNetMessageField usePlanePortal = new ChoiceNetMessageField("Use Plane Portal", portal, portalHelpURL);
					ChoiceNetMessageField usePlanePlugIn = new ChoiceNetMessageField("Use Plane Plugin", "Download VLC Player", plugInHelpURL);
					ChoiceNetMessageField[] newPayload = {handleID,usePlanePortal,usePlanePlugIn}; 
					Packet newPacket = new Packet(PacketType.ACK_USE_ATTEMPT,myName,"",myType,providerType,newPayload);
					send(newPacket);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else
			{
				//TODO: 
				// a once valid Token is expired
			}
		}
		else
		{
			//TODO:
			// Token ID does not exist in our database
		}
	}
	/**
	 * Customer
	 * User driven action
	 * Send a purchase order
	 */
	public void sendRequest(Packet packet) {
		Logger.log("Sending Request");
		String name = (String) packet.getOriginatorName().getValue();
		String oType = (String) packet.getOriginatorType().getValue();
		String pType = (String) packet.getOriginatorProviderType().getValue();
		data = (ChoiceNetMessageField[]) packet.getMessageSpecific().getValue();
		PacketType packetType = packet.getActionCode();
		Packet packageOut = new Packet(packetType,name,"",oType,pType,data);
		send(packageOut);
	}



}
