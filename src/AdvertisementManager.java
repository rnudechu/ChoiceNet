/**
 * Manager of all the Client's capabilities
 * @author Robinson Udechukwu
 */

import java.util.concurrent.CopyOnWriteArrayList;

public class AdvertisementManager {
	private CopyOnWriteArrayList<Advertisement> advertisementList = new CopyOnWriteArrayList<Advertisement>();
	private static AdvertisementManager instance = new AdvertisementManager();
	public static AdvertisementManager getInstance() 
	{
		return instance;
	}
	
	public CopyOnWriteArrayList<Advertisement> getSingleInstance()
	{
		return advertisementList;
	}
	
	
	public void addAdvertisement (Advertisement value)
	{
		advertisementList.add(value);
	}
	
	public void flush ()
	{
		advertisementList.clear();
	}
	
	public void removeAdvertisement (Advertisement obj)
	{
		advertisementList.remove(obj);
	}
	
	public int getSize ()
	{
		return advertisementList.size();
	}

	public Advertisement getAdvertisementByID (String name)
	{
		for (Advertisement currAdvertisement : getSingleInstance())
		{
			if(currAdvertisement.getId().equals(name))
			{
				return currAdvertisement; 
			}
		}
		return null;
	}
	public Advertisement getAdvertisementByName (String name)
	{
		
		for (Advertisement currAdvertisement : getSingleInstance())
		{
			if(currAdvertisement.getService().getName().equals(name))
			{
				return currAdvertisement; 
			}
		}
		return null;
	}
	
}
