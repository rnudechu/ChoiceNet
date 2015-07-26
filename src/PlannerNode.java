import java.util.ArrayList;

public class PlannerNode {

	private int resourceCost = 0;
	private String nodeName = "Default";
	private ArrayList<PlannerNode> adjancies = new ArrayList<PlannerNode>();
	public enum NodeType {
		SOURCE,
		REGULAR,
		DESTINATION
	}
	NodeType status = NodeType.REGULAR;
	AdvertisementDisplay advertisement;
	
	
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
		return "PlannerNode [resourceCost=" + resourceCost + ", nodeName=" + nodeName
				+ ", adjancies=" + adjancies + "]";
	}
	 
}
