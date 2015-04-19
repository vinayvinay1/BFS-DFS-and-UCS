import java.io.*;
import java.util.*;



public class Agent {
	
	
	//Create a linked list that dynamically stores updated cost_from_source information and parent node information
	static class Link{
		String nodename;
		String parentnodename;
		int pathcost;
		Link(String n,String p, int c){
			nodename=n;
			parentnodename=p;
			pathcost=c;
		}
	}
		
	
	public static void main(String []args) throws IOException{
	
		int searchtask;
		int totalnodes;
		String sourcenode = null;
		String destnode = null;	
		Scanner s = null;	
		s = new Scanner (new BufferedReader(new FileReader("input.txt")));
		
		// Extract data from input.txt
		searchtask = s.nextInt();	
		sourcenode = s.next();
		destnode = s.next();
		totalnodes = s.nextInt();
		
		// Copy all the nodes into a String array.
		String[] allnodes = new String[totalnodes+1];
		for(int i=1;i<=totalnodes;i++){
			allnodes[i]=s.next();
		}
		
		
		//Build the path cost table
		int[][] pathtable = new int[totalnodes+1][totalnodes+1];
		for(int i=1;i<=totalnodes;i++){
			for(int j=1;j<=totalnodes;j++){
				pathtable[i][j] = s.nextInt();
			}
		} 
		
		
		//Rearrange columns of path cost table alphabetically to make sure popping is done alphabetically
		String[] sortedallnodes = new String[totalnodes+1];
		sortedallnodes=allnodes.clone();
		sortedallnodes[0] = "";
		Arrays.sort(sortedallnodes,String.CASE_INSENSITIVE_ORDER);
		sortedallnodes[0] = null;
		int[][] sortedpathtable = new int[totalnodes+1][totalnodes+1];
		for(int j=1;j<=totalnodes;j++){
			int g = Arrays.asList(allnodes).indexOf(sortedallnodes[j]);
			for(int i=1;i<=totalnodes;i++){
				sortedpathtable[i][j] = pathtable[i][g];
			}
			
		}
		
		
		if(searchtask==1)
			BFS(sourcenode,destnode,totalnodes,allnodes,sortedallnodes,sortedpathtable);
		else if(searchtask==2)
			DFS(sourcenode,destnode,totalnodes,allnodes,sortedallnodes,sortedpathtable);
		else if(searchtask==3)
			UCS(sourcenode,destnode,totalnodes,allnodes,sortedallnodes,sortedpathtable);
		s.close();
	}
	
