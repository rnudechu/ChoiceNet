import java.util.ArrayList;


public class PlannerSearchParameter {
	long identifier = System.currentTimeMillis();
	String originatorName;
	ArrayList<String> srcLocation = new ArrayList<String>();
	ArrayList<String> srcTypeLocation = new ArrayList<String>();
	ArrayList<String> dstLocation = new ArrayList<String>();
	ArrayList<String> dstTypeLocation = new ArrayList<String>();
	ArrayList<String> srcFormat = new ArrayList<String>();
	ArrayList<String> srcTypeFormat = new ArrayList<String>();
	ArrayList<String> dstFormat = new ArrayList<String>();
	ArrayList<String> dstTypeFormat = new ArrayList<String>();
	int cost = Integer.MAX_VALUE;
	String costType;
	int totalElements;
	boolean status = true;
	
	public String getCostType() {
		return costType;
	}

	public void setCostType(String costType) {
		this.costType = costType;
	}

	PlannerGraphMatrix graphMatrix = new PlannerGraphMatrix();
	
	public PlannerGraphMatrix getGraphMatrix() {
		return graphMatrix;
	}

	public void setGraphMatrix(PlannerGraphMatrix graphMatrix) {
		this.graphMatrix = graphMatrix;
	}

	public long getIdentifier() {
		return identifier;
	}

	public void setIdentifier(long identifier) {
		this.identifier = identifier;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public PlannerSearchParameter (String requestorName)
	{
		this.originatorName = requestorName;
	}
	
	public String getOriginatorName() {
		return originatorName;
	}
	public void setOriginatorName(String originatorName) {
		this.originatorName = originatorName;
	}

	public int getTotalElements() {
		totalElements = 0;
		totalElements += srcLocation.size();
		totalElements += dstLocation.size();
		totalElements += srcFormat.size();
		totalElements += dstFormat.size();
		
		totalElements += srcTypeLocation.size();
		totalElements += dstTypeLocation.size();
		totalElements += srcTypeFormat.size();
		totalElements += dstTypeFormat.size();
		return totalElements;
	}
	public ArrayList<String> getSrcTypeLocation() {
		return srcTypeLocation;
	}
	public void setSrcTypeLocation(ArrayList<String> srcTypeLocation) {
		this.srcTypeLocation = srcTypeLocation;
	}
	public ArrayList<String> getDstTypeLocation() {
		return dstTypeLocation;
	}
	public void setDstTypeLocation(ArrayList<String> dstTypeLocation) {
		this.dstTypeLocation = dstTypeLocation;
	}
	public ArrayList<String> getSrcTypeFormat() {
		return srcTypeFormat;
	}
	public void setSrcTypeFormat(ArrayList<String> srcTypeFormat) {
		this.srcTypeFormat = srcTypeFormat;
	}
	public ArrayList<String> getDstTypeFormat() {
		return dstTypeFormat;
	}
	public void setDstTypeFormat(ArrayList<String> dstTypeFormat) {
		this.dstTypeFormat = dstTypeFormat;
	}
	public void setTotalElements(int totalElements) {
		this.totalElements = totalElements;
	}
	public ArrayList<String> getSrcLocation() {
		return srcLocation;
	}
	public void setSrcLocation(ArrayList<String> srcLocation) {
		this.srcLocation = srcLocation;
	}
	public ArrayList<String> getDstLocation() {
		return dstLocation;
	}
	public void setDstLocation(ArrayList<String> dstLocation) {
		this.dstLocation = dstLocation;
	}
	public ArrayList<String> getSrcFormat() {
		return srcFormat;
	}
	public void setSrcFormat(ArrayList<String> srcFormat) {
		this.srcFormat = srcFormat;
	}
	public ArrayList<String> getDstFormat() {
		return dstFormat;
	}
	public void setDstFormat(ArrayList<String> dstFormat) {
		this.dstFormat = dstFormat;
	}
	public int getCost() {
		return cost;
	}
	public void setCost(int cost) {
		this.cost = cost;
	}
	
	@Override
	public String toString() {
		return "PlannerSearchParameter [identifier=" + identifier
				+ ", originatorName=" + originatorName + ", srcLocation="
				+ srcLocation + ", srcTypeLocation=" + srcTypeLocation
				+ ", dstLocation=" + dstLocation + ", dstTypeLocation="
				+ dstTypeLocation + ", srcFormat=" + srcFormat
				+ ", srcTypeFormat=" + srcTypeFormat + ", dstFormat="
				+ dstFormat + ", dstTypeFormat=" + dstTypeFormat + ", cost="
				+ cost + ", costType=" + costType + ", totalElements="
				+ totalElements + ", status=" + status + ", graphMatrix="
				+ graphMatrix + "]";
	}
	
	
}
