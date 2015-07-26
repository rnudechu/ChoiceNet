import java.util.ArrayList;
import java.util.Stack;

// Source: http://www.geeksforgeeks.org/find-paths-given-source-destination/
// Source: https://github.com/pirated/algorithms/tree/graph.allpath.problem

public class PlannerGraphMatrix {

	ArrayList<PlannerNode> nodeGraph = new ArrayList<PlannerNode>(); 


	boolean[] visited;
	Stack<PlannerNode> nodeStack = new Stack<PlannerNode>();
	ArrayList<PlannerServiceRecipe> recipes = new ArrayList<PlannerServiceRecipe>();

	public ArrayList<PlannerNode> getNodeGraph() {
		return nodeGraph;
	}

	public void setNodeGraph(ArrayList<PlannerNode> nodeGraph) {
		this.nodeGraph = nodeGraph;
	}
	public void addEdge(String src, int srcCost, String dst, int dstCost)
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

	public int getNodeIndex(String nodeName)
	{
		int index = 0;
		for(PlannerNode node: nodeGraph)
		{
			if(node.getNodeName().equals(nodeName))
			{
				return index;
			}
			index++;
		}
		return -1;
	}

	public void findAllPath(String src,String dst,boolean[] visited){
		PlannerNode srcNode, dstNode = null;
		int srcIndex = getNodeIndex(src);
		int currIndex = -1;
		srcNode = getNode(src);
		dstNode = getNode(dst);
		nodeStack.add(srcNode);

		if(srcNode.equals(dstNode)){
			printNodeStack(nodeStack);
		}

		if(visited[srcIndex] != true)
			visited[srcIndex] = true;

		ArrayList<PlannerNode> adjNodes = srcNode.getAdjancies();
		if(adjNodes.size()>0){	
			for (PlannerNode node: adjNodes) {
				currIndex = getNodeIndex(node.getNodeName());
				if(visited[currIndex]!=true){
					findAllPath(node.getNodeName(), dst,visited);
				}
			}
		}

		visited[srcIndex] = false;
		nodeStack.remove(nodeStack.size()-1);
	}




	private void printNodeStack(Stack<PlannerNode> stack) {
		int total = 0;
		String advertisementList = "";
		for (PlannerNode node: stack) {
			advertisementList += (node.getNodeName())+",";
			total += node.getResourceCost();
		}
		advertisementList = advertisementList.substring(0, advertisementList.length()-1);
		PlannerServiceRecipe myRecipe = new PlannerServiceRecipe(advertisementList, total);
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
					for(String x:dstLocX)
					{
						for(String y:srcLocY)
						{
							if(x.equals(y))
							{
								addEdge(nodeX.getNodeName(),nodeX.getResourceCost(),nodeY.getNodeName(),nodeY.getResourceCost());
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
	
	public void run()
	{
		int size = nodeGraph.size();
		createAdjancies();
		for(PlannerNode nodeX: nodeGraph)
		{
			for(PlannerNode nodeY: nodeGraph)
			{
				if(nodeX.getStatus().equals(PlannerNode.NodeType.SOURCE) && 
						nodeY.getStatus().equals(PlannerNode.NodeType.DESTINATION))
				{
					findAllPath(nodeX.getNodeName(), nodeY.getNodeName(),new boolean[size]);
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