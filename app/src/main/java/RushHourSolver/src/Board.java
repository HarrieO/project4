/*********************************
 * H.R. Oosterhuis - 10196129
 * Marysia Winkels - 10163727
 * Kunstmatige Intelligentie
 * Datastructuren 2012-2013
 * Project Opdracht
 *********************************/
/*
 * class containing a board state
 */
public class Board {
	// freeSpots holds the total free blocks on the field
	// heur holds the heuristic if up to date
	// exit holds the place of the exit
	// exitCar holds the red car (that needs to exit the field)
	private int height, width, amountOfCars, exit, exitCar, freeSpots, heur ;
	private Car cars[];
	// field is a matrix representation of the field
	//allowing for quick collision detection
	private int[][] field;
	// exitWall holds the wall where the exit is placed
	private Wall exitWall;
	// heurUpToDate is set to true if heur holds the hueristic
	private boolean heurUpToDate ;
	
	public Board( int height, int width, int exit, Wall exitWall){
		this.height = height;
		this.width = width;
		this.exitWall = exitWall;
		this.exitWall = exitWall;
		this.field = new int[width][height];
		this.exit = exit;
		this.amountOfCars = 0;
		this.exitCar = -1;
		this.freeSpots = this.height*this.width;
		this.heurUpToDate = false ;
	}
	public Board( int height, int width, int exit, Wall exitWall, Car cars[], int amountOfCars, int exitCar){
		this.height = height;
		this.width = width;
		this.exit = exit;
		this.exitWall = exitWall;
		this.cars = cars;
		this.field = new int[width][height];
		this.amountOfCars = amountOfCars;
		this.exitCar = exitCar;
		this.freeSpots = this.height*this.width - amountOfCars;
		for( int i = 0 ; i < amountOfCars ; i ++){
			placeCar(i);
		}
		this.heurUpToDate = false ;
	}
	private Board( int height, int width, int exit, Wall exitWall, Car cars[], int amountOfCars, int exitCar, int heuristic ){
		this.height = height;
		this.width = width;
		this.exit = exit;
		this.exitWall = exitWall;
		this.cars = cars;
		this.field = new int[width][height];
		this.amountOfCars = amountOfCars;
		this.exitCar = exitCar;
		this.freeSpots = this.height*this.width - amountOfCars;
		for( int i = 0 ; i < amountOfCars ; i ++){
			placeCar(i);
		}
		this.heurUpToDate = true ;
		this.heur = heuristic;
	}
	
	public enum Wall {	 TOP, BOTTOM, LEFT, RIGHT;  	}
	
	public int 	getHeight(){ 			return height ; 		}
	public int 	getWidth(){		 		return width ; 			}
	public int 	getExit(){ 				return exit ; 			}
	public int 	getTotalCars(){ 		return amountOfCars ;	}
	public int 	getExitCar(){			return exitCar;			}
	public int 	getFreeSpots(){			return freeSpots;		}
	public Wall getExitWall(){			return exitWall;		}
	public Car 	getCar(int carNumber){ 	return cars[carNumber]; }
	public Car[] getCars(){				return cars ; 			}
	public int[][] getBoardArray(){		return field;			}
	
