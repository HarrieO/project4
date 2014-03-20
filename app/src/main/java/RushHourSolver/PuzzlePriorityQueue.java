/*********************************
 * H.R. Oosterhuis - 10196129
 *********************************/
package RushHourSolver ;
/*
 * this class is used to generate the puzzles in generated.txt
 * so that a 1000 puzzles could be benchmarked
 * it is added for your curiosity and is not part of the main solver
 * 
 * this is a PriorityQueue for puzzles
 * with a set length,
 * the ordering is on the steps in a solution of the puzzle
 * puzzles with a solution to short are discarded
 */
public class PuzzlePriorityQueue {
	Puzzle array[] ;
	int prior[] ;
	int filled, max, totalPrior ;
	boolean ascend ;
	
	public PuzzlePriorityQueue( int initialLength, boolean ascend){
		this(initialLength, -1, ascend);
	}
	public PuzzlePriorityQueue( int initialLength, int max, boolean ascend){
		initialLength = Math.max(initialLength, max);
		this.array 	= new Puzzle[initialLength] ;
		this.prior = new int[initialLength];
		this.max 	= max ;
		this.ascend = ascend ;
		this.filled = 0;
	}
	public void extend( int extension ){
		int newLength;
		if( max != -1 ){
			newLength = Math.min(array.length + extension, max);
		} else {
			newLength = array.length + extension ;
		}
		if(newLength != array.length){
			Puzzle temp[] = array ;
			int tempInt[] = prior ;
			
			array = new Puzzle[newLength];
			prior = new int[newLength];
			
			System.arraycopy(temp, 0, array, 0, filled);
			System.arraycopy(tempInt, 0, prior, 0, filled);
		}
	}
	public boolean push( Puzzle puzzle, int priority){
		if( max != -1 && filled == max && 
		((ascend && prior[0] > priority) ||	(!ascend && prior[0] < priority))){
			totalPrior -= prior[0];
			int i = 0;
			while( i < filled -1 && ((ascend && prior[i+1] > priority) ||
									(!ascend && prior[i+1] < priority))){
				array[i] = array[i+1];
				prior[i] = prior[i+1];
				i++;
			}
			array[i]	= puzzle ;
			prior[i]	= priority ;
			totalPrior += priority ;
			return true ;
			
		} else if(max == -1 || filled < max){
			if(filled == max)
				extend(array.length);
			for( int i = filled++ ; i >= 0 ; i-- ){
				if( i > 0 && ( (ascend && prior[i-1] < priority )
					|| (!ascend && prior[i-1] > priority) ) ){
						array[i] = array[i-1];
						prior[i] = prior[i-1];
				} else {
						array[i] = puzzle ;
						prior[i] = priority ;
						totalPrior += priority ;
						return true;
				}
			}
		}
		return false ;
		
	}
	public Puzzle 	last(){			return array[0];			}
	public Puzzle 	first(){		return array[filled-1];		}
	public Puzzle 	pop(){			return array[--filled];		}
	public boolean 	maxReached(){	return filled == max ;		}
	public boolean 	isEmpty(){		return filled == 0 ;		}
	public int	 	bottomPrior(){	return prior[0];			}
	public int	 	topPrior(){		return prior[filled-1];		}
	public int		size(){			return filled;				}
	public double	averagePrior(){	return (double)totalPrior/filled;	}
	
	public PuzzlePriorityQueue copy(){
		PuzzlePriorityQueue newQueue = new PuzzlePriorityQueue( max, max, ascend);
		for(int i = 0 ; i < filled ; i++ ){
			newQueue.array[i] = array[i];
			newQueue.prior[i] = prior[i];
		}
		newQueue.filled = filled ;
		return newQueue ;
	}
	
	public void		push(PuzzleStack stack){
		int steps ;
		System.out.println("Level Name - Amount of Moves in Solution : Time taken to find Solution");
		long startingTime = System.nanoTime();
		long beginTime = System.nanoTime();
		int total = stack.size();
		while(!stack.isEmpty()){
			beginTime = System.nanoTime();
			steps = stack.peek().solution().size();
			System.out.println(stack.peek().getName() + " - " + steps + " : " + (System.nanoTime() - beginTime)/1000000);
			push(stack.pop(), steps);
		}
		System.out.println(total + " puzzles took " +(System.nanoTime() - startingTime)/1000000 + " miliseconds.");
		if( total > 0)
		{
			System.out.println("That gives us an average of " + 
					(System.nanoTime() - startingTime)/1000000/(total-stack.size()) + " miliseconds");
		}
	}
	
}
