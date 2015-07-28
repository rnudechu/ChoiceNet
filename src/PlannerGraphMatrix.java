import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Stack;

// Source: http://www.geeksforgeeks.org/find-paths-given-source-destination/
// Source: https://github.com/pirated/algorithms/tree/graph.allpath.problem

public class PlannerGraphMatrix {

	ArrayList<PlannerNode> nodeGraph = new ArrayList<PlannerNode>(); 


	boolean[] visited;
	Stack<PlannerNode> nodeStack = new Stack<PlannerNode>();
	Stack<String> provisionParameterStack = new Stack<String>();
	ArrayList<PlannerServiceRecipe> recipes = new ArrayList<PlannerServiceRecipe>();

	public ArrayList<PlannerNode> getNodeGraph() {
		return nodeGraph;
	}

	public void setNodeGraph(ArrayList<PlannerNode> nodeGraph) {
		this.nodeGraph = nodeGraph;
	}
	// sharedValue = provisionParameter
	public void addEdge(String src, int srcCost, String dst, int dstCost, String sharedValue)
	{
		PlannerNode srcNode, dstNode = null;
		if(!doesNodeExist(src))
		{
			addNode(src, srcCost);
		}
		srcNode = getNode(src);
		if(!doesNodeExist(dst))
		{
			addNode(dst, dstCost);
		}
		dstNode = getNode(dst);
		if(srcNode!=null && dstNode!=null)
		{
			srcNode.getAdjancies().add(dstNode);
			srcNode.getProvisionParameter().add(sharedValue);
		}
	}

	public boolean doesNodeExist(String nodeName)
	{
		for(PlannerNode node: nodeGraph)
		{
			if(node.getNodeName().equals(nodeName))
			{
				return true;
			}
		}
		return false;
	}

	public void addNode(String nodeName, int cost)
	{
		PlannerNode node = new PlannerNode(nodeName, cost);
		nodeGraph.add(node);
	}

	public PlannerNode getNode(String nodeName)
	{
		for(PlannerNode node: nodeGraph)
		{
			if(node.getNodeName().equals(nodeName))
			{
				return node;
			}
		}
		return null;
	}

	public int getNodeIndex(String nodeName, ArrayList<PlannerNode> graph)
	{
		int index = 0;
		for(PlannerNode node: graph)
		{
			if(node.getNodeName().equals(nodeName))
			{
				return index;
			}
			index++;
		}
		return -1;
	}

	public void findAllPath(String src,String dst,boolean[] visited, String provisionParameter){
		PlannerNode srcNode, dstNode = null;
		int srcIndex = getNodeIndex(src, nodeGraph);
		int currIndex, parameterIndex = -1;
		//int currIndex = -1;
		srcNode = getNode(src);
		dstNode = getNode(dst);
		nodeStack.add(srcNode);
		if(provisionParameter == null)
		{
			provisionParameter = srcNode.getSearchedParameter();
		}
		provisionParameterStack.add(provisionParameter);
		System.out.println("Source Node: "+srcNode.getNodeName());
		System.out.println("Source Parameters: "+srcNode.getProvisionParameter());
		for(PlannerNode node:srcNode.getAdjancies())
		{
			System.out.println("\tSource Adjancies: "+node.getNodeName());
		}
		System.out.println("Provision Paramer: "+provisionParameter);
		System.out.println("Destination Node: "+dstNode.getNodeName());


		if(srcNode.equals(dstNode)){
			printNodeStack(nodeStack, provisionParameterStack);
		}

		if(visited[srcIndex] != true)
			visited[srcIndex] = true;

		ArrayList<PlannerNode> adjNodes = srcNode.getAdjancies();
		if(adjNodes.size()>0){	
			for (PlannerNode node: adjNodes) {
				currIndex = getNodeIndex(node.getNodeName(), nodeGraph);
				parameterIndex = getNodeIndex(node.getNodeName(), adjNodes);
				provisionParameter = srcNode.getProvisionParameter().get(parameterIndex);
				if(visited[currIndex]!=true){
					findAllPath(node.getNodeName(), dst,visited, provisionParameter);
				}
			}
		}

		visited[srcIndex] = false;
		nodeStack.remove(nodeStack.size()-1);
		if(provisionParameterStack.size()>0)
		{
			provisionParameterStack.remove(provisionParameterStack.size()-1);
		}
	}




	private void printNodeStack(Stack<PlannerNode> stack, Stack<String> parameterStack) {
		int total = 0;
		ArrayList<String> advertisementList = new ArrayList<String>();
		ArrayList<String> provisioningParameter = new ArrayList<String>();
		System.out.println("Parameter Stack: "+parameterStack);
		for (PlannerNode node: stack) {
			advertisementList.add(node.getNodeName());
			total += node.getResourceCost();
			//provisionParameter = parameterStack.get(i);
			//provisioningParameter.add(provisionParameter);
		}

		for (String provisionParameter: parameterStack) {
			provisioningParameter.add(provisionParameter);
		}
		PlannerServiceRecipe myRecipe = new PlannerServiceRecipe(advertisementList, total, provisioningParameter);
		recipes.add(myRecipe);
	}

