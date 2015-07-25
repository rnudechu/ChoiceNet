
public class PlannerServiceRecipe {
	String advertisementList;
	int totalCost;
	
	public PlannerServiceRecipe(String advertisementList, int totalCost) {
		super();
		this.advertisementList = advertisementList;
		this.totalCost = totalCost;
	}

	public String getAdvertisementList() {
		return advertisementList;
	}
	public void setAdvertisementList(String advertisementList) {
		this.advertisementList = advertisementList;
	}
	public int getTotalCost() {
		return totalCost;
	}
	public void setTotalCost(int totalCost) {
		this.totalCost = totalCost;
	}
	
	@Override
	public String toString() {
		return "PlannerServiceRecipe [advertisementList=" + advertisementList
				+ ", totalCost=" + totalCost + "]";
	}
}
