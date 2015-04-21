
public class AdvertisementDisplay {

	private String considerationMethod;
	private int considerationValue;
	private String entityName;
	private String serviceName;
	private String advertiserAddress;
	private int advertiserPortAddress;
	private String advertiserAddressScheme;
	private String id;
	private String _rev;
	private String description;
	private String srcLocationAddrScheme;
	private String srcLocationAddrValue;
	private String dstLocationAddrScheme;
	private String dstLocationAddrValue;
	private String srcFormatScheme;
	private String srcFormatValue;
	private String dstFormatScheme;
	private String dstFormatValue;	
	private static final long serialVersionUID = 2L;
	
	public AdvertisementDisplay(String considerationMethod,
			int considerationValue, String entityName, String serviceName,
			String advertiserAddress, int advertiserPortAddress,
			String advertiserAddressScheme, String id, String _rev,
			String description, String srcLocationAddrScheme,
			String srcLocationAddrValue, String dstLocationAddrScheme,
			String dstLocationAddrValue, String srcFormatScheme,
			String srcFormatValue, String dstFormatScheme, String dstFormatValue) {
		super();
		this.considerationMethod = considerationMethod;
		this.considerationValue = considerationValue;
		this.entityName = entityName;
		this.serviceName = serviceName;
		this.advertiserAddress = advertiserAddress;
		this.advertiserPortAddress = advertiserPortAddress;
		this.advertiserAddressScheme = advertiserAddressScheme;
		this.id = id;
		this._rev = _rev;
		this.description = description;
		this.srcLocationAddrScheme = srcLocationAddrScheme;
		this.srcLocationAddrValue = srcLocationAddrValue;
		this.dstLocationAddrScheme = dstLocationAddrScheme;
		this.dstLocationAddrValue = dstLocationAddrValue;
		this.srcFormatScheme = srcFormatScheme;
		this.srcFormatValue = srcFormatValue;
		this.dstFormatScheme = dstFormatScheme;
		this.dstFormatValue = dstFormatValue;
	}

	public String getConsiderationMethod() {
		return considerationMethod;
	}

	public void setConsiderationMethod(String considerationMethod) {
		this.considerationMethod = considerationMethod;
	}

	public int getConsiderationValue() {
		return considerationValue;
	}

	public void setConsiderationValue(int considerationValue) {
		this.considerationValue = considerationValue;
	}

	public String getEntityName() {
		return entityName;
	}


	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}


	public String getServiceName() {
		return serviceName;
	}


	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}


	public String getAdvertiserAddress() {
		return advertiserAddress;
	}


	public void setAdvertiserAddress(String advertiserAddress) {
		this.advertiserAddress = advertiserAddress;
	}


	public int getAdvertiserPortAddress() {
		return advertiserPortAddress;
	}


	public void setAdvertiserPortAddress(int advertiserPortAddress) {
		this.advertiserPortAddress = advertiserPortAddress;
	}


	public String getAdvertiserAddressScheme() {
		return advertiserAddressScheme;
	}


	public void setAdvertiserAddressScheme(String advertiserAddressScheme) {
		this.advertiserAddressScheme = advertiserAddressScheme;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String get_rev() {
		return _rev;
	}

	public void set_rev(String _rev) {
		this._rev = _rev;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSrcLocationAddrScheme() {
		return srcLocationAddrScheme;
	}

	public void setSrcLocationAddrScheme(String srcLocationAddrScheme) {
		this.srcLocationAddrScheme = srcLocationAddrScheme;
	}

	public String getSrcLocationAddrValue() {
		return srcLocationAddrValue;
	}

	public void setSrcLocationAddrValue(String srcLocationAddrValue) {
		this.srcLocationAddrValue = srcLocationAddrValue;
	}

	public String getDstLocationAddrScheme() {
		return dstLocationAddrScheme;
	}

	public void setDstLocationAddrScheme(String dstLocationAddrScheme) {
		this.dstLocationAddrScheme = dstLocationAddrScheme;
	}

	public String getDstLocationAddrValue() {
		return dstLocationAddrValue;
	}

	public void setDstLocationAddrValue(String dstLocationAddrValue) {
		this.dstLocationAddrValue = dstLocationAddrValue;
	}

	public String getSrcFormatScheme() {
		return srcFormatScheme;
	}

	public void setSrcFormatScheme(String srcFormatScheme) {
		this.srcFormatScheme = srcFormatScheme;
	}

	public String getSrcFormatValue() {
		return srcFormatValue;
	}

	public void setSrcFormatValue(String srcFormatValue) {
		this.srcFormatValue = srcFormatValue;
	}

	public String getDstFormatScheme() {
		return dstFormatScheme;
	}

	public void setDstFormatScheme(String dstFormatScheme) {
		this.dstFormatScheme = dstFormatScheme;
	}

	public String getDstFormatValue() {
		return dstFormatValue;
	}

	public void setDstFormatValue(String dstFormatValue) {
		this.dstFormatValue = dstFormatValue;
	}

	@Override
	public String toString() {
		return String.format("(id=%s, serviceName=%s, description=%s,  considerationMethod=%s, considerationValue=%s, " +
				"srcLocationAddrScheme=%s, srcLocationAddrValue=%s, dstLocationAddrScheme=%s, dstLocationAddrValue=%s,"+
				"srcFormatScheme=%s, srcFormatValue=%s, dstFormatScheme=%s, dstFormatValue=%s,"+
				"entityName=%s, advertiserAddressScheme=%s, advertiserAddress=%s, advertiserPortAddress=%s, _rev=%s)"
				, id, serviceName, description, considerationMethod, considerationValue,
				srcLocationAddrScheme, srcLocationAddrValue, dstLocationAddrScheme, dstLocationAddrValue,
				srcFormatScheme, srcFormatValue, dstFormatScheme, dstFormatValue,
				entityName, advertiserAddressScheme, advertiserAddress, advertiserPortAddress, _rev);
	}

}
