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

	private Element createLocation()
	{
		return null;
	}
	public void createAdvertisement(String myName,String srcLoc, String dstLoc, String srcForm, String dstForm, String providerName, String portalLocation, String portalPort)
	{
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
			name.appendChild(doc.createTextNode(myName));
			service.appendChild(name);

			// details elements
			Element details = doc.createElement("details");
			// location elements
			Element srcLocation = doc.createElement("location");
			srcLocation.setAttribute("type", "source");
			Element srcIP = doc.createElement("ip");
			srcIP.setAttribute("version", "4");
			srcIP.appendChild(doc.createTextNode(srcLoc));
			srcLocation.appendChild(srcIP);
			Element dstLocation = doc.createElement("location");
			Element dstIP = doc.createElement("ip");
			dstIP.setAttribute("version", "4");
			dstLocation.setAttribute("type", "destination");
			dstIP.appendChild(doc.createTextNode(dstLoc));
			dstLocation.appendChild(dstIP);

			details.appendChild(srcLocation);
			details.appendChild(dstLocation);

			// format elements
			Element srcFormat = doc.createElement("format");
			srcFormat.setAttribute("type", "source");
			Element srcMedia = doc.createElement("media");
			srcMedia.appendChild(doc.createTextNode(srcForm));
			srcFormat.appendChild(srcMedia);
			Element dstFormat = doc.createElement("format");
			Element dstMedia = doc.createElement("media");
			dstFormat.setAttribute("type", "destination");
			dstMedia.appendChild(doc.createTextNode(dstForm));
			dstFormat.appendChild(dstMedia);

			details.appendChild(srcFormat);
			details.appendChild(dstFormat);
			// append the details to the service
			service.appendChild(details);
			Element description = doc.createElement("description");
			description.appendChild(doc.createTextNode("Generated Advertisement"));
			service.appendChild(description);
			// append the service to the advertisement
			advertisement.appendChild(service);

			// price element
			Element price = doc.createElement("price");
			Element method = doc.createElement("method");
			method.appendChild(doc.createTextNode("Bitcoin"));
			Element value = doc.createElement("value");
			value.appendChild(doc.createTextNode("2"));

			price.appendChild(method);
			price.appendChild(value);

			// append the price to the advertisement
			advertisement.appendChild(price);

			Element providerID = doc.createElement("providerID");
			providerID.appendChild(doc.createTextNode(providerName));
			// append the price to the advertisement
			advertisement.appendChild(providerID);

			//purchasePortal element
			Element purchasePortal = doc.createElement("purchasePortal");
			Element generalLocation = doc.createElement("location");
			Element generalIP = doc.createElement("ip");
			generalIP.setAttribute("version", "4");
			generalIP.appendChild(doc.createTextNode(portalLocation));
			Element generalPort = doc.createElement("port");
			generalPort.setAttribute("type", "udp");
			generalPort.appendChild(doc.createTextNode(portalPort));
			generalLocation.appendChild(generalIP);
			generalLocation.appendChild(generalPort);

			purchasePortal.appendChild(generalLocation);
			advertisement.appendChild(purchasePortal);

			Element provisioningParameters = doc.createElement("provisioningParameters");
			Element ports = doc.createElement("ports");
			ports.appendChild(doc.createTextNode("8080"));
			provisioningParameters.appendChild(ports);
			advertisement.appendChild(provisioningParameters);

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("generated/genAdvertisement_"+System.currentTimeMillis()+".xml"));

			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);

			transformer.transform(source, result);

			System.out.println("File saved!");

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

	public static void main(String argv[]) {

		AdvertisementRandomGenerator advRG = new AdvertisementRandomGenerator();
		String adName, srcLoc, dstLoc, srcFormat, dstFormat;
		int max = 10;
		for(int i=0;i<max;i++)
		{
			adName = "Ad "+i;
			// nextInt is normally exclusive of the top value,
			// so add 1 to make it inclusive
			srcLoc = advRG.randomNum(1,10)+"."+advRG.randomNum(1,10)+"."+advRG.randomNum(1,10)+"."+advRG.randomNum(1,10);
			dstLoc = advRG.randomNum(1,10)+"."+advRG.randomNum(1,10)+"."+advRG.randomNum(1,10)+"."+advRG.randomNum(1,10);
			srcFormat = advRG.randomNum(1,10)+"";
			dstFormat = advRG.randomNum(1,10)+"";
			advRG.createAdvertisement(adName, srcLoc, dstLoc, srcFormat, dstFormat, "ACME Systems", "127.0.0.1", "4040");
		}
	}

}
