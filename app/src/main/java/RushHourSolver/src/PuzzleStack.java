/*********************************
 * H.R. Oosterhuis - 10196129
 * Marysia Winkels - 10163727
 * Kunstmatige Intelligentie
 * Datastructuren 2012-2013
 * Project Opdracht
 *********************************/
// class representing an array-stack of puzzles
public class PuzzleStack {
	private Puzzle array[];
	private int filled ;
	//default size of the array is 15
	public PuzzleStack(){
		this(15);
	}
	// constructor for a different initial size
	public PuzzleStack( int size ){
		array = new Puzzle[size];
		filled = 0;
	}
	// constructor used for the copy method
	private PuzzleStack(Puzzle array[], int filled){
		this.array = new Puzzle[array.length];
		this.filled = filled;
		for(int i =0; i< filled;i++){
			this.array[i] = array[i].copy();
		}
	}
	
	public Puzzle push( Puzzle puzzle){
		if(array == null || array.length == 0){
			array = new Puzzle[5];
		} else if(filled == array.length){
			extend(array.length);
		}
		array[filled++] = puzzle ;
		return puzzle ;
	}
	
	public Puzzle peek( ){
		if(array == null || array.length == 0 || filled == 0){
			throw( new java.util.NoSuchElementException() );
		}
		return array[filled-1] ;
	}
	
	public Puzzle pop( ){
		if(array == null || array.length == 0 || filled == 0){
			throw( new java.util.NoSuchElementException() );
		}
		return array[--filled] ;
	}
	public int size(){
		return filled ;
	}
	public boolean isEmpty(){
		return filled == 0 ;
	}
	// enlarges the array to allow more entries
	private void extend( int extention ){
		Puzzle temp[] = new Puzzle[array.length + extention];
		System.arraycopy(array, 0, temp, 0, filled);
		array = temp;
	}
	// returns a copy of the current stack
	// did you really expect it to do anything else?
	public PuzzleStack copy(){
		return new PuzzleStack(array, filled);
	}
}
