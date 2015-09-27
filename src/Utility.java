import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class Utility {
	// http://stackoverflow.com/a/10484311
		public static boolean netMatch(String subnet, String addr){ 

	        String[] parts = addr.split("/");
	        if(parts.length != 2)
	        {
	        	return false;
	        }
	        addr = parts[0];
	        
	        parts = subnet.split("/");
	        if(parts.length != 2)
	        {
	        	return false;
	        }
	        String ip = parts[0];
	        int prefix;

	        if (parts.length < 2) {
	            prefix = 0;
	        } else {
	            prefix = Integer.parseInt(parts[1]);
	        }

	        Inet4Address a =null;
	        Inet4Address a1 =null;
	        try {
	            a = (Inet4Address) InetAddress.getByName(ip);
	            a1 = (Inet4Address) InetAddress.getByName(addr);
	        } catch (UnknownHostException e){}

	        byte[] b = a.getAddress();
	        int subnetBytes = ((b[0] & 0xFF) << 24) |
	                         ((b[1] & 0xFF) << 16) |
	                         ((b[2] & 0xFF) << 8)  |
	                         ((b[3] & 0xFF) << 0);

	        byte[] b1 = a1.getAddress();
	        int ipBytes = ((b1[0] & 0xFF) << 24) |
	                         ((b1[1] & 0xFF) << 16) |
	                         ((b1[2] & 0xFF) << 8)  |
	                         ((b1[3] & 0xFF) << 0);

	        int mask = ~((1 << (32 - prefix)) - 1);

	        if ((subnetBytes & mask) == (ipBytes & mask)) {
	            return true;
	        }
	        else {
	            return false;
	        }
		}
}
