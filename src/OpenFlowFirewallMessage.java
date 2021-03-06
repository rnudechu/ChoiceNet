import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class OpenFlowFirewallMessage {

	private String operation = "Request";
	private long transactionNumber;
	private String action;
	private String addressVersion;
	private String protocol;
	private String sourceAddress;
	private String destinationAddress;
	private String sourcePort;
	private String destinationPort;
	private String provisioningParameter;
	private String previousNetwork;
	private String serviceType;
	private String serviceName;
	private String subject; // intended to hold additional message with a payload. i.e. when searching for a substring value
	private String status = "UNKNOWN";
	private String duration;
	private String reason;
	
	public OpenFlowFirewallMessage(){}

	public OpenFlowFirewallMessage(long transactionNumber,
			String action, String addressVersion, String protocol,
			String sourceAddress, String destinationAddress, String sourcePort,
			String destinationPort) {
		super();
		this.operation = "Request";
		this.transactionNumber = transactionNumber;
		this.action = action;
		this.addressVersion = addressVersion;
		this.protocol = protocol;
		this.sourceAddress = sourceAddress;
		this.destinationAddress = destinationAddress;
		this.sourcePort = sourcePort;
		this.destinationPort = destinationPort;
	}
	public OpenFlowFirewallMessage(long transactionNumber,
			String status, String reason) {
		super();
		this.operation = "Response";
		this.transactionNumber = transactionNumber;
		this.status = status;
		this.reason = reason;
	}
	public String getOperation() {
		return operation;
	}
	@XmlAttribute
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public long getTransactionNumber() {
		return transactionNumber;
	}
	@XmlAttribute
	public void setTransactionNumber(int transactionNumber) {
		this.transactionNumber = transactionNumber;
	}
	public String getAction() {
		return action;
	}
	@XmlElement
	public void setAction(String action) {
		this.action = action;
	}
	public String getAddressVersion() {
		return addressVersion;
	}
	@XmlElement
	public void setAddressVersion(String addressVersion) {
		this.addressVersion = addressVersion;
	}
	public String getProtocol() {
		return protocol;
	}
	@XmlElement
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getSourceAddress() {
		return sourceAddress;
	}
	@XmlElement
	public void setSourceAddress(String sourceAddress) {
		this.sourceAddress = sourceAddress;
	}
	public String getDestinationAddress() {
		return destinationAddress;
	}
	@XmlElement
	public void setDestinationAddress(String destinationAddress) {
		this.destinationAddress = destinationAddress;
	}
	public String getSourcePort() {
		return sourcePort;
	}
	@XmlElement
	public void setSourcePort(String sourcePort) {
		this.sourcePort = sourcePort;
	}
	public String getDestinationPort() {
		return destinationPort;
	}
	@XmlElement
	public void setDestinationPort(String destinationPort) {
		this.destinationPort = destinationPort;
	}
	public String getStatus() {
		return status;
	}
	@XmlElement
	public void setStatus(String status) {
		this.status = status;
	}
	public String getReason() {
		return reason;
	}
	@XmlElement
	public void setReason(String reason) {
		this.reason = reason;
	}
	@XmlElement
	public void setTransactionNumber(long transactionNumber) {
		this.transactionNumber = transactionNumber;
	}
	public String getDuration() {
		return duration;
	}
	@XmlElement
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getProvisioningParameter() {
		return provisioningParameter;
	}
	@XmlElement
	public void setProvisioningParameter(String provisioningParameter) {
		this.provisioningParameter = provisioningParameter;
	}
	public String getPreviousNetwork() {
		return previousNetwork;
	}
	@XmlElement
	public void setPreviousNetwork(String previousNetwork) {
		this.previousNetwork = previousNetwork;
	}
	public String getServiceType() {
		return serviceType;
	}
	@XmlElement
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public String getServiceName() {
		return serviceName;
	}
	@XmlElement
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getSubject() {
		return subject;
	}
	@XmlElement
	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Override
	public String toString() {
		return "OpenFlowFirewallMessage [operation=" + operation
				+ ", transactionNumber=" + transactionNumber + ", action="
				+ action + ", addressVersion=" + addressVersion + ", protocol="
				+ protocol + ", sourceAddress=" + sourceAddress
				+ ", destinationAddress=" + destinationAddress
				+ ", sourcePort=" + sourcePort + ", destinationPort="
				+ destinationPort + ", provisioningParameter="
				+ provisioningParameter + ", previousNetwork="
				+ previousNetwork + ", serviceType=" + serviceType
				+ ", serviceName=" + serviceName + ", subject=" + subject
				+ ", status=" + status + ", duration=" + duration + ", reason="
				+ reason + "]";
	}
}
