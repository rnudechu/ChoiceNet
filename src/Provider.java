import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;


public class Provider {

	CouchDBOperations couchDBsocket = CouchDBOperations.getInstance();
	AdvertisementManager adMgr = AdvertisementManager.getInstance();
	

	public void createDatabase(Server server)
	{
		Scanner sc;
		String response = "";
		// load couchdb view
		String content = "";
		try {
			sc = new Scanner(new FileReader("marketplace_view.json"));
			while (sc.hasNextLine()) {
				content += sc.nextLine();
			}
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		response = couchDBsocket.getRestInterface(Server.marketplaceRESTAPI);
		if(response.equals("404"))
		{
			// insert the table
			couchDBsocket.putRestInterface(Server.marketplaceRESTAPI,"");
			//server.loadCouchDBView("/_design/marketplace", content);
			// load the View
			couchDBsocket.postRestInterface(Server.marketplaceRESTAPI,content);
		}
		// Install a default listing service
		String entityName = Server.myName;
		ProvisioningProperty sProp = new ProvisioningProperty("Time Length", "10 minutes");
		ProvisioningProperty serviceProps[] = {sProp};
		Service service  = new Service("Advertisement Listing", "Listing", "UDPv4", "10.1.0.1:80", "UDPv4", "10.1.0.1:80", serviceProps, "List Service");
		String advertiserAddr = server.getLocalIpAddress("IP");
		int advertiserPort = Integer.parseInt(server.getLocalIpAddress("Port"));
		long moreTime = 600000*2;// 10 minutes * 2
		Advertisement ad1 = new Advertisement("BitCoin",200,entityName,service,advertiserAddr,advertiserPort,"UDPv4",System.currentTimeMillis()+moreTime,"UDPv4","127.0.0.1:9000");

		adMgr.flush();
		adMgr.addAdvertisement(ad1);

		// Load the marketplace with advertisements
		for(Advertisement myAd : adMgr.getSingleInstance())
		{
			couchDBsocket.postRestInterface(Server.marketplaceRESTAPI, myAd);
		}
	}

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
			loop++;
			System.out.println("Loop count "+loop);
		}
	}

}