	// Implement BFS method
	public static void BFS(String source,String dest, int noofnodes,String[] nodelist,String[] sortednodelist, int[][] pathcost) throws IOException{
		String sourcenode=source;
		final String destnode=dest;
		final int totalnodes=noofnodes;
		final String[] allnodes=nodelist;
		final String[] sortedallnodes=sortednodelist;
		int[][]pathtable=pathcost;
		ArrayList<String> opennodes=new ArrayList<String>();
		ArrayList<String> visitednodes=new ArrayList<String>();
		LinkedList<String> BFSQueue = new LinkedList<String>();
		String log="";
		final PrintWriter out = new PrintWriter(new FileWriter("output.txt"));
		
		
		//Create a class that stores and manupulates array of Link objects
		class LinkArray{
			Link[] linkArray= new Link[totalnodes];
			int flag=0;
			LinkArray(){
				for(int i=0;i<totalnodes;i++){
					linkArray[i]=new Link(allnodes[i+1],null,1000000);  //Assigning very large value to initial pathcost to treat it as infinite
				}
			}
			
			//method updates parent of n to p
			void updateParent(String n,String p){
				for(Link l:linkArray){
					if(l.nodename.equals(n))
						l.parentnodename=p;
				}
			}
			
			//method updates the cost from source of the node n
			void updatePathcost(String n,int c){
				for(Link l:linkArray){
					if(l.nodename.equals(n))
						l.pathcost=c;
				}
			}
			
			
			//method returns the current parent of node n
			String getParent(String n){
				for(Link l:linkArray){
					if(l.nodename.equals(n))
						return l.parentnodename;
				}
				return null;
			}
			
			
			//method returns lowest path cost of the node n recorded so far
			int getPathcost(String n){
				int c=1000000;
				outerloop:
				for(Link l:linkArray){
					if(l.nodename.equals(n)){
						c=l.pathcost;
						break outerloop;
					}		
				}
			    return c;
			}
			
			
			//method prints the shortest path to the goal node from source node
			void printShortestpath(String d){
				String currentnode = d;
				String currentparent= getParent(d);
				for(Link l:linkArray){
					if(!currentparent.equals("nothing") && l.nodename.equals(currentparent)){
						printShortestpath(l.nodename);
					}
				}
				if(flag==0){
					out.print(currentnode);
					flag=1;
				}
				else out.print("-"+currentnode);
				
			}
		}
		LinkArray l=new LinkArray();
		
		
		//Start BFS search
		
		//Update source to BFS queue
		l.updatePathcost(sourcenode,0);
		l.updateParent(sourcenode, "nothing");
		BFSQueue.add(sourcenode);
		opennodes.add(sourcenode); //add source into list of opened nodes
		
		
		int flag=0;
		String goaltest="";
		goalreached:
		while(!BFSQueue.isEmpty()){
			//Expand the first item in Queue and update the pathcosts of their children
			for(int i=1;i<=totalnodes;i++){
				String currentnode= BFSQueue.element();    //store the current node in queue which is being expanded
				int currentnodecost= l.getPathcost(currentnode);
				int g = pathtable[Arrays.asList(allnodes).indexOf(currentnode)][i];
				if(g !=0 && !l.getParent(BFSQueue.element()).equals(sortedallnodes[i]) && !opennodes.contains(sortedallnodes[i])){
					if(currentnodecost+g < l.getPathcost(sortedallnodes[i])){
						l.updateParent(sortedallnodes[i],currentnode);
						l.updatePathcost(sortedallnodes[i], g+l.getPathcost(currentnode));	
						BFSQueue.add(sortedallnodes[i]);
						opennodes.add(sortedallnodes[i]);
					}
					
				}
			}
			
			visitednodes.add(BFSQueue.element());
			goaltest=BFSQueue.element(); //Add each popped node to visitednodes
			
			
			//Added popped node to log element
			if(flag==0){
				log=log+BFSQueue.remove();
				flag=1;
			}
			else if(flag==1){
				log=log+"-"+BFSQueue.remove();
			}
			
			if(BFSQueue.contains(destnode)){
				log=log+"-"+destnode;
				BFSQueue.clear();
				break goalreached;
			}	
		}
		
		
		
		if(l.getPathcost(destnode)!=0 && l.getPathcost(destnode)!=1000000){   //check if path to goal exists
			out.println(log);
			l.printShortestpath(destnode);
			out.println();
			out.println(l.getPathcost(destnode));
		}
		else{
			out.println(log);
			out.println("NoPathAvailable");
		}
		out.close();
	}
	
	
	// Implement DFS method
		public static void DFS(String source,String dest, int noofnodes,String[] nodelist,String[] sortednodelist, int[][] pathcost) throws IOException{
			String sourcenode=source;
			String destnode=dest;
			final int totalnodes=noofnodes;
			final String[] allnodes=nodelist;
			final String[] sortedallnodes=sortednodelist;
			int[][]pathtable=pathcost;
			ArrayList<String> opennodes= new ArrayList<String>();
			ArrayList<String> visitednodes=new ArrayList<String>();
			LinkedList<String> DFSStack = new LinkedList<String>();
			String log="";
			final PrintWriter out = new PrintWriter(new FileWriter("output.txt"));
			
			
			//Create a class that stores and manupulates array of Link objects
			class LinkArray{
				Link[] linkArray= new Link[totalnodes];
				int flag=0;
				LinkArray(){
					for(int i=0;i<totalnodes;i++){
						linkArray[i]=new Link(allnodes[i+1],null,1000000);  //Assigning very large value to initial pathcost to treat it as infinite
					}
				}
				
				//method updates parent of n to p
				void updateParent(String n,String p){
					for(Link l:linkArray){
						if(l.nodename.equals(n))
							l.parentnodename=p;
					}
				}
				
				//method updates the cost from source of the node n
				void updatePathcost(String n,int c){
					for(Link l:linkArray){
						if(l.nodename.equals(n))
							l.pathcost=c;
					}
				}
				
				
				//method returns the current parent of node n
				String getParent(String n){
					for(Link l:linkArray){
						if(l.nodename.equals(n))
							return l.parentnodename;
					}
					return null;
				}
				
				
				//method returns lowest path cost of the node n recorded so far
				int getPathcost(String n){
					int c=1000000;
					outerloop:
					for(Link l:linkArray){
						if(l.nodename.equals(n)){
							c=l.pathcost;
							break outerloop;
						}		
					}
				    return c;
				}
				
				
				//method prints the shortest path to the goal node from source node
				void printShortestpath(String d){
					String currentnode = d;
					String currentparent= getParent(d);
					for(Link l:linkArray){
						if(!currentparent.equals("nothing") && l.nodename.equals(currentparent)){
							printShortestpath(l.nodename);
						}
					}
					if(flag==0){
						out.print(currentnode);
						flag=1;
					}
					else out.print("-"+currentnode);
					
				}
			}
			LinkArray l=new LinkArray();
			
			
			//Start DFS search
			
			//Update source to DFS queue
			l.updatePathcost(sourcenode,0);
			l.updateParent(sourcenode, "nothing");
			DFSStack.push(sourcenode);
			opennodes.add(sourcenode); //add source into list of opened nodes
			
			
			int flag=0;
			String goaltest="";
			goalreached:
			while(!DFSStack.isEmpty()){
				//Expand the first item in Stack and update the pathcosts of their children
				String currentnode= DFSStack.element();    //store the current node in Stack which is being expanded
				int currentnodecost= l.getPathcost(currentnode);
				visitednodes.add(DFSStack.element());  //Add each pushed node to visitednodes
				//Added popped node to log element
				goaltest=DFSStack.peekFirst();
				if(!Arrays.asList(log).contains(destnode)){  //This removes futile node searches after finding goal node for first time
					if(flag==0){
						log=log+DFSStack.pop();
						flag=1;
					}
					else if(flag==1){
					log=log+"-"+DFSStack.pop();
					}
				}
				
				if(goaltest.equals(destnode)){
					DFSStack.clear();
					break goalreached;
				}
				
				for(int i=totalnodes;i>=1;i--){
					int g = pathtable[Arrays.asList(allnodes).indexOf(currentnode)][i];
					if(g !=0 && !opennodes.contains(sortedallnodes[i])){
                    	if(currentnodecost+g < l.getPathcost(sortedallnodes[i])){
                    		l.updateParent(sortedallnodes[i],currentnode);
							l.updatePathcost(sortedallnodes[i], g+l.getPathcost(currentnode));	
							DFSStack.push(sortedallnodes[i]);
							opennodes.add(sortedallnodes[i]);
						}
					}
				}
			}
			
			
			
			if(l.getPathcost(destnode)!=0 && l.getPathcost(destnode)!=1000000){   //check if path to goal exists
				out.println(log);
				l.printShortestpath(destnode);
				out.println();
				out.println(l.getPathcost(destnode));
			}
			else{
				out.println(log);
				out.println("NoPathAvailable");
			}
			out.close();
		}
		
		
		
