/*********************************
 * H.R. Oosterhuis - 10196129
 *********************************/
package RushHourSolver ;
/* represents a move
 * the forward or backwards movement of a single car
 * most methods are self explanatory
 */
public class Move {
	//carNumber is the index of the car in the array of its board
	private int carNumber, movement ;
	public Move(int carNumber, int movement){
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
