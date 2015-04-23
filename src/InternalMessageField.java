import java.io.Serializable;

public class InternalMessageField implements Serializable {
	private static final long serialVersionUID = 1L;

	String attributeName;
	Object value;
	String url;	

	public InternalMessageField() {
		super();
		this.attributeName = "";
		this.value = "";
		this.url = "";
	}
	
	public InternalMessageField(String attributeName, Object value, String url) {
		super();
		this.attributeName = attributeName;
		this.value = value;
		this.url = url;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "ChoiceNetMessageField [attributeName=" + attributeName
				+ ", value=" + value + ", url=" + url + "]";
	}

	
}
