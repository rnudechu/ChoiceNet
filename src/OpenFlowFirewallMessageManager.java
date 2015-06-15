/**
 * Manager of all the Sequence Numbers the particular device is waiting for an ACK from
 * @author Robinson Udechukwu
 */

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class OpenFlowFirewallMessageManager {
	private static OpenFlowFirewallMessageManager instance = new OpenFlowFirewallMessageManager();
	private static final ConcurrentHashMap<Long, OpenFlowFirewallMessage> items = new ConcurrentHashMap<Long, OpenFlowFirewallMessage>();
	Map<Long, OpenFlowFirewallMessage> map = new TreeMap<Long, OpenFlowFirewallMessage>();

	public static OpenFlowFirewallMessageManager getInstance() 
	{
		return instance;
	}

	public Collection<OpenFlowFirewallMessage> getOpenFlowFirewallMessage()
	{
		return Collections.unmodifiableCollection(items.values());
	}

	public OpenFlowFirewallMessage getFirstOpenFlowFirewallMessageFromMapping()
	{
		for (Map.Entry<Long, OpenFlowFirewallMessage> entry : map.entrySet())
		{
			OpenFlowFirewallMessage myOpenFlowFirewallMessage = entry.getValue();
			return myOpenFlowFirewallMessage;
		}
		return null;
	}

	public void addOpenFlowFirewallMessage (long id, OpenFlowFirewallMessage obj)
	{
		items.put(id, obj);
		map.put(id, obj);
	}

	public void remove(long id) 
	{
		items.remove(id);
		map.remove(id);
	}

	public String printAvailableOpenFlowFirewallMessages()
	{
		String result = "";
		int count = 1;
		for (Map.Entry<Long, OpenFlowFirewallMessage> entry : map.entrySet())
		{

			OpenFlowFirewallMessage myOpenFlowFirewallMessage = entry.getValue();
			System.out.println(entry.getKey()+": "+myOpenFlowFirewallMessage);
			result += "Item "+count+":\n";
			result += "\tAction: "+myOpenFlowFirewallMessage.getAction()+"\n";
			result += "\tAddress Version: "+myOpenFlowFirewallMessage.getAddressVersion()+"\n";	
			result += "\tProtocol: "+myOpenFlowFirewallMessage.getProtocol()+"\n";
			result += "\tSource Address: "+myOpenFlowFirewallMessage.getSourceAddress()+"\n";
			result += "\tSource Port: "+myOpenFlowFirewallMessage.getSourcePort()+"\n";
			result += "\tDestination Address: "+myOpenFlowFirewallMessage.getDestinationAddress()+"\n";
			result += "\tDestination Port: "+myOpenFlowFirewallMessage.getDestinationPort()+"\n";
			result += "\tStatus: "+myOpenFlowFirewallMessage.getStatus()+"\n";
			result += "====================\n";
			count++;
		}
		return result;
	}

}
