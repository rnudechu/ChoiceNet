/**
 * Manager of all the Sequence Numbers the particular device is waiting for an ACK from
 * @author Robinson Udechukwu
 */

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class TokenManager {
	private static TokenManager instance = new TokenManager();
	private static final ConcurrentHashMap<Long, Token> items = new ConcurrentHashMap<Long, Token>();
	Map<Long, Token> map = new TreeMap<Long, Token>();
		
	public static TokenManager getInstance() 
	{
		return instance;
	}
	
	public Collection<Token> getToken()
	{
		return Collections.unmodifiableCollection(items.values());
	}
	
	public Token getFirstTokenFromMapping()
	{
		for (Map.Entry<Long, Token> entry : map.entrySet())
		{
			Token myToken = entry.getValue();
			if(myToken.getExpirationTime() > System.currentTimeMillis())
			{
				return myToken;
			}
		}
		return null;
	}
	
	public int getToken (String id)
	{
		// If the id does not exist return -1
		if(items.get(id)==null)
		{
			return -1;
		}
		// check to see if this sequence number expiration has reached
		// if the timestamp for this object already expired return -1
		long totalTime = items.get(id).getExpirationTime();
		if(totalTime <= System.currentTimeMillis())
		{
			return -1;
		}
		return items.get(id).getId();
	}
	
	public long getTokenCreationTime (int id)
	{
		for(Token myToken : getToken())
		{
			if(myToken.getId() == id)
			{
				return myToken.getCreationTime(); 
			}
		}
		return 0;
	}
	
	public static Token getSingleToken (long id)
	{
		// If the id does not exist return -1
		if(items.get(id)==null)
		{
			return null;
		}
		// check to see if this sequence number expiration has reached
		// if the timestamp for this object already expired return -1
		long eTime = items.get(id).getExpirationTime();
		if(eTime <= System.currentTimeMillis())
		{
			long result = System.currentTimeMillis()-eTime;
			System.out.println("Token expired! "+result+" milliseconds has passed!");
			return null;
		}
		return items.get(id);
	}
	
	public void addToken (long id, Token obj)
	{
		items.put(id, obj);
		map.put(id, obj);
	}
	
	public void remove(long id) 
	{
		items.remove(id);
		map.remove(id);
	}
	
	public int createTokenID()
	{
		DecimalFormat df = new DecimalFormat("#######");
		String seqNumString = df.format((Math.random()*1000000));
		while(getToken(seqNumString)!=-1)
		{
			seqNumString = df.format((Math.random()*1000000));
		}
		// save the sequence number
		int result = Integer.parseInt(seqNumString);
		return result;
	}
	
	public String printAvailableTokens()
	{
		String result = "#\tToken ID\tIssued By\t\tToken Service\t\tStatus\n";
		int count = 1;
		for (Map.Entry<Long, Token> entry : map.entrySet())
		{
			
			Token myToken = entry.getValue();
			System.out.println(entry.getKey()+": "+myToken);
			result += count+"\t";
			result += myToken.getId()+"\t";
			result += myToken.getIssuedBy()+"\t";	
			result += myToken.getServiceName()+"\t\t";
			if(myToken.getExpirationTime() <= System.currentTimeMillis())
			{
				result += "Expired\n";
			}
			else
			{
				result += "Valid\n";
			}
			count++;
		}
		return result;
	}
	
}
