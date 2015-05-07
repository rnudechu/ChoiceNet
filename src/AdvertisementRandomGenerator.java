import java.io.File;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AdvertisementRandomGenerator {

	private Element createServiceDetails(Document doc, String service, String srcLoc, String dstLoc, String srcForm, String dstForm)
	{
		Element details = doc.createElement("details");
		Element srcLocation = doc.createElement("source_location");
		Element dstLocation = doc.createElement("destination_location");
		Element srcFormat = doc.createElement("source_format");
		Element dstFormat = doc.createElement("destination_format");
		// Transit Service (same format, different location)
		if(service.equals("Transit"))
		{
			// location elements
			srcLocation.setAttribute("scheme", "IPv4");
			srcLocation.setAttribute("value", srcLoc);

			dstLocation.setAttribute("scheme", "IPv4");
			dstLocation.setAttribute("value", dstLoc);

			// format elements
			srcFormat.setAttribute("scheme", "transport media");
			srcFormat.setAttribute("value", srcForm);

			dstFormat.setAttribute("scheme", "transport media");
			dstFormat.setAttribute("value", srcForm);
		}
		// Forwarding Service (same location, different format)
		if(service.equals("Forwarding"))
		{
			// location elements
			srcLocation.setAttribute("scheme", "IPv4");
			srcLocation.setAttribute("value", srcLoc);

			dstLocation.setAttribute("scheme", "IPv4");
			dstLocation.setAttribute("value", srcLoc);

			// format elements
			srcFormat.setAttribute("scheme", "audio media");
			srcFormat.setAttribute("value", srcForm);

			dstFormat.setAttribute("scheme", "audio media");
			dstFormat.setAttribute("value", dstForm);
		}
		// Planner 
		details.appendChild(srcLocation);
		details.appendChild(dstLocation);
		details.appendChild(srcFormat);
		details.appendChild(dstFormat);

		return details;
	}
	public void createAdvertisement(String serviceType, String myName, String srcLoc, String dstLoc, String srcForm, String dstForm, String providerName, String portalLocation)
	{
		String timestamp = ""+System.currentTimeMillis();
		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("listings");
			doc.appendChild(rootElement);

			// advertisement elements
			Element advertisement = doc.createElement("advertisement");
			rootElement.appendChild(advertisement);

			// set attribute to advertisement element
			Attr attr = doc.createAttribute("id");
			attr.setValue("1");
			advertisement.setAttributeNode(attr);

			Element service = doc.createElement("service");

			// name elements
			Element name = doc.createElement("name");
			name.appendChild(doc.createTextNode(myName+" "+timestamp));
			service.appendChild(name);

			// details elements
			//			Element details = doc.createElement("details");
			// location elements
			Element details = createServiceDetails(doc, serviceType, srcLoc,  dstLoc,  srcForm,  dstForm);
			//			Element srcLocation = doc.createElement("source_location");
			//			srcLocation.setAttribute("scheme", "IPv4");
			//			srcLocation.setAttribute("value", srcLoc);
			//			Element dstLocation = doc.createElement("destination_location");
			//			dstLocation.setAttribute("scheme", "IPv4");
			//			dstLocation.setAttribute("value", dstLoc);
			//
			//			details.appendChild(srcLocation);
			//			details.appendChild(dstLocation);
			//
			//			// format elements
			//			Element srcFormat = doc.createElement("source_format");
			//			srcFormat.setAttribute("scheme", "transport media");
			//			srcFormat.setAttribute("value", srcForm);
			//			Element dstFormat = doc.createElement("destination_format");
			//			dstFormat.setAttribute("scheme", "transport media");
			//			dstFormat.setAttribute("value", dstForm);
			//
			//			details.appendChild(srcFormat);
			//			details.appendChild(dstFormat);
			// append the details to the service
			service.appendChild(details);
			Element description = doc.createElement("description");
			description.appendChild(doc.createTextNode("Generated "+serviceType+" Service Advertisement"));
			service.appendChild(description);
			// append the service to the advertisement
			advertisement.appendChild(service);

			// price element
			Element price = doc.createElement("price");
			price.setAttribute("method", "Bitcoin");
			int priceValue = randomNum(1, 100);
			price.setAttribute("value", priceValue+"");

			// append the price to the advertisement
			advertisement.appendChild(price);

			Element providerID = doc.createElement("providerID");
			providerID.appendChild(doc.createTextNode(providerName));
			// append the price to the advertisement
			advertisement.appendChild(providerID);

			//purchasePortal element
			Element purchasePortal = doc.createElement("purchasePortal");
			Element generalLocation = doc.createElement("destination_location");
			generalLocation.setAttribute("scheme", "UDPv4");
			generalLocation.setAttribute("value", portalLocation);
			purchasePortal.appendChild(generalLocation);
			advertisement.appendChild(purchasePortal);

			Element provisioningParameters = doc.createElement("provisioningParameters");
			Element configurableParameters = doc.createElement("configurableParameters");
			Element ports = doc.createElement("ports");
			ports.setAttribute("value", "8080");
			configurableParameters.appendChild(ports);
			provisioningParameters.appendChild(configurableParameters);
			advertisement.appendChild(provisioningParameters);

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("generated/genAdvertisement_"+timestamp+".xml"));

			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);

			transformer.transform(source, result);

			System.out.println("Service Type:"+serviceType+". File saved!");

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}

	private int randomNum(int min, int max)
	{
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}

	private void sendAdvertisement(String marketplaceIPAddr, int marketplacePort)
	{

		Server server = new Server("transport.properties");
		// Provider: Transport
		System.out.println("Test");
		String serviceName = "Advertisement Listing";
		String target = "ABC Marketplace";
		String exchangeType = "Bitcoin";
		String exchangeAmount = "USD 200";
		String fileName = "";
		String message = "";
		File folder = new File("generated/");
		for (File fileEntry : folder.listFiles()) {
			fileName = fileEntry.getPath();
			server.transferConsiderationMessage(serviceName, target, exchangeType, exchangeAmount, marketplaceIPAddr, marketplacePort);
			message = server.tokenMgr.printAvailableTokens();
			System.out.println(message);
			Token token = server.tokenMgr.getFirstTokenFromMapping();
			if(token != null)
			{
				String tokenID = ""+token.getId();
				server.transferListingMessage(fileName, target, tokenID, marketplaceIPAddr, marketplacePort);
				System.out.println( "Filename: "+fileName+"\n"+
						"Token ID: "+tokenID);
			}
		}

	}

	private void generate(int max)
	{
		String adName, srcLoc, dstLoc, srcFormat, dstFormat, serviceType;
		for(int i=0;i<max;i++)
		{
			adName = "Ad "+i;

			if(randomNum(1,10)>5)
			{
				serviceType = "Forwarding";
				srcFormat = randomNum(1,10)+"";
				dstFormat = randomNum(1,10)+"";
			}
			else
			{
				serviceType = "Transit";
				srcFormat = randomNum(1,10)+"";
				dstFormat = randomNum(1,10)+"";
			}
			// nextInt is normally exclusive of the top value,
			// so add 1 to make it inclusive
			srcLoc = randomNum(1,10)+"."+randomNum(1,10)+"."+randomNum(1,10)+".0/24";
			dstLoc = randomNum(1,10)+"."+randomNum(1,10)+"."+randomNum(1,10)+".0/24";

			createAdvertisement(serviceType, adName, srcLoc, dstLoc, srcFormat, dstFormat, "ACME Systems", "127.0.0.1:4040");
		}
	}
	public static void main(String argv[]) {

		AdvertisementRandomGenerator advRG = new AdvertisementRandomGenerator();

		int max = 10;
		File folder = new File("generated/");
		// Generate Advertisements
		//		advRG.generate(max);
		//  marketplace IP Address and port
		String marketplaceIPAddr = "127.0.0.1";
		int marketplacePort = 4040;
		advRG.sendAdvertisement(marketplaceIPAddr, marketplacePort);

	}

}
