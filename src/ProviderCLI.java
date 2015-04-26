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
