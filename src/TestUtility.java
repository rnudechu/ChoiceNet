import java.io.File;
import java.io.IOException;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.naming.directory.AttributeModificationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
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
import org.xml.sax.SAXException;


public class TestUtility {



	public void parseAdvertisementXML(String filename)
	{
		ChoiceNetMessageField advertisement = null;
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
			NodeList myList, thisList = null;
			Node myNode = null;
			Element myElement = null;
			NamedNodeMap attributes = null;
			System.out.println(">>> XML "+filename);
			int priceValue = 0;
			int numLoc = 0;
			String type, addressType = "", portalType = "", location = "";
			String priceMethod, pValue, providerID, provisioningParameters, purchasePortal, usePlaneAddressValue, advertiserName, serviceName, serviceType, 
			serviceDescription, propertyName, propertyValue, srcAddressScheme, srcAddressValue, dstAddressScheme, dstAddressValue;
			priceMethod = pValue = providerID = provisioningParameters = purchasePortal = usePlaneAddressValue = advertiserName = serviceName = serviceType = serviceDescription =  
					propertyName = propertyValue = srcAddressScheme = srcAddressValue = dstAddressScheme = dstAddressValue = null;
			for (int temp = 0; temp < advertisementCount; temp++) 
			{
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					// Price
					myList = doc.getElementsByTagName("price");
					myNode = myList.item(temp);
					if (myNode.getNodeType() == Node.ELEMENT_NODE) {

						myElement = (Element) myNode;
						priceMethod = myElement.getElementsByTagName("method").item(0).getTextContent();
						pValue = myElement.getElementsByTagName("value").item(0).getTextContent();
						priceValue = Integer.parseInt(pValue);
					}
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
						myList = myElement.getElementsByTagName("location");
						if (myNode.getNodeType() == Node.ELEMENT_NODE) 
						{
							myElement = (Element) myNode;
							if(myElement.getElementsByTagName("url").getLength() > 0)
							{
								portalType = "URL";
								purchasePortal = myElement.getElementsByTagName("url").item(0).getTextContent();
							}
							if(myElement.getElementsByTagName("ip").getLength() > 0)
							{

								attributes = myElement.getElementsByTagName("ip").item(0).getAttributes();

								purchasePortal = myElement.getElementsByTagName("ip").item(0).getTextContent();

								portalType += "v"+(attributes.item(0).getNodeValue());
								attributes = myElement.getElementsByTagName("port").item(0).getAttributes();
								portalType = (attributes.item(0).getNodeValue()).toUpperCase()+portalType;
								purchasePortal += ":"+myElement.getElementsByTagName("port").item(0).getTextContent();
							}
						}

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
							provisioningParameters += (myList.item(i).getTextContent()).trim()+";";
						}
						myList = myElement.getElementsByTagName("wavelength");
						for(int i=0; myList.getLength()!=0 && i<myList.getLength(); i++)
						{
							provisioningParameters += (myList.item(i).getTextContent()).trim()+";";
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
						myNode = myList.item(temp);
						if (myNode.getNodeType() == Node.ELEMENT_NODE) {

							myElement = (Element) myNode;
							myList = myElement.getElementsByTagName("location");
							System.out.println(myList.getLength());

							for(int i=0;i<myList.getLength();i++)
							{
								attributes = myList.item(i).getAttributes();
								// only looking at the first element under location and first attribute
								if (myNode.getNodeType() == Node.ELEMENT_NODE && !attributes.getNamedItem("type").equals("null")) 
								{
									myElement = (Element) myNode;	
									addressType = attributes.item(0).getNodeValue();
									if(myElement.getElementsByTagName("ip").getLength() > 0)
									{
										numLoc++;
										thisList = myElement.getElementsByTagName("ip");
										attributes = thisList.item(0).getAttributes();
										location = myElement.getElementsByTagName("ip").item(i).getTextContent().trim();//+";";
										type = attributes.item(0).getNodeValue();
										if(type.equals("4"))
										{
											if(addressType.equals("source"))
											{
												srcAddressScheme = "IPv4";
												srcAddressValue = location;
											}
											if(addressType.equals("destination"))
											{
												dstAddressScheme = "IPv4";
												dstAddressValue = location;
											}
											System.out.println(location);
											System.out.println(addressType+" IPv4");
										}
										if(type.equals("6"))
										{
											if(addressType.equals("source"))
											{
												srcAddressScheme = "IPv6";
												srcAddressValue = location;
											}
											if(addressType.equals("destination"))
											{
												dstAddressScheme = "IPv6";
												dstAddressValue = location; 
											}
											System.out.println(addressType+" IPv6");
										}
									}
									if(myElement.getElementsByTagName("url").getLength() > 0)
									{
										location = myElement.getElementsByTagName("url").item(0).getTextContent().trim();
									}
								}
							}
						}
						if(numLoc==2)
						{
							serviceType="Transit";
						}
						System.out.println("Hello "+serviceType);

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


			}
		} catch (Exception e) {
			Server.systemMessage = e.getMessage();
			//e.printStackTrace();
		}
		System.out.println("----------------------------");
	}

	public void parse(String filename)
	{
		File fXmlFile = new File(filename);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();

			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			System.out.println("Root element:" + doc.getDocumentElement().getNodeName());

			//optional, but recommended
			//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work


			NodeList nList = doc.getElementsByTagName("advertisement");
			int advertisementCount = nList.getLength();
			System.out.println("----------------------------");
			NodeList myList = null;
			Node myNode = null;
			Element myElement = null;
			System.out.println(">>> XML "+filename);

			for (int temp = 0; temp < advertisementCount; temp++) 
			{
				myNode = nList.item(temp);

				if (myNode.getNodeType() == Node.ELEMENT_NODE) {
					NamedNodeMap attributes = myNode.getAttributes();

					for (int a = 0; a < attributes.getLength(); a++) 
					{
						Node theAttribute = attributes.item(a);
						System.out.println(theAttribute.getNodeName() + "=" + theAttribute.getNodeValue());
					}
				}
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String createPacketXML(Packet pkt)
	{
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
			StreamResult sResult = new StreamResult(new File("C:\\file.xml"));
			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);

			transformer.transform(source, sResult);

			System.out.println("File saved!");

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
		String result = "";

		return result;
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
			if(pkt.getValue().getClass().equals(String.class))
			{
				myValue = (String) pkt.getValue();
			}
			if(pkt.getValue().getClass().equals(Integer.class))
			{
				int temp = (Integer) pkt.getValue();
				myValue = Integer.toString(temp);
			}
			value.appendChild(doc.createTextNode(myValue));
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
	
	public ChoiceNetMessageField getChoiceNetMessage (String packetXML, String attributeName)
	{
		ChoiceNetMessageField message = new ChoiceNetMessageField(attributeName, "", "");
		try
		{
			File xml = new File(packetXML);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xml);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("field");
			int fieldPresence = 0;
			
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					if(eElement.getAttribute("attributeName").equals(attributeName))
					{
						System.out.println(nList.getLength());
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
			File xml = new File(packetXML);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xml);
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
	
//	public ChoiceNetMessageField getChoiceNetMessage (String packetXML, String attributeName)
//	{
//		ChoiceNetMessageField message = new ChoiceNetMessageField(attributeName, "", "");
//		try
//		{
//			File xml = new File(packetXML);
//			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//
//			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//			Document doc = dBuilder.parse(xml);
//			doc.getDocumentElement().normalize();
//			NodeList nList = doc.getElementsByTagName("field");
//			int fieldPresence = 0;
//			for (int temp = 0; temp < nList.getLength(); temp++) {
//				Node nNode = nList.item(temp);
//				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
//					Element eElement = (Element) nNode;
//					if(eElement.getAttribute("attributeName").equals(attributeName))
//					{
//						String url = eElement.getAttribute("url");
//						message.setUrl(url);
//						if(!attributeName.equals("Message Specific"))
//						{
//							String value = eElement.getElementsByTagName("value").item(0).getTextContent();
//							message.setValue(value);
//						}
//						else
//						{
//							NodeList myList = eElement.getElementsByTagName("value");
//							createChoiceNetMessagePayload(myList, myList.getLength()-1);
//							int size = eElement.getElementsByTagName("value").getLength();
//							for(int i=1;i<size;i++)
//							{
//								nNode = myList.item(i);
//								if (nNode.getNodeType() == Node.ELEMENT_NODE) {
//									eElement = (Element) nNode;
//									fieldPresence = eElement.getElementsByTagName("field").getLength();
//									System.out.println("R==> ++R "+eElement.getElementsByTagName("field").getLength());
//								}
//								//								eElement.getElementsByTagName("value").item(i).getTextContent();
//								//								System.out.println("==> Node child "+eElement.getElementsByTagName("value").item(i).getChildNodes().getLength());
//								//								System.out.println("==> Node child "+eElement.getElementsByTagName("value").item(i).getChildNodes(). );
//							}
//							System.out.println("==>"+eElement.getChildNodes().getLength());
//							System.out.println("==>"+eElement.getElementsByTagName("value").getLength());
//							System.out.println("==>"+eElement.getElementsByTagName("value").toString());
//						}
//
//						return message;
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}

	private ChoiceNetMessageField[] createChoiceNetMessagePayload (NodeList nList, int iniSize)
	{
		int numFields = 0;
//		int size = nList.getLength(); // number of values in the value
		int size = iniSize;
		System.out.println(size);
		// I should count the number fields .. but why
		ChoiceNetMessageField[] payload = new ChoiceNetMessageField[size];
		ChoiceNetMessageField[] newPayload;
		ChoiceNetMessageField message = new ChoiceNetMessageField("", "", "");
		String attributeName = "";
		String url = "";//nNode.getAttributes().ggetAttribute("attributeName");
		for(int i=0;i<size;i++)
		{
			System.out.println("Round "+i+"\n=====================\n");
			Node nNode = nList.item(i);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;

				System.out.println("Number of fields: "+eElement.getElementsByTagName("field").getLength());
				NodeList myList = eElement.getElementsByTagName("field");
				numFields = myList.getLength();
				if(numFields>0)
				{
					for(int j=0; j<numFields;j++)
					{
						Node myNode = myList.item(j);
						if (nNode.getNodeType() == Node.ELEMENT_NODE) {
							Element myElement = (Element) myNode;
							attributeName = myElement.getAttribute("attributeName");
							url = myElement.getAttribute("url");
							// call the func again
							newPayload = createChoiceNetMessagePayload(myElement.getElementsByTagName("value"), numFields);
							message.setAttributeName(attributeName);
							message.setUrl(url);
							message.setValue(newPayload);
							System.out.println("Message len "+newPayload.length);
							System.out.println("Message "+message);

						}
					}
				}
				else
				{
					//System.out.println("==> U "+eElement.getElementsByTagName("field").item(0).getChildNodes().getLength());
					NodeList childNodeList = nNode.getChildNodes();
					System.out.println("==> Node child "+childNodeList.getLength());
					nNode.getAttributes();
					System.out.println("==> Attribute list "+nNode.getAttributes());

					// no children
					if(childNodeList.getLength() == 1 )
					{
						String value = nNode.getTextContent();
						message = new ChoiceNetMessageField(attributeName, value, url);
						System.out.println("Should not have this case"); 
					}
					else
					{
						ChoiceNetMessageField[] value = createChoiceNetMessagePayload(childNodeList, childNodeList.getLength()-1);
						message = new ChoiceNetMessageField(attributeName, value, url);
					}
				}
			}
			System.out.println("payload increment with message: "+message); 
			payload[i] = message;
		}

		return payload;
	}

	public static void main(String[] args) 
	{
		TestUtility tu = new TestUtility();
		//tu.parseAdvertisementXML("test.xml"); // <-- working version?
		//tu.parse("test.xml");
		//		ChoiceNetMessageField transactionNumber = new ChoiceNetMessageField("Transaction Number", "transcactionNum", "");
		//		ChoiceNetMessageField considerationTarget = new ChoiceNetMessageField("Consideration Target", "cTarget", "");
		//		ChoiceNetMessageField serviceName = new ChoiceNetMessageField("Service Name", "sName", "");
		//		ChoiceNetMessageField considerationExchMethod = new ChoiceNetMessageField("Consideration Exchange Method", "exchangeMethod", "");
		//		ChoiceNetMessageField considerationExchValue = new ChoiceNetMessageField("Consideration Exchange Value", 900, "");
		//		ChoiceNetMessageField[] payload = {transactionNumber,considerationTarget,serviceName,considerationExchMethod,considerationExchValue};
		//		Packet packet = new Packet(PacketType.TRANSFER_CONSIDERATION,Server.myName,"",Server.myType, Server.providerType,payload);
		//		tu.createPacketXML(packet);
//		ChoiceNetMessageField oName = tu.getChoiceNetMessage("packet1.xml","Originator Name");
//		ChoiceNetMessageField oSign = tu.getChoiceNetMessage("packet1.xml","Originator Signature");
//		ChoiceNetMessageField oType = tu.getChoiceNetMessage("packet1.xml","Originator Type");
//		ChoiceNetMessageField oProviderType = tu.getChoiceNetMessage("packet1.xml","Originator Provider Type");
//		ChoiceNetMessageField messageType = tu.getChoiceNetMessage("packet1.xml","Message Type");
//		ChoiceNetMessageField messageSpecific = tu.getChoiceNetMessage("packet1.xml","Message Specific");
		ChoiceNetMessageField[] x = tu.getChoiceNetMessageArray("packet1.xml","Advertisement ID");
		System.out.println(x.length);
		System.out.println(x[0].getValue());
		System.out.println(x[1].getValue());
		//		String property = System.getProperty("java.library.path");
		//		StringTokenizer parser = new StringTokenizer(property, ";");
		//		while (parser.hasMoreTokens()) {
		//		    System.err.println(parser.nextToken());
		//		    }

	}

}
