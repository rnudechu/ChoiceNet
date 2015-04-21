
public class DiscoveredEntities {
	String name;
	String type;
	String ipAddr;
	int port;
	String acceptedConsideration;
	String availableConsideration;
	
	public DiscoveredEntities(String name, String type, String ipAddr,
			int port, String acceptedConsideration,
			String availableConsideration) {
		super();
		this.name = name;
		this.type = type;
		this.ipAddr = ipAddr;
		this.port = port;
		this.acceptedConsideration = acceptedConsideration;
		this.availableConsideration = availableConsideration;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getIpAddr() {
		return ipAddr;
	}
	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	
	public String getAcceptedConsideration() {
		return acceptedConsideration;
	}
	public void setAcceptedConsideration(String acceptedConsideration) {
		this.acceptedConsideration = acceptedConsideration;
	}
	public String getAvailableConsideration() {
		return availableConsideration;
	}
	public void setAvailableConsideration(String availableConsideration) {
		this.availableConsideration = availableConsideration;
	}
	@Override
	public String toString() {
		return "DiscoveredEntities [name=" + name + ", type=" + type
				+ ", ipAddr=" + ipAddr + ", port=" + port
				+ ", acceptedConsideration=" + acceptedConsideration
				+ ", availableConsideration=" + availableConsideration + "]";
	}
}
