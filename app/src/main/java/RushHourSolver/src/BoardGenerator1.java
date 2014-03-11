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
public abstract class BoardGenerator1 {

	
	public static Board generate( int height, int width, int amountOfCars){
		Board.Wall exitWall;
		Car exitCar, helperCar;
		// int exit is used once to choose one of the four walls
		int exit = (int) Math.round(Math.random()*4);
		exit = 3;
		if(exit == 0){
			exitWall 	= Board.Wall.BOTTOM ;
			exit 		= (int) Math.round(Math.random()*(width-1));
			int y 		= height -2 - (int) Math.round(Math.random()*(height-2));
			exitCar 	= new Car(exit,y,2,false);
			helperCar	= new Car(exit,y+2,height-y-2,false);
		} else if(exit == 1){
			exitWall 	= Board.Wall.TOP ;
			exit 		= (int) Math.round(Math.random()*(width-1));
			int y 		= (int) Math.round(Math.random()*(height-2));
			exitCar 	= new Car(exit,y,2,false);
			helperCar 	= new Car(exit,0,y,false);
		} else if(exit == 2){
			exitWall 	= Board.Wall.LEFT ;
			exit 		= (int) Math.round(Math.random()*(height-1));
			int x 		= (int) Math.round(Math.random()*(width-2));
			exitCar 	= 	new Car(x,exit,2,true);
			helperCar 	= new Car(0,exit,x,true);
		} else {
			exitWall 	= Board.Wall.RIGHT ;
			exit 		= (int) Math.round(Math.random()*(height-1));
			int x 		= width -2 - (int) Math.round(Math.random()*(width-2));
			exitCar 	= 	new Car(x,exit,2,true);
			helperCar 	= new Car(x+2,exit,width-x-2,true);
		}
		Board board = new Board(height, width, exit, exitWall);
		board.addExitCar(exitCar);
		board.addCar(helperCar);
		fillBoard(board,amountOfCars-1);
		board.removeCar(1);
		shuffleBoard(board,100);
		return board ;
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
	public static void fillBoard(Board board, int amountOfCars){
		fillBoard(board, amountOfCars, amountOfCars*5);
	}
	public static void fillBoard(Board board, int amountOfCars, int tries){
		int size, spot, placed = 0;
		boolean horizontal ;
		Coordinate coor ;
		for(int i = 0; i < tries && placed<amountOfCars && board.getFreeSpots() >= 2; i++){
			horizontal = (int) Math.round(Math.random()) == 0 ;
			spot = (int) Math.floor(Math.random()*(board.getFreeSpots()));
			if( horizontal){
				do {
					size = 2 + (int) Math.round(Math.random()*(board.getWidth()-3));
				} while( Math.random() < (float) size / (float) board.getWidth()  );
			} else {
				do {
					size = 2 + (int) Math.round(Math.random()*(board.getHeight()-3));
				} while( Math.random() < (float) size / (float) board.getHeight()  );
			}
			coor = freeSpot(spot,board);
			if (board.addCar(new Car(coor.x,coor.y,size,horizontal))){
				placed++;
			}
		}
	}
	public static void shuffleBoard(Board board, int shuffles ){
		MoveStack moves;
		while(shuffles-->0){
			moves = board.possibleMoves();
			if( moves.isEmpty() ){
				break;
			}
			board.move(moves.randomPeek());
		}
	}
	public static void findBestSetting(Board board, int triesPerFind){
		MoveStack moves;
		BoardHashTable table = new BoardHashTable(10000, new Division(10000));
		Board bestFind = board.copy();
		table.put(board,0);
		int min, bestMin = 0;
		for(int i = triesPerFind; i > 0 ;i--){
			moves = board.possibleMoves();
			if(moves.isEmpty())
				break;
			board.move(moves.randomPeek());
			if(table.put(board,0)){
				i = triesPerFind;
				min = board.heuristic();
				if( bestMin > min ||
				   (bestMin == min && Math.random() >0.2 )){
					bestFind = board.copy();
					bestMin = bestFind.heuristic();
				}
			}
		}
		board.set(bestFind);
	}
	public static Board generatePuzzle( int height, int width, int tries ){
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
	}
}
