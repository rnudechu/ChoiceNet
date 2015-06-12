/**
 * @author Rob Udechukwu
 *  Handles the different stages of a client and sensor device interaction with the server.
 *  Performs all the Socket operations
 */


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
	String jsonSeparator = "\n";

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
			clientIPAddress = InetAddress.getByName(ipAddress);
			clientPort = port;
			System.out.println ("**** Client Info Begins****");
			System.out.println ("To: " + clientIPAddress + ":" + clientPort);
			System.out.println ("**** Client Info Ends ****");
		} catch (UnknownHostException e) {
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
			String pktXML = cnLibrary.createPacketXML(packet);
			System.out.println("Sending XML:\n"+pktXML);
			sendData = pktXML.getBytes();
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
	
	/**
	 * 	Send
	 * This function simply takes in a Packet object to be sent across the network.  Its destination is encoded into the Packet object.
	 * This function will send the Packet out the relevant socket.
	 */
	private int sendFirewallMsg (String firewallXML) {
		try 
		{
			System.out.println("Sending XML:\n"+firewallXML);
			sendData = firewallXML.getBytes();
			System.out.println("Size of payload: "+sendData.length);
			// as of now the IP Address and port are being ignored
			// all packets from the Provider address is treated as control
			sendPacket = new DatagramPacket(sendData, sendData.length, clientIPAddress, clientPort); 
			socket.send(sendPacket);
			Logger.log("Following information is being to sent to "+clientIPAddress+":"+clientPort+"\n"+firewallXML);
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
				//				ByteArrayInputStream bis = new ByteArrayInputStream(receivePacket.getData(), receivePacket.getOffset(),receivePacket.getLength());
				while(!socket.isClosed() && receivePacket != null)
				{

					try
					{
						//outToClient.flush();
						//bis = new ByteArrayInputStream(receivePacket.getData());
						System.out.println("Byte Array Input Stream :\n"+bis);

						// I can convert it to a XML object at this step 
						// Depending on the packet type received the server attempts to process that packet 
						String pktXML = new String(receivePacket.getData(), receivePacket.getOffset(),receivePacket.getLength(), "UTF-8");
						System.out.println("XML size :\n"+pktXML.length()+" byte array length: "+receivePacket.getLength());
						System.out.println("XML received :\n"+pktXML);
						Packet packet = cnLibrary.convertXMLtoPacket(pktXML);
						receivePacket = null;
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
							//if(packetType == PacketType.ACK_AND_SEND_TOKEN)CONSIDERATION_ACK
							if(packetType == PacketType.CONSIDERATION_ACK)
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
								if(providerType.equals("Marketplace"))
								{
									respondToMarketplaceQuery(packet);
								}
								else
								{
									System.out.println("ERROR: "+providerType+" requested "+PacketType.MARKETPLACE_QUERY);
								}
							}
							// Marketplace Query
							if(packetType == PacketType.MARKETPLACE_RESPONSE)
							{
								respondToMarketplaceResponse(packet);
							}
							// Marketplace Query
							if(packetType == PacketType.PLANNER_REQUEST)
							{
								if(providerType.equals("Planner"))
								{
									respondToPlannerRequest(packet);
								}
								else
								{
									System.out.println("ERROR: "+providerType+" requested "+PacketType.PLANNER_REQUEST);
								}
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
					}
					catch(ClassCastException e)
					{
						System.err.println(e.getMessage());
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
		payload = (ChoiceNetMessageField[]) reqParameters.getValue();
		System.out.println("Payload Length: "+payload.length);
		System.out.println("Parameter: "+payload[0].getValue());
		int size = payload.length;
		String content = "";
		String attr = "";
		String value = "";
		for(int i=0; i<size; i++)
		{
			attr = (String) payload[i].getAttributeName();
			value = (String) payload[i].getValue();
			content += attr+": "+value+", "; 
		}
		content = content.substring(0, content.length()-2);
		System.out.println("Content: "+content);
		String message = "";
		boolean testing = true;
		if(testing)
		{
			message = "Dummy Response sent to client requesting planner service\n\nContent: "+content;
			ChoiceNetMessageField resultsField = new ChoiceNetMessageField("Advertisement List", message, "");
			ChoiceNetMessageField[] myPayload = {resultsField};
			packet = new Packet(PacketType.PLANNER_RESPONSE,myName,"",myType, providerType,myPayload);
		}
		else
		{
			message = "No Planner to handle request";
			
			ChoiceNetMessageField[] myPayload = createNACKPayload(PacketType.PLANNER_REQUEST.toString(), 1, message);
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
		if(!Server.runningMode.equals("standalone"))
		{
			ProviderGUI.updateTextArea(results);
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
		InternalMessageField[] queryPayload = determineQueryType(payload);

		System.out.println("payload length is "+payload.length);
		String queryValue, adID, query, response = "";
		CouchDBResponse cResponse;

		Map<String, String> responseCollection = new HashMap<String, String>();
		for(InternalMessageField searchField : queryPayload)
		{
			queryValue = (String) searchField.getValue();
			queryValue = queryValue.replaceAll(" ", "%20");
			query = "/_design/marketplace/_view/"+queryValue;
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
		if(results.length()>0)
		{
			results = results.substring(0, results.length()-jsonSeparator.length());
		}
		results = "<![CDATA["+results.toString()+"\n]]>";
		System.out.println("Size of payload is "+results.length());
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
				if(cResponse != null)
				{
					for(CouchDBContainer currentAdv : cResponse.getRows())
					{
						myAd = currentAdv.getValue();
						message += "ID: "+myAd.getId()+"\n";
						message += "\tDescription: "+myAd.getDescription()+"\n";
						message += "\tCost: "+myAd.getConsiderationMethod()+":"+myAd.getConsiderationValue()+"\n";
						
						message += "\tLocation Source: "+Arrays.toString(myAd.getSrcLocationAddrScheme())+":"+Arrays.toString(myAd.getSrcLocationAddrValue())+"\n";
						message += "\tLocation Destination: "+Arrays.toString(myAd.getDstLocationAddrScheme())+":"+Arrays.toString(myAd.getDstLocationAddrValue())+"\n";
						message += "\tFormat Source: "+Arrays.toString(myAd.getSrcFormatScheme())+":"+Arrays.toString(myAd.getSrcFormatValue())+"\n";
						message += "\tFormat Destination: "+Arrays.toString(myAd.getDstFormatScheme())+":"+Arrays.toString(myAd.getDstFormatValue())+"\n";
						message += "\n";
					}
				}
				else
				{
					message = "No results were found";
				}
			}
			ProviderGUI.updateTextArea(message);
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
		//String message = (String) nackType.getValue()+": Operation Code="+(opCodeValue)+" due to: "+(String) reason.getValue();
		String message = "Failed: "+(String) reason.getValue();
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
			if(!Server.runningMode.equals("standalone"))
			{
				ProviderGUI.updateTextArea(message);
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
		String targetConsiderationContent = (String) payload[3].getValue();
		String[] consideration = targetConsiderationContent.split(":");
		String targetConsiderationMethod = consideration[0];
		String targetConsiderationAccount = consideration[1];
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
				// Should perform payment transaction
				targetConsiderationContent = (String) payload[4].getValue();
				consideration = targetConsiderationContent.split(" ");
				String targetConsiderationAmount = consideration[0];
				String targetConsiderationCurrency = consideration[1]; 
				
				String reason = "ChoiceNet Entity: "+packet.getOriginatorName()+" purchased "+myName+": "+sName;
				reason = reason.replace(" ", "%20");
				System.out.println("Reason: "+reason);
				String url = Server.purchasePortalValidator+"?paymentMethod="+targetConsiderationMethod+"&currency="+targetConsiderationCurrency+
						"&amount="+targetConsiderationAmount+"&transactionID="+targetConsiderationAccount+"&reason="+reason;
				System.out.println("Purchase Portal: "+url);
				String considerationConfirmation = couchDBsocket.getRestInterface(url);
				System.out.println("Consideration Confirmation: "+considerationConfirmation);
				if(considerationConfirmation.contains("success"))
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
							tokenType = "Listing";
						}
						if(providerType.equals("Planner"))
						{
							tokenType = "Planner";
						}
						if(providerType.equals("Transport"))
						{
							tokenType = "Transport";
						}
					}
					ChoiceNetMessageField token = cnLibrary.createToken(originatorName, myName, tokenType, eTime, true);
					/*
					// retrieve the Traffic Properties field ... if not empty send it to your (OpenFlow-enabled) firewall
					String firewallXML = (String) payload[5].getValue();
					if(!firewallXML.equals(""))
					{
						System.out.println(firewallXML);
						sendFirewallMsg(firewallXML);
					}
					*/
					// Send ACK with Token
					ChoiceNetMessageField transactionNum = new ChoiceNetMessageField("Transaction Number", tNumber, "");
					// TODO: Empty gateway credentials: Marketplace should provide something here
					ChoiceNetMessageField gatewayCredentials = new ChoiceNetMessageField("ChoiceNet Gateway Credentials", "", "");
					ChoiceNetMessageField[] newPayload = {transactionNum,token,gatewayCredentials}; 
					InetAddress providerIPAddress = clientIPAddress;
					int providerPort = clientPort;
					// TODO: Note: Depending on the Provider Type more operations may be necessary
//					if(myType.equals("Provider") && !providerType.equals("Marketplace"))
//					{
//						// Contact the service's ChoiceNet Gateway for given service advertisement
//						Advertisement myAd = adMgr.getAdvertisementByName(sName);
//						if(myAd != null)
//						{
//							String usePlaneAddrType = myAd.getUsePlaneType();
//							String usePlaneAddr = myAd.getUsePlaneAddress();
//							ChoiceNetMessageField gatewayAddrType = new ChoiceNetMessageField("Addressing Scheme", usePlaneAddrType, "");
//							ChoiceNetMessageField gatewayAddr = new ChoiceNetMessageField("Addressing Value", usePlaneAddr, "");
//							ChoiceNetMessageField[] info = {gatewayAddrType,gatewayAddr}; 
//							gatewayCredentials = new ChoiceNetMessageField("ChoiceNet Gateway Credentials", info, "");
//							newPayload[2] = gatewayCredentials;
//							if(usePlaneAddrType.equals("TCPv4") || usePlaneAddrType.equals("UDPv4"))
//							{
//								String[] addr = usePlaneAddr.split(":");
//								try {
//									clientIPAddress = InetAddress.getByName(addr[0]);
//									clientPort = Integer.parseInt(addr[1]);
//									ChoiceNetMessageField[] signalingPayload = {token};
//									newPacket = new Packet(PacketType.USE_PLANE_SIGNAL,myName,"",myType,providerType,signalingPayload);
//									send(newPacket);
//								} catch (UnknownHostException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								} catch (NumberFormatException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//							}
//						}
//						else
//						{
//							Logger.log("Warning no ChoiceNet Gateway Credentials found for this service: "+sName+"\nNo advertisement recorded for that service.");
//						}
//					}
					clientIPAddress = providerIPAddress;
					clientPort = providerPort;
					//newPacket = new Packet(PacketType.ACK_AND_SEND_TOKEN,myName,"",myType,providerType,newPayload);CONSIDERATION_ACK
					newPacket = new Packet(PacketType.CONSIDERATION_ACK,myName,"",myType,providerType,newPayload);
				}
				else
				{
					String reasonVal;
					int opCodeVal = 4;
					reasonVal = "Consideration Confirmation: "+considerationConfirmation+" for account "+targetConsiderationAccount;
					Logger.log(reasonVal);
					
					ChoiceNetMessageField[] newPayload = createNACKPayload(PacketType.TRANSFER_CONSIDERATION.toString(),  opCodeVal, reasonVal); 
					newPacket = new Packet(PacketType.NACK,myName,"",myType,providerType,newPayload);
				}
			}
			else
			{
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
				ChoiceNetMessageField[] newPayload = createNACKPayload(PacketType.TRANSFER_CONSIDERATION.toString(),  opCodeVal, reasonVal);
				newPacket = new Packet(PacketType.NACK,myName,"",myType,providerType,newPayload);
			}

		}
		else
		{
			int opCodeVal = 3;
			String reasonVal = "Consideration Target: {"+intendedEntityName+"} does not match this entity's name";
			Logger.log(reasonVal);
			ChoiceNetMessageField[] newPayload = createNACKPayload(PacketType.TRANSFER_CONSIDERATION.toString(),  opCodeVal, reasonVal);
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
			if(!Server.runningMode.equals("standalone"))
			{
				String msg = "Token "+myToken.getId()+" has been added to the system database"; // should be removed for the client
				ProviderGUI.updateTextArea(msg);
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
		String msg = "<html>";
		for(int i=0;i<size;i++)
		{
			ChoiceNetMessageField[] value = (ChoiceNetMessageField[]) payload[i].getValue();
			String sName = (String) value[1].getValue();
			String adID = (String) value[0].getValue();
			Logger.log("Advertisement ID: "+adID);
			Logger.log("Service name: "+sName);
			Advertisement myAd = adMgr.getAdvertisementByName(sName);
			myAd.setId(adID);
			myAd.setState("Advertised");
			msg += "Success service "+sName+" is being listed with an <br>Advertisement ID: "+adID+"<br>";
		}
		msg += "</html>";
		if(!Server.runningMode.equals("standalone"))
		{
			ProviderGUI.updateTextArea(msg);
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
					String addrType = userAddrInfo.getAttributeName();
					String addrName = (String) userAddrInfo.getValue();
					String command = "";

					
					// Send an ACK when everything is completed
					// hard coded 
					String portal = "rtsp://"+Server.providerAddress;
					String portalHelpURL = "http://www4.ncsu.edu/~rnudechu/files/GENI/EPB/VLC/help.html";
					String plugInHelpURL = "http://www.vlc.com";
					ChoiceNetMessageField handleID = new ChoiceNetMessageField("Handle ID", System.currentTimeMillis(), "");
					ChoiceNetMessageField usePlanePortal = new ChoiceNetMessageField("Use Plane Portal", portal, portalHelpURL);
					ChoiceNetMessageField usePlanePlugIn = new ChoiceNetMessageField("Use Plane Plugin", "Download VLC Player", plugInHelpURL);
					ChoiceNetMessageField[] newPayload = {handleID,usePlanePortal,usePlanePlugIn}; 
					Packet newPacket = new Packet(PacketType.ACK_USE_ATTEMPT,myName,"",myType,providerType,newPayload);
					send(newPacket);
			}
			else
			{
				//TODO: 
				// a once valid Token is expired
				System.out.println("Expired Token has been used. Can not provide Use ATTEMPT ACK");
			}
		}
		else
		{
			//TODO:
			// Token ID does not exist in our database
			System.out.println("Token ID does not exist in the database. Can not provide Use ATTEMPT ACK");
		}
	}
	private InternalMessageField[] determineQueryType(ChoiceNetMessageField[] message)
	{
		ArrayList<InternalMessageField> list = new ArrayList<InternalMessageField>();
		String sourceLoc = "";
		String destinationLoc = "";
		String sourceFormat = "";
		String destinationFormat = "";
		String sourceLocType = "";
		String destinationLocType = "";
		String sourceFormatType = "";
		String destinationFormatType = "";
		String adID = "";
		String cost = "";
		String costType = "";
		String attr = "";
		for(ChoiceNetMessageField msg: message)
		{
			attr = msg.getAttributeName();
			if(attr.equals(RequestType.LOCATION_SRC.toString()))
			{
				sourceLoc = (String) msg.getValue();
			}
			if(attr.equals(RequestType.LOCATION_DST.toString()))
			{
				destinationLoc = (String) msg.getValue();
			}
			if(attr.equals(RequestType.FORMAT_SRC.toString()))
			{
				sourceFormat = (String) msg.getValue();
			}
			if(attr.equals(RequestType.FORMAT_DST.toString()))
			{
				destinationFormat = (String) msg.getValue();
			}
			if(attr.equals(RequestType.LOCATION_SRC_TYPE.toString()))
			{
				sourceLocType = (String) msg.getValue();
			}
			if(attr.equals(RequestType.LOCATION_DST_TYPE.toString()))
			{
				destinationLocType = (String) msg.getValue();
			}
			if(attr.equals(RequestType.FORMAT_SRC_TYPE.toString()))
			{
				sourceFormatType = (String) msg.getValue();
			}
			if(attr.equals(RequestType.FORMAT_DST_TYPE.toString()))
			{
				destinationFormatType = (String) msg.getValue();
			}
			if(attr.equals(RequestType.COST.toString()))
			{
				cost = (String) msg.getValue();
			}
			if(attr.equals(RequestType.COST_TYPE.toString()))
			{
				costType = (String) msg.getValue();
			}
			if(attr.equals(RequestType.ADVERTISEMENT_ID.toString()))
			{
				adID = (String) msg.getValue();
			}
		}

		// determine the query property by content submitted in the location/format source/destination
		InternalMessageField searchedContent;
		// determine the query property by content submitted in the location/format source/destination
		// check if any of the query parameters contain a comma if so multiple searches need to be performed
		String[] srcLocArr = sourceLoc.split(",");
		String[] dstLocArr = destinationLoc.split(",");
		String[] srcFormatArr = sourceFormat.split(",");
		String[] dstFormatArr = destinationFormat.split(",");
		String[] srcLocTypeArr = sourceLocType.split(",");
		String[] dstLocTypeArr = destinationLocType.split(",");
		String[] srcFormatTypeArr = sourceFormatType.split(",");
		String[] dstFormatTypeArr = destinationFormatType.split(",");

		int i = 0;
		int j = 0;
		int k = 0;
		int l = 0;
		boolean operate;
		// loop through the possible combinations between this four fields
		while(i<srcLocArr.length)
		{
			operate = false;
			searchedContent = determineQueryParameter(srcLocArr[i],dstLocArr[j],srcFormatArr[k],dstFormatArr[l],srcLocTypeArr[i],dstLocTypeArr[j],srcFormatTypeArr[k],dstFormatTypeArr[l]);
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
		String data = "";
		String queryField = "";
		// decode the source field
		if(!cost.isEmpty())
		{
			queryField = "byCost?startkey=[\""+costType+"\"]&endkey=";
			data = queryField+"[\""+costType+"\","+cost+"]";
			searchedContent = new InternalMessageField(""+QueryType.COST, data, "");
			list.add(searchedContent);
		}
		// decode the advertisement ID field
		if(!adID.isEmpty())
		{
			queryField = "byID?key=";
			data = queryField+"[\""+adID+"\"]";
			searchedContent = new InternalMessageField(""+QueryType.ADVERTISEMENT_ID, data, "");
			list.add(searchedContent);
		}
		int size = list.size();
		InternalMessageField payload[] = new InternalMessageField[size];
		for(i=0;i<size;i++)
		{
			payload[i] = list.get(i);
		}
		return payload;
	}
	/**
	 * 
	 * @param sourceLoc
	 * @param destinationLoc
	 * @param sourceFormat
	 * @param destinationFormat
	 * @return
	 */
	private InternalMessageField determineQueryParameter(String sourceLoc, String destinationLoc, String sourceFormat, String destinationFormat, 
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
		InternalMessageField searchedContent = null;
		String data = "";
		String queryField = "";
		if(!sourceLoc.isEmpty() && !destinationLoc.isEmpty() && !sourceFormat.isEmpty() && !destinationFormat.isEmpty())
		{
			queryField = "bySrcDstLocationSrcDstFormat?key=";
			data = queryField+"[\""+sourceLoc+"\",\""+destinationLoc+"\",\""+sourceFormat+"\",\""+destinationFormat+"\"]";
			searchedContent = new InternalMessageField(""+QueryType.LOCATION_SRC_DST_FORMAT_SRC_DST, data, "");
			return searchedContent;
		}
		// Triplets
		if(!sourceLoc.isEmpty() && !destinationLoc.isEmpty() && !sourceFormat.isEmpty())
		{
			queryField = "bySrcDstLocationSrcFormat?key=";
			data = queryField+"[\""+sourceLoc+"\",\""+destinationLoc+"\",\""+sourceFormat+"\"]";
			searchedContent = new InternalMessageField(""+QueryType.LOCATION_SRC_DST_FORMAT_SRC, data, "");
			return searchedContent;
		}
		if(!sourceLoc.isEmpty() && !destinationLoc.isEmpty() && !destinationFormat.isEmpty())
		{
			queryField = "bySrcDstLocationDstFormat?key=";
			data = queryField+"[\""+sourceLoc+"\",\""+destinationLoc+"\",\""+destinationFormat+"\"]";
			searchedContent = new InternalMessageField(""+QueryType.LOCATION_SRC_DST_FORMAT_DST, data, "");
			return searchedContent;
		}
		if(!sourceLoc.isEmpty() && !sourceFormat.isEmpty() && !destinationFormat.isEmpty())
		{
			queryField = "bySrcLocationSrcDstFormat?key=";
			data = queryField+"[\""+sourceLoc+"\",\""+sourceFormat+"\",\""+destinationFormat+"\"]";
			searchedContent = new InternalMessageField(""+QueryType.LOCATION_SRC_FORMAT_SRC_DST, data, "");
			return searchedContent;
		}
		if(!destinationLoc.isEmpty() && !sourceFormat.isEmpty() && !destinationFormat.isEmpty())
		{
			queryField = "byDstLocationSrcDstFormat?key=";
			data = queryField+"[\""+destinationLoc+"\",\""+sourceFormat+"\",\""+destinationFormat+"\"]";
			searchedContent = new InternalMessageField(""+QueryType.LOCATION_DST_FORMAT_SRC_DST, data, "");
			return searchedContent;
		}
		// Pair Case
		if(!sourceLoc.isEmpty() && !destinationLoc.isEmpty())
		{
			queryField = "bySrcDstLocation?key=";
			data = queryField+"[\""+sourceLoc+"\",\""+destinationLoc+"\"]";
			searchedContent = new InternalMessageField(""+QueryType.LOCATION_SRC_DST, data, "");
			return searchedContent;
		}
		if(!sourceLoc.isEmpty() && !sourceFormat.isEmpty())
		{
			queryField = "bySrcLocationSrcFormat?key=";
			data = queryField+"[\""+sourceLoc+"\",\""+sourceLocType+"\",\""+sourceFormat+"\",\""+sourceFormatType+"\"]";
			searchedContent = new InternalMessageField(""+QueryType.LOCATION_SRC_FORMAT_SRC, data, "");
			return searchedContent;
		}
		if(!sourceLoc.isEmpty() && !destinationFormat.isEmpty())
		{
			queryField = "bySrcLocationDstFormat?key=";
			data = queryField+"[\""+sourceLoc+"\",\""+destinationFormat+"\"]";
			searchedContent = new InternalMessageField(""+QueryType.LOCATION_SRC_FORMAT_DST, data, "");
			return searchedContent;
		}
		if(destinationLoc.isEmpty() && !sourceFormat.isEmpty())
		{
			queryField = "byDstLocationSrcFormat?key=";
			data = queryField+"[\""+destinationLoc+"\",\""+sourceFormat+"\"]";
			searchedContent = new InternalMessageField(""+QueryType.LOCATION_DST_FORMAT_SRC, data, "");
			return searchedContent;
		}
		if(!destinationLoc.isEmpty() &&  !destinationFormat.isEmpty())
		{
			queryField = "byDstLocationDstFormat?key=";
			data = queryField+"[\""+destinationLoc+"\",\""+destinationFormat+"\"]";
			searchedContent = new InternalMessageField(""+QueryType.LOCATION_DST_FORMAT_DST, data, "");
			return searchedContent;
		}
		if(!sourceFormat.isEmpty() && !destinationFormat.isEmpty())
		{
			queryField = "bySrcDstFormat?key=";
			data = queryField+"[\""+sourceFormat+"\",\""+destinationFormat+"\"]";
			searchedContent = new InternalMessageField(""+QueryType.FORMAT_SRC_DST, data, "");
			return searchedContent;
		}
		// Single case
		if(!sourceLoc.isEmpty())
		{
			queryField = "bySrcLocation?key=";
			data = queryField+"[\""+sourceLoc+"\",\""+sourceLocType+"\"]";
			searchedContent = new InternalMessageField(""+QueryType.LOCATION_SRC, data, "");
			return searchedContent;
		}
		if(!destinationLoc.isEmpty())
		{
			queryField = "byDstLocation?=";
			data = queryField+"[\""+destinationLoc+"\",\""+destinationLocType+"\"]";
			searchedContent = new InternalMessageField(""+QueryType.LOCATION_DST, data, "");
			return searchedContent;
		}
		if(!sourceFormat.isEmpty())
		{
			queryField = "bySrcFormat?key=";
			data = queryField+"[\""+sourceFormat+"\",\""+sourceFormatType+"\"]";
			searchedContent = new InternalMessageField(""+QueryType.FORMAT_SRC, data, "");
			return searchedContent;
		}
		if(!destinationFormat.isEmpty())
		{
			queryField = "byDstFormat?key=";
			data = queryField+"[\""+destinationFormat+"\",\""+destinationFormatType+"\"]";
			searchedContent = new InternalMessageField(""+QueryType.FORMAT_DST, data, "");
			return searchedContent;
		}

		return searchedContent;
	}
	
	private ChoiceNetMessageField[] createNACKPayload (String nackType, int operationCode, String reasonVal)
	{
		ChoiceNetMessageField type = new ChoiceNetMessageField("NACK Type", nackType, "");
		ChoiceNetMessageField opCode = new ChoiceNetMessageField("Operation Code", operationCode, "");
		ChoiceNetMessageField reason = new ChoiceNetMessageField("Reason", reasonVal, "");
		ChoiceNetMessageField[] payload = {type,opCode,reason};
		return payload;
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
