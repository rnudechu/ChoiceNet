/**
 * Manager of all the Sequence Numbers the particular device is waiting for an ACK from
 * @author Robinson Udechukwu
 */

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionManager {
	private static TransactionManager instance = new TransactionManager();
	private static final ConcurrentHashMap<String, Transaction> items = new ConcurrentHashMap<String, Transaction>();
		
	public static TransactionManager getInstance() 
	{
		return instance;
	}
	
	public Collection<Transaction> getTransaction()
	{
		return Collections.unmodifiableCollection(items.values());
	}
	
	public int getTransaction (String id)
	{
		// If the id does not exist return -1
		if(items.get(id)==null)
		{
			return -1;
		}
		return items.get(id).getId();
	}
	
	public static Transaction getSingleTransaction (String id)
	{
		// If the id does not exist return -1
		if(items.get(id)==null)
		{
			return null;
		}
		return items.get(id);
	}
	
	public void addTransaction (String id, Transaction obj)
	{
		items.put(id, obj);
	}
	
	public void remove(String id) 
	{
		items.remove(id);
	}
	
	public int createAndSaveTransaction(String target, String serviceID)
	{
		DecimalFormat df = new DecimalFormat("#######");
		String seqNumString = df.format((Math.random()*1000000));
		while(getTransaction(seqNumString)!=-1)
		{
			seqNumString = df.format((Math.random()*1000000));
		}
		// save the sequence number
		int result = Integer.parseInt(seqNumString);
		Transaction seqNum = new Transaction(result, target, serviceID);
		addTransaction(seqNumString, seqNum);
		return result;
	}
	
}
