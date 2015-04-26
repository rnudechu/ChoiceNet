
public class Service {

	String name;
	String type;
	String[] srcLocationAddrScheme;
	String[] srcLocationAddrValue;
	String[] dstLocationAddrScheme;
	String[] dstLocationAddrValue;
	String[] srcFormatScheme;
	String[] srcFormatValue;
	String[] dstFormatScheme;
	String[] dstFormatValue;	
	ProvisioningProperty[] property;
	String description;
	
	public Service(String name, String type, String[] srcLocationAddrScheme,
			String[] srcLocationAddrValue, String[] dstLocationAddrScheme,
			String[] dstLocationAddrValue, ProvisioningProperty[] property, String description) {
		super();
		this.name = name;
		this.type = type;
		this.srcLocationAddrScheme = srcLocationAddrScheme;
		this.srcLocationAddrValue = srcLocationAddrValue;
		this.dstLocationAddrScheme = dstLocationAddrScheme;
		this.dstLocationAddrValue = dstLocationAddrValue;
		this.property = property;
		this.description = description;
	}

	public Service(String name, String type, String description) {
		super();
		this.name = name;
		this.type = type;
		this.description = description;
	}

	public Service(String name, String type, String[] srcLocationAddrScheme,
			String[] srcLocationAddrValue, String[] dstLocationAddrScheme,
			String[] dstLocationAddrValue, String[] srcFormatScheme,
			String[] srcFormatValue, String[] dstFormatScheme,
			String[] dstFormatValue, ProvisioningProperty[] property, String description) {
		super();
		this.name = name;
		this.type = type;
		this.srcLocationAddrScheme = srcLocationAddrScheme;
		this.srcLocationAddrValue = srcLocationAddrValue;
		this.dstLocationAddrScheme = dstLocationAddrScheme;
		this.dstLocationAddrValue = dstLocationAddrValue;
		this.srcFormatScheme = srcFormatScheme;
		this.srcFormatValue = srcFormatValue;
		this.dstFormatScheme = dstFormatScheme;
		this.dstFormatValue = dstFormatValue;
		this.property = property;
		this.description = description;
	}

	public String[] getSrcFormatScheme() {
		return srcFormatScheme;
	}

	public void setSrcFormatScheme(String[] srcFormatScheme) {
		this.srcFormatScheme = srcFormatScheme;
	}

	public String[] getSrcFormatValue() {
		return srcFormatValue;
	}

	public void setSrcFormatValue(String[] srcFormatValue) {
		this.srcFormatValue = srcFormatValue;
	}

	public String[] getDstFormatScheme() {
		return dstFormatScheme;
	}

	public void setDstFormatScheme(String[] dstFormatScheme) {
		this.dstFormatScheme = dstFormatScheme;
	}

	public String[] getDstFormatValue() {
		return dstFormatValue;
	}

	public void setDstFormatValue(String[] dstFormatValue) {
		this.dstFormatValue = dstFormatValue;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String[] getSrcLocationAddrScheme() {
		return srcLocationAddrScheme;
	}

	public void setSrcLocationAddrScheme(String[] srcLocationAddrScheme) {
		this.srcLocationAddrScheme = srcLocationAddrScheme;
	}

	public String[] getSrcLocationAddrValue() {
		return srcLocationAddrValue;
	}

	public void setSrcLocationAddrValue(String[] srcLocationAddrValue) {
		this.srcLocationAddrValue = srcLocationAddrValue;
	}

	public String[] getDstLocationAddrScheme() {
		return dstLocationAddrScheme;
	}

	public void setDstLocationAddrScheme(String[] dstLocationAddrScheme) {
		this.dstLocationAddrScheme = dstLocationAddrScheme;
	}

	public String[] getDstLocationAddrValue() {
		return dstLocationAddrValue;
	}

	public void setDstLocationAddrValue(String[] dstLocationAddrValue) {
		this.dstLocationAddrValue = dstLocationAddrValue;
	}

	public ProvisioningProperty[] getProperty() {
		return property;
	}

	public void setProperty(ProvisioningProperty[] property) {
		this.property = property;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Service [name=" + name + ", type=" + type
				+ ", srcLocationAddrScheme=" + srcLocationAddrScheme
				+ ", srcLocationAddrValue=" + srcLocationAddrValue
				+ ", dstLocationAddrScheme=" + dstLocationAddrScheme
				+ ", dstLocationAddrValue=" + dstLocationAddrValue
				+ ", srcFormatScheme=" + srcFormatScheme + ", srcFormatValue="
				+ srcFormatValue + ", dstFormatScheme=" + dstFormatScheme
				+ ", dstFormatValue=" + dstFormatValue + ", property="
				+ property + ", description=" + description + "]";
	}
	
}
