/**
 * Manager of all the Sequence Numbers the particular device is waiting for an ACK from
 * @author Robinson Udechukwu
 */

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

public class PurchaseManager {
	private static PurchaseManager instance = new PurchaseManager();
	private static final ConcurrentHashMap<String, Purchase> items = new ConcurrentHashMap<String, Purchase>();
		
	public static PurchaseManager getInstance() 
	{
		return instance;
	}
	
	public Collection<Purchase> getPurchase()
	{
		return Collections.unmodifiableCollection(items.values());
	}
	
	public Purchase getPurchase (String id)
	{
		return items.get(id);
	}
	
	
	public void addPurchase (String id, Purchase obj)
	{
		items.put(id, obj);
	}
	
	public void remove(String id) 
	{
		items.remove(id);
	}
	
	
}
