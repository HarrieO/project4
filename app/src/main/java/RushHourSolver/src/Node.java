/*********************************
 * H.R. Oosterhuis - 10196129
 * Marysia Winkels - 10163727
 * Kunstmatige Intelligentie
 * Datastructuren 2012-2013
 * Project Opdracht
 *********************************/
/*
 * Node used in A* holds a board, cost and path
 */
public class Node {
	private int cost ;
	private Board board;
	private MoveChain chain;
	
	//constructor of root node ( with cost 0 )
	public Node(Board board){
		this(0,board.copy(),null);
	}
	// default constructor
	public Node( int cost, Board board, MoveChain chain ){
		this.cost 		= cost ;
		// copy of the board is made
		// to allow no outside function to make changes
		this.board 		= board.copy() ;
		this.chain		= chain;
	}
	// returns cost expected to reach winning state
	// based on heuristic function
	public int expectedCost(){
		return cost + board.heuristic() ;
	}
	// returns amount of moves used to reached current board
	public int getCost(){
		return cost ;
	}
	// returns a NodeQueue with all the nodes reachable with one move
	// from current board
	// queue is ordered descending to allow for quicker sorting
	// when added to ascending queue in A*
	public NodeQueue expandNode(){
		NodeQueue queue = new NodeQueue(false);
		MoveStack pos = board.possibleMoves();
		Board newBoard ;
		MoveChain newChain ;
		Move newMove;
		while(!pos.isEmpty()){
			newMove = pos.pop();
			newBoard = board.copy();
			newBoard.move(newMove);
			newChain = new MoveChain(newMove,chain);
			queue.push(new Node(cost+1,newBoard,newChain));
		}
		return queue ;
	}
	// a copy of the board is returned to allow no outside access
	public Board board(){
		return board.copy();
	}
	public Node copy(){
		return new Node(cost, board.copy(), chain.copy());
	}
	// returns move taken to reach current board
	public MoveStack getPath(){
		if(chain == null)
			return null ;
		return chain.movePath();
	}
}
