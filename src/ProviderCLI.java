import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;


public class ProviderCLI  extends Thread {

	static Server server = new Server ("transport.properties");
	//static Server server = new Server ();
	CouchDBOperations couchDBsocket = CouchDBOperations.getInstance();
	AdvertisementManager adMgr = AdvertisementManager.getInstance();

	public void showRendevousMenu(Scanner sc)
	{
		int port = -1;
		boolean err = false;
		System.out.println("Enter Rendezvous Target");
		String target = sc.nextLine();
		System.out.println("IP Address to send the packet");
		String ipAddr = sc.nextLine();
		if(ipAddr.isEmpty())
		{
			System.err.println("No IP Address supplied");
			err = true;
		}
		System.out.println("Port address to send the packet");
		try {
			port = Integer.parseInt(sc.nextLine());
		} catch (IllegalArgumentException e) {
			System.out.println("Port address entered is not an integer!");
		}
		if(port == -1 || err == true)
		{
			System.err.println("No Rendevous message was sent due to reported error messages.");
		}
		else
		{
			server.sendRendevouzMessage(target, ipAddr, port);
		}
	}

	public void showTransferConsiderationMenu(Scanner sc)
	{
		int port = -1;
		boolean err = false;
		System.out.println("Enter Consideration Target");
		String target = sc.nextLine();
		System.out.println("Enter Location to Service Advertisement XML for the service name: ");
		String fileName = sc.nextLine();
		System.out.println("Enter Consideration Exchange Method");
		String method = sc.nextLine();
		System.out.println("Enter Consideration Exchange Value");
		String value = sc.nextLine();
		System.out.println("IP Address to send the packet");
		String ipAddr = sc.nextLine();
		if(ipAddr.isEmpty())
		{
			System.err.println("No IP Address supplied");
			err = true;
		}
		System.out.println("Port address to send the packet");
		try {
			port = Integer.parseInt(sc.nextLine());
		} catch (IllegalArgumentException e) {
			System.out.println("Port address entered is not an integer!");
		}
		if(port == -1 || err == true)
		{
			System.err.println("No Rendevous message was sent due to reported error messages.");
		}
		else
		{
			server.transferConsiderationMessage(fileName, target, method, value, ipAddr, port);
		}
	}

	public void showTransferListingMenu(Scanner sc)
	{
		
		int port = -1;
		boolean err = false;
		System.out.println("Enter Location to Service Advertisement XML");
		String fileName = sc.nextLine();
		System.out.println("Enter Entity Name providing the listing service");
		String listingServer = sc.nextLine();
		System.out.println("IP Address to send the packet");
		String ipAddr = sc.nextLine();
		if(ipAddr.isEmpty())
		{
			System.err.println("No IP Address supplied");
			err = true;
		}
		System.out.println("Port address to send the packet");
		try {
			port = Integer.parseInt(sc.nextLine());
		} catch (IllegalArgumentException e) {
			System.out.println("Port address entered is not an integer!");
		}
		if(port == -1 || err == true)
		{
			System.err.println("No Rendevous message was sent due to reported error messages.");
		}
		else
		{
			server.transferListingMessage(fileName, Server.myName, listingServer, ipAddr, port);
		}
	}

	public void createDatabase()
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
		// flows created for Advertisement purpose must have negative integer for demandID
		// to prevent inadvervant flow installation
		String entityName = Server.myName;
		ProvisioningProperty sProp = new ProvisioningProperty("Bandwidth", "500 Mbps");
		ProvisioningProperty serviceProps[] = {sProp};
		Service service  = new Service("Service Name", "Transit", "UDPv4", "10.1.0.1", "UDPv4", "10.2.0.1", serviceProps, "From RENCI to NCSU");
		String advertiserAddr = server.getLocalIpAddress("IP");
		int advertiserPort = Integer.parseInt(server.getLocalIpAddress("Port"));
		Advertisement ad1 = new Advertisement("Dollars",200,entityName,service,advertiserAddr,advertiserPort,"UDPv4",System.currentTimeMillis()+600000, null, null);

		adMgr.flush();
		adMgr.addAdvertisement(ad1);

		// Load the marketplace with advertisements
		for(Advertisement myAd : adMgr.getSingleInstance())
		{
			couchDBsocket.postRestInterface(Server.marketplaceRESTAPI, myAd);
		}
	}

	class ExtraServer extends Thread {
		@Override
		public void run() {
			while(true)
			{
			try {
				server.startServer();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ProviderCLI cli = new ProviderCLI();
		cli.createDatabase();
		System.out.println(Server.CONFIG_FILE);
		server.printServerProperties();
	
		//cli.new ExtraServer().run(); // WHOA
		
		Scanner sc = new Scanner(System.in);
		int option = -1;
		String menu = 
				"1: Send Rendezvous \n" +
						"2: Transfer Contribution \n" +
						"3: Transfer Listing \n" +
						"";
		System.out.println(menu);
		while (sc.hasNextLine()) 
		{

			try {
				option = Integer.parseInt(sc.nextLine());
				switch (option) {
				case 1: 
					cli.showRendevousMenu(sc);
					break;
				case 2: 
					cli.showTransferConsiderationMenu(sc);
					break;  
				case 3: 
					cli.showTransferListingMenu(sc);
					break;            	
				default: 
					System.out.println("default case reached!");
					break;
				}
				
			} catch (IllegalArgumentException e) {
				System.out.println("Option entered is not an integer!");
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println(menu);
		}
	}

}
