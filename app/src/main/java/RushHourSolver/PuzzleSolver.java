/*********************************
 * H.R. Oosterhuis - 10196129
 *********************************/
package RushHourSolver ;

public abstract class PuzzleSolver {
	
	/*
	 *  if set to true, information about search tree and hash table
	 * used to solve the puzzle will be displayed
	 */
	public static boolean hashInfo = false ;
	
	/* solves the puzzle using A*
	* the algorithm is hardwired to discarded any boards
	* it has reached before with a lower cost
	* 
	* returns a stack containing the solution moves in order
	*/
	public static MoveStack solve( Board board ){
		//NodeQueue is a priority queue it orders the nodes on expected cost
		// queue is set to ascending meaning the lowest expected cost
		// is returned first
		NodeQueue newNodes, queue = new NodeQueue(500, true);
		Node node = new Node(board);
		
		BoardHashTable table = new BoardHashTable(1000);
		//nodes considered holds the total of new nodes created
		int nodesConsidered = 0;
		
		// the first node is added to the table and queue
		table.put(board, 0);
		queue.push(node);
		nodesConsidered++;
		
		while(!queue.isEmpty() && !queue.peek().board().won()){
			// since the queue is ordered on expected cost
			// the popped node has the smallest expected cost
			node = queue.pop();
			newNodes = node.expandNode();
			while(!newNodes.isEmpty()){
				nodesConsidered++;
				node = newNodes.pop();
				//the node is only added to the open nodes
				//if it is the first node containing that board
				//or it has a lower cost than the last time the board was considered
				if(table.put(node.board(), node.getCost())){
					queue.push(node);
				}
			}
            if(Thread.interrupted())
                return new MoveStack(1);
		}

		if( hashInfo ){
			 System.out.printf("Considered: %d Relevant: %d Abandoned: %d Closed: %d. ",
					 nodesConsidered,table.size(),(nodesConsidered - table.size()),queue.size());
		}
		// the moveStack will hold the solution
		// given by the node with the smallest path to the winning state
		MoveStack stack = null ;
		if(queue != null && !queue.isEmpty()){
			stack = queue.peek().getPath();
		}
		// if the puzzle is solved or unsolvable an empty
		// stack is returned
		if( stack == null ){
			stack = new MoveStack(1);
		}
		return stack ;
	}
	
	
}