	public Board copy(){
		Car newCars[] = new Car[cars.length];
		for(int i = 0; i < amountOfCars;i++){
			newCars[i] = cars[i].copy();
		}
		if(heurUpToDate)
			return new Board( height, width, exit, exitWall, newCars, amountOfCars, exitCar, heur);
		return new Board( height, width, exit, exitWall, newCars, amountOfCars, exitCar);
	}
	// sets the current board to be equal to the given board
	// used in BoardGenerator as a trick to allow Pass By Reference
	public void set( Board board ){
		this.height = board.height;
		this.width = board.width;
		this.exit = board.exit;
		this.exitWall = board.exitWall;
		this.cars = new Car[board.cars.length];
		this.field = new int[width][height];
		this.amountOfCars = board.amountOfCars;
		this.exitCar = board.exitCar;
		this.freeSpots = this.height*this.width - amountOfCars;
		for( int i = 0 ; i < amountOfCars ; i++ ){
			cars[i] = board.cars[i].copy();
			placeCar(i);
		}
		this.heurUpToDate = board.heurUpToDate ;
		this.heur = board.heur ;
		
	}
	// adding the car to exit
	public boolean addExitCar(Car car){
		if(!addCar(car))
			return false;
		exitCar = amountOfCars -1;
		heurUpToDate = false ;
		return true;
	}
	// adding a car that does not need to exit the field
	public boolean addCar(Car car){
		if(!canBePlaced(car))
			return false ;
		if(cars == null){
			cars = new Car[9];
			amountOfCars = 0;
		} else if(cars.length == amountOfCars){
			Car temp[] = new Car[cars.length *2 + 1];
			System.arraycopy(cars, 0, temp, 0, amountOfCars);
			cars = temp;
		}
		cars[amountOfCars] = car;
		placeCar(amountOfCars++);
		freeSpots -= car.getSize();
		heurUpToDate = false ;
		return true;
	}
	// places a car on the field representation
	private void placeCar( int carNumber){
		Car car = cars[carNumber];
		for( int i = 0 ; i < car.getSize() ; i++ ){
			if( car.isHorizontal() ){
				field[car.getx() + i][car.gety()] = carNumber + 1;
			} else {
				field[car.getx()][car.gety() + i] = carNumber + 1;
			}
		}
	}
	// removes a car from the field representation
	private void eraseCar( int carNumber){
		Car car = cars[carNumber];
		for( int i = 0 ; i < car.getSize() ; i++ ){
			if( car.isHorizontal() ){
				field[car.getx() + i][car.gety()] = 0;
			} else {
				field[car.getx()][car.gety() + i] = 0;
			}
		}
	}
	// returns true if car has a valid x and y
	private boolean inField( Car car ){
		if( car.getx() < 0 || car.gety() < 0 ){
			return false ;
		} else if(car.isHorizontal()){
			return car.getx() + car.getSize() <= width && car.gety() < height ;
		} else {
			return car.getx() < width && car.gety() + car.getSize() <= height ;
		}
	}
	public boolean canBePlaced(Car car){
		return inField(car) && spaceFree(car) ;
	}
	// uses field representation to check if space for the car is free
	// FIRST MAKE SURE THE CAR IS IN FIELD WHEN USING THIS METHOD!
	private boolean spaceFree( Car car){
		for(int i = 0 ; i < car.getSize() ; i++){
			if(car.isHorizontal() && field[car.getx() + i][car.gety()] != 0){
				return false ;
			} else if((!car.isHorizontal()) && field[car.getx()][car.gety()+i] != 0) {
				return false ;
			}
		}
		return true ;
	}
	//removes car from board
	public void removeCar(int carNumber){
		if( carNumber >= 0 && carNumber < amountOfCars ){
			eraseCar(carNumber);
			freeSpots += cars[carNumber].getSize();
			amountOfCars--;
			heurUpToDate = false ;
			for(int i = carNumber; i < amountOfCars; i++ ){
				cars[i] = cars[i+1];
				placeCar(i);
			}
			cars[amountOfCars] = null;
		}
	}
	
