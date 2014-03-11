/*********************************
 * H.R. Oosterhuis - 10196129
 * Marysia Winkels - 10163727
 * Kunstmatige Intelligentie
 * Datastructuren 2012-2013
 * Project Opdracht
 *********************************/
/*
 * this class is used to generate the puzzles in generated.txt
 * so that a 1000 puzzles could be benchmarked
 * it is added for your curiosity and is not part of the main solver
 */
public class BoardGenerator2 implements BoardGenerator {

	public BoardGenerator2(){
		
	}
	
	public Board generate( int height, int width){
		Board.Wall exitWall;
		
		// int exit is used once to choose one of the four walls
		int exit = (int) Math.round(Math.random()*4);
		exit = 3;
		
		exitWall 	= Board.Wall.RIGHT ;
		exit 		= (int) Math.round(Math.random()*(height/2-1));
		int x 		= width -3 - (int) Math.round(Math.random()*(width-3));
		Car exitCar = 	new Car(x,exit,2,true);
		
		Board board = new Board(height, width, exit, exitWall);
		board.addExitCar(exitCar);
		
		//int[] bestOf = { 1, 1, 10, 15, 20, 20, 130, 130, 130, 10,10,10,10,10,10};
		int[] bestOf = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,1,1,1,1,1};
		
		int min = 1;
		int newmin = addBlockingCar(board, 1, 1, 10) ;
		