		// Implement UCS method
		public static void UCS(String source,String dest, int noofnodes,String[] nodelist,String[] sortednodelist, int[][] pathcost) throws IOException{
			String sourcenode=source;
			final String destnode=dest;
			final int totalnodes=noofnodes;
			final String[] allnodes=nodelist;
			final String[] sortedallnodes=sortednodelist;
			int[][]pathtable=pathcost;
			ArrayList<String> opennodes=new ArrayList<String>();
			ArrayList<String> visitednodes=new ArrayList<String>();
			LinkedList<String> UCSQueue = new LinkedList<String>();
			String log="";
			final PrintWriter out = new PrintWriter(new FileWriter("output.txt"));
			
			
			//Create a class that stores and manupulates array of Link objects
			class LinkArray{
				Link[] linkArray= new Link[totalnodes];
				int flag=0;
				LinkArray(){
					for(int i=0;i<totalnodes;i++){
						linkArray[i]=new Link(allnodes[i+1],null,1000000);  //Assigning very large value to initial pathcost to treat it as infinite
					}
				}
				
				//method updates parent of n to p
				void updateParent(String n,String p){
					for(Link l:linkArray){
						if(l.nodename.equals(n))
							l.parentnodename=p;
					}
				}
				
				//method updates the cost from source of the node n
				void updatePathcost(String n,int c){
					for(Link l:linkArray){
						if(l.nodename.equals(n))
							l.pathcost=c;
					}
				}
				
				
				//method returns the current parent of node n
				String getParent(String n){
					for(Link l:linkArray){
						if(l.nodename.equals(n))
							return l.parentnodename;
					}
					return null;
				}
				
				
				//method returns lowest path cost of the node n recorded so far
				int getPathcost(String n){
					int c=1000000;
					outerloop:
					for(Link l:linkArray){
						if(l.nodename.equals(n)){
							c=l.pathcost;
							break outerloop;
						}		
					}
				    return c;
				}
				
				
				//method prints the shortest path to the goal node from source node
				void printShortestpath(String d){
					String currentnode = d;
					String currentparent= getParent(d);
					for(Link l:linkArray){
						if(!currentparent.equals("nothing") && l.nodename.equals(currentparent)){
							printShortestpath(l.nodename);
						}
					}
					if(flag==0){
						out.print(currentnode);
						flag=1;
					}
					else out.print("-"+currentnode);
					
				}
			}
			LinkArray l=new LinkArray();
			
			
			//Start UCS search
			
			//Update source to UCS queue
			l.updatePathcost(sourcenode,0);
			l.updateParent(sourcenode, "nothing");
			UCSQueue.add(sourcenode);
			opennodes.add(sourcenode); //mark source as visited
			
			
			int flag=0;
			String goaltest="";
			String currentnode=";";
			int currentnodecost=0;
			goalreached:
			while(!UCSQueue.isEmpty()){
				//Expand the first item in Queue and update the pathcosts of their children
				currentnode= UCSQueue.element();    //store the current node in queue which is being expanded
				
				// Unlike BFS, ensure that the node in Queue with least cost be expanded first
				for(int m=0;m<UCSQueue.size();m++){
					if(l.getPathcost(UCSQueue.get(m))<l.getPathcost(currentnode)){
						currentnode=UCSQueue.get(m);
					}
				}
				
				for(int i=1;i<=totalnodes;i++){
					currentnodecost= l.getPathcost(currentnode);
					int g = pathtable[Arrays.asList(allnodes).indexOf(currentnode)][i];
					if(g !=0 && !l.getParent(currentnode).equals(sortedallnodes[i]) && !opennodes.contains(sortedallnodes[i])){
						if(currentnodecost+g < l.getPathcost(sortedallnodes[i])){
							l.updateParent(sortedallnodes[i],currentnode);
							l.updatePathcost(sortedallnodes[i], g+l.getPathcost(currentnode));	
							UCSQueue.add(sortedallnodes[i]);
							opennodes.add(sortedallnodes[i]);
						}
						
					}
				}
				
				visitednodes.add(currentnode);
				goaltest=currentnode; //Add each popped node to visitednodes
				
				//Added popped node to log element
				if(flag==0){
					log=log+currentnode;
					UCSQueue.remove(currentnode);
					flag=1;
				}
				else if(flag==1){
					log=log+"-"+currentnode;
					UCSQueue.remove(currentnode);
				}
				
				if(goaltest.equals(destnode)){
					UCSQueue.clear();
					break goalreached;
				}	
			}
			
			if(l.getPathcost(destnode)!=0 && l.getPathcost(destnode)!=1000000){   //check if path to goal exists
				out.println(log);
				l.printShortestpath(destnode);
				out.println();
				out.println(l.getPathcost(destnode));
			}
			else{
				out.println(log);
				out.println("NoPathAvailable");
			}
			out.close();
		}
}




