/*********************************
 * H.R. Oosterhuis - 10196129
 *********************************/
package RushHourSolver ;
/* represents a chain of moves
 * class is used for a node to store its path
 * an element of moveChain can only have a single parent
 * but can have multiple children
 * this is because of the branching nature of nodes in A*
 */
public class MoveChain {
	public Move move ;
	public MoveChain previous ;
	
	// constructor for a root node
	public MoveChain(Move move){
		this.move = move;
		this.previous = null;
	}
	
	//default constructor
	public MoveChain(Move move, MoveChain previous){
		this.move = move;
		this.previous = previous;
	}
	
	// returns a stack with all the moves that
	// lead the current node to the root
	// the top move on the stack is the root node
	public MoveStack movePath(){
		MoveStack stack = new MoveStack();
		stack.push(move);
		MoveChain prev = previous;
		while(prev != null){
			stack.push(prev.move);
			prev = prev.previous;
		}
		return stack;
	}
	
	public MoveChain copy(){
		return new MoveChain(move.copy(), previous);
	}
}
