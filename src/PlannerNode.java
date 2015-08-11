import java.util.ArrayList;

public class PlannerNode {

	private int resourceCost = 0;
	private String nodeName = "Default";
	private ArrayList<PlannerNode> adjancies = new ArrayList<PlannerNode>();
	public enum NodeType {
		SOURCE,
		REGULAR,
		DESTINATION,
		SOLUTION
	}
	private NodeType status = NodeType.REGULAR;
	private AdvertisementDisplay advertisement;
	private ArrayList<String> provisionParameter = new ArrayList<String>();
	private String searchedParameterLocation = "";
	private String searchedParameterFormat = "";
	
	public String findMatchingSearchCriteria(String field, String searchCriteria, String searchType)
	{
		AdvertisementDisplay myAd = getAdvertisement();
		String result = "";
		String[] values = {}, types = {};
		if(field.equals("Location"))
		{
			values = myAd.getSrcLocationAddrValue();
			types = myAd.getSrcLocationAddrScheme();
		}
		if(field.equals("Format"))
		{
			values = myAd.getSrcFormatValue();
			types = myAd.getSrcFormatScheme();
		}
		
		int size = types.length;
		boolean check = false;
		for(int i =0; (i<size && !check); i++)
		{
			if(types[i].equals(searchType))
			{
				check = checkField(searchType, searchCriteria, values[i]);
			}
			if(check)
			{
				result = values[i];
			}
		}
		return result;
	}
	
	public boolean checkField(String searchType, String searchCriteria, String matchingCriteria)
	{
		boolean result = false;
		if(searchType.equals("IPv4"))
		{
			result = Utility.netMatch(searchCriteria, matchingCriteria);
		}
		return result;
				
	}
	
	public String getSearchedParameter()
	{
		String searchParameter = "";
		String searchedFormat = getSearchedParameterFormat();
		String searchedLocation = getSearchedParameterLocation();
		if(!searchedFormat.isEmpty())
		{
			searchParameter = searchedFormat;
		}
		if(!searchedLocation.isEmpty())
		{
			searchParameter = searchedLocation;
		}
		if(!searchedFormat.isEmpty() && !searchedLocation.isEmpty())
		{
			searchParameter = searchedLocation+":"+searchedFormat;
		}
		return searchParameter;
	}
	
	public String getSearchedParameterLocation() {
		return searchedParameterLocation;
	}

	public void setSearchedParameterLocation(String searchedParameterLocation) {
		this.searchedParameterLocation = searchedParameterLocation;
	}

	public String getSearchedParameterFormat() {
		return searchedParameterFormat;
	}

	public void setSearchedParameterFormat(String searchedParameterFormat) {
		this.searchedParameterFormat = searchedParameterFormat;
	}
	
	public ArrayList<String> getProvisionParameter() {
		return provisionParameter;
	}

	public void setProvisionParameter(ArrayList<String> provisionParameter) {
		this.provisionParameter = provisionParameter;
	}

	public AdvertisementDisplay getAdvertisement() {
		return advertisement;
	}

	public void setAdvertisement(AdvertisementDisplay advertisement) {
		this.advertisement = advertisement;
	}

	public NodeType getStatus() {
		return status;
	}

	public void setStatus(NodeType status) {
		this.status = status;
	}

	public PlannerNode(String nodeName, int cost) {
		this.nodeName = nodeName;
		this.resourceCost = cost;
	}
	
	public PlannerNode(String nodeName, int cost, AdvertisementDisplay advertisement) {
		this.nodeName = nodeName;
		this.resourceCost = cost;
		this.advertisement = advertisement;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public int getResourceCost() {
		return resourceCost;
	}
	
	public void setResourceCost(int resourceCost) {
		this.resourceCost = resourceCost;
	}
	
	public ArrayList<PlannerNode> getAdjancies() {
		return adjancies;
	}

	public void setAdjancies(ArrayList<PlannerNode> adjancies) {
		this.adjancies = adjancies;
	}

	@Override
	public String toString() {
		return "PlannerNode [resourceCost=" + resourceCost + ", nodeName="
				+ nodeName + ", adjancies=" + adjancies + ", status=" + status
				+ ", advertisement=" + advertisement + ", provisionParameter="
				+ provisionParameter + "]";
	}
	 
}
