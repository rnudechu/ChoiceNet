
public class ServiceRequirement {
	
	String sourceLoc;
	String sourceLocType; 
	String destinationLoc;
	String destinationLocType;
	String sourceFormat; 
	String sourceFormatType; 
	String destinationFormat; 
	String destinationFormatType;
	Cost cost;
	
	public ServiceRequirement(String sourceLoc, String sourceLocType,
			String destinationLoc, String destinationLocType,
			String sourceFormat, String sourceFormatType,
			String destinationFormat, String destinationFormatType, Cost cost) {
		super();
		this.sourceLoc = sourceLoc;
		this.sourceLocType = sourceLocType;
		this.destinationLoc = destinationLoc;
		this.destinationLocType = destinationLocType;
		this.sourceFormat = sourceFormat;
		this.sourceFormatType = sourceFormatType;
		this.destinationFormat = destinationFormat;
		this.destinationFormatType = destinationFormatType;
		this.cost = cost;
	}
	public String getSourceLoc() {
		return sourceLoc;
	}
	public void setSourceLoc(String sourceLoc) {
		this.sourceLoc = sourceLoc;
	}
	public String getDestinationLoc() {
		return destinationLoc;
	}
	public void setDestinationLoc(String destinationLoc) {
		this.destinationLoc = destinationLoc;
	}
	public String getSourceFormat() {
		return sourceFormat;
	}
	public void setSourceFormat(String sourceFormat) {
		this.sourceFormat = sourceFormat;
	}
	public String getDestinationFormat() {
		return destinationFormat;
	}
	public void setDestinationFormat(String destinationFormat) {
		this.destinationFormat = destinationFormat;
	}
	public String getSourceLocType() {
		return sourceLocType;
	}
	public void setSourceLocType(String sourceLocType) {
		this.sourceLocType = sourceLocType;
	}
	public String getDestinationLocType() {
		return destinationLocType;
	}
	public void setDestinationLocType(String destinationLocType) {
		this.destinationLocType = destinationLocType;
	}
	public String getSourceFormatType() {
		return sourceFormatType;
	}
	public void setSourceFormatType(String sourceFormatType) {
		this.sourceFormatType = sourceFormatType;
	}
	public String getDestinationFormatType() {
		return destinationFormatType;
	}
	public void setDestinationFormatType(String destinationFormatType) {
		this.destinationFormatType = destinationFormatType;
	}
	public Cost getCost() {
		return cost;
	}
	public void setCost(Cost cost) {
		this.cost = cost;
	}
	@Override
	public String toString() {
		return "ServiceRequirement [sourceLoc=" + sourceLoc
				+ ", sourceLocType=" + sourceLocType + ", destinationLoc="
				+ destinationLoc + ", destinationLocType=" + destinationLocType
				+ ", sourceFormat=" + sourceFormat + ", sourceFormatType="
				+ sourceFormatType + ", destinationFormat=" + destinationFormat
				+ ", destinationFormatType=" + destinationFormatType
				+ ", cost=" + cost + "]";
	}


	
	
}
