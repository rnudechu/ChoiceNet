/**
 * Manager of all the Sequence Numbers the particular device is waiting for an ACK from
 * @author Robinson Udechukwu
 */

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

public class DiscoveredEntitiesManager {
	private static DiscoveredEntitiesManager instance = new DiscoveredEntitiesManager();
	private static final ConcurrentHashMap<String, DiscoveredEntities> items = new ConcurrentHashMap<String, DiscoveredEntities>();
		
	public static DiscoveredEntitiesManager getInstance() 
	{
		return instance;
	}
	
	public Collection<DiscoveredEntities> getDiscoveredEntities()
	{
		return Collections.unmodifiableCollection(items.values());
	}
	
	
	public static DiscoveredEntities getSingleDiscoveredEntities (String id)
	{
		// If the id does not exist return -1
		if(items.get(id)==null)
		{
			return null;
		}
		return items.get(id);
	}
	
	public void addDiscoveredEntities (String id, DiscoveredEntities obj)
	{
		items.put(id, obj);
	}
	
	public void remove(String id) 
	{
		items.remove(id);
	}
	
	public String printDiscoveredEntities()
	{
		String result = "";
		// Only display existing DiscoveredEntities values
		for(DiscoveredEntities myDiscoveredEntities: getDiscoveredEntities())
		{
			result += "Name:\t\t"+myDiscoveredEntities.getName()+"\n";
			result += "Type:\t\t"+myDiscoveredEntities.getType()+"\n";
			result += "IP Address:\t\t"+myDiscoveredEntities.getIpAddr()+"\n";
			result += "Port:\t\t"+myDiscoveredEntities.getPort()+"\n";
			result += "Accepted Consideration:\t"+myDiscoveredEntities.getAcceptedConsideration()+"\n";
			result += "Available Consideration:\t"+myDiscoveredEntities.getAvailableConsideration()+"\n\n";
		}
		return result;
	}
}
