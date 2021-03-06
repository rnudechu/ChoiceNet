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

	public void findAllPath(String src,String dst,boolean[] visited, String provisionParameter, PlannerSearchParameter parameters){
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
			// check to see if all the parameters from the orignal query is satisfied
			if(checkStack(parameters, nodeStack))
			{
				printNodeStack(nodeStack, provisionParameterStack);
			}
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
					findAllPath(node.getNodeName(), dst,visited, provisionParameter, parameters);
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


	private boolean checkStack(PlannerSearchParameter parameters, Stack<PlannerNode> myStack)
	{
		boolean result = false;
		boolean[] itemizedResult = new boolean[4];
		int index = 0;
		ArrayList<String> srcLocationType = parameters.getSrcTypeLocation();
		ArrayList<String> srcLocation = parameters.getSrcLocation();
		ArrayList<String> dstLocationType = parameters.getDstTypeLocation();
		ArrayList<String> dstLocation = parameters.getDstLocation();
		ArrayList<String> srcFormatType = parameters.getSrcTypeFormat();
		ArrayList<String> srcFormat = parameters.getSrcFormat();
		ArrayList<String> dstFormatType = parameters.getDstTypeFormat();
		ArrayList<String> dstFormat = parameters.getDstFormat();
		boolean resultVal = false;
		for(PlannerNode myNode: myStack)
		{
			int size = srcLocation.size();
			index = 0;
			if(size>0)
			{
				for(int i = 0; i<size ; i++)
				{
					resultVal = myNode.anyMatchingSearchCriteria("SrcLocation",  srcLocation.get(i), srcLocationType.get(i));
					if(resultVal)
					{
						itemizedResult[index] = true;
					}
				}
			}
			else
			{
				itemizedResult[index] = true;
			}
			size = dstLocation.size();
			index = 1;
			if(size>0)
			{
				
				for(int i = 0; i<size ; i++)
				{
					resultVal = myNode.anyMatchingSearchCriteria("DstLocation",  dstLocation.get(i), dstLocationType.get(i));
					if(resultVal)
					{
						itemizedResult[index] = true;
					}
				}
			}
			else
			{
				itemizedResult[index] = true;
			}
			size = srcFormat.size();
			index = 2;
			if(size>0)
			{
				
				for(int i = 0; i<size ; i++)
				{
					resultVal = myNode.anyMatchingSearchCriteria("SrcFormat",  srcFormat.get(i), srcFormatType.get(i));
					if(resultVal)
					{
						itemizedResult[index] = true;
					}
				}
			}
			else
			{
				itemizedResult[index] = true;
			}
			
			size = dstFormat.size();
			index = 3;
			if(size>0)
			{
				for(int i = 0; i<size ; i++)
				{
					resultVal = myNode.anyMatchingSearchCriteria("DstFormat",  dstFormat.get(i), dstFormatType.get(i));
					if(resultVal)
					{
						itemizedResult[index] = true;
					}
				}
			}
			else
			{
				itemizedResult[index] = true;
			}
		}
		int count = 0;
		for(boolean q: itemizedResult)
		{
			if(q)
			{
				count++;
			}
		}
		if(count == itemizedResult.length)
		{
			result = true;
		}
		System.out.println(count+" "+result);
		return result;
	}


	private void printNodeStack(Stack<PlannerNode> stack, Stack<String> parameterStack) {
		int total = 0;
		ArrayList<String> advertisementList = new ArrayList<String>();
		ArrayList<String> provisioningParameter = new ArrayList<String>();
		System.out.println("Parameter Stack: "+parameterStack);
		for (PlannerNode node: stack) {
			System.out.println("Help "+node.getSearchedParameter());
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

		System.out.println("NodeGraph size is "+nodeGraph.size()+"?");
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
							//if(subnet.equals(ipAddr))
							System.out.println(ipAddr+" is within "+subnet+"?");
							if(Utility.netMatch(subnet, ipAddr))
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
	


	public void run(PlannerSearchParameter parameters)
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
						findAllPath(nodeX.getNodeName(), nodeY.getNodeName(),new boolean[size],null, parameters);
					}
				}
			}
		}
	}

	
}