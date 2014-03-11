/*********************************
 * H.R. Oosterhuis - 10196129
 * Marysia Winkels - 10163727
 * Kunstmatige Intelligentie
 * Datastructuren 2012-2013
 * Project Opdracht
 *********************************/
/*
 * NodeQueue is a special priority queue
 * that orders its Nodes based on their expected cost
 * 
 * NodeQueu uses InsertionSort
 * this implementation is chosen for we expect
 * the nodes that are added by A* to have the best priorities
 * instead of random priorities
 * insertion sort moves the best priorities first to make room
 * making it more efficient over selection sort
 */
public class NodeQueue {
	public Node array[];
	private int filled ;
	// determines the sorting of the array
	// true for ascending, false for descending
	private boolean ascend ;
	
	// the default length for a NodeQueue is 15
	// default constructors:
	public NodeQueue(){
		this(15, true);
	}
	public NodeQueue(int initialSize){
		this(initialSize, true);
	}
	public NodeQueue(boolean ascend){
		this(15, ascend);
	}
	public NodeQueue(int initialSize, boolean ascend){
		if( initialSize <=0 ){
			array = new Node[1];
		} else {
			array = new Node[initialSize];
		}
		filled = 0;
		this.ascend = ascend;
	}
	// constructor used for copy function
	private NodeQueue( Node array[], int filled, boolean ascend){
		this.array = new Node[array.length];
		this.filled = filled;
		this.ascend = ascend;
		for(int i = 0; i < filled;i++){
			this.array[i] = array[i];
		}
	}
	// extends the array containing the queue to
	private void extend( int extension ){
		Node temp[] = array;
		array = new Node[array.length + extension];
		System.arraycopy(temp, 0, array, 0, filled);
	}
	// adding a node in the right place using selection sort
	public Node push( Node node){
		if(array.length == filled){
			if(array.length == 0)
				extend(5);
			else
				extend(array.length);
		}
		for( int i = filled++ ; i >= 0 ; i-- ){
			if( i > 0 && ( (ascend && array[i-1].expectedCost() < node.expectedCost())
				|| (!ascend && array[i-1].expectedCost() > node.expectedCost()) ) ){
					array[i] = array[i-1];
			} else {
					array[i] = node ;
					break;
			}
		}
		return node ;
	}
	public Node peek(){
		return array[filled-1];
	}
	public Node pop(){
		return array[--filled];
	}
	public int size(){
		return filled ;
	}
	public boolean isEmpty(){
		return filled == 0;
	}
	
	public NodeQueue copy(){
		return new NodeQueue(array, filled, ascend);
	}
}
