
public class Transaction {

	private int id;
	private String target;
	private String serviceID;
	
	public Transaction(int id, String target, String serviceID) {
		super();
		this.id = id;
		this.target = target;
		this.serviceID = serviceID;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getServiceID() {
		return serviceID;
	}
	public void setServiceID(String serviceID) {
		this.serviceID = serviceID;
	}
	@Override
	public String toString() {
		return "Transaction [id=" + id + ", target=" + target
				+ ", serviceID=" + serviceID + "]";
	}

	
}