	public void print(){
		System.out.print("#-");
		for( int x = 0 ; x < width*3 ; x++){
			if(exitWall.equals(Wall.TOP) && (((float)x)/2) == exit){
				System.out.print("O");
			} else {
				System.out.print("-");
			}
		}
		System.out.print("#\n");
		for(int y = 0; y < height; y++){
			if(exitWall.equals(Wall.LEFT) && y == exit){
				System.out.print("O ");
			} else {
				System.out.print("| ");
			}
			for( int x = 0 ; x < width ; x++){
					if (field[x][y] == 0)
						System.out.print("  ");
					else if( field[x][y] <= 10)
						System.out.print((field[x][y]-1) + " ");
					else
						System.out.print((field[x][y]-1));
					if( x != width -1)
						System.out.print(".");
			}
			if(exitWall.equals(Wall.RIGHT) && y == exit){
				System.out.print(" O\n");
			} else {
				System.out.print(" |\n");
			}
		}
		System.out.print("#-");
		for( int x = 0 ; x < width*3 ; x++){
			if(exitWall.equals(Wall.BOTTOM) && (((float)x)/2) == exit ){
				System.out.print("O");
			} else {
				System.out.print("-");
			}
		}
		System.out.print("#\n");
	}
	// performs move on board
	// returns true if succeeded
	public boolean move( Move move){
		if(!legalMove(move))
			return false ;
		if(move.getMovement() == 0)
			return true ;
		heurUpToDate = false ;
		eraseCar(move.getCarNumber());
		cars[move.getCarNumber()].move(move);
		placeCar(move.getCarNumber());
		return true ;
	}
	// returns true if move keeps its car in field
	private boolean staysInField(Move move){
		if( move.getCarNumber() < 0 || move.getCarNumber() > amountOfCars){
			return false ;
		}
		Car car = cars[move.getCarNumber()];
		if(car.isHorizontal() && ( car.getx() + move.getMovement() < 0
				  || car.getx() + car.getSize() + move.getMovement() > width) ){
			return false;
		} else if(car.isVertical() && ( car.gety() + move.getMovement() < 0
				  || car.gety() + car.getSize() + move.getMovement() > height) ){
			return false;
		}
		return true;
	}
	// returns true if a move is legal
	// ( existing car; no collisions )
	public boolean legalMove( Move move ){
		if( move.getCarNumber() < 0 || move.getCarNumber() > amountOfCars){
			return false ;
		} else if(move.getMovement() == 0){
			return true;
		}
		Car car = cars[move.getCarNumber()];
		if(car.isHorizontal() && ( car.getx() + move.getMovement() < 0
				  || car.getx() + car.getSize() + move.getMovement() > width) ){
			return false;
		} else if(car.isVertical() && ( car.gety() + move.getMovement() < 0
				  || car.gety() + car.getSize() + move.getMovement() > height) ){
			return false;
		}
		int begin, direction = Integer.signum(move.getMovement());
		if( move.getMovement() > 0 ){
			if( car.isHorizontal() ){
				begin = car.getx() + car.getSize();
			} else {
				begin = car.gety() + car.getSize();
			}  
		} else {
			if( car.isHorizontal() ){
				begin = car.getx() -1;
			} else{
				begin = car.gety() -1 ;
			} 
		}
		
		for( int i = 0; i < move.getMovement(); i++){
			if( (car.isHorizontal() && field[begin + i*direction][car.gety()] != 0)
			 || ( car.isVertical()  && field[car.getx()][begin + i*direction] != 0)){
					return false ;
			}
		}
		return true ;
	}
	// returns a stack holding all the moves legal on the current board
	public MoveStack possibleMoves(){
		MoveStack stack = new MoveStack();
		for( int i = 0; i < amountOfCars ; i++){
			if(cars[i].isHorizontal()){
				horizontalMoves(i, stack);
			} else {
				verticalMoves(i, stack);
			}
		}
		return stack;
	}
	// returns possible horizontal moves
	private void horizontalMoves( int carNumber, MoveStack stack ){
		Car car = cars[carNumber];
		int m , x;
		for( m = 1, x = car.getx() + car.getSize() -1; x + m < width ; m++ ){
			if( field[x + m][car.gety()] != 0 ){
				break;
			} else {
				stack.push(new Move(carNumber, m));
			}
		}
		for( m = -1, x = car.getx(); x + m >= 0 ; m-- ){
			if( field[x + m][car.gety()] != 0 ){
				break;
			} else {
				stack.push(new Move(carNumber, m));
			}
		}
	}
	// returns possible vertical moves
	private void verticalMoves( int carNumber, MoveStack stack ){
		Car car = cars[carNumber];
		int m , y;
		for( m = 1, y = car.gety() + car.getSize() -1; y + m < height ; m++ ){
			if( field[car.getx()][y + m] != 0 ){
				break;
			} else {
				stack.push(new Move(carNumber, m));
			}
		}
		for( m = -1, y = car.gety(); y + m >= 0 ; m-- ){
			if( field[car.getx()][y + m] != 0 ){
				break;
			} else {
				stack.push(new Move(carNumber, m));
			}
		}
	}
	// returns move necessary to win
	// if board is unsolvable returns the move to place the car against winning exit
	public Move winningMove(){
		int movement = 0;
		if(exitWall.equals(Wall.LEFT)){
			movement = -cars[exitCar].getx();
		} else if(exitWall.equals(Wall.RIGHT)){
			movement = width - cars[exitCar].getx() - cars[exitCar].getSize();
		} else if(exitWall.equals(Wall.TOP)){
			movement = -cars[exitCar].gety();
		} else if(exitWall.equals(Wall.BOTTOM)){
			movement = height - cars[exitCar].gety() - cars[exitCar].getSize();
		} 
		return new Move(exitCar,movement);
	}
	// true if board is in finished state
	// or unsolvable
	public boolean won(){
		return winningMove().getMovement() == 0 ;
	}
	public int heuristic(){
		if(!heurUpToDate){
			heur = minimumCarsToBeMoved(winningMove());
			heurUpToDate = true ;
		}
		return heur ;
	}
	public int minimumCarsToBeMoved( Move move){
		if(winningMove().getMovement() == 0)
			return 0 ;
		CarNumberChain toBeMoved = new CarNumberChain(move.getCarNumber());
		toBeMoved = smallestSetToMove(move, toBeMoved, amountOfCars);
		if(toBeMoved == null)
			return 0 ;
		return toBeMoved.getLength();
	}
	// returns smallest set of cars that can be determined to be blocking the exitCar
	// returns null if there is no set that can unblock the move ( meaning walls are in the way)
	// max holds the minimum depth of an earlier set if max is exceeded the set returns null
	// for it can no longer be the smallest
	private CarNumberChain smallestSetToMove( Move move, CarNumberChain toBeMoved, int max){
		if(!staysInField(move)){
			return null ;
		} else {
			if(toBeMoved.getLength() == max)
				return toBeMoved ;
			if(cars[move.getCarNumber()].isHorizontal()){
				toBeMoved = smallSetHor( move, toBeMoved, max);
			} else {
				toBeMoved = smallSetVer( move, toBeMoved, max);
			}
		}
		return toBeMoved ;
	}
	// only for horizontal moves
	// returns smallest set of cars that can be determined to be blocking the exitCar
	// returns null if there is no set that can unblock the move ( meaning walls are in the way)
	// max holds the minimum depth of an earlier set if max is exceeded the set returns null
	// for it can no longer be the smallest
	private CarNumberChain smallSetHor( Move move, CarNumberChain toBeMoved, int max){
		Car blocking, car = cars[move.getCarNumber()];
		int upMov, downMov, blockingIndex ;
		CarNumberChain lastEnd = toBeMoved ;
		int start, step, found ;
		Move unblockMove ;
		if(move.getMovement()>0){
			start = car.getx() + car.getSize();
			step = 1;
		} else {
			start = car.getx()-1;
			step = -1;
		}
		for(int i = 0; i != move.getMovement() ; i += step){
			found = field[start + i][car.gety()] -1 ;
			if ( found != -1 && !toBeMoved.contains(found) ) {
				toBeMoved = toBeMoved.branch(found);
			}
		}
		CarNumberChain iterator = toBeMoved ;
		CarNumberChain option1, option2;
		while( iterator != lastEnd ){
			blockingIndex = iterator.getCarNumber();
			blocking = cars[blockingIndex];
			if(blocking.isVertical()){
				upMov = (car.gety() - blocking.getSize()) - blocking.gety() ;
				downMov = car.gety() - blocking.gety() + 1 ;
				option1 = smallestSetToMove(new Move(blockingIndex,upMov ), toBeMoved, max);
				max = CarNumberChain.minLength(option1, max);
				option2 = smallestSetToMove(new Move(blockingIndex,downMov ), toBeMoved, max);
				toBeMoved = CarNumberChain.min(option1, option2);
			} else if(blocking.getx() < car.getx()){
				upMov = car.getx()+move.getMovement()-blocking.getSize()-blocking.getx() ;
				unblockMove = new Move(blockingIndex,upMov );
				toBeMoved = smallestSetToMove(unblockMove, toBeMoved, max);
			} else if(blocking.getx() > car.getx()){
				upMov = car.getx()+car.getSize()+move.getMovement()-blocking.getx() ;
				unblockMove = new Move(blockingIndex,upMov );
				toBeMoved = smallestSetToMove(unblockMove, toBeMoved, max);
			}
			if(toBeMoved == null)
				return null ;
			iterator = iterator.previous;
		}
		return toBeMoved ;
	}
	// only for vertical moves
	// returns smallest set of cars that can be determined to be blocking the exitCar
	// returns null if there is no set that can unblock the move ( meaning walls are in the way)
	// max holds the minimum depth of an earlier set if max is exceeded the set returns null
	// for it can no longer be the smallest
	private CarNumberChain smallSetVer( Move move, CarNumberChain toBeMoved, int max){
		Car blocking, car = cars[move.getCarNumber()];
		int upMov, downMov, blockingIndex ;
		CarNumberChain lastEnd = toBeMoved ;
		int start, step, found ;
		Move unblockMove ;
		if(move.getMovement()>0){
			start = car.gety() + car.getSize();
			step = 1;
		} else {
			start = car.gety()-1;
			step = -1;
		}
		for(int i = 0; i != move.getMovement(); i += step){
			found = field[car.getx()][start + i] -1 ;
			if ( found != -1 && !toBeMoved.contains(found) ) {
				toBeMoved = toBeMoved.branch(found);
			}
		}
		CarNumberChain iterator = toBeMoved ;
		CarNumberChain option1, option2;
		while( iterator != lastEnd ){
			blockingIndex = iterator.getCarNumber() ;
			blocking = cars[blockingIndex];
			if(blocking.isHorizontal()){
				upMov = (car.getx() - blocking.getSize()) - blocking.getx() ;
				downMov = car.getx() - blocking.getx() + 1 ;
				unblockMove = new Move(blockingIndex,upMov );
				option1 = smallestSetToMove(new Move(blockingIndex,upMov ), toBeMoved, max);
				max = CarNumberChain.minLength(option1, max);
				option2 = smallestSetToMove(new Move(blockingIndex,downMov ), toBeMoved, max);
				toBeMoved = CarNumberChain.min(option1, option2);
			} else if(blocking.getx() < car.getx()){
				upMov = (car.gety() + move.getMovement() - blocking.getSize()) - blocking.gety() ;
				unblockMove = new Move(blockingIndex,upMov );
				toBeMoved = smallestSetToMove(unblockMove, toBeMoved, max);
			} else if(blocking.getx() > car.getx()){
				upMov = (car.gety() + car.getSize() + move.getMovement() ) - blocking.gety() ;
				unblockMove = new Move(blockingIndex,upMov );
				toBeMoved = smallestSetToMove(unblockMove, toBeMoved, max);
			}
			if(toBeMoved == null)
				return null ;
			iterator = iterator.previous;
		}
		return toBeMoved ;
	}
	// returns the field in a nice string format
	// used in hash table
	public String fieldToString(){
		String str = "";
		for( int y = 0 ; y < height ; y++){
			for( int x = 0 ; x < width ; x++){
				if(field[x][y] > 0){
					str += (field[x][y]-1) ;
				} else {
					str += " " ;
				}
				if(field[x][y] <= 10)
					str += " ";
				str += ".";
			}
			str += "\n";
		}
		return str ;
	}
	// returns the board in a string format
	// used as key in hash table
	public String toString(){
		return height + "x" + width + ":" + exitWall.toString() + "," + exitCar
			          + System.getProperty("line.separator") + fieldToString();
	}
	// returns the board in a string format
	// used to save field in to file
	public String savingFormat(){
		String str = height + "x" + width + ":" ;
		if(exitWall.equals(Wall.TOP)){
			str += "TOP";
		} else if(exitWall.equals(Wall.BOTTOM)){
			str += "BOTTOM" ; 
		} else if(exitWall.equals(Wall.LEFT)){
			str += "LEFT" ; 
		} else {
			str += "RIGHT" ;
		}
		str += exit + "," + exitCar + System.getProperty("line.separator") ;
		for(int y = 0 ; y < height; y++){
			for(int x = 0; x < width;x++){
				if(field[x][y] == 0){
					str += "  .";
				} else if(field[x][y] <= 10){
					str += (field[x][y]-1) + " .";
				} else {
					str += (field[x][y]-1) + ".";
				}
			}
			str += System.getProperty("line.separator");
		}
		return str;
	}
}
