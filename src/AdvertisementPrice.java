
public class AdvertisementPrice {
	String method;
	int value;
	
	public AdvertisementPrice(String method, int value) {
		super();
		this.method = method;
		this.value = value;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "AdvertisementPrice [method=" + method + ", value=" + value
				+ "]";
	}
	
}
