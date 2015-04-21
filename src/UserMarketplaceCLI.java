
public class UserMarketplaceCLI {
	static Server server = new Server ("clientCLI.properties");
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		int size = args.length; 
		
		if(args.length>1)
		{
			String marketplaceAddr = args[0];
			
			String sourceLoc = "";
			String destinationLoc = "";
			String cost = "";
			String sourceFormat = "";
			String destinationFormat = "";
			String sourceLocType = "";
			String destinationLocType = "";
			String sourceFormatType = "";
			String destinationFormatType = "";
			String adID = "";
			String[] parseContent;
			String argument;
			for(int i=1; i<size; i++)
			{
				parseContent = args[i].split("=");
				argument = parseContent[0]; 
				if(parseContent.length==2)
				{
					parseContent[1] = parseContent[1].replace("_", " ");
					if(argument.equals("srcLoc"))
					{
						sourceLoc = parseContent[1];
					} 
					if(argument.equals("srcLocType"))
					{
						sourceLocType = parseContent[1];
					} 
					if(argument.equals("dstLoc"))
					{
						destinationLoc = parseContent[1];
					} 
					if(argument.equals("dstLocType"))
					{
						destinationLocType = parseContent[1];
					} 
					if(argument.equals("srcFormat"))
					{
						sourceFormat = parseContent[1];
					} 
					if(argument.equals("srcFormatType"))
					{
						sourceFormatType = parseContent[1];
					} 
					if(argument.equals("dstFormat"))
					{
						destinationFormat = parseContent[1];
					} 
					if(argument.equals("dstFormatType"))
					{
						destinationFormatType = parseContent[1];
					}  
					if(argument.equals("cost"))
					{
						cost = parseContent[1];
					} 
					if(argument.equals("adID"))
					{
						adID = parseContent[1];
					}
				}
			}
			server.sendMarketplaceQuery(marketplaceAddr, sourceLoc, destinationLoc, sourceFormat, destinationFormat,  sourceLocType, destinationLocType, sourceFormatType, destinationFormatType, cost, adID);
			server.startServer();
		}
		else
		{
			String warning = "Program requires a marketplace address and atleast one key/value pair\n" +
					"Available keys={srcLoc, srcLocType, dstLoc, dstLocType, srcFormat, srcFormatType, dstFormat, dstFormatType, cost, adID}\n" +
					"Search value is separated from keys with an equal (=) sign\n" +
					"Search values containing a space should be replaced with an underscore.\n" +
					"Results will be saved in a marketplace.response file\n\n" +
					"Syntax: java -jar UserMarketplace.jar <marketplace_address> <key=value> [key=value]\n" +
					"Example: java -jar UserMarketplaceCLI.jar 127.0.0.1:4040 srcLoc=10.0.0.0\n" +
					"Example: java -jar UserMarketplaceCLI.jar 127.0.0.1:4040 srcLoc=10.0.0.0 dstLoc=20.0.0.0";
			System.out.println(warning);
		}
	}
}
