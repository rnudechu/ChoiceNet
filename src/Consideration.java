
public class Consideration {
	private long creationID;
	private String paymentMethod;
	private String account;
	private String amount;
	private String reason;
	private String confirmationID;
	private String serviceName;
	
	public Consideration(long creationID, String paymentMethod, String account,
			String amount, String reason, String confirmationID, String serviceName) {
		super();
		this.creationID = creationID;
		this.paymentMethod = paymentMethod;
		this.account = account;
		this.amount = amount;
		this.reason = reason;
		this.confirmationID = confirmationID;
		this.serviceName = serviceName;
	}

	public long getCreationID() {
		return creationID;
	}

	public void setCreationID(long creationID) {
		this.creationID = creationID;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getConfirmationID() {
		return confirmationID;
	}

	public void setConfirmationID(String confirmationID) {
		this.confirmationID = confirmationID;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	@Override
	public String toString() {
		return "Consideration [creationID=" + creationID + ", paymentMethod="
				+ paymentMethod + ", account=" + account + ", amount=" + amount
				+ ", reason=" + reason + ", confirmationID=" + confirmationID
				+ ", serviceName=" + serviceName + "]";
	}
	
}