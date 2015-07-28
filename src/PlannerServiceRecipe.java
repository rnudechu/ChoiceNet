import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class PlannerServiceRecipe {
	private ArrayList<String> advertisementList;
	private ArrayList<String> provisioningParameters;
	private int totalCost;

	public PlannerServiceRecipe(){}
	public PlannerServiceRecipe(ArrayList<String> advertisementList, int totalCost, ArrayList<String> provisioningParameters) {
		super();
		this.advertisementList = advertisementList;
		this.totalCost = totalCost;
		this.provisioningParameters = provisioningParameters;
	}

	public ArrayList<String> getAdvertisementList() {
		return advertisementList;
	}
	public void setAdvertisementList(ArrayList<String> advertisementList) {
		this.advertisementList = advertisementList;
	}
	public int getTotalCost() {
		return totalCost;
	}
	public void setTotalCost(int totalCost) {
		this.totalCost = totalCost;
	}

	public ArrayList<String> getProvisioningParameters() {
		return provisioningParameters;
	}

	public void setProvisioningParameters(ArrayList<String> provisioningParameters) {
		this.provisioningParameters = provisioningParameters;
	}

	public String createXML()
	{
		String xml = "";
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("recipe");
			doc.appendChild(rootElement);

			// Cost
			Element cost = doc.createElement("cost");
			Attr attr = doc.createAttribute("value");
			attr.setValue(getTotalCost()+"");
			cost.setAttributeNode(attr);
			rootElement.appendChild(cost);
			
			// items elements
			Element services = doc.createElement("serviceAdvertisements");
			int size = getAdvertisementList().size();
			String adName, provisionParameter;
			Element advertisement;
			for(int i = 0; i<size; i++)
			{
				advertisement = doc.createElement("advertisement");
				adName = getAdvertisementList().get(i);
				provisionParameter = getProvisioningParameters().get(i);
				//advertisement.appendChild(doc.createTextNode(adName));
				
				attr = doc.createAttribute("identifier");
				attr.setValue(adName);
				advertisement.setAttributeNode(attr);
				
				attr = doc.createAttribute("provisioningParameter");
				attr.setValue(provisionParameter);
				advertisement.setAttributeNode(attr);
				
				services.appendChild(advertisement);
				
//				services.appendChild(advertisement);
//				provisioningParameter = doc.createElement("provisioningParameter");

//				advertisement.appendChild(doc.createTextNode(provisionParameter));
//				services.appendChild(provisioningParameter);
			}
			rootElement.appendChild(services);
			
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

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return xml;
	}

	public PlannerServiceRecipe parseXML(String xml)
	{
		PlannerServiceRecipe recipe = new PlannerServiceRecipe();
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		
		Document doc = null;
		doc = dBuilder.parse(new InputSource(new StringReader(xml)));
		NodeList nList = doc.getElementsByTagName("recipe");
		NodeList myList = null;
		Node myNode = null;
		Element myElement = null;
		int recipeCount = nList.getLength();
		int totalCost, size = -1;
		String cValue = "";
		String advertisement, provisionParameter;
		ArrayList<String> advertisementList = new ArrayList<String>();
		ArrayList<String> provisioningParameters = new ArrayList<String>();
		for (int temp = 0; temp < recipeCount; temp++) 
		{
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				// Price
				myList = doc.getElementsByTagName("cost");
				cValue = getAttributes(myList, "value",0);
				totalCost = Integer.parseInt(cValue);
				recipe.setTotalCost(totalCost);
			}
			myList = doc.getElementsByTagName("serviceAdvertisements");
			myNode = myList.item(temp);
			if (myNode.getNodeType() == Node.ELEMENT_NODE) 
			{
				myElement = (Element) myNode;
				myList = myElement.getElementsByTagName("advertisement");
				size = myList.getLength();
				for(int i=0;i<size;i++)
				{
					advertisement = getAttributes(myList, "identifier", i);
					provisionParameter = getAttributes(myList, "provisioningParameter", i);
					advertisementList.add(advertisement);
					provisioningParameters.add(provisionParameter);
				}
				recipe.setAdvertisementList(advertisementList);
				recipe.setProvisioningParameters(provisioningParameters);
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
		return recipe;
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
	
	@Override
	public String toString() {
		return "PlannerServiceRecipe [advertisementList=" + advertisementList
				+ ", provisioningParameters=" + provisioningParameters
				+ ", totalCost=" + totalCost + "]";
	}
}
