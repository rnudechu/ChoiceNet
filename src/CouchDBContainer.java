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

}
