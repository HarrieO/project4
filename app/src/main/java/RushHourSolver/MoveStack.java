/*********************************
 * H.R. Oosterhuis - 10196129
 *********************************/
package RushHourSolver ;
/*
 * an array-stack for objects of class Move
 */
public class MoveStack {
	private Move array[];
	private int filled ;
	
	// the default size of the array is 15
	public MoveStack(){
		this(15);
	}
	// constructor for a different initial array-size
	public MoveStack( int size ){
		array = new Move[size];
		filled = 0;
	}
	// constructor used by the copy function
	private MoveStack(Move array[], int filled){
		this.array = new Move[array.length];
		this.filled = filled;
		for(int i =0; i< filled;i++){
			this.array[i] = array[i].copy();
		}
	}
	
	public Move push( Move move){
		if(array == null || array.length == 0){
			array = new Move[5];
		} else if(filled == array.length){
			extend(array.length);
		}
		array[filled++] = move;
		return move ;
	}
	public Move peek( ){
		if(array == null || array.length == 0 || filled == 0){
			throw( new java.util.NoSuchElementException() );
		}
		return array[filled-1] ;
	}
	public Move pop( ){
		if(array == null || array.length == 0 || filled == 0){
			throw( new java.util.NoSuchElementException() );
		}
		return array[--filled] ;
	}
	// peeks at a random entry in the array
	// used by BoardGenerator to perform random moves
	public Move randomPeek( ){
		if(array == null || array.length == 0 || filled == 0){
			throw( new java.util.NoSuchElementException() );
		}
		return array[(int)Math.round(Math.random()*(filled-1))] ;
	}
	public int size(){
		return filled ;
	}
	public boolean isEmpty(){
		return filled == 0 ;
	}
	// extends the array to allow more entries
	private void extend( int extention ){
		Move temp[] = new Move[array.length + extention];
		System.arraycopy(array, 0, temp, 0, filled);
		array = temp;
	}
	public MoveStack copy(){
		return new MoveStack(array, filled);
	}
}
