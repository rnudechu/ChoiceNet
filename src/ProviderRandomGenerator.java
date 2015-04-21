import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;


public class ProviderRandomGenerator {

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
			System.out.println("\n\nYou may provide a configuration file to the provider.jar as an argument");
			System.out.println("\nExample: java -jar provider.jar <configName>");
			System.out.println("\n\nNo config file supplied, using default Provider configuration.");
			server = new Server ("transport.properties");
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
			Advertisement myAd = server.createListingServiceAdvertisement();
			adMgr.addAdvertisement(myAd);
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
			
			// foreach file "send consideration", "send purchase request"
			//server.transferConsiderationMessage(serviceName, target, exchangeType, exchangeAmount, ipAddr, port);
			//server.transferListingMessage(fileName, target, tokenID, ipAddr, port);
			loop++;
			System.out.println("Loop count "+loop);
		}
	}

}
