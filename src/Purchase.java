
public class Purchase {

	private String purchaseID;
	private PurchaseType status;
	private String token;
	private String advertiserAddress;
	private String advertiserPortAddress;
	
	public Purchase(String purchaseID, PurchaseType status, String token,
			String advertiserAddress, String advertiserPortAddress) {
		this.purchaseID = purchaseID;
		this.status = status;
		this.token = token;
		this.advertiserAddress = advertiserAddress;
		this.advertiserPortAddress = advertiserPortAddress;
	}


	public String getPurchaseID() {
		return purchaseID;
	}

	public void setPurchaseID(String purchaseID) {
		this.purchaseID = purchaseID;
	}

	public PurchaseType getStatus() {
		return status;
	}

	public void setStatus(PurchaseType status) {
		this.status = status;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getAdvertiserAddress() {
		return advertiserAddress;
	}

	public void setAdvertiserAddress(String advertiserAddress) {
		this.advertiserAddress = advertiserAddress;
	}

	public String getAdvertiserPortAddress() {
		return advertiserPortAddress;
	}

	public void setAdvertiserPortAddress(String advertiserPortAddress) {
		this.advertiserPortAddress = advertiserPortAddress;
	}
	
	
}