		int tries = 10 ;
		while( newmin > min || (board.getTotalCars() < 4 && tries > 0) ){
			tries--;
			min = newmin;
			if( board.getTotalCars() < 6 ){
				newmin = addBlockingCar(board,min,tries*2-5,30);
			} else {
				newmin = addBlockingCar(board,min,1,Math.min(100,board.getTotalCars()*10));
			}
			//board.print();
		}
		
		
		return board ;
	}
	
	private static boolean blocking( Coordinate coor, Board board ){
		Car[] cars = board.getCars();
		for( int i = 0 ; i < cars.length ; i++ ){
			if( ( cars[i].isHorizontal() && coor.x == cars[i].getx() ) ||
				( cars[i].isVertical()   && coor.y == cars[i].gety() ) ){
				return true ;
			}
		}
		return false ;
	}
	
	private static class Coordinate{
		public int x;
		public int y;
		Coordinate(int x, int y){
			this.x = x;
			this.y = y;
		}
	}
	public static Coordinate freeSpot( int spotnumber, Board board){
		int found = 0;
		int[][] field = board.getBoardArray();
		for( int y = 0; y < board.getHeight() ; y++){
			for( int x = 0; x < board.getWidth() ; x++){
				if(field[x][y] == 0){
					if(found++ == spotnumber){
						return new Coordinate(x,y);
					}
				}
			}
		}
		return new Coordinate(-1,-1);
	}
	public static int totalFreeSpots(Board board){
		int found = 0;
		int[][] field = board.getBoardArray();
		for( int x = 0; x < board.getWidth() ; x++){
			for( int y = 0; y < board.getHeight() ; y++){
				if(field[x][y] == 0){
					found++;
				}
			}
		}
		return found;
	}
	
	public static int addBlockingCar(Board board, int min, int bestOf, int tries){
		
		int size, spot ;
		boolean horizontal ;
		Coordinate coor ;
		
		Car bestCar = new Car(0,0,2,true);
		int bestMin  = 0 ;
		
		for(int i = 0; i < tries && board.getFreeSpots() >= 2; i++){
			horizontal = (int) Math.round(Math.random()) == 0 ;
			
			spot = (int) Math.floor(Math.random()*(board.getFreeSpots()));
			Car exitCar = board.getCars()[board.getExitCar()];
			coor = freeSpot(spot,board);
			if( horizontal){
				if( board.getCars()[board.getExitCar()].isVertical() ){
					int max = Math.max(exitCar.getx(), board.getWidth()-exitCar.getx()-1);
					size = 2 + (int) Math.round(Math.random()*(max-2));
				} else {
					size = 2 + (int) Math.round(Math.random()*(board.getWidth()-3));
				}
			} else {
				if( board.getCars()[board.getExitCar()].isHorizontal() ){
					int max = Math.max(exitCar.gety(), board.getHeight()-exitCar.gety()-1);
					size = 2 + (int) Math.round(Math.random()*(max-2));
				} else {
					size = 2 + (int) Math.round(Math.random()*(board.getHeight()-3));
				}
			}
			
			if (board.addCar(new Car(coor.x,coor.y,size,horizontal))){
				int newmin = PuzzleSolver.solve(board).size();
				if( min < newmin ){
					bestOf--;
					if( newmin > bestMin){
						bestMin = newmin;
						bestCar = new Car(coor.x,coor.y,size,horizontal);
					}
					board.removeCar(board.getTotalCars() -1);
					if( bestOf <= 0 ){
						board.addCar(bestCar);
						System.out.print(newmin + ",");
						return bestMin ;
					}
				} else {
					if( newmin >= bestMin ){
						bestCar = new Car(coor.x,coor.y,size,horizontal);
					}
					board.removeCar(board.getTotalCars() -1);
				}
			}
		}
		if( bestMin > min){
			board.addCar(bestCar);
			System.out.print(bestMin + ",");
			return bestMin ;
		}
		return min ;
	}
	
	/*public static Board generatePuzzle( int height, int width, int tries ){
		Board board, bestBoard = generate(height, width, 0 );
		int minFound, highestMin = 0, maxCars = (height * width)/2 -1 ;
		do {
			board = generate(height, width, (int) Math.floor(Math.random()*maxCars) );
			findBestSetting(board, 30);
			minFound = board.heuristic();
			if(highestMin < minFound || 
			 (highestMin == minFound && Math.random() < 0.2 )){
				bestBoard = board;
				highestMin = minFound;
			}
		} while( tries-- > 0);
		return bestBoard;
	}
	public static Board generateHardPuzzle( int height, int width, int tries ){
		Board board, bestBoard = generate(height, width, 0 );
		int minFound, bestHeuristic = 0, highestMin = 0, maxCars = (height * width)/2 -1 ;
		do {
			board = generate(height, width, (int) Math.floor(Math.random()*(maxCars/3)*2 + Math.random()*(maxCars/3)));
			minFound = findHardestSetting(board, 1, bestHeuristic);
			if(highestMin < minFound || 
			 (highestMin == minFound && Math.random() < 0.2 )){
				bestBoard = board;
				highestMin = minFound;
				bestHeuristic = board.heuristic();
				System.out.println( "new min: " + minFound  + "\n" + board.savingFormat());
				
			}
			System.out.println(tries + " : " + highestMin + " : " + minFound);
			
		} while( tries-- > 0);
		return bestBoard;
	}
	public static int findHardestSetting(Board board, int triesPerFind, int bestHeuristic){
		MoveStack moves;
		BoardHashTable table = new BoardHashTable(1000);
		Board bestFind = null ;
		table.put(board,0);
		int min = 0, bestMin = -1, iterator = 0 ;
		for(int i = triesPerFind; i > 0 ;i--){
			moves = board.possibleMoves();
			if(moves.isEmpty())
				break;
			board.move(moves.randomPeek());
			if(table.put(board,iterator++)){
				if(board.heuristic() >= bestHeuristic){
					min = PuzzleSolver.solve(board).size();
					if( bestMin < min ||
					   (bestMin == min && Math.random() >0.4 )){
						if(bestMin < min)
							i = triesPerFind + min ;
						bestFind = board.copy();
						bestMin = min;
					} else if( bestMin > min && Math.random() >0.8){
						board.set(bestFind);
						min = bestMin ;
					}
				}
			}
		}
		if( bestFind != null)
			board.set(bestFind);
		return bestMin ;
	}
	public static PuzzlePriorityQueue generatePuzzles( int total, int height, int width, int tries){
		PuzzlePriorityQueue queue = new PuzzlePriorityQueue(total, total, false);
		return generatePuzzles( queue, height, width, tries);
	}
	public static PuzzlePriorityQueue generatePuzzles( PuzzleStack stack, int total, int height, int width, int tries){
		PuzzlePriorityQueue queue = new PuzzlePriorityQueue(total, total, false);
		queue.push(stack);
		return generatePuzzles( queue, height, width, tries);
	}
	public static PuzzlePriorityQueue generatePuzzles( PuzzlePriorityQueue queue, int height, int width, int tries){
		Board board ;
		Puzzle puzzle ;
		int minFound, minHeuristic = 2, maxCars = (height * width)/2 -1, tot = tries ;
		if(!queue.isEmpty()){
			minHeuristic = queue.last().heuristic() ;
		}
		while(tries-->0){
			board = generate(height, width, (int) Math.floor(Math.random()*(maxCars/3)*2 + Math.random()*(maxCars/3)));
			minFound = findHardestSetting(board, 1 + minHeuristic/3, minHeuristic);
			if(minFound < 0)
				continue;
			puzzle = new Puzzle(board.copy(), "");
			if(queue.push( puzzle, minFound )){
					puzzle.setName("Generation " + (tot-tries) + " " + height + "x" + width  );
			}
			
		}
		
		return queue ;
	}*/
}
