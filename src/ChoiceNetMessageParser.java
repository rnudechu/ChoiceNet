import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class ChoiceNetMessageParser {

	public ChoiceNetMessageField getChoiceNetMessage (String packetXML, String attributeName)
	{
		ChoiceNetMessageField message = new ChoiceNetMessageField(attributeName, "", "");
		try
		{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new InputSource(new StringReader(packetXML)));
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("field");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					if(eElement.getAttribute("attributeName").equals(attributeName))
					{
						String url = eElement.getAttribute("url");
						message.setUrl(url);
						if(!attributeName.equals("Message Specific"))
						{
							String value = eElement.getElementsByTagName("value").item(0).getTextContent();
							message.setValue(value);
						}
						else
						{

						}

						return message;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public ChoiceNetMessageField[] getChoiceNetMessageArray (String packetXML, String attributeName)
	{
		ArrayList<ChoiceNetMessageField> storage = new ArrayList<ChoiceNetMessageField>();
		ChoiceNetMessageField message = null;
		ChoiceNetMessageField[] payload = null;
		try
		{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new InputSource(new StringReader(packetXML)));
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("field");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					if(eElement.getAttribute("attributeName").equals(attributeName))
					{
						message = new ChoiceNetMessageField(attributeName, "", "");
						String url = eElement.getAttribute("url");
						message.setUrl(url);
						if(!attributeName.equals("Message Specific"))
						{
							String value = eElement.getElementsByTagName("value").item(0).getTextContent();
							message.setValue(value);
							storage.add(message);
						}
						else
						{

						}

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		payload = new ChoiceNetMessageField[storage.size()];
		int i = 0;
		for(ChoiceNetMessageField msg: storage)
		{
			payload[i] = msg;
			i++;
		}
		return payload;
	}

	// Return the appropriate PacketType Enum
	public PacketType findPacketType(String myPktType)
	{
		for(PacketType pktType: PacketType.values())
		{
			if(pktType.name().equals(myPktType))
			{
				return pktType;
			}
		}
		return null;
	}

	public ChoiceNetMessageField[] getRequestElements(String pktXML)
	{
		ArrayList<ChoiceNetMessageField> queries = new ArrayList<ChoiceNetMessageField>();
		ChoiceNetMessageField[] contents;
		// Loop through all possible QueryTypes and see if XML has any matching fields
		for(RequestType requestType: RequestType.values())
		{
			contents = getChoiceNetMessageArray(pktXML,requestType.toString());
			if(contents.length>0)
			{
				for(ChoiceNetMessageField q: contents)
				{
					queries.add(q);
				}
			}
		}
		// Loop over the stored arrays in Queries and store them inside the queryPayload
		ChoiceNetMessageField[] payload = new ChoiceNetMessageField[queries.size()];
		int i = 0;
		for(ChoiceNetMessageField q: queries)
		{
			payload[i] = q;
			i++;
		}
		return payload;
	}


	// Parser functions 
	public Packet parseChoiceNetMessageXML (String pktXML)
	{
		ChoiceNetMessageField oName = getChoiceNetMessage(pktXML,"Originator Name");
		ChoiceNetMessageField oSign = getChoiceNetMessage(pktXML,"Originator Signature");
		ChoiceNetMessageField oType = getChoiceNetMessage(pktXML,"Originator Type");
		ChoiceNetMessageField oProviderType = getChoiceNetMessage(pktXML,"Originator Provider Type");
		ChoiceNetMessageField messageType = getChoiceNetMessage(pktXML,"Message Type");
		String myPktType = (String) messageType.getValue();
		PacketType packetType = findPacketType(myPktType);
		if(packetType != null)
		{
			String myName = (String) oName.getValue();
			String myType = (String) oType.getValue();
			String mySignature = (String) oSign.getValue();
			String providerType = (String) oProviderType.getValue();
			ChoiceNetMessageField[] payload = parseMessageSpecific(packetType, pktXML);
			Packet packet = new Packet(packetType,myName,mySignature,myType, providerType,payload);
			return packet;
		}
		else
		{
			System.out.println("ChoiceNetMessageParser Error: The supplied packet XML does not have a known Message Type+\n===============\n"+pktXML+"===============");
			return null;
		}

	}

	public ChoiceNetMessageField[] parseMessageSpecific (PacketType pktType, String pktXML)
	{
		ChoiceNetMessageField[] payload = null;

		if(pktType == PacketType.RENDEZVOUS_REQUEST)
		{
			payload = parseRendezvousRequestMessage(pktXML);
		}
		if(pktType == PacketType.RENDEZVOUS_RESPONSE)
		{
			payload = parseRendezvousResponseMessage(pktXML);
		}
		if(pktType == PacketType.TOKEN_REQUEST)
		{
			payload = parseTransferConsiderationMessage(pktXML);
		}
		//if(pktType == PacketType.ACK_AND_SEND_TOKEN)CONSIDERATION_ACK
		if(pktType == PacketType.TOKEN_RESPONSE)
		{
			payload = parseConsiderationAcknowledgementMessage(pktXML);
		}
		if(pktType == PacketType.LISTING_REQUEST)
		{
			payload = parseListingRequestMessage(pktXML);
		}
		if(pktType == PacketType.LISTING_CONFIRMATION)
		{
			payload = parseListingResponseMessage(pktXML);
		}
		if(pktType == PacketType.MARKETPLACE_QUERY)
		{
			payload = parseMarketplaceQueryMessage(pktXML);
		}
		if(pktType == PacketType.MARKETPLACE_RESPONSE)
		{
			payload = parseMarketplaceResponseMessage(pktXML);
		}
		if(pktType == PacketType.PLANNER_REQUEST)
		{
			payload = parsePlannerRequestMessage(pktXML);
		}
		if(pktType == PacketType.PLANNER_RESPONSE)
		{
			payload = parsePlannerResponseMessage(pktXML);
		}
		if(pktType == PacketType.NACK)
		{
			payload = parseNACKMessage(pktXML);
		}
		if(pktType == PacketType.USE_ATTEMPT)
		{
			payload = parseUseAttemptMessage(pktXML);
		}
		if(pktType == PacketType.USE_ATTEMPT_CONFIRMATION)
		{
			payload = parseUseAttemptConfirmationMessage(pktXML);
		}

		return payload;
	}

	// Parser for Message Specific 
	public ChoiceNetMessageField[] parseRendezvousRequestMessage (String pktXML)
	{
		ChoiceNetMessageField rendezvousTarget = getChoiceNetMessage(pktXML,"Rendezvous Target");
		ChoiceNetMessageField acceptedConsiderationFld = getChoiceNetMessage(pktXML,"Accepted Consideration");
		ChoiceNetMessageField availableConsiderationFld = getChoiceNetMessage(pktXML,"Available Consideration");
		ChoiceNetMessageField[] payload = {rendezvousTarget,acceptedConsiderationFld,availableConsiderationFld};
		return payload;
	}

	public ChoiceNetMessageField[] parseRendezvousResponseMessage (String pktXML)
	{
		ChoiceNetMessageField rendezvousTarget = getChoiceNetMessage(pktXML,"Target's Originator Name");
		ChoiceNetMessageField acceptedConsiderationFld = getChoiceNetMessage(pktXML,"Accepted Consideration");
		ChoiceNetMessageField availableConsiderationFld = getChoiceNetMessage(pktXML,"Available Consideration");
		ChoiceNetMessageField[] payload = {rendezvousTarget,acceptedConsiderationFld,availableConsiderationFld};
		return payload;
	}

	public ChoiceNetMessageField[] parseTransferConsiderationMessage (String pktXML)
	{
		ChoiceNetMessageField transactionNumber = getChoiceNetMessage(pktXML,"Transaction Number");
		String value = (String) transactionNumber.getValue();
		transactionNumber.setValue(Integer.parseInt(value));
		ChoiceNetMessageField considerationTarget = getChoiceNetMessage(pktXML,"Consideration Target");
		ChoiceNetMessageField serviceName = getChoiceNetMessage(pktXML,"Service Name");
		ChoiceNetMessageField considerationExchMethod = getChoiceNetMessage(pktXML,"Consideration Exchange Method");
		ChoiceNetMessageField considerationExchValue = getChoiceNetMessage(pktXML,"Consideration Exchange Value");
		ChoiceNetMessageField[] payload = {transactionNumber,considerationTarget,serviceName,considerationExchMethod,considerationExchValue};
		return payload;
	}

	public ChoiceNetMessageField[] parseConsiderationAcknowledgementMessage (String pktXML)
	{
		ChoiceNetMessageField transactionNumber = getChoiceNetMessage(pktXML,"Transaction Number");
		String value = (String) transactionNumber.getValue();
		transactionNumber.setValue(Integer.parseInt(value));
		ChoiceNetMessageField token = parseTokenMessage(pktXML);
		ChoiceNetMessageField[] payload = {transactionNumber,token};
		return payload;
	}

	public ChoiceNetMessageField parseTokenMessage(String pktXML)
	{
		// Fields within the Token
		ChoiceNetMessageField token = getChoiceNetMessage(pktXML,"Token");
		ChoiceNetMessageField tokenID = getChoiceNetMessage(pktXML,"Token ID");
		String value = (String) tokenID.getValue();
		tokenID.setValue(Integer.parseInt(value));
		ChoiceNetMessageField issuedTo = getChoiceNetMessage(pktXML,"Issued To");
		ChoiceNetMessageField issuedBy = getChoiceNetMessage(pktXML,"Issued By");
		ChoiceNetMessageField sName = getChoiceNetMessage(pktXML,"Service Name");
		ChoiceNetMessageField eTime = getChoiceNetMessage(pktXML,"Expiration Time");
		value = (String) eTime.getValue();
		eTime.setValue(Long.parseLong(value));
		ChoiceNetMessageField[] tokenPayload = {tokenID, issuedTo, issuedBy, sName, eTime};
		token.setValue(tokenPayload);
		return token;
	}

	private ChoiceNetMessageField[] parseListingRequestMessage(String pktXML) {
		ChoiceNetMessageField advertisement = getChoiceNetMessage(pktXML,"Advertisement");
		ChoiceNetMessageField token = parseTokenMessage(pktXML);
		ChoiceNetMessageField[] payload = {advertisement,token};
		return payload;
	}

	private ChoiceNetMessageField[] parseListingResponseMessage(String pktXML) {
		ChoiceNetMessageField[] adID = getChoiceNetMessageArray(pktXML,"Advertisement ID");
		ChoiceNetMessageField[] sName = getChoiceNetMessageArray(pktXML,"Service Name");
		int adSize = adID.length;
		int serviceSize = sName.length;
		ChoiceNetMessageField[] payload = new ChoiceNetMessageField[adSize];
		System.out.println("Listing Response Deserializer");
		if(adSize == serviceSize)
		{
			for(int i =0; i<adSize; i++)
			{
				ChoiceNetMessageField[] thisPayload = {adID[i], sName[i]};
				payload[i] = new ChoiceNetMessageField("Advertised Service", thisPayload, "");
			}
		}
		return payload;
	}

	private ChoiceNetMessageField[] parseMarketplaceQueryMessage(String pktXML) {
		ChoiceNetMessageField[] queryPayload = getRequestElements(pktXML);
		ChoiceNetMessageField searchParameter = new ChoiceNetMessageField("Search Parameter", queryPayload, "");
		ChoiceNetMessageField[] payload = {searchParameter}; 
		return payload;
	}

	private ChoiceNetMessageField[] parseMarketplaceResponseMessage(String pktXML) {
		// TODO Auto-generated method stub
		ChoiceNetMessageField results = getChoiceNetMessage(pktXML,"Results");
		ChoiceNetMessageField[] payload = {results}; 
		return payload;
	}

	private ChoiceNetMessageField[] parsePlannerRequestMessage(String pktXML) {
		ChoiceNetMessageField[] requestPayload = getRequestElements(pktXML);
		ChoiceNetMessageField serviceReq = new ChoiceNetMessageField("Service Requirement", requestPayload, "");
		ChoiceNetMessageField[] payload = {serviceReq}; 
		return payload;
	}

	private ChoiceNetMessageField[] parsePlannerResponseMessage(String pktXML) {
		ChoiceNetMessageField adList = getChoiceNetMessage(pktXML,"Advertisement List");
		ChoiceNetMessageField costType = getChoiceNetMessage(pktXML,"COST_TYPE");
		ChoiceNetMessageField costValue = getChoiceNetMessage(pktXML,"COST");
		ChoiceNetMessageField[] payload = {adList, costType, costValue}; 
		return payload;
	}

	private ChoiceNetMessageField[] parseNACKMessage(String pktXML) {
		ChoiceNetMessageField nackType = getChoiceNetMessage(pktXML,"NACK Type");
		ChoiceNetMessageField operationCode = getChoiceNetMessage(pktXML,"Operation Code");
		ChoiceNetMessageField reason = getChoiceNetMessage(pktXML,"Reason");
		String value = (String) operationCode.getValue();
		operationCode.setValue(Integer.parseInt(value));
		ChoiceNetMessageField[] payload = {operationCode,nackType,reason};
		return payload;
	}
	
	private ChoiceNetMessageField[] parseUseAttemptMessage(String pktXML) {
		ChoiceNetMessageField trafficProp = getChoiceNetMessage(pktXML,"Traffic Properties");
		ChoiceNetMessageField token = parseTokenMessage(pktXML);
		ChoiceNetMessageField[] payload = {trafficProp,token};
		return payload;
	}
	
	private ChoiceNetMessageField[] parseUseAttemptConfirmationMessage(String pktXML) {
		ChoiceNetMessageField trafficProp = getChoiceNetMessage(pktXML,"Handle ID");
		ChoiceNetMessageField token = parseTokenMessage(pktXML);
		ChoiceNetMessageField[] payload = {trafficProp,token};
		return payload;
	}

}
