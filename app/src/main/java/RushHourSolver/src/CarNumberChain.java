/*********************************
 * H.R. Oosterhuis - 10196129
 * Marysia Winkels - 10163727
 * Kunstmatige Intelligentie
 * Datastructuren 2012-2013
 * Project Opdracht
 *********************************/
/*
 * class containing a set of cars
 * cars are represented by their carNumber
 * set is saved in a chain format in order to work
 * with the branching nature of the heuristic function
 */
public class CarNumberChain {
	public int carNumber;
	public CarNumberChain previous ;
	// the length of the chain is stored
	// to save computation time
	private int length ;
	
	// constructor for a root node
	public CarNumberChain(int carNumber){
		this.carNumber = carNumber ;
		this.previous = null;
		this.length = 0;
	}
	// default constructor
	public CarNumberChain(int carNumber, CarNumberChain previous){
		this.carNumber = carNumber ;
		this.previous = previous ;
		this.length = previous.getLength() + 1;
	}
	
	public int getLength(){
		return length ;
	}
	public int getCarNumber(){
		return carNumber ;
	}
	// returns a new chain that has branched from
	// this chain, this way multiple sets of cars
	// can share the matching part of their chain
	public CarNumberChain branch(int carNumber){
		return new CarNumberChain(carNumber, this);
	}
	// returns true if a carNumber is contained in the tree
	public boolean contains(int number){
		if(carNumber == number)
			return true ;
		CarNumberChain iter = previous ;
		while(iter != null){
			if(iter.carNumber == number)
				return true;
			iter = iter.previous ;
		}
		return false ;
	}
	
	// static functions used in heuristic function
	
	// returns biggest chain of the two
	// or null if both chains are null
	public static CarNumberChain max( CarNumberChain chain1, CarNumberChain chain2 ){
		if( chain1 == null )
			return chain2 ;
		if( chain2 == null )
			return chain1 ;
		if(chain1.getLength() >= chain2.getLength())
			return chain1 ;
		return chain2 ;
	}
	// returns biggest smallest of the two
	// or null if both chains are null
	public static CarNumberChain min( CarNumberChain chain1, CarNumberChain chain2 ){
		if( chain1 == null )
			return chain2 ;
		if( chain2 == null )
			return chain1 ;
		if(chain1.getLength() <= chain2.getLength())
			return chain1 ;
		return chain2 ;
	}
	// returns biggest smallest length of the two
	// or -1 if both chains are null
	public static int minLength(CarNumberChain chain1, CarNumberChain chain2){
		if( chain1 == null && chain2 == null )
			return -1;
		if( chain1 == null )
			return chain2.getLength() ;
		if( chain2 == null )
			return chain1.getLength() ;
		if(chain1.getLength() <= chain2.getLength())
			return chain1.getLength() ;
		return chain2.getLength() ;
	}
	// returns length of the chain if it is smaller than the given number
	// returns the number otherwise
	public static int minLength(CarNumberChain chain, int number ){
		if( chain == null )
			return number ;
		if(chain.getLength() <= number)
			return chain.getLength() ;
		return number ;
	}
}
