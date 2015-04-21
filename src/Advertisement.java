public class Advertisement {

	private String id;
	private String considerationMethod;
	private int considerationValue;
	private String entityName;
	private Service service;
	private String advertiserAddress;
	private int advertiserPortAddress;
	private String advertiserAddressScheme;
	private long expirationTime;
	private long creationTime;
	private String state; // Attempting, Advertised, Expired, Not Available
	private String usePlaneAddress;
	private String usePlaneType;
	
	public Advertisement(String considerationMethod, int considerationValue,
			String entityName, Service service, String advertiserAddress,
			int advertiserPortAddress, String advertiserAddressScheme,
			long expirationTime, String usePlaneType, String usePlaneAddr) {
		super();
		this.considerationMethod = considerationMethod;
		this.considerationValue = considerationValue;
		this.entityName = entityName;
		this.service = service;
		this.advertiserAddress = advertiserAddress;
		this.advertiserPortAddress = advertiserPortAddress;
		this.advertiserAddressScheme = advertiserAddressScheme;
		this.expirationTime = expirationTime;
		this.creationTime = System.currentTimeMillis();
		this.usePlaneType = usePlaneType;
		this.usePlaneAddress = usePlaneAddress;
		state = "Attempting";
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public long getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(long expirationTime) {
		this.expirationTime = expirationTime;
	}

	public String getConsiderationMethod() {
		return considerationMethod;
	}

	public void setConsiderationMethod(String considerationMethod) {
		this.considerationMethod = considerationMethod;
	}

	public int getConsiderationValue() {
		return considerationValue;
	}

	public void setConsiderationValue(int considerationValue) {
		this.considerationValue = considerationValue;
	}

	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	public Service getService() {
		return service;
	}
	public void setService(Service service) {
		this.service = service;
	}
	public String getAdvertiserAddress() {
		return advertiserAddress;
	}
	public void setAdvertiserAddress(String advertiserAddress) {
		this.advertiserAddress = advertiserAddress;
	}
	public int getAdvertiserPortAddress() {
		return advertiserPortAddress;
	}
	public void setAdvertiserPortAddress(int advertiserPortAddress) {
		this.advertiserPortAddress = advertiserPortAddress;
	}
	public String getAdvertiserAddressScheme() {
		return advertiserAddressScheme;
	}
	public void setAdvertiserAddressScheme(String advertiserAddressScheme) {
		this.advertiserAddressScheme = advertiserAddressScheme;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	public String getUsePlaneAddress() {
		return usePlaneAddress;
	}

	public void setUsePlaneAddress(String usePlaneAddress) {
		this.usePlaneAddress = usePlaneAddress;
	}

	public String getUsePlaneType() {
		return usePlaneType;
	}

	public void setUsePlaneType(String usePlaneType) {
		this.usePlaneType = usePlaneType;
	}

	//@Override
	public String toString() {
		return "Advertisement [id=" + id + ", considerationMethod="
				+ considerationMethod + ", considerationValue="
				+ considerationValue + ", entityName=" + entityName
				+ ", service=" + service + ", advertiserAddress="
				+ advertiserAddress + ", advertiserPortAddress="
				+ advertiserPortAddress + ", advertiserAddressScheme="
				+ advertiserAddressScheme + ", expirationTime="
				+ expirationTime + ", creationTime=" + creationTime + "]";
	}
}
