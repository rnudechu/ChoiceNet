/**
 * Manager of all the Sequence Numbers the particular device is waiting for an ACK from
 * @author Robinson Udechukwu
 */

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class ConsiderationManager {
	private static ConsiderationManager instance = new ConsiderationManager();
	private static final ConcurrentHashMap<Long, Consideration> items = new ConcurrentHashMap<Long, Consideration>();
	Map<Long, Consideration> map = new TreeMap<Long, Consideration>();
		
	public static ConsiderationManager getInstance() 
	{
		return instance;
	}
	
	public Collection<Consideration> getConsideration()
	{
		return Collections.unmodifiableCollection(items.values());
	}
	
	public Consideration getFirstConsiderationFromMapping()
	{
		for (Map.Entry<Long, Consideration> entry : map.entrySet())
		{
			Consideration myConsideration = entry.getValue();
			return myConsideration;
		}
		return null;
	}
	
	public void addConsideration (long id, Consideration obj)
	{
		items.put(id, obj);
		map.put(id, obj);
	}
	
	public void remove(long id) 
	{
		items.remove(id);
		map.remove(id);
	}
	
	public String printAvailableConsiderations()
	{
		String result = "#  Consideration ID\t\tAccount:Purchased Service\n";
		int count = 1;
		for (Map.Entry<Long, Consideration> entry : map.entrySet())
		{
			
			Consideration myConsideration = entry.getValue();
			result += count+"  ";
			result += myConsideration.getConfirmationID()+"\t";
			result += myConsideration.getAccount()+":"+myConsideration.getServiceName();
			count++;
		}
		return result;
	}
	
}
