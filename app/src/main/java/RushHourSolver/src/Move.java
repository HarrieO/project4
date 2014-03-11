/*********************************
 * H.R. Oosterhuis - 10196129
 * Marysia Winkels - 10163727
 * Kunstmatige Intelligentie
 * Datastructuren 2012-2013
 * Project Opdracht
 *********************************/
/* represents a move
 * the forward or backwards movement of a single car
 * most methods are self explenatory
 */
public class Move {
	//carNumber is the index of the car in the array of its board
	private int carNumber, movement ;
	Move(int carNumber, int movement){
		this.carNumber = carNumber ;
		this.movement = movement ;
	}
	public int getCarNumber(){
		return carNumber ;
	}
	public int getMovement(){
		return movement ;
	}
	public Move copy(){
		return new Move(carNumber, movement);
	}
	public Move reverse(){
		return new Move(carNumber, -movement);
	}
	public boolean equals( Move move ){
		return     move.carNumber == carNumber
				&& move.movement  == movement ;
	}
}
