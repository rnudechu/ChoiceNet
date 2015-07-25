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
		// Pathlet Service (same format, different location)
		if(service.equals("Pathlet"))
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
		// Transforming Service (same location, different format)
		if(service.equals("Transforming"))
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
			
			// type element
			Element type = doc.createElement("type");
			type.appendChild(doc.createTextNode(serviceType));
			service.appendChild(type);

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
			price.setAttribute("method", "USD");
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
//			Element generalLocation = doc.createElement("destination_location");
			Element generalLocation = doc.createElement("source_location");
			generalLocation.setAttribute("scheme", "Payment");
			generalLocation.setAttribute("value", "Bitcoin:mknFpFW8x5pvLH8WSSLSKQRBrkPPiPPoxFv");
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
		Token token;
		for (File fileEntry : folder.listFiles()) {
			fileName = fileEntry.getPath();
			server.transferConsiderationMessage(serviceName, target, exchangeType, exchangeAmount, marketplaceIPAddr, marketplacePort);
			message = server.tokenMgr.printAvailableTokens();
			System.out.println(message);
			token = server.tokenMgr.getFirstTokenFromMapping();
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
		String adName, srcLoc, dstLoc, srcFormat, dstFormat, serviceType,providerID;
		int temp;
		for(int i=0;i<max;i++)
		{
			adName = "Ad "+i;

			if(randomNum(1,10)>5)
			{
				serviceType = "Transforming";
				srcFormat = randomNum(1,10)+"";
				dstFormat = randomNum(1,10)+"";
			}
			else
			{
				serviceType = "Pathlet";
				srcFormat = randomNum(1,10)+"";
				dstFormat = randomNum(1,10)+"";
			}
			// nextInt is normally exclusive of the top value,
			// so add 1 to make it inclusive
			srcLoc = randomNum(1,10)+"."+randomNum(1,10)+"."+randomNum(1,10)+".0/24";
			dstLoc = randomNum(1,10)+"."+randomNum(1,10)+"."+randomNum(1,10)+".0/24";
			
			// Select providerID
			temp = randomNum(1,4);
			providerID = "ACME Corporation";
			if(temp==1)
			{
				providerID = "Wayne Enterprisen";
			}
			else
			{
				if(temp==2)
				{
					providerID = "Stark Industries";
				}
			}
			createAdvertisement(serviceType, adName, srcLoc, dstLoc, srcFormat, dstFormat, providerID, "127.0.0.1:4040");
		}
	}
	public static void main(String argv[]) {

		AdvertisementRandomGenerator advRG = new AdvertisementRandomGenerator();

		int max = 20;
		advRG.generate(max);
//		if(argv.length == 1)
//		{
//			int val = Integer.parseInt(argv[0]);
//			if(val>0)
//			{
//				max = val;
//			}
//			File folder = new File("generatedAdvertisement");
//			// Generate Advertisements
//			advRG.generate(max);
//			//  marketplace IP Address and port
//			String marketplaceIPAddr = "127.0.0.1";
//			int marketplacePort = 4040;
//			advRG.sendAdvertisement(marketplaceIPAddr, marketplacePort);
//		}
//		else
//		{
//			String message = "java -jar advertisementGenerator.jar <number of advertisement> <folder to store advertisements>";
//			System.out.println(message);
//		}
		

	}

}
