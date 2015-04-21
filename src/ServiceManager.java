/**
 * Manager of all the Sequence Numbers the particular device is waiting for an ACK from
 * @author Robinson Udechukwu
 */

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceManager {
	private static ServiceManager instance = new ServiceManager();
	private static final ConcurrentHashMap<String, Service> items = new ConcurrentHashMap<String, Service>();
		
	public static ServiceManager getInstance() 
	{
		return instance;
	}
	
	public Collection<Service> getService()
	{
		return Collections.unmodifiableCollection(items.values());
	}
	
	
	public static Service getSingleService (String id)
	{
		// If the id does not exist return -1
		if(items.get(id)==null)
		{
			return null;
		}
		return items.get(id);
	}
	

	public boolean doesServiceExist (String id)
	{
		// If the id does not exist return -1
		if(items.get(id)==null)
		{
			return false;
		}
		return true;
	}
	
	public void addService (String id, Service obj)
	{
		items.put(id, obj);
	}
	
	public void remove(String id) 
	{
		items.remove(id);
	}
	
	
}
