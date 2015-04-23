import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class ChoiceNetLibrary {

	private static ChoiceNetLibrary instance = new ChoiceNetLibrary();
	TokenManager tokenMgr = TokenManager.getInstance();
	AdvertisementManager adMgr = AdvertisementManager.getInstance();
	// Parsers
	ChoiceNetMessageParser cnMessageParser = new ChoiceNetMessageParser();
	
	public static ChoiceNetLibrary getInstance() 
	{
		return instance;
	}

	// 	========================== XML Reader 2.0 ================================
	// General function to turn Java ChoiceNet Message constructs to XML
	public String createPacketXML(Packet pkt)
	{
		String xml = "";
		try
		{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("choicenetmessage");
			Element result;
			doc.appendChild(rootElement);
			// originatorName
			result = createChoiceNetMessageFieldXML(doc, pkt.getOriginatorName());
			rootElement.appendChild(result);
			// originatorSignature
			result = createChoiceNetMessageFieldXML(doc, pkt.getOriginatorSignature());
			rootElement.appendChild(result);
			// originatorType
			result = createChoiceNetMessageFieldXML(doc, pkt.getOriginatorType());
			rootElement.appendChild(result);
			// originatorProviderType
			result = createChoiceNetMessageFieldXML(doc, pkt.getOriginatorProviderType());
			rootElement.appendChild(result);
			// messageType
			result = createChoiceNetMessageFieldXML(doc, pkt.getMessageType());
			rootElement.appendChild(result);
			// messageSpecific
			result = createChoiceNetMessageFieldXML(doc, pkt.getMessageSpecific());
			rootElement.appendChild(result);

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
//			StreamResult sResult = new StreamResult(new File("C:\\file.xml"));
			StringWriter writer = new StringWriter();
			StreamResult sResult = new StreamResult(writer);
			
			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);

			transformer.transform(source, sResult);
			xml = writer.getBuffer().toString();

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
		

		return xml;
	}

	private Element createChoiceNetMessageFieldXML(Document doc, ChoiceNetMessageField pkt)
	{
		Element field = doc.createElement("field");
		field.setAttribute("attributeName", pkt.getAttributeName());
		field.setAttribute("url", pkt.getUrl());
		Element value = doc.createElement("value");

		if(pkt.getValue().getClass().equals(ChoiceNetMessageField[].class))
		{
			value = createChoiceNetMessageFieldsXML(doc, (ChoiceNetMessageField[]) pkt.getValue(), value);
		}
		else
		{
			String myValue = "UNKNOWN";
			boolean containsCDATA = false;
			if(pkt.getValue().getClass().equals(String.class))
			{
				myValue = (String) pkt.getValue();
				if(myValue.contains("CDATA"))
				{
					myValue = myValue.replace("<![CDATA[", "");
					myValue = myValue.replace("]]>", "");
					containsCDATA = true;
				}
			}
			if(pkt.getValue().getClass().equals(Integer.class))
			{
				int temp = (Integer) pkt.getValue();
				myValue = Integer.toString(temp);
			}
			if(pkt.getValue().getClass().equals(Long.class))
			{
				long temp = (Long) pkt.getValue();
				myValue = Long.toString(temp);
			}
			
			if(containsCDATA)
			{
				value.appendChild(doc.createCDATASection(myValue));
			}
			else
			{
				value.appendChild(doc.createTextNode(myValue));
			}
		}
		field.appendChild(value);

		return field;
	}
	private Element createChoiceNetMessageFieldsXML(Document doc, ChoiceNetMessageField[] pkt, Element parentElement)
	{
		Element result;
		for(int i = 0; i<pkt.length; i++)
		{
			result = createChoiceNetMessageFieldXML(doc, pkt[i]);
			parentElement.appendChild(result);
		}
		return parentElement;
	}
	
	public Packet convertXMLtoPacket (String xml)
	{
		Packet packet = null;
		packet = cnMessageParser.parseChoiceNetMessageXML(xml);
		
		return packet;
	}

	
	
	// 	========================== XML Reader ================================
	public int getChildElementCount(NodeList childNodes) {
		int count = 0;
		for (int i = 0; i < childNodes.getLength(); i++) {
			if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
				count++;
			}
		}
		return count;
	}
	
	public ArrayList<Advertisement> getAdvertisementsFromXML(String filename, String source)
	{
		ArrayList<Advertisement> result = new ArrayList<Advertisement>();
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = null;
			if(source.equals("File") || source.equals("String"))
			{
				if(source.equals("File"))
				{
					File fXmlFile = new File(filename);
					doc = dBuilder.parse(fXmlFile);
				}
				if(source.equals("String"))
				{
					System.out.println("ChoiceNetLibrary: String parameter given");
					doc = dBuilder.parse(new InputSource(new StringReader(filename)));
				}
			}
			else
			{
				System.out.println("ChoiceNetLibrary: Error getting advertisements from XML (Only String and File are supported)");
			}
			doc.getDocumentElement().normalize();

			System.out.println("Root element:" + doc.getDocumentElement().getNodeName());

			NodeList nList = doc.getElementsByTagName("advertisement");
			int advertisementCount = nList.getLength();
			System.out.println("Advertisement Count: " + advertisementCount);
			System.out.println("----------------------------");
			NodeList myList, thisList = null;
			Node myNode = null;
			Element myElement = null;
			NamedNodeMap attributes = null;
			System.out.println(">>> XML "+filename);
			int priceValue = 0;
			int numLoc = 0;
			int numFormat = 0;
			String type, addressType = "", portalType = "", location = "";
			String priceMethod, pValue, providerID, provisioningParameters, purchasePortal, advertiserName, serviceName, serviceType, 
			serviceDescription, srcAddressScheme, srcAddressValue, dstAddressScheme, dstAddressValue, srcFormatScheme, srcFormatValue, dstFormatScheme, dstFormatValue;
			priceMethod = pValue = providerID = provisioningParameters = purchasePortal = advertiserName = serviceName = serviceType = serviceDescription =  
					srcAddressScheme = srcAddressValue = dstAddressScheme = dstAddressValue = srcFormatScheme = srcFormatValue = dstFormatScheme = dstFormatValue = null;
			ProvisioningProperty pProp;
			ArrayList<ProvisioningProperty> serviceProperties;
			for (int temp = 0; temp < advertisementCount; temp++) 
			{
				Node nNode = nList.item(temp);
				serviceProperties = new ArrayList<ProvisioningProperty>();
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					// Price
					myList = doc.getElementsByTagName("price");
					priceMethod = getAttributes(myList, "method",0);
					pValue = getAttributes(myList, "value",0);
					priceValue = Integer.parseInt(pValue);
					System.out.println("Hello "+priceMethod);
					// Provider ID
					providerID = doc.getElementsByTagName("providerID").item(0).getTextContent();
					providerID = providerID.trim();
					System.out.println("Hello "+providerID);
					// Purchase Portal
					myList = doc.getElementsByTagName("purchasePortal");
					myNode = myList.item(temp);
					if (myNode.getNodeType() == Node.ELEMENT_NODE) {
						myElement = (Element) myNode;
						//						myList = myElement.getElementsByTagName("source_location");
						myList = myElement.getElementsByTagName("destination_location");
						portalType = getAttributes(myList, "scheme", 0);
						purchasePortal = getAttributes(myList, "value", 0);
						System.out.println("My list "+portalType+" "+purchasePortal+"<<<");
						purchasePortal = purchasePortal.trim();
					}
					System.out.println("Hello "+purchasePortal);
					// Provisioning Parameters
					myList = doc.getElementsByTagName("provisioningParameters");
					myNode = myList.item(temp);
					provisioningParameters = "";
					if (myNode.getNodeType() == Node.ELEMENT_NODE) {
						myElement = (Element) myNode;
						myList = myElement.getElementsByTagName("ports");
						for(int i=0; myList.getLength()>0 && i<myList.getLength(); i++)
						{
							provisioningParameters = getAttributes(myList, "value", i);
							pProp = new ProvisioningProperty("Ports", provisioningParameters);
							serviceProperties.add(pProp);
						}
						myList = myElement.getElementsByTagName("wavelength");
						for(int i=0; myList.getLength()!=0 && i<myList.getLength(); i++)
						{
							provisioningParameters = getAttributes(myList, "value", i);
							pProp = new ProvisioningProperty("Wavelength", provisioningParameters);
							serviceProperties.add(pProp);
						}
					}
					System.out.println("Hello "+provisioningParameters);
					// Service Information
					myList = doc.getElementsByTagName("service");
					myNode = myList.item(temp);
					if (myNode.getNodeType() == Node.ELEMENT_NODE) {
						myElement = (Element) myNode;
						serviceName =  myElement.getElementsByTagName("name").item(0).getTextContent();
						serviceDescription =  myElement.getElementsByTagName("description").item(0).getTextContent();//	
						myList = myElement.getElementsByTagName("details");
						myNode = myList.item(0);

						myList = myElement.getElementsByTagName("source_location");
						System.out.println(myList.getLength());

						for(int i=0;i<myList.getLength();i++)
						{
							srcAddressScheme = getAttributes(myList, "scheme", i);
							srcAddressValue = getAttributes(myList, "value", i);
						}
						System.out.println("Scheme "+srcAddressScheme+" Value "+srcAddressValue);
						myList = myElement.getElementsByTagName("destination_location");
						for(int i=0;i<myList.getLength();i++)
						{
							dstAddressScheme = getAttributes(myList, "scheme", i);
							dstAddressValue = getAttributes(myList, "value", i);
						}
						System.out.println("Scheme "+dstAddressScheme+" Value "+dstAddressValue);
						myList = myElement.getElementsByTagName("source_format");
						for(int i=0;i<myList.getLength();i++)
						{
							srcFormatScheme = getAttributes(myList, "scheme", i);
							srcFormatValue = getAttributes(myList, "value", i);
						}
						System.out.println("Scheme "+srcFormatScheme+" Value "+srcFormatValue);
						myList = myElement.getElementsByTagName("destination_format");
						for(int i=0;i<myList.getLength();i++)
						{	
							dstFormatScheme = getAttributes(myList, "scheme", i);
							dstFormatValue = getAttributes(myList, "value", i);
						}
						System.out.println("Scheme "+dstFormatScheme+" Value "+dstFormatValue);
					}
					// Print Out
					String printOut = "Advertisement \n" +
							"Provider ID: "+providerID+"\n" +
							"Provisioning Parameters: "+provisioningParameters+"\n" +
							"Service Name: "+serviceName+"\n";
					System.out.println(printOut);

				}
				// Assume 
				int advertiserPortAddress = -1;
				String advertiserAddress = "";
				if(portalType.equals("UDPv4") || portalType.equals("TCPv4"))
				{
					String[] addr = purchasePortal.split(":");
					advertiserAddress = addr[0];
					advertiserPortAddress = Integer.parseInt(addr[1]);
				}
				else
				{
					advertiserAddress = purchasePortal;
					advertiserPortAddress = 0;
				}

				ProvisioningProperty sProp[] = new ProvisioningProperty[serviceProperties.size()];
				for(int i = 0; i<serviceProperties.size();i++)
				{
					sProp[i] = serviceProperties.get(i);
				}
				System.out.println(srcFormatValue);
				Cost myCost = new Cost(priceMethod, pValue);

				Service myService = new Service(serviceName, serviceType, srcAddressScheme, srcAddressValue, dstAddressScheme, dstAddressValue, srcFormatScheme, srcFormatValue, dstFormatScheme, dstFormatValue, sProp, serviceDescription);
				System.out.println(myService);
				Advertisement myAd = new Advertisement(priceMethod, priceValue, providerID, myService, advertiserAddress, advertiserPortAddress, portalType, 0,"UNKNOWN","UNKNOWN");
				result.add(myAd);
			}
		} catch (Exception e) {
			Server.systemMessage = e.getMessage();
			//e.printStackTrace();
		}

		return result;
	}
	
	public String getAttributes(NodeList list, String attribute, int index)
	{
		String results = "";
		Element element;
		Node myNode = list.item(index);
		if (myNode.getNodeType() == Node.ELEMENT_NODE) {

			element = (Element) myNode;
			results = element.getAttribute(attribute);
		}
		return results;
	}

	public void storeAdvertisement(ArrayList<Advertisement> advertisements)
	{
		for(Advertisement myAds: advertisements)
		{
			adMgr.addAdvertisement(myAds);
		}
	}
	
	public String getServiceNameFromAdvertisementXML(String filename)
	{
		String serviceName = "";
		try {
			File fXmlFile = new File(filename);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			//optional, but recommended
			//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			System.out.println("Root element:" + doc.getDocumentElement().getNodeName());

			NodeList nList = doc.getElementsByTagName("advertisement");
			int advertisementCount = nList.getLength();
			System.out.println("----------------------------");
			NodeList myList = null;
			Node myNode = null;
			Element myElement = null;
			for (int temp = 0; temp < advertisementCount; temp++) {

				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					// Service Information
					myList = doc.getElementsByTagName("service");
					myNode = myList.item(temp);
					if (myNode.getNodeType() == Node.ELEMENT_NODE) {
						myElement = (Element) myNode;
						serviceName =  myElement.getElementsByTagName("name").item(0).getTextContent();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("----------------------------");
		return serviceName;
	}

	public ChoiceNetMessageField createAdvertisement(Advertisement myAd)
	{
		// Service Object
		ChoiceNetMessageField service = createService(myAd.getService());
		// Consideration
		//		ChoiceNetMessageField cMethod = new ChoiceNetMessageField("Consideration Method", considerationMethod, "");
		//		ChoiceNetMessageField cValue = new ChoiceNetMessageField("Consideration Value", considerationValue, "");
		//		ChoiceNetMessageField[] cPayload = {cMethod,cValue};  
		//		ChoiceNetMessageField consideration = new ChoiceNetMessageField("Consideration", cPayload, "");
		ChoiceNetMessageField cPayload = new ChoiceNetMessageField(myAd.getConsiderationMethod(), myAd.getConsiderationValue(), "");
		ChoiceNetMessageField consideration = new ChoiceNetMessageField("Consideration", cPayload, "");
		// Provider Economy Plane Address
		ChoiceNetMessageField addressingScheme = new ChoiceNetMessageField("Addressing Scheme", myAd.getAdvertiserAddressScheme(), "");
		ChoiceNetMessageField addressingValue = new ChoiceNetMessageField("Addressing Value", myAd.getAdvertiserAddress()+":"+myAd.getAdvertiserPortAddress(), "");
		ChoiceNetMessageField entityName = new ChoiceNetMessageField("Entity's Name", myAd.getEntityName(), "");
		ChoiceNetMessageField[] value = {addressingScheme,addressingValue,entityName};
		ChoiceNetMessageField economyAddress = new ChoiceNetMessageField("Provider Economy Plane Address", value, "");
		// Advertisement Object
		ChoiceNetMessageField[] payload = {service,consideration,economyAddress};
		ChoiceNetMessageField advertisement = new ChoiceNetMessageField("Advertisement", payload, "");
		return advertisement;
	}
	/**
	public ChoiceNetMessageField createAdvertisement(String considerationMethod, int considerationValue, 
			String addrSchemeVal, String addrVal, String advertiserName,
			String name, String type, String description, String pName, String pValue, 
			String srcAddrScheme, String srcAddrVal, String dstAddrScheme, String dstAddrVal)
	{
		// Service Object
		ChoiceNetMessageField service = createService(name, type, description, pName, pValue, srcAddrScheme, srcAddrVal, dstAddrScheme, dstAddrVal);
		// Consideration
		//		ChoiceNetMessageField cMethod = new ChoiceNetMessageField("Consideration Method", considerationMethod, "");
		//		ChoiceNetMessageField cValue = new ChoiceNetMessageField("Consideration Value", considerationValue, "");
		//		ChoiceNetMessageField[] cPayload = {cMethod,cValue};  
		//		ChoiceNetMessageField consideration = new ChoiceNetMessageField("Consideration", cPayload, "");
		ChoiceNetMessageField cPayload = new ChoiceNetMessageField(considerationMethod, considerationValue, "");
		ChoiceNetMessageField consideration = new ChoiceNetMessageField("Consideration", cPayload, "");
		// Provider Economy Plane Address
		ChoiceNetMessageField addressingScheme = new ChoiceNetMessageField("Addressing Scheme", addrSchemeVal, "");
		ChoiceNetMessageField addressingValue = new ChoiceNetMessageField("Addressing Value", addrVal, "");
		ChoiceNetMessageField entityName = new ChoiceNetMessageField("Entity's Name", advertiserName, "");
		ChoiceNetMessageField[] value = {addressingScheme,addressingValue,entityName};
		ChoiceNetMessageField economyAddress = new ChoiceNetMessageField("Provider Economy Plane Address", value, "");
		// Advertisement Object
		ChoiceNetMessageField[] payload = {service,consideration,economyAddress};
		ChoiceNetMessageField advertisement = new ChoiceNetMessageField("Advertisement", payload, "");
		return advertisement;
	}
	 */
	/**
	 * Create Service Object
	 * 
	 * @param name
	 * @param type
	 * @param description
	 * @param pName
	 * @param pValue
	 * @param srcAddrScheme
	 * @param srcAddrVal
	 * @param dstAddrScheme
	 * @param dstAddrVal
	 * @return
	 */
	public ChoiceNetMessageField createService(Service myService)
	{
		ChoiceNetMessageField serviceName = new ChoiceNetMessageField("Service Name", myService.getName(), "");
		ChoiceNetMessageField serviceType = new ChoiceNetMessageField("Service Type", myService.getType(), "");
		ChoiceNetMessageField serviceDescription = new ChoiceNetMessageField("Description", myService.getDescription(), "");

		// Service Specific Value
		ProvisioningProperty[] prop = myService.getProperty();
		ChoiceNetMessageField[] property = new ChoiceNetMessageField[prop.length*2];
		int j = 0;
		for(int i=0;i<prop.length; i++)
		{
			ChoiceNetMessageField propertyName = new ChoiceNetMessageField("Property Name", prop[i].getType(), "");
			ChoiceNetMessageField propertyValue = new ChoiceNetMessageField("Property Value", prop[i].getValue(), "");
			property[j] = propertyName;
			property[j+1] = propertyValue;
			j+=2;
		}

		ChoiceNetMessageField serviceProperty = new ChoiceNetMessageField("Service Specific Property", property, "");
		ChoiceNetMessageField srcLocation = null;
		ChoiceNetMessageField dstLocation = null;
		ChoiceNetMessageField srcFormat = null;
		ChoiceNetMessageField dstFormat = null;


		if(myService.getSrcLocationAddrScheme() !=null)
		{
			// Location (Source)
			ChoiceNetMessageField srcLocScheme = new ChoiceNetMessageField("Addressing Scheme", myService.getSrcLocationAddrScheme(), "");
			ChoiceNetMessageField srcLocVal = new ChoiceNetMessageField("Addressing Value", myService.getSrcLocationAddrValue(), "");
			ChoiceNetMessageField[] srcLocationVal = {srcLocScheme,srcLocVal};
			srcLocation = new ChoiceNetMessageField("Location", srcLocationVal, "");
		}
		if(myService.getDstLocationAddrScheme() !=null)
		{
			// Location (Destination)
			ChoiceNetMessageField dstLocScheme = new ChoiceNetMessageField("Addressing Scheme", myService.getDstLocationAddrScheme(), "");
			ChoiceNetMessageField dstLocVal = new ChoiceNetMessageField("Addressing Value", myService.getDstLocationAddrValue(), "");
			ChoiceNetMessageField[] dstLocationVal = {dstLocScheme,dstLocVal};
			dstLocation = new ChoiceNetMessageField("Location", dstLocationVal, "");
		}
		if(myService.getSrcFormatScheme() !=null)
		{
			// Format (Source)
			ChoiceNetMessageField srcFormatScheme = new ChoiceNetMessageField("Addressing Scheme", myService.getSrcFormatScheme(), "");
			ChoiceNetMessageField srcFormatVal = new ChoiceNetMessageField("Addressing Value", myService.getSrcFormatValue(), "");
			ChoiceNetMessageField[] srcLocationVal = {srcFormatScheme,srcFormatVal};
			srcFormat = new ChoiceNetMessageField("Location", srcLocationVal, "");
		}
		if(myService.getDstFormatScheme() !=null)
		{
			// Format (Destination)
			ChoiceNetMessageField dstFormatScheme = new ChoiceNetMessageField("Addressing Scheme", myService.getDstFormatScheme(), "");
			ChoiceNetMessageField dstFormatVal = new ChoiceNetMessageField("Addressing Value", myService.getDstFormatValue(), "");
			ChoiceNetMessageField[] dstLocationVal = {dstFormatScheme,dstFormatVal};
			dstFormat = new ChoiceNetMessageField("Location", dstLocationVal, "");
		}
		ChoiceNetMessageField[] payload = {serviceName,serviceType,serviceDescription,serviceProperty,srcLocation,dstLocation,srcFormat,dstFormat};

		ChoiceNetMessageField service = new ChoiceNetMessageField("Service", payload, "");
		return service;
	}

	public ChoiceNetMessageField createService(String name, String type, String description, 
			String pName, String pValue, String srcAddrScheme, String srcAddrVal, String dstAddrScheme, String dstAddrVal)
	{
		ChoiceNetMessageField serviceName = new ChoiceNetMessageField("Service Name", name, "");
		ChoiceNetMessageField serviceType = new ChoiceNetMessageField("Service Type", type, "");
		ChoiceNetMessageField serviceDescription = new ChoiceNetMessageField("Description", description, "");
		// Service Specific Value
		ChoiceNetMessageField propertyName = new ChoiceNetMessageField("Property Name", pName, "");
		ChoiceNetMessageField propertyValue = new ChoiceNetMessageField("Property Value", pValue, "");
		ChoiceNetMessageField[] property = {propertyName,propertyValue};
		ChoiceNetMessageField serviceProperty = new ChoiceNetMessageField("Service Specific Property", property, "");
		// Location (Source)
		ChoiceNetMessageField srcLocScheme = new ChoiceNetMessageField("Addressing Scheme", srcAddrScheme, "");
		ChoiceNetMessageField srcLocVal = new ChoiceNetMessageField("Addressing Value", srcAddrVal, "");
		ChoiceNetMessageField[] srcLocationVal = {srcLocScheme,srcLocVal};
		ChoiceNetMessageField srcLocation = new ChoiceNetMessageField("Location", srcLocationVal, "");
		// Location (Destination)
		ChoiceNetMessageField dstLocScheme = new ChoiceNetMessageField("Addressing Scheme", dstAddrScheme, "");
		ChoiceNetMessageField dstLocVal = new ChoiceNetMessageField("Addressing Value", dstAddrVal, "");
		ChoiceNetMessageField[] dstLocationVal = {dstLocScheme,dstLocVal};
		ChoiceNetMessageField dstLocation = new ChoiceNetMessageField("Location", dstLocationVal, "");
		ChoiceNetMessageField[] payload = {serviceName,serviceType,serviceDescription,serviceProperty,srcLocation,dstLocation};

		ChoiceNetMessageField service = new ChoiceNetMessageField("Service", payload, "");
		return service;
	}

	public ChoiceNetMessageField createToken(String issuedToVal, String myName, String sName, long eTime, boolean calcExpirationTime)
	{
		int tID = tokenMgr.createTokenID();
		ChoiceNetMessageField tokenID = new ChoiceNetMessageField("Token ID", tID, "");
		ChoiceNetMessageField issuedTo = new ChoiceNetMessageField("Issued To", issuedToVal, "");
		ChoiceNetMessageField issuedBy = new ChoiceNetMessageField("Issued By", myName, "");
		// convert from minutes to milliseconds
		if(calcExpirationTime)
		{
			eTime = System.currentTimeMillis()+(eTime*60000);
		}
		ChoiceNetMessageField expirationTme = new ChoiceNetMessageField("Expiration Time", eTime, "");
		ChoiceNetMessageField serviceName = new ChoiceNetMessageField("Service Name", sName, "");
		ChoiceNetMessageField[] payload = {tokenID,issuedTo,issuedBy,serviceName,expirationTme};

		ChoiceNetMessageField token = new ChoiceNetMessageField("Token", payload, "");
		return token;
	}

	public Advertisement extractAdvertisementContent(ChoiceNetMessageField advertisement, long expirationTime)
	{
		ChoiceNetMessageField[] tPayload;
		ChoiceNetMessageField[] payload = (ChoiceNetMessageField[]) advertisement.getValue();
		ChoiceNetMessageField myService = (ChoiceNetMessageField) payload[0];
		Service service = extractServiceContent(myService); 
		// Consideration
		ChoiceNetMessageField consideration = (ChoiceNetMessageField) payload[1];
		//		tPayload = (ChoiceNetMessageField[]) consideration.getValue();
		//		String considerationMethod = (String) tPayload[0].getValue();
		//		int considerationValue = (Integer) tPayload[1].getValue();
		ChoiceNetMessageField contents = (ChoiceNetMessageField) consideration.getValue();
		String considerationMethod = (String) contents.getAttributeName();
		int considerationValue = (Integer) contents.getValue();
		ChoiceNetMessageField economyAddrProp = (ChoiceNetMessageField) payload[2];
		tPayload = (ChoiceNetMessageField[]) economyAddrProp.getValue();
		String advertiserAddressScheme = (String) tPayload[0].getValue();
		String advertiserAddress = (String) tPayload[1].getValue();
		int advertiserPortAddress = -1;
		if(advertiserAddressScheme.equals("UDPv4") || advertiserAddressScheme.equals("TCPv4"))
		{
			System.out.println(advertiserAddress);
			String[] addr = advertiserAddress.split(":");
			advertiserAddress = addr[0];
			advertiserPortAddress = Integer.parseInt(addr[1]);
		}
		String entityName = (String) tPayload[2].getValue();
		Advertisement myAd = new Advertisement(considerationMethod, considerationValue, entityName, service, advertiserAddress, advertiserPortAddress, advertiserAddressScheme, expirationTime, null,null);

		return myAd;
	}

	public Service extractServiceContent(ChoiceNetMessageField service) {
		ChoiceNetMessageField[] tPayload;
		ChoiceNetMessageField[] payload = (ChoiceNetMessageField[]) service.getValue();
		String name = (String) payload[0].getValue();
		String type = (String) payload[1].getValue();
		String description = (String) payload[2].getValue();

		ChoiceNetMessageField property = (ChoiceNetMessageField) payload[3];
		tPayload = (ChoiceNetMessageField[]) property.getValue();
		// should loop around as an array

		String propertyType = (String) tPayload[0].getValue();
		String propertyValue = (String) tPayload[1].getValue();
		ProvisioningProperty sProp = new ProvisioningProperty(propertyType, propertyValue);
		ProvisioningProperty serviceProperties[] = {sProp};

		String srcLocationAddrScheme = "";
		String srcLocationAddrValue = "";
		String dstLocationAddrScheme = "";
		String dstLocationAddrValue = "";
		String srcFormatScheme = "";
		String srcFormatValue = "";
		String dstFormatScheme = "";
		String dstFormatValue = "";

		ChoiceNetMessageField srcLocation = (ChoiceNetMessageField) payload[4];
		if(srcLocation!=null)
		{
			tPayload = (ChoiceNetMessageField[]) srcLocation.getValue();
			srcLocationAddrScheme = (String) tPayload[0].getValue();
			srcLocationAddrValue = (String) tPayload[1].getValue();
		}
		ChoiceNetMessageField dstLocation = (ChoiceNetMessageField) payload[5];
		if(dstLocation!=null)
		{
			tPayload = (ChoiceNetMessageField[]) dstLocation.getValue();
			dstLocationAddrScheme = (String) tPayload[0].getValue();
			dstLocationAddrValue = (String) tPayload[1].getValue();
		}

		ChoiceNetMessageField srcFormat = (ChoiceNetMessageField) payload[6];
		if(srcFormat!=null)
		{
			tPayload = (ChoiceNetMessageField[]) srcFormat.getValue();
			srcFormatScheme = (String) tPayload[0].getValue();
			srcFormatValue = (String) tPayload[1].getValue();
		}
		ChoiceNetMessageField dstFormat = (ChoiceNetMessageField) payload[7];
		if(dstFormat!=null)
		{
			tPayload = (ChoiceNetMessageField[]) dstFormat.getValue();
			dstFormatScheme = (String) tPayload[0].getValue();
			dstFormatValue = (String) tPayload[1].getValue();
		}
		Service myService = new Service(name, type, srcLocationAddrScheme, srcLocationAddrValue, dstLocationAddrScheme, dstLocationAddrValue, 
				srcFormatScheme, srcFormatValue, dstFormatScheme, dstFormatValue, serviceProperties, description);
		return myService;
	}

	public Token extractTokenContent (ChoiceNetMessageField token)
	{
		ChoiceNetMessageField[] payload = (ChoiceNetMessageField[]) token.getValue();
		int tokenID = (Integer) payload[0].getValue();
		String issuedTo = (String) payload[1].getValue();
		String issuedBy = (String) payload[2].getValue();
		String serviceName = (String) payload[3].getValue();
		long expirationTime = (Long) payload[4].getValue();
		Token myToken = new Token(tokenID, issuedTo, issuedBy, serviceName, expirationTime);
		return myToken;
	}
}