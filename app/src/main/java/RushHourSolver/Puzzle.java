/*********************************
 * H.R. Oosterhuis - 10196129
 *********************************/
package RushHourSolver ;

import android.util.Log;

/*
 * puzzle contains a board and name
 * additional functions to make the game playable
 * with an io interface are also given.
 * We have abandoned the io interface and went
 * for a graphic instead,
 * helper functions for a io interface are still present.
 */
public class Puzzle {
	private Board board;
	private String name;
    private int minimum ;
	
	// keeps track of the total moves made since start
	private int moves ;
	// keeps track of the moves performed to allow undoing
	private MoveStack stack ;
	
	//default constructor
	public Puzzle( Board board, String name){
		this.board 	= board;
		this.name 	= name;
		this.moves 	= 0;
		this.stack = new MoveStack();
	}
	//constructor used by copy method
	private Puzzle( Board board, String name, int moves, MoveStack stack){
		this.board 	= board;
		this.name 	= name;
		this.moves 	= moves;
		this.stack = stack;
	}

    public Puzzle(String state){
        this.stack = new MoveStack();
        this.name = state.substring(0, state.indexOf(':'));
        state = state.substring(state.indexOf(':')+1);
        this.minimum = Integer.valueOf(state.substring(0, state.indexOf(':')));
        state = state.substring(state.indexOf(':')+1);
        this.board = new Board(minimum, state);
    }
	// returns true if the board is in winning state
	public boolean solved() {
		return board.won();
	}
    public Board getBoard() { return board;       }
    public boolean winningMovePossible(){
        return board.legalMove(board.winningMove());
    }
    public Move winningMove(){
        return board.winningMove();
    }
	
	// print function for io interface
	public void print(){
		System.out.println(name + " - moves performed: " + moves );
		board.print();
		if(board.won()){
			System.out.println("Puzzle " + name + " was solved in " + moves + " steps!" );
		}
	}
	// returns a MoveStack containing the moves for the smallest solution
	public MoveStack solution(){
		return PuzzleSolver.solve(board);
	}
	// performs moves on the puzzle to solve the board
	// if parameter is true the board is printed after every move
	public void solve(boolean printSteps){
		MoveStack stack = solution();
		if(printSteps){
			print();
		}
		if(stack != null){
			while(!stack.isEmpty()){
				Move move = new Move(0,0);
				if(printSteps)
					move = stack.peek();
				move(stack.pop());
				if(printSteps){
					System.out.println("Car " + move.getCarNumber() + " is moved by " + move.getMovement() + "." );
					print();
				}
			}
		}
	}
	// performs move if it is a legal move
	// move is stored in stack to allow undoing
	public boolean move(Move move){
		if(board.move(move)){
			moves++;
			stack.push(move);
            return true ;
		}
        return false ;
	}
	// undoes a move and lowers total moves accordingly
	public void undo(){
		move(stack.pop().reverse());
		moves -= 2;
	}
	public void setName(String name){
		this.name = name ;
	}
	public String getName(){
		return name;
	}
	// returns the amount of moves necessary to get to a winning state
	// estimated by the boards heuristic function
	public int heuristic(){
		return board.heuristic() ;
	}
	
	public Puzzle copy(){
		return new Puzzle(board.copy(), name, moves, stack.copy());
	}
	// returns a string holding the puzzle in a format
	// PuzzleReader can read it from a file
	public String savingFormat(){
		String str = name + System.getProperty("line.separator");
		str += board.savingFormat();
		return str;
	}

}
