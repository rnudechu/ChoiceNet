public class Advertisement {

	private String id;
	private AdvertisementPrice price;
	private AdvertisementPurchasePortal purchasePortal;
	private String providerID;
	private Service service;
	private long creationTime;
	private String state; // Attempting, Advertised, Expired, Not Available
	private String usePlaneAddress;
	private String usePlaneType;
	
	public Advertisement(String considerationMethod, int considerationValue,
			String providerID, Service service, String purchasePortalValue,
			String purchasePortalScheme,
			String usePlaneType, String usePlaneAddr) {
		super();
		price = new AdvertisementPrice(considerationMethod, considerationValue);
		this.providerID = providerID;
		this.service = service;
		purchasePortal = new AdvertisementPurchasePortal(purchasePortalScheme, purchasePortalValue);
		this.creationTime = System.currentTimeMillis();
		this.usePlaneType = usePlaneType;
		this.usePlaneAddress = usePlaneAddr;
		state = "Attempting";
	}
	
	public Advertisement(String considerationMethod, int considerationValue,
			String providerID, Service service, String purchasePortalValue,
			String purchasePortalScheme) {
		super();
		price = new AdvertisementPrice(considerationMethod, considerationValue);
		this.providerID = providerID;
		this.service = service;
		purchasePortal = new AdvertisementPurchasePortal(purchasePortalScheme, purchasePortalValue);
		this.creationTime = System.currentTimeMillis();
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

	public AdvertisementPrice getPrice() {
		return price;
	}

	public void setPrice(AdvertisementPrice price) {
		this.price = price;
	}

	public String getProviderID() {
		return providerID;
	}

	public void setProviderID(String providerID) {
		this.providerID = providerID;
	}

	public String getproviderID() {
		return providerID;
	}
	public void setproviderID(String providerID) {
		this.providerID = providerID;
	}
	public Service getService() {
		return service;
	}
	public void setService(Service service) {
		this.service = service;
	}

	public AdvertisementPurchasePortal getPurchasePortal() {
		return purchasePortal;
	}

	public void setPurchasePortal(AdvertisementPurchasePortal purchasePortal) {
		this.purchasePortal = purchasePortal;
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

	@Override
	public String toString() {
		return "Advertisement [id=" + id + ", price=" + price
				+ ", purchasePortal=" + purchasePortal + ", providerID="
				+ providerID + ", service=" + service + ", creationTime="
				+ creationTime + ", state=" + state + ", usePlaneAddress="
				+ usePlaneAddress + ", usePlaneType=" + usePlaneType + "]";
	}
}