	public void printRecipe()
	{
		for(PlannerServiceRecipe myRecipe: recipes)
		{
			System.out.println("Advertisement List: "+myRecipe.getAdvertisementList());
			System.out.println("Cost: "+myRecipe.getTotalCost());
		}
	}

	public ArrayList<PlannerServiceRecipe> getRecipes() {
		return recipes;
	}

	public void setRecipes(ArrayList<PlannerServiceRecipe> recipes) {
		this.recipes = recipes;
	}

	public void createAdjancies()
	{
		String[] dstLocX;
		String[] srcLocY;
		String[] dstFormatX;
		String[] srcFormatY;
		boolean alreadyCreatedEdge = false;
		for(PlannerNode nodeX: nodeGraph)
		{
			for(PlannerNode nodeY: nodeGraph)
			{
				alreadyCreatedEdge = false;
				if(nodeX != nodeY)
				{
					dstLocX = nodeX.getAdvertisement().getDstLocationAddrValue(); 
					srcLocY = nodeY.getAdvertisement().getSrcLocationAddrValue();
					for(String subnet:dstLocX)
					{
						for(String ipAddr:srcLocY)
						{
							if(subnet.equals(ipAddr))
							//System.out.println(ipAddr+" is within "+subnet+"?");
//							if(netMatch(subnet, ipAddr))
							{
								System.out.println(ipAddr+" is within "+subnet+" = TRUE");
								addEdge(nodeX.getNodeName(),nodeX.getResourceCost(),nodeY.getNodeName(),nodeY.getResourceCost(), ipAddr);
								alreadyCreatedEdge = true;
							}
						}
					}
					//					if(!alreadyCreatedEdge)
					//					{
					//						dstFormatX = nodeX.getAdvertisement().getDstFormatValue(); 
					//						srcFormatY = nodeY.getAdvertisement().getSrcFormatValue();
					//						for(String x:dstFormatX)
					//						{
					//							for(String y:srcFormatY)
					//							{
					//								if(x.equals(y))
					//								{
					//									addEdge(nodeX.getNodeName(),nodeX.getResourceCost(),nodeY.getNodeName(),nodeY.getResourceCost());
					//								}
					//							}
					//						}
					//					}
				}
			}
		}
	}
	
	// http://stackoverflow.com/a/10484311
		public boolean netMatch(String subnet, String addr){ 

	        String[] parts = subnet.split("/");
	        String ip = parts[0];
	        int prefix;

	        if (parts.length < 2) {
	            prefix = 0;
	        } else {
	            prefix = Integer.parseInt(parts[1]);
	        }

	        Inet4Address a =null;
	        Inet4Address a1 =null;
	        try {
	            a = (Inet4Address) InetAddress.getByName(ip);
	            a1 = (Inet4Address) InetAddress.getByName(addr);
	        } catch (UnknownHostException e){}

	        byte[] b = a.getAddress();
	        int subnetBytes = ((b[0] & 0xFF) << 24) |
	                         ((b[1] & 0xFF) << 16) |
	                         ((b[2] & 0xFF) << 8)  |
	                         ((b[3] & 0xFF) << 0);

	        byte[] b1 = a1.getAddress();
	        int ipBytes = ((b1[0] & 0xFF) << 24) |
	                         ((b1[1] & 0xFF) << 16) |
	                         ((b1[2] & 0xFF) << 8)  |
	                         ((b1[3] & 0xFF) << 0);

	        int mask = ~((1 << (32 - prefix)) - 1);

	        if ((subnetBytes & mask) == (ipBytes & mask)) {
	            return true;
	        }
	        else {
	            return false;
	        }
	}

	public void run()
	{
		int size = nodeGraph.size();
		createAdjancies();
		for(PlannerNode nodeX: nodeGraph)
		{
			if(nodeX.getStatus().equals(PlannerNode.NodeType.SOLUTION))
			{
				ArrayList<String> advertisementList = new ArrayList<String>();
				ArrayList<String> provisioningParameter = new ArrayList<String>();
				advertisementList.add(nodeX.getNodeName());
				provisioningParameter.add(nodeX.getSearchedParameter());
				PlannerServiceRecipe myRecipe = new PlannerServiceRecipe(advertisementList, nodeX.getResourceCost(), provisioningParameter);
				recipes.add(myRecipe);
			}
			else
			{
				for(PlannerNode nodeY: nodeGraph)
				{
					if(nodeX.getStatus().equals(PlannerNode.NodeType.SOURCE) && 
							nodeY.getStatus().equals(PlannerNode.NodeType.DESTINATION))
					{
						//					System.out.println(">>>"+nodeX.getNodeName()+" to "+nodeY.getNodeName());
						findAllPath(nodeX.getNodeName(), nodeY.getNodeName(),new boolean[size],null);
					}
				}
			}
		}
	}

	

	//	public static void main(String[] args) {
	//
	//		PlannerGraphMatrix gm = new PlannerGraphMatrix();
	//		gm.addEdge("0",2,"1",1);
	//		gm.addEdge("0",2,"2",1);
	//		gm.addEdge("0",2,"3",1);
	//		gm.addEdge("1",1,"3",1);
	//		gm.addEdge("2",1,"0",2);
	//		gm.addEdge("2",1,"1",1);
	//		gm.findAllPath("2", "3",new boolean[gm.nodeGraph.size()]);
	//		gm.printRecipe();
	//	}
}