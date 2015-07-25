import java.util.ArrayList;

public class PlannerNode {

	private int resourceCost = 0;
	private String nodeName = "Default";
	private ArrayList<PlannerNode> adjancies = new ArrayList<PlannerNode>();
	boolean isRoot = false;
	CouchDBResponse advertisement;
	
	
	public CouchDBResponse getAdvertisement() {
		return advertisement;
	}

	public void setAdvertisement(CouchDBResponse advertisement) {
		this.advertisement = advertisement;
	}

	public boolean isRoot() {
		return isRoot;
	}

	public void setVisited(boolean isRoot) {
		this.isRoot = isRoot;
	}

	public PlannerNode(String nodeName, int cost) {
		this.nodeName = nodeName;
		this.resourceCost = cost;
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
