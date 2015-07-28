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


	ArrayList<String> discoveredSrcLocation = new ArrayList<String>();
	ArrayList<String> discoveredSrcTypeLocation = new ArrayList<String>();
	ArrayList<String> discoveredDstLocation = new ArrayList<String>();
	ArrayList<String> discoveredDstTypeLocation = new ArrayList<String>();
	ArrayList<String> discoveredSrcFormat = new ArrayList<String>();
	ArrayList<String> discoveredSrcTypeFormat = new ArrayList<String>();
	ArrayList<String> discoveredDstFormat = new ArrayList<String>();
	ArrayList<String> discoveredDstTypeFormat = new ArrayList<String>();
	
	// Store 
	int totalQueries;
	int cost = Integer.MAX_VALUE;
	String costType;
	int totalElements;
	boolean status = true;

	

	public ArrayList<String> getDiscoveredSrcLocation() {
		return discoveredSrcLocation;
	}

	public void setDiscoveredSrcLocation(ArrayList<String> discoveredSrcLocation) {
		this.discoveredSrcLocation = discoveredSrcLocation;
	}

	public void addDiscoveredSrcLocation(String[] newLocation, String[] newLocationType)
	{
		int i = 0;
		for(String location : newLocation)
		{
			if(!discoveredSrcLocation.contains(location))
			{
				discoveredSrcLocation.add(location);
				discoveredSrcTypeLocation.add(newLocationType[i]);
			}
			i++;
		}
	}

	public void addDiscoveredDstLocation(String[] newLocation, String[] newLocationType)
	{
		int i = 0;
		for(String location : newLocation)
		{
			if(!discoveredDstLocation.contains(location))
			{
				discoveredDstLocation.add(location);
				discoveredDstTypeLocation.add(newLocationType[i]);
			}
			i++;
		}
	}

	public void addDiscoveredSrcFormat(String[] newFormat, String[] newFormatType)
	{
		int i = 0;
		for(String format : newFormat)
		{
			if(!discoveredSrcFormat.contains(format))
			{
				discoveredSrcFormat.add(format);
				discoveredSrcTypeFormat.add(newFormatType[i]);
			}
			i++;
		}
	}

	public void addDiscoveredDstFormat(String[] newFormat, String[] newFormatType)
	{
		int i = 0;
		for(String format : newFormat)
		{
			if(!discoveredDstFormat.contains(format))
			{
				discoveredDstFormat.add(format);
				discoveredDstTypeFormat.add(newFormatType[i]);
			}
			i++;
		}
	}


	public ArrayList<String> getDiscoveredSrcTypeLocation() {
		return discoveredSrcTypeLocation;
	}

	public void setDiscoveredSrcTypeLocation(
			ArrayList<String> discoveredSrcTypeLocation) {
		this.discoveredSrcTypeLocation = discoveredSrcTypeLocation;
	}

	public ArrayList<String> getDiscoveredDstLocation() {
		return discoveredDstLocation;
	}

	public void setDiscoveredDstLocation(ArrayList<String> discoveredDstLocation) {
		this.discoveredDstLocation = discoveredDstLocation;
	}

	public ArrayList<String> getDiscoveredDstTypeLocation() {
		return discoveredDstTypeLocation;
	}

	public void setDiscoveredDstTypeLocation(
			ArrayList<String> discoveredDstTypeLocation) {
		this.discoveredDstTypeLocation = discoveredDstTypeLocation;
	}

	public ArrayList<String> getDiscoveredSrcFormat() {
		return discoveredSrcFormat;
	}

	public void setDiscoveredSrcFormat(ArrayList<String> discoveredSrcFormat) {
		this.discoveredSrcFormat = discoveredSrcFormat;
	}

	public ArrayList<String> getDiscoveredSrcTypeFormat() {
		return discoveredSrcTypeFormat;
	}

	public void setDiscoveredSrcTypeFormat(ArrayList<String> discoveredSrcTypeFormat) {
		this.discoveredSrcTypeFormat = discoveredSrcTypeFormat;
	}

	public ArrayList<String> getDiscoveredDstFormat() {
		return discoveredDstFormat;
	}

	public void setDiscoveredDstFormat(ArrayList<String> discoveredDstFormat) {
		this.discoveredDstFormat = discoveredDstFormat;
	}

	public ArrayList<String> getDiscoveredDstTypeFormat() {
		return discoveredDstTypeFormat;
	}

	public void setDiscoveredDstTypeFormat(ArrayList<String> discoveredDstTypeFormat) {
		this.discoveredDstTypeFormat = discoveredDstTypeFormat;
	}

	public int getTotalQueries() {
		return totalQueries;
	}

	public void setTotalQueries(int totalQueries) {
		this.totalQueries = totalQueries;
	}

	public void decrementQueries() {
		int count = getTotalQueries() - 1;
		setTotalQueries(count);
	}

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

	public PlannerSearchParameter (String requestorName, int numberOfFreeQueries, int numberOfPaidQueries)
	{
		this.originatorName = requestorName;
		this.totalQueries = numberOfFreeQueries+numberOfPaidQueries;
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

		totalElements += discoveredSrcLocation.size();
		totalElements += discoveredDstLocation.size();
		totalElements += discoveredSrcFormat.size();
		totalElements += discoveredDstFormat.size();

		totalElements += discoveredSrcTypeLocation.size();
		totalElements += discoveredDstTypeLocation.size();
		totalElements += discoveredSrcTypeFormat.size();
		totalElements += discoveredDstTypeFormat.size();
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
				+ dstFormat + ", dstTypeFormat=" + dstTypeFormat
				+ ", discoveredSrcLocation=" + discoveredSrcLocation
				+ ", discoveredSrcTypeLocation=" + discoveredSrcTypeLocation
				+ ", discoveredDstLocation=" + discoveredDstLocation
				+ ", discoveredDstTypeLocation=" + discoveredDstTypeLocation
				+ ", discoveredSrcFormat=" + discoveredSrcFormat
				+ ", discoveredSrcTypeFormat=" + discoveredSrcTypeFormat
				+ ", discoveredDstFormat=" + discoveredDstFormat
				+ ", discoveredDstTypeFormat=" + discoveredDstTypeFormat
				+ ", totalQueries=" + totalQueries + ", cost=" + cost
				+ ", costType=" + costType + ", totalElements=" + totalElements
				+ ", status=" + status + ", graphMatrix=" + graphMatrix + "]";
	}


}
