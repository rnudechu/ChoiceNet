
public class Cost {

	public String method;
	public String amount;

	public Cost(String method, String amount) {
		super();
		this.method = method;
		this.amount = amount;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	@Override
	public String toString() {
		return "Cost [method=" + method + ", amount=" + amount + "]";
	}
	
}
