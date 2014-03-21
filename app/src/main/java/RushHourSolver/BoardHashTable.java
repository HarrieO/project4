/*********************************
 * H.R. Oosterhuis - 10196129
 *********************************/
package RushHourSolver ;
/*
 * Hash Table containing boards as keys and cost to reach the board as values
 * the table is used by the PuzzleSolver to decide if a Node should be discarded
 * nodes containing boards that have been reached before with a lower cost will be discarded
 * 
 * BoardHashTable is a chained hash table for the size necessary can vary immensely 
 */
public class BoardHashTable  {
	private String[][] array;
	private int[][] costArray;
	private int filled ;
	private Division function ;
	private int hash_size;
	
	// wrapper constructor that adds default hash function
	BoardHashTable(int hash_size){
		this(hash_size, new Division(hash_size) );
	}
	// default constructor
	BoardHashTable( int hash_size, Division function ){
		array = new String[hash_size][] ;
		costArray = new int[hash_size][] ;
		filled = 0 ;
		this.function = function ;
		this.hash_size = hash_size ;
	}
	// wrapper put that converts board to string
	// and uses the string to call the default put
	public boolean put( Board board, int cost ){
		return put(board.fieldToString(), cost);
	}
	// puts element into the hashtable
	// returns true if board is not yet present or
	// if the board is already present with a higher cost
	// the higher cost is the also overwritten by the new lower cost
	// returns false otherwise ( the node should be discarded )
	public boolean put(String element, int cost){
		int binIndex = function.calcIndex(element);
		int index = 0;
		if(array[binIndex] == null){
			array[binIndex] = new String[5];
			costArray[binIndex] = new int[5];
		}
		while(array[binIndex][index] != null){
			if( array[binIndex][index].contentEquals(element) ){
				if( costArray[binIndex][index] > cost ){
					costArray[binIndex][index] = cost ;
					return true;
				} else {
					return false ;
				}
			}
			index++;
			if(index >= array[binIndex].length){
				extendBin( binIndex, array[binIndex].length );
			}
		}
		array[binIndex][index] = element;
		costArray[binIndex][index] = cost ;
		filled++;
		return true ;
	}
	// extends bin in hash table to allow for more entries
	private void extendBin( int binIndex, int extension ){
		String[] temp = array[binIndex];
		int[] costTemp = costArray[binIndex];
		array[binIndex] = new String[array[binIndex].length + extension];
		costArray[binIndex] = new int[array[binIndex].length + extension];
		System.arraycopy(temp, 0, array[binIndex], 0, temp.length);
		System.arraycopy(costTemp, 0, costArray[binIndex], 0, temp.length);
	}
	// returns true if hashtable contains element
	public boolean contains(String element){
		int binIndex = function.calcIndex(element);
		if(array[binIndex] == null){
			return false ;
		}
		for( int i = 0; i < array[binIndex].length ; i++){
			if(array[binIndex][i] == null){
				return false ;
			}
			if(array[binIndex][i].contentEquals(element)){
				return true ;
			}
		}
		return false ;
	}
	// returns the amount of words in hashtable
	public int size(){
		return filled ;
	}
	// returns the size of the hashtable
	public int hash_size(){
		return hash_size ;
	}
	
}

