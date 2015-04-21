/**
 * Packet structure/ class
 * @author Robinson Udechukwu
 */
import java.io.Serializable;

public class Packet implements Serializable {
	private static final long serialVersionUID = 1L;
	private String preamble;
	private PacketType actionCode;// messageType
    private ChoiceNetMessageField originatorName; // originator name
    private ChoiceNetMessageField originatorSignature; 
    private ChoiceNetMessageField originatorType;
    private ChoiceNetMessageField originatorProviderType;
    private ChoiceNetMessageField messageType; 
    private ChoiceNetMessageField messageSpecific; 
    
	public Packet(PacketType actionCode, String oName,
			String oSignature, String oType, String oPType,
			ChoiceNetMessageField[] payload) {
		super();
		this.preamble = "CHOICENET";
		this.actionCode = actionCode;
		originatorName = new ChoiceNetMessageField("Originator Name", oName, "");
		originatorSignature = new ChoiceNetMessageField("Originator Signature", oSignature, "");
		originatorType = new ChoiceNetMessageField("Originator Type", oType, "");
		originatorProviderType = new ChoiceNetMessageField("Originator Provider Type", oPType, "");
		messageType = new ChoiceNetMessageField("Message Type", actionCode.toString(), "");
		messageSpecific = new ChoiceNetMessageField("Message Specific", payload, "");
	}

	public String getPreamble() {
		return preamble;
	}

	public void setPreamble(String preamble) {
		this.preamble = preamble;
	}

	public PacketType getActionCode() {
		return actionCode;
	}

	public void setActionCode(PacketType actionCode) {
		this.actionCode = actionCode;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	// fills only used for specific payloads
	
	public ChoiceNetMessageField getOriginatorName() {
		return originatorName;
	}

	public void setOriginatorName(ChoiceNetMessageField originatorName) {
		this.originatorName = originatorName;
	}

	public ChoiceNetMessageField getMessageType() {
		return messageType;
	}

	public void setMessageType(ChoiceNetMessageField messageType) {
		this.messageType = messageType;
	}

	public ChoiceNetMessageField getMessageSpecific() {
		return messageSpecific;
	}

	public void setMessageSpecific(ChoiceNetMessageField messageSpecific) {
		this.messageSpecific = messageSpecific;
	}

	public ChoiceNetMessageField getOriginatorSignature() {
		return originatorSignature;
	}

	public void setOriginatorSignature(ChoiceNetMessageField originatorSignature) {
		this.originatorSignature = originatorSignature;
	}

	public ChoiceNetMessageField getOriginatorType() {
		return originatorType;
	}

	public void setOriginatorType(ChoiceNetMessageField originatorType) {
		this.originatorType = originatorType;
	}

	public ChoiceNetMessageField getOriginatorProviderType() {
		return originatorProviderType;
	}

	public void setOriginatorProviderType(
			ChoiceNetMessageField originatorProviderType) {
		this.originatorProviderType = originatorProviderType;
	}

	@Override
	public String toString() {
		return "Packet [preamble=" + preamble + ", actionCode=" + actionCode
				+ ", originatorName=" + originatorName
				+ ", originatorSignature=" + originatorSignature
				+ ", originatorType=" + originatorType + ", messageType="
				+ messageType + ", messageSpecific=" + messageSpecific + "]";
	}

}
