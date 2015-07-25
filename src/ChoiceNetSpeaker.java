import java.io.File;
import java.util.ArrayList;



public class ChoiceNetSpeaker {

	CouchDBOperations couchDBsocket = CouchDBOperations.getInstance();
	AdvertisementManager adMgr = AdvertisementManager.getInstance();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Server server; //= new Server ("marketplace.properties");
		String argument = "";
		if(args.length == 1)
		{
			argument = args[0];
		}
		if(argument.isEmpty() || argument == "")
		{
			System.out.println("\n\nYou may provide a configuration file to the choicenetSpeaker.jar as an argument");
			System.out.println("\nExample: java -jar choicenetSpeaker.jar <configName>");
			System.out.println("\n\nNo config file supplied, using default Marketplace Provider configuration.");
			server = new Server ("marketplace.properties");
		}
		else
		{
			server = new Server (argument);
		}
		System.out.println(System.currentTimeMillis());
		//		ServiceManager serviceMgr = ServiceManager.getInstance();
		AdvertisementManager adMgr = AdvertisementManager.getInstance();
		// Insert a default service into provider database ... we should do check with CouchDB but what good will that good if Admin tool wipes the database
		if(Server.providerType.equals("Marketplace"))
		{
			boolean result = true;
			if(!Server.marketplaceListing.equals("Unknown"))
			{
				// check if file exist
				File f = new File(Server.marketplaceListing);
				if(!f.exists()) 
				{
					result = false;
				}
			}
			Advertisement myAd;
			if(!result)
			{
				myAd = server.createListingServiceAdvertisement();
			}
			else
			{
				String adXML = server.readFile(Server.marketplaceListing);
				ArrayList<Advertisement> adList = server.cnLibrary.getAdvertisementsFromXML(adXML, "String");
				myAd = adList.get(0);
			}
			adMgr.addAdvertisement(myAd);
			
			// Load the marketplace with advertisements
			server.couchDBsocket.postRestInterface(Server.marketplaceRESTAPI, myAd);
			Logger.log("Included the following advertisement into CouchDB: \n"+myAd);
		}
		server.printServerProperties();
		int loop = 0;
		while(true)
		{
			try {
				server.startServer();
			} catch (Exception e) {
				System.out.println("Server is already running at the given port");
			}
			loop++;
			System.out.println("Loop count "+loop);
		}
	}

}
