
public class Token {
	private int id;
	private String issuedTo;
	private String issuedBy;
	private String serviceName;
	private long expirationTime;
	private long creationTime;
	
	public Token(int id, String issuedTo, String issuedBy, String serviceName,
			long expirationTime) {
		super();
		this.id = id;
		this.issuedTo = issuedTo;
		this.issuedBy = issuedBy;
		this.serviceName = serviceName;
		this.expirationTime = expirationTime;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIssuedTo() {
		return issuedTo;
	}

	public void setIssuedTo(String issuedTo) {
		this.issuedTo = issuedTo;
	}

	public String getIssuedBy() {
		return issuedBy;
	}

	public void setIssuedBy(String issuedBy) {
		this.issuedBy = issuedBy;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public long getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(long expirationTime) {
		this.expirationTime = expirationTime;
	}

	@Override
	public String toString() {
		return "Token [id=" + id + ", issuedTo=" + issuedTo + ", issuedBy="
				+ issuedBy + ", serviceName=" + serviceName
				+ ", expirationTime=" + expirationTime + "]";
	}
	
}