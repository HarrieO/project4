/*********************************
 * H.R. Oosterhuis - 10196129
 *********************************/
package RushHourSolver ;
//represents a single car
//most methods are self explanatory
public class Car {
	
	private int x, y, size;
	//orientation of the car
	private boolean horizontal;
	
	public Car(int x, int y, int size, boolean horizontal){
		this.x = x;
		this.y = y;
		this.size = size;
		this.horizontal = horizontal;
	}

	public int getx(){
		return x ;
	}
	public int gety(){
		return y ;
	}
	public int getSize(){
		return size ;
	}
	public boolean isHorizontal(){
		return horizontal ;
	}
	public boolean isVertical(){
		return !horizontal ;
	}
	// moves car
	// note: does not move car in field representation of board!
	public void move( Move move ){
		if(horizontal){
			x += move.getMovement();
		} else {
			y += move.getMovement();
		}
	}
	public Car copy(){
		return new Car(x,y,size,horizontal);
	}
}
