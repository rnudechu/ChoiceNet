
public class AdvertisementPurchasePortal {
	private String scheme = "Payment";
	private String value;
	public AdvertisementPurchasePortal(String scheme, String value) {
		super();
		this.scheme = scheme;
		this.value = value;
	}
	public String getScheme() {
		return scheme;
	}
	public void setScheme(String scheme) {
		this.scheme = scheme;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return "AdvertisementPurchasePortal [scheme=" + scheme + ", value="
				+ value + "]";
	}
	
	
}
