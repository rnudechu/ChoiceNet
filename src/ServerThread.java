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
			byte[] ipAddrBytes = new byte[]{10, 0, 1, 100};
			InetAddress addr = InetAddress.getByAddress(ipAddrBytes);

			System.out.println("Sending Firewall XML:\n"+firewallXML);
			sendData = firewallXML.getBytes();
			System.out.println("Size of payload: "+sendData.length);
			// as of now the IP Address and port are being ignored
			// all packets from the Provider address is treated as control
			sendPacket = new DatagramPacket(sendData, sendData.length, addr, 6000); 
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
							if(packetType == PacketType.TOKEN_REQUEST)
							{
								respondToTransferConsiderationRequest(packet);
							}
							// Received a Request to List Message
							if(packetType == PacketType.LISTING_REQUEST)
							{
								//	if(providerType.equals("Marketplace"))
								//	{
								respondToListingRequest(packet);
								//	}
								//	else
								//	{
								//		System.out.println("ERROR: "+providerType+" requested "+PacketType.LISTING_REQUEST);
								//	}
							}
							// Received a ACK Consideration Message
							//if(packetType == PacketType.ACK_AND_SEND_TOKEN)TOKEN_RESPONSE
							if(packetType == PacketType.TOKEN_RESPONSE)
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
							// Received a purchase ACK
							if(packetType == PacketType.USE_ATTEMPT_CONFIRMATION)
							{
								logUseAttemptAck(packet);
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
		String[] temp;
		String originatorName = (String) packet.getOriginatorName().getValue();
		// Parse value
		PlannerSearchParameter searchParameter = new PlannerSearchParameter(originatorName, Server.numberOfFreeQueries, 0);
		// Check to see if Planner has communicated with this entity before if not force a "silent" Rendezvous Interaction
		if(dEMgr.getDiscoveredEntityByName(originatorName) == null)
		{
			String originatorProviderType = (String) packet.getOriginatorProviderType().getValue();
			ChoiceNetMessageField[] rendezvousPayload = cnLibrary.createRendevouzMessage(originatorProviderType, acceptedConsideration, availableConsideration);
			Packet rendezvousPacket = new Packet(PacketType.RENDEZVOUS_REQUEST,myName,"",myType, providerType,rendezvousPayload);
			sendRequest(rendezvousPacket);
		}
		//new ServerThread(socket, clientBroadcastAddress, rendezvousPacket);
		for(int i=0; i<size; i++)
		{
			attr = (String) payload[i].getAttributeName();
			value = (String) payload[i].getValue();
			temp = value.split(",");
			if(attr.equals(RequestType.LOCATION_SRC_TYPE.toString()))
			{
				for(String val: temp)
				{
					searchParameter.getSrcTypeLocation().add(val);
				}
			}
			if(attr.equals(RequestType.LOCATION_SRC.toString()))
			{
				for(String val: temp)
				{
					searchParameter.getSrcLocation().add(val);
				}
			}
			if(attr.equals(RequestType.LOCATION_DST_TYPE.toString()))
			{
				for(String val: temp)
				{
					searchParameter.getDstTypeLocation().add(val);
				}
			}
			if(attr.equals(RequestType.LOCATION_DST.toString()))
			{
				for(String val: temp)
				{
					searchParameter.getDstLocation().add(val);
				}
			}
			if(attr.equals(RequestType.FORMAT_SRC_TYPE.toString()))
			{
				for(String val: temp)
				{
					searchParameter.getSrcTypeFormat().add(val);
				}
			}
			if(attr.equals(RequestType.FORMAT_SRC.toString()))
			{
				for(String val: temp)
				{
					searchParameter.getSrcFormat().add(val);
				}
			}
			if(attr.equals(RequestType.FORMAT_DST_TYPE.toString()))
			{
				for(String val: temp)
				{
					searchParameter.getDstTypeFormat().add(val);
				}
			}
			if(attr.equals(RequestType.FORMAT_DST.toString()))
			{
				for(String val: temp)
				{
					searchParameter.getDstFormat().add(val);
				}
			}
			if(attr.equals(RequestType.COST.toString()))
			{
				int total = Integer.parseInt(value);
				searchParameter.setCost(total);
			}
			if(attr.equals(RequestType.COST_TYPE.toString()))
			{
				searchParameter.setCostType(value);
			}
			content += attr+": "+value+" "; 
		}
		Server.searchParameterList.add(searchParameter);
		Server.searchParameterHistory.add(searchParameter);

		handlePlannerServiceParameters();
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
			// parse the results if XML data
			if(results.contains("xml"))
			{
				String parsedResults = results;
				parsedResults = parsedResults.replace("<![CDATA[", "");
				parsedResults = parsedResults.replace("\n]]>", "");

				PlannerServiceRecipe recipe = new PlannerServiceRecipe();
				recipe.parseXML(parsedResults);
				if(recipe.getAdvertisementList().size()>0)
				{
					System.out.println(recipe);
					parsedResults = "Total Cost: "+recipe.getTotalCost()+"\n";
					parsedResults += "Advertisement List:\n";
					int size = recipe.getAdvertisementList().size();
					for(int i=0; i<size; i++ )
					{
						parsedResults += "\t Advertisement "+(i+1)+": "+recipe.getAdvertisementList().get(i)+"\n";
						parsedResults += "\t\t Provision Parameter:: "+recipe.getProvisioningParameters().get(i)+"\n";
					}
					results = parsedResults;
				}
			}
			ChoiceNetSpeakerGUI.updateTextArea(results);
		}
		Logger.log("Planner Response:\n"+results);
	}

	/**
	 * Planner Provider to any
	 * Handle Service Parameter
	 */
	private void handlePlannerServiceParameters()
	{
		// Check searchParameterList for searchParameter(s), for any given searchParameter completed send a recipe
		// Query Marketplace per element
		System.out.println("Handle Planner Service Parameters");
		boolean readyToSendPacket = false;
		PlannerServiceRecipe selectedRecipe = null;
		System.out.println("Handle Planner Service Parameters Size: "+Server.searchParameterList.size());
		if(Server.searchParameterList.size()>0)
		{
			System.out.println("Handling Planner Service Parameters");
			PlannerSearchParameter serviceParameter = Server.searchParameterList.get(0);
			// serviceParameter total element is not zero then a complete query on all the elements has not been completed
			if(serviceParameter.getTotalQueries()>0) // serviceParameter.getTotalElements()>0 
			{
				System.out.println("Service Parameters Total Elements: "+serviceParameter.getTotalElements());
				Packet packet = null;
				boolean check = true;
				Server.searchedParameterIsSource = false;
				Server.searchedParameterIsDestination = false;
				String sourceLoc = "";
				String destinationLoc = ""; 
				String sourceFormat = ""; 
				String destinationFormat = ""; 
				String sourceLocType = ""; 
				String destinationLocType = ""; 
				String sourceFormatType = ""; 
				String destinationFormatType = ""; 
				String cost = ""; 
				String cMethod = ""; 
				String adID = ""; 
				String providerID = "";
				if(check && serviceParameter.getSrcLocation().size()>0)
				{
					check = false;
					Server.searchedParameterIsSource = true;
					sourceLoc = serviceParameter.getSrcLocation().get(0);
					Server.searchedParameterLocation = sourceLoc;
					sourceLocType = serviceParameter.getSrcTypeLocation().get(0);
					Server.searchedParameterLocationType = sourceLocType;
					serviceParameter.getSrcLocation().remove(0);
					serviceParameter.getSrcTypeLocation().remove(0);
				}
				if(check && serviceParameter.getDstLocation().size()>0)
				{
					check = false;
					Server.searchedParameterIsDestination = true;
					destinationLoc = serviceParameter.getDstLocation().get(0);
					destinationLocType = serviceParameter.getDstTypeLocation().get(0);
					serviceParameter.getDstLocation().remove(0);
					serviceParameter.getDstTypeLocation().remove(0);
				}
				if(check && serviceParameter.getSrcFormat().size()>0)
				{
					check = false;
					Server.searchedParameterIsSource = true;
					sourceFormat = serviceParameter.getSrcFormat().get(0);
					Server.searchedParameterFormat = sourceFormat;
					sourceFormatType = serviceParameter.getSrcTypeFormat().get(0);
					Server.searchedParameterFormatType = sourceFormatType;
					serviceParameter.getSrcFormat().remove(0);
					serviceParameter.getSrcTypeFormat().remove(0);
				}
				if(check && serviceParameter.getDstFormat().size()>0)
				{
					check = false;
					Server.searchedParameterIsDestination = true;
					destinationFormat = serviceParameter.getDstFormat().get(0);
					destinationFormatType = serviceParameter.getDstTypeFormat().get(0);
					serviceParameter.getDstFormat().remove(0);
					serviceParameter.getDstTypeFormat().remove(0);
				}

				if(check && serviceParameter.getDiscoveredSrcLocation().size()>0)
				{
					check = false;
					sourceLoc = serviceParameter.getDiscoveredSrcLocation().get(0);
					sourceLocType = serviceParameter.getDiscoveredSrcTypeLocation().get(0);
					serviceParameter.getDiscoveredSrcLocation().remove(0);
					serviceParameter.getDiscoveredSrcTypeLocation().remove(0);
				}
				if(check && serviceParameter.getDiscoveredDstLocation().size()>0)
				{
					check = false;
					destinationLoc = serviceParameter.getDiscoveredDstLocation().get(0);
					destinationLocType = serviceParameter.getDiscoveredDstTypeLocation().get(0);
					serviceParameter.getDiscoveredDstLocation().remove(0);
					serviceParameter.getDiscoveredDstTypeLocation().remove(0);
				}
				if(check && serviceParameter.getDiscoveredSrcFormat().size()>0)
				{
					check = false;
					sourceFormat = serviceParameter.getDiscoveredSrcFormat().get(0);
					sourceFormatType = serviceParameter.getDiscoveredSrcTypeFormat().get(0);
					serviceParameter.getDiscoveredSrcFormat().remove(0);
					serviceParameter.getDiscoveredSrcTypeFormat().remove(0);
				}
				if(check && serviceParameter.getDiscoveredDstFormat().size()>0)
				{
					destinationFormat = serviceParameter.getDiscoveredDstFormat().get(0);
					destinationFormatType = serviceParameter.getDiscoveredDstTypeFormat().get(0);
					serviceParameter.getDiscoveredDstFormat().remove(0);
					serviceParameter.getDiscoveredDstTypeFormat().remove(0);
				}

				ChoiceNetMessageField[] payload = cnLibrary.createMarketplaceQuery(sourceLoc, destinationLoc, sourceFormat, destinationFormat, sourceLocType, destinationLocType, 
						sourceFormatType, destinationFormatType, cost, cMethod, adID, providerID);
				try {
					serviceParameter.decrementQueries();
					clientIPAddress = InetAddress.getByName(Server.marketplaceAddr);
					clientPort = Server.marketplacePort;
					packet = new Packet(PacketType.MARKETPLACE_QUERY,myName,"",myType, providerType,payload);
					System.out.println("Planner is sending Marketplace Query packet");
					send(packet);

				} catch (UnknownHostException e) {
					System.out.println("Planner failed sending Marketplace Query packet");
					e.printStackTrace();
				}
			}
			else
			{
				long id = serviceParameter.getIdentifier();
				serviceParameter = Server.getSearchParameter(id);

				System.out.println("Planner attempting discover if any recipes exist with known components");

				Server.searchParameterList.remove(0);
				// At this point only advertisements with only a single connection or no connection will be found at this time
				// Should another round of queries be made against initial discovered advertisements
				// ... maybe additional three queries per item
				serviceParameter.getGraphMatrix().run();
				// Check the Graph Matrix to see if any feasible plan can be achieved
				// Possible to have dumb recipes that cycle the provider resources e.g. SRC-A-B-A-DST rather than SRC-A-DST
				// these recipes are still valid
				ArrayList<PlannerServiceRecipe> recipes = serviceParameter.getGraphMatrix().getRecipes();
				System.out.println("Number of potential recipes: "+recipes.size());

				//serviceParameter.getGraphMatrix().printRecipe();
				int lowestPrice = Integer.MAX_VALUE;
				int currCost;
				int svcParameterCost = serviceParameter.getCost();
				for(PlannerServiceRecipe recipe: recipes)
				{
					currCost = recipe.getTotalCost();
					if(currCost<svcParameterCost)
					{
						lowestPrice = Math.min(currCost, lowestPrice);
						System.out.println("Potential recipes "+recipe.getAdvertisementList());
						if(lowestPrice == currCost)
						{
							selectedRecipe = recipe;
						}
					}
				}
				System.out.println("Selected recipe: "+selectedRecipe);
				readyToSendPacket = true;
				// update the ip address and port to initiating entity
				DiscoveredEntities entity = dEMgr.getDiscoveredEntityByName(serviceParameter.getOriginatorName());
				try {
					clientIPAddress = InetAddress.getByName(entity.getIpAddr());
					clientPort = entity.getPort();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
		}

		if(readyToSendPacket)
		{
			Packet packet = null;
			String message = "";
			if(selectedRecipe!=null)
			{
				message ="<![CDATA["+selectedRecipe.createXML()+"\n]]>";
			}
			else
			{
				message = "No recipes discovered with the given service requirements";
			}
			ChoiceNetMessageField resultsField = new ChoiceNetMessageField("Advertisement List", message, "");
			ChoiceNetMessageField[] myPayload = {resultsField};
			packet = new Packet(PacketType.PLANNER_RESPONSE,myName,"",myType, providerType,myPayload);
			System.out.println(message);

			send(packet);
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
			String url = queryValue; 
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

		if(providerType.equals("Planner"))
		{
			// TODO: Map Query Responses with advertisement search parameters
			// TODO: It would be nice if the response had an identifier which another thread could refer to inorder to map query responses with search parameter
			// Save Search parameter list along with contact
			// Load next search parameter
			// Store the response with the advertisement
			// Create new Advertisement Node for each object
			System.out.println("Marketplace Response received by a Planner");
			CouchDBResponse cResponse;
			AdvertisementDisplay myAd;
			long id = Server.searchParameterList.get(0).getIdentifier(); 
			PlannerSearchParameter searchParameter = Server.getSearchParameter(id);
			PlannerNode advertisementNode;
			System.out.println("Thread respondToMarketplaceResponse(): parse response");
			for(String x: results)
			{
				cResponse = CouchDBResponse.parseJson(x);
				if(cResponse != null)
				{
					for(CouchDBContainer currentAdv : cResponse.getRows())
					{
						myAd = currentAdv.getValue();
						if(!searchParameter.getGraphMatrix().doesNodeExist(myAd.getId()))
						{	
							System.out.println("Thread respondToMarketplaceResponse(): attempting to install ad");
							advertisementNode = new PlannerNode(myAd.getId(), myAd.getConsiderationValue(), myAd);
							if(Server.searchedParameterIsSource || Server.searchedParameterIsDestination)
							{
								if(Server.searchedParameterIsSource)
								{
									advertisementNode.setStatus(PlannerNode.NodeType.SOURCE);

									searchParameter.addDiscoveredSrcLocation(myAd.getDstLocationAddrValue(), myAd.getDstLocationAddrScheme());
									//searchParameter.addDiscoveredDstFormat(myAd.getDstFormatValue(), myAd.getDstFormatScheme());
									if(!Server.searchedParameterLocation.isEmpty())
									{
										// search among the source locations for the likely field that passed this search
										String result = advertisementNode.findMatchingSearchCriteria("Location", Server.searchedParameterLocation, Server.searchedParameterLocationType);
										advertisementNode.setSearchedParameterLocation(result);
									}
									if(!Server.searchedParameterFormat.isEmpty())
									{
										String result = advertisementNode.findMatchingSearchCriteria("Format", Server.searchedParameterFormat, Server.searchedParameterFormatType);
										advertisementNode.setSearchedParameterFormat(result);
									}
								}
								if(Server.searchedParameterIsDestination)
								{
									advertisementNode.setStatus(PlannerNode.NodeType.DESTINATION);
									searchParameter.addDiscoveredDstLocation(myAd.getSrcLocationAddrValue(), myAd.getSrcLocationAddrScheme());
									//searchParameter.addDiscoveredSrcFormat(myAd.getSrcFormatValue(), myAd.getSrcFormatScheme());
								}
							}

							searchParameter.getGraphMatrix().getNodeGraph().add(advertisementNode);
						}
						else
						{
							// PlannerNode already exist check to see if NodeType is SOURCE or DESTINATION
							// if so check to see if this ran would have tagged it as the other option
							// if so then classify this node with a special NodeType SOLUTION that should be added as a possible recipe
							advertisementNode = searchParameter.getGraphMatrix().getNode(myAd.getId());
							if((advertisementNode.getStatus().equals(PlannerNode.NodeType.DESTINATION) && Server.searchedParameterIsSource)
								|| (advertisementNode.getStatus().equals(PlannerNode.NodeType.SOURCE) && Server.searchedParameterIsDestination))
							{
								advertisementNode.setStatus(PlannerNode.NodeType.SOLUTION);
								if(!Server.searchedParameterLocation.isEmpty())
								{
									String result = advertisementNode.findMatchingSearchCriteria("Location", Server.searchedParameterLocation, Server.searchedParameterLocationType);
									advertisementNode.setSearchedParameterLocation(result);								}
								if(!Server.searchedParameterFormat.isEmpty())
								{
									String result = advertisementNode.findMatchingSearchCriteria("Format", Server.searchedParameterFormat, Server.searchedParameterFormatType);
									advertisementNode.setSearchedParameterFormat(result);
								}
							}
						}
					}
				}
			}
			handlePlannerServiceParameters();
		}
		else
		{
			// Standard View differentiated based on the interface
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
				for(String x: results)
				{
					cResponse = CouchDBResponse.parseJson(x);
					if(cResponse != null)
					{
						for(CouchDBContainer currentAdv : cResponse.getRows())
						{
							message += currentAdv.printAdvertisementDisplay();
						}
					}
					else
					{
						message = "No results were found";
					}
				}
				ChoiceNetSpeakerGUI.updateTextArea(message);
			}
		}
	}

	private void logUseAttemptAck(Packet packet) {
		// TODO Auto-generated method stub
		ChoiceNetMessageField[] payload = (ChoiceNetMessageField[]) packet.getMessageSpecific().getValue();
		String handleID = (String) payload[0].getValue();
		ChoiceNetMessageField token = payload[1];
		Token myToken = cnLibrary.extractTokenContent(token);
		String message = "<html>Received an Use Attempt Acknowlegdement for the Token: "+myToken.getId()+"with an Handle ID: "+handleID+"</html>";
		ChoiceNetSpeakerGUI.updateTextArea(message);
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
		//		Server.systemMessage = message;
		if(!Server.runningMode.equals("standalone"))
		{
			ChoiceNetSpeakerGUI.updateTextArea(message);
		}
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
		String message = "";
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
			String ipAddr = clientIPAddress.toString();
			ipAddr = ipAddr.substring(1,ipAddr.length());
			DiscoveredEntities entity = new DiscoveredEntities(originatorName, originatorType, ipAddr, clientPort,acceptedConsideration, availableConsideration);
			String id = clientIPAddress.toString()+":"+clientPort;
			dEMgr.addDiscoveredEntities(id, entity);
			message = "Entity: "+originatorName+" has been included in the Known Entity list";
			Logger.log(message);
			// prevent other clients from running this logger
			if(!Server.runningMode.equals("standalone"))
			{
				ChoiceNetSpeakerGUI.updateTextArea(message);
			}

		}
		else
		{
			message = "Name Mismatched. Intended Entity Name is {"+intendedEntityName+"} and not {"+myName+"}";

			ChoiceNetMessageField[] myPayload = createNACKPayload(PacketType.RENDEZVOUS_RESPONSE.toString(), 1, message);
			packet = new Packet(PacketType.NACK,myName,"",myType, providerType,myPayload);
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

					long eTime = 15; // where eTime is measured in minutes
					String originatorName = (String) packet.getOriginatorName().getValue();
					String tokenType = "UNKNOWN";
					if(myType.equals("Provider"))
					{
						if(providerType.equals("Marketplace"))
						{
							tokenType = "Listing";
						}
						else
						{
							tokenType = sName;
						}
						//						if(providerType.equals("Planner"))
						//						{
						//							tokenType = "Planner";
						//						}
						//						if(providerType.equals("Transport"))
						//						{
						//							tokenType = "Transport";
						//							tokenType = "Transport";
						//						}
					}
					// creates Token
					ChoiceNetMessageField token = cnLibrary.createToken(originatorName, myName, tokenType, eTime, true);

					// Send ACK with Token
					ChoiceNetMessageField transactionNum = new ChoiceNetMessageField("Transaction Number", tNumber, "");
					// TODO: Empty gateway credentials: Marketplace should provide something here
					ChoiceNetMessageField[] newPayload = {transactionNum,token}; 

					newPacket = new Packet(PacketType.TOKEN_RESPONSE,myName,"",myType,providerType,newPayload);
				}
				else
				{
					String reasonVal;
					int opCodeVal = 4;
					reasonVal = "Consideration Confirmation failed for the account "+targetConsiderationAccount;
					Logger.log(reasonVal);

					ChoiceNetMessageField[] newPayload = createNACKPayload(PacketType.TOKEN_REQUEST.toString(),  opCodeVal, reasonVal); 
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
				ChoiceNetMessageField[] newPayload = createNACKPayload(PacketType.TOKEN_REQUEST.toString(),  opCodeVal, reasonVal);
				newPacket = new Packet(PacketType.NACK,myName,"",myType,providerType,newPayload);
			}

		}
		else
		{
			int opCodeVal = 3;
			String reasonVal = "Consideration Target: {"+intendedEntityName+"} does not match this entity's name";
			Logger.log(reasonVal);
			ChoiceNetMessageField[] newPayload = createNACKPayload(PacketType.TOKEN_REQUEST.toString(),  opCodeVal, reasonVal);
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
				ChoiceNetSpeakerGUI.updateTextArea(msg);
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
		int tokenID = (Integer) payload[0].getValue(); // Token ID
		String intendedEntityName = (String) payload[2].getValue(); // Issued By
		// check that this response is both intended for this entity node 
		// TODO: also check the tokenID exist in your TokenManager
		if(intendedEntityName.equals(myName) && tokenMgr.getTokenCreationTime(tokenID)!=0)
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
			if(tokenMgr.getTokenCreationTime(tokenID)==0)
			{
				Logger.log("Listing Request: Token supplied does not match any value within the Token Manager database");
			}
			if(!intendedEntityName.equals(myName))
			{
				Logger.log("Listing Request: Entity name did not match. Intended for "+intendedEntityName);
			}
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
			ChoiceNetSpeakerGUI.updateTextArea(msg);
		}
	}

	// The assumption here is that only the ChoiceNet Gateway will have to respond to this 
	// this functionality will need to be split into its own minimum system
	private void respondToUseAttempt(Packet packet) {
		// TODO: Finish this ... 
		Logger.log("Respond to Use Attempt");
		ChoiceNetMessageField[] payload = (ChoiceNetMessageField[]) packet.getMessageSpecific().getValue();
		String trafficProp = (String) payload[0].getValue();
		ChoiceNetMessageField token = payload[1];
		payload = (ChoiceNetMessageField[]) token.getValue();
		int tokenID = (Integer) payload[0].getValue(); // Token ID
		long creationTimeID = tokenMgr.getTokenCreationTime(tokenID);
		Token myToken = TokenManager.getSingleToken(creationTimeID);
		if(myToken != null)
		{
			if(myToken.getExpirationTime() >= System.currentTimeMillis())
			{
				// Valid Token
				trafficProp = trafficProp.replace("<![CDATA[", "");
				trafficProp = trafficProp.replace("\n]]>", "");
				OpenFlowFirewallMessage msg = cnLibrary.convertXMLtoOpenFlowFireWallMessage(trafficProp);
				long result = myToken.getExpirationTime()-System.currentTimeMillis();
				result = result/1000; // Save it in seconds
				msg.setDuration(result+"");
				// return a new string with additional properties
				trafficProp = cnLibrary.getOpenFlowFireWallMessageXML(msg);
				cnLibrary.getOpenFlowFireWallMessageXML(msg);
				// Send Use Plane Signal to the controller
				sendFirewallMsg(trafficProp);

				// Send an ACK when everything is completed
				// hard coded 
				ChoiceNetMessageField handleID = new ChoiceNetMessageField("Handle ID", System.currentTimeMillis(), "");
				ChoiceNetMessageField[] newPayload = {handleID,token}; 
				Packet newPacket = new Packet(PacketType.USE_ATTEMPT_CONFIRMATION,myName,"",myType,providerType,newPayload);
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
		String query = "/_design/marketplace/_view/";
		// Retrieve CouchDB unique identifier for the document containing its entry to get its document ID, this will be used as an Advertisement ID
		String url = Server.marketplaceRESTAPI+query;
		int i = 0;
		int j = 0;
		int k = 0;
		int l = 0;
		boolean operate;
		// loop through the possible combinations between this four fields
		while(i<srcLocArr.length)
		{
			operate = false;
			searchedContent = determineQueryParameter(url, srcLocArr[i],dstLocArr[j],srcFormatArr[k],dstFormatArr[l],srcLocTypeArr[i],dstLocTypeArr[j],srcFormatTypeArr[k],dstFormatTypeArr[l]);
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
			data = url+queryField+"[\""+costType+"\","+cost+"]";
			searchedContent = new InternalMessageField(""+QueryType.COST, data, "");
			list.add(searchedContent);
		}
		// decode the advertisement ID field
		if(!adID.isEmpty())
		{
			queryField = "byID?key=";
			data = url+queryField+"[\""+adID+"\"]";
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
	private InternalMessageField determineQueryParameter(String defaultURL, String sourceLoc, String destinationLoc, String sourceFormat, String destinationFormat, 
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
			data = defaultURL+queryField+"[\""+sourceLoc+"\",\""+destinationLoc+"\",\""+sourceFormat+"\",\""+destinationFormat+"\"]";
			searchedContent = new InternalMessageField(""+QueryType.LOCATION_SRC_DST_FORMAT_SRC_DST, data, "");
			return searchedContent;
		}
		// Triplets
		if(!sourceLoc.isEmpty() && !destinationLoc.isEmpty() && !sourceFormat.isEmpty())
		{
			queryField = "bySrcDstLocationSrcFormat?key=";
			data = defaultURL+queryField+"[\""+sourceLoc+"\",\""+destinationLoc+"\",\""+sourceFormat+"\"]";
			searchedContent = new InternalMessageField(""+QueryType.LOCATION_SRC_DST_FORMAT_SRC, data, "");
			return searchedContent;
		}
		if(!sourceLoc.isEmpty() && !destinationLoc.isEmpty() && !destinationFormat.isEmpty())
		{
			queryField = "bySrcDstLocationDstFormat?key=";
			data = defaultURL+queryField+"[\""+sourceLoc+"\",\""+destinationLoc+"\",\""+destinationFormat+"\"]";
			searchedContent = new InternalMessageField(""+QueryType.LOCATION_SRC_DST_FORMAT_DST, data, "");
			return searchedContent;
		}
		if(!sourceLoc.isEmpty() && !sourceFormat.isEmpty() && !destinationFormat.isEmpty())
		{
			queryField = "bySrcLocationSrcDstFormat?key=";
			data = defaultURL+queryField+"[\""+sourceLoc+"\",\""+sourceFormat+"\",\""+destinationFormat+"\"]";
			searchedContent = new InternalMessageField(""+QueryType.LOCATION_SRC_FORMAT_SRC_DST, data, "");
			return searchedContent;
		}
		if(!destinationLoc.isEmpty() && !sourceFormat.isEmpty() && !destinationFormat.isEmpty())
		{
			queryField = "byDstLocationSrcDstFormat?key=";
			data = defaultURL+queryField+"[\""+destinationLoc+"\",\""+sourceFormat+"\",\""+destinationFormat+"\"]";
			searchedContent = new InternalMessageField(""+QueryType.LOCATION_DST_FORMAT_SRC_DST, data, "");
			return searchedContent;
		}
		// Pair Case
		if(!sourceLoc.isEmpty() && !destinationLoc.isEmpty())
		{
			queryField = "bySrcDstLocation?key=";
			data = defaultURL+queryField+"[\""+sourceLoc+"\",\""+destinationLoc+"\"]";
			searchedContent = new InternalMessageField(""+QueryType.LOCATION_SRC_DST, data, "");
			return searchedContent;
		}
		if(!sourceLoc.isEmpty() && !sourceFormat.isEmpty())
		{
			queryField = "bySrcLocationSrcFormat?key=";
			data = defaultURL+queryField+"[\""+sourceLoc+"\",\""+sourceLocType+"\",\""+sourceFormat+"\",\""+sourceFormatType+"\"]";
			searchedContent = new InternalMessageField(""+QueryType.LOCATION_SRC_FORMAT_SRC, data, "");
			return searchedContent;
		}
		if(!sourceLoc.isEmpty() && !destinationFormat.isEmpty())
		{
			queryField = "bySrcLocationDstFormat?key=";
			data = defaultURL+queryField+"[\""+sourceLoc+"\",\""+destinationFormat+"\"]";
			searchedContent = new InternalMessageField(""+QueryType.LOCATION_SRC_FORMAT_DST, data, "");
			return searchedContent;
		}
		if(destinationLoc.isEmpty() && !sourceFormat.isEmpty())
		{
			queryField = "byDstLocationSrcFormat?key=";
			data = defaultURL+queryField+"[\""+destinationLoc+"\",\""+sourceFormat+"\"]";
			searchedContent = new InternalMessageField(""+QueryType.LOCATION_DST_FORMAT_SRC, data, "");
			return searchedContent;
		}
		if(!destinationLoc.isEmpty() &&  !destinationFormat.isEmpty())
		{
			queryField = "byDstLocationDstFormat?key=";
			data = defaultURL+queryField+"[\""+destinationLoc+"\",\""+destinationFormat+"\"]";
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
			if(sourceLocType.equals("IPv4"))
			{
				data = Server.marketplaceProcessingAgent+"?query=bySrcLocationType&value="+sourceLoc+"&type=[\""+sourceLocType+"\"]";
			}
			else
			{
				queryField = "bySrcLocation?key=";
				data = defaultURL+queryField+"[\""+sourceLoc+"\",\""+sourceLocType+"\"]";
			}
			searchedContent = new InternalMessageField(""+QueryType.LOCATION_SRC, data, "");
			return searchedContent;
		}
		if(!destinationLoc.isEmpty())
		{
			if(destinationLocType.equals("IPv4"))
			{
				data = Server.marketplaceProcessingAgent+"?query=byDstLocationType&value="+destinationLoc+"&type=[\""+destinationLocType+"\"]";
			}
			else
			{
				queryField = "byDstLocation?key=";
				data = defaultURL+queryField+"[\""+destinationLoc+"\",\""+destinationLocType+"\"]";
			}
			searchedContent = new InternalMessageField(""+QueryType.LOCATION_DST, data, "");
			return searchedContent;
		}
		if(!sourceFormat.isEmpty())
		{
			queryField = "bySrcFormat?key=";
			data = defaultURL+queryField+"[\""+sourceFormat+"\",\""+sourceFormatType+"\"]";
			searchedContent = new InternalMessageField(""+QueryType.FORMAT_SRC, data, "");
			return searchedContent;
		}
		if(!destinationFormat.isEmpty())
		{
			queryField = "byDstFormat?key=";
			data = defaultURL+queryField+"[\""+destinationFormat+"\",\""+destinationFormatType+"\"]";
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
