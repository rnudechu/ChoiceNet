import java.util.Arrays;

// https://code.google.com/p/r-email/source/browse/trunk/src/org/eclipse/remail/couchdb/helper/CouchDBResponse.java?r=121

public class CouchDBContainer {
        private String id;
        private String[] key;
        private AdvertisementDisplay value;
       
        public CouchDBContainer (String id, String[] key, AdvertisementDisplay value)
        {
                this.id=id;
                this.key=key;
                this.value=value;
        }
       
        @Override
        public String toString()
        {
                return String.format("(id=%s, key=%s, value=%s)", id, key.toString(), value.toString());
        }
       
        public String getId() {
                return id;
        }
        public void setId(String id) {
                this.id = id;
        }
        public String[] getKey() {
                return key;
        }
        public void setKey(String[] key) {
                this.key = key;
        }
        public AdvertisementDisplay getValue() {
                return value;
        }
        public void setValue(AdvertisementDisplay value) {
                this.value = value;
        }
        
        public String printAdvertisementDisplay()
        {
        	String message = "";
        	AdvertisementDisplay myAd = getValue();
        	message += "ID: "+myAd.getId()+"\n";
			message += "\tDescription: "+myAd.getDescription()+"\n";
			message += "\tCost: "+myAd.getConsiderationValue()+" "+myAd.getConsiderationMethod()+"\n";

			message += "\tLocation Source: "+Arrays.toString(myAd.getSrcLocationAddrScheme())+":"+Arrays.toString(myAd.getSrcLocationAddrValue())+"\n";
			message += "\tLocation Destination: "+Arrays.toString(myAd.getDstLocationAddrScheme())+":"+Arrays.toString(myAd.getDstLocationAddrValue())+"\n";
			message += "\tFormat Source: "+Arrays.toString(myAd.getSrcFormatScheme())+":"+Arrays.toString(myAd.getSrcFormatValue())+"\n";
			message += "\tFormat Destination: "+Arrays.toString(myAd.getDstFormatScheme())+":"+Arrays.toString(myAd.getDstFormatValue())+"\n";
			message += "\n";
        	return message;
        }

}
