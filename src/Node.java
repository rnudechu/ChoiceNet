
public class Node {

	private int opticalCircuitSwitchCapacity = -1;
	private int opticalPacketSwitchCapacity = -1;
	private int nodeDelay = -1;
	private int opticalCircuitSwitchResidualCapacity = -1;
	private int opticalPacketSwitchResidualCapacity = -1;
	private int nodeID = -1;

	private String nodeName = "Default";

	public Node(int opticalCircuitSwitchCapacity,
			int opticalPacketSwitchCapacity, int nodeDelay,
			int opticalCircuitSwitchResidualCapacity,
			int opticalPacketSwitchResidualCapacity, int nodeID) {
		super();
		this.opticalCircuitSwitchCapacity = opticalCircuitSwitchCapacity;
		this.opticalPacketSwitchCapacity = opticalPacketSwitchCapacity;
		this.nodeDelay = nodeDelay;
		this.opticalCircuitSwitchResidualCapacity = opticalCircuitSwitchResidualCapacity;
		this.opticalPacketSwitchResidualCapacity = opticalPacketSwitchResidualCapacity;
		this.nodeID = nodeID;
		this.nodeName = "Node "+nodeID;
	}

	public int getNodeCapacity ()
	{
		int sum = getOpticalCircuitSwitchCapacity()+getOpticalPacketSwitchCapacity();
		return sum;
	}
	
	public int getNodeResidualCapacity() {
		int sum = getOpticalCircuitSwitchResidualCapacity()+getOpticalPacketSwitchResidualCapacity();
		return sum;
	}
	
	public void setNodeResidualCapacity(int capacity, String linkType)
	{
		if(linkType.equals("Circuit"))
		{
			setOpticalCircuitSwitchResidualCapacity(capacity);
		}
		if(linkType.equals("Packet"))
		{
			setOpticalPacketSwitchResidualCapacity(capacity);
		}
	}
	
	public int getNodeDelay() {
		return nodeDelay;
	}

	public void setNodeDelay(int nodeDelay) {
		this.nodeDelay = nodeDelay;
	}

	public int getOpticalCircuitSwitchCapacity() {
		return opticalCircuitSwitchCapacity;
	}

	public void setOpticalCircuitSwitchCapacity(
			int opticalCircuitSwitchCapacity) {
		this.opticalCircuitSwitchCapacity = opticalCircuitSwitchCapacity;
	}

	public int getOpticalPacketSwitchCapacity() {
		return opticalPacketSwitchCapacity;
	}

	public void setOpticalPacketSwitchCapacity(int opticalPacketSwitchCapacity) {
		this.opticalPacketSwitchCapacity = opticalPacketSwitchCapacity;
	}

	public int getOpticalCircuitSwitchResidualCapacity() {
		return opticalCircuitSwitchResidualCapacity;
	}

	public void setOpticalCircuitSwitchResidualCapacity(
			int opticalCircuitSwitchResidualCapacity) {
		this.opticalCircuitSwitchResidualCapacity = opticalCircuitSwitchResidualCapacity;
	}

	public int getOpticalPacketSwitchResidualCapacity() {
		return opticalPacketSwitchResidualCapacity;
	}

	public void setOpticalPacketSwitchResidualCapacity(
			int opticalPacketSwitchResidualCapacity) {
		this.opticalPacketSwitchResidualCapacity = opticalPacketSwitchResidualCapacity;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public int getNodeID() {
		return nodeID;
	}

	public void setNodeID(int nodeID) {
		this.nodeID = nodeID;
	}

	@Override
	public String toString() {
		return "Node [opticalCircuitSwitchCapacity="
				+ opticalCircuitSwitchCapacity
				+ ", opticalPacketSwitchCapacity="
				+ opticalPacketSwitchCapacity + ", nodeDelay=" + nodeDelay
				+ ", opticalCircuitSwitchResidualCapacity="
				+ opticalCircuitSwitchResidualCapacity
				+ ", opticalPacketSwitchResidualCapacity="
				+ opticalPacketSwitchResidualCapacity + ", nodeID=" + nodeID
				+ ", nodeName=" + nodeName + "]";
	}
	 
}
