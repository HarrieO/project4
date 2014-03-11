import javax.swing.JFrame;

/*********************************
 * H.R. Oosterhuis - 10196129
 * Marysia Winkels - 10163727
 * Kunstmatige Intelligentie
 * Datastructuren 2012-2013
 * Project Opdracht
 *********************************/
// RushHourSolver contains the main class for our solver
public class RushHourSolver {

	// if set to false no Graphics will be shown
	public static boolean enableGraphics = true ;
	
	public static void main(String[] args) {
		if( args.length == 0 ){
			System.out.println("Usage:\n    "
				      + "java RushHourSolver fileName [-d disableGraphics] [-s stepsPerMove] [-f framerate] [-h hashinfo]");
		} else {
			String filename = interpretArguments( args );
			System.out.printf("Opening file %s.\n", filename);
			PuzzleStack puzzles = PuzzleReader.read( filename );
			System.out.printf("There were %d puzzles found in %s.\n",puzzles.size(), filename);
			if(!puzzles.isEmpty()){
				System.out.println("All puzzles will be timed and solved.");
				benchmark( puzzles );
			}
		}
		
		
		/*BoardGenerator2 generator = new BoardGenerator2();
		
		/*GeneratorBenchmarker.benchmark(generator);
		
		if( 1 == 1 )
			return;*/
		/*
		String filename = "test.txt" ;
		PuzzleStack puzzles = PuzzleReader.read( filename );
		
		System.out.printf("There were %d puzzles found in %s.\n",puzzles.size(), filename);
		
		PuzzlePriorityQueue queue ;
		if(!puzzles.isEmpty()){
			System.out.println("All puzzles will be timed and solved.");
			//enableGraphics = false ;
			queue = benchmark( puzzles );
		} else {
			queue = new PuzzlePriorityQueue(100, 100, false);
		}
		
		
		int check = 5;
		int show = 50;
		for(int i = 0; true ; i++){
			System.out.println("\nPuzzle No." + i );
			Board board = generator.generate(4, 9);
			queue.push(new Puzzle(board, "Generation " +  i),PuzzleSolver.solve(board).size());
			check--;
			if(check == 0){
				check = 5;
				System.out.print( "\n range: " + queue.bottomPrior() + " -> " + queue.topPrior()  + " writing...");
				if(PuzzleWriter.writeToFile( queue.copy(), filename ))
					System.out.println( "done.");
				else
					System.out.println( "!! backup could not be made !!!");
			}
			show--;
			if(show == 0){
				PuzzlePriorityQueue nque = queue.copy();
				PuzzleStack nstack = new PuzzleStack(queue.size());
				while(!nque.isEmpty()){
					nstack.push(queue.pop());
				}
				benchmark(nstack);
			}
		}
		/*
		PuzzlePriorityQueue temp = queue.copy();
		PuzzleWriter.writeToFile(temp, filename);
		
		while(!queue.isEmpty()){stack.push(queue.pop());}
		benchmark(stack);*/
	}
	// interprets the command line arguments
	private static String interpretArguments( String[] args ){
		for( int i = 1 ; i < args.length ; i++ ){
			if(args[i].equals("-d")){
				//disables graphic output
				enableGraphics = false ;
			} else if( args[i].equals("-h") ){
				// if enabled hashInfo displays information
				// about the nodes that it has added/discarted
				PuzzleSolver.hashInfo = true ;
			} else {
				i = interpretInteger( args, i, args[i]);
			}
		}
		return args[0];
	}
	//reads the command line arguments for values regarding the play speed
	// of the graphic interface
	public static int interpretInteger(String[] args, int index, String flag){
		String variable ;
		if( flag.equals("-f") ){
			variable = "frameRate";
		} else if( flag.equals("-s") ){
			variable = "moveSteps";
		} else {
			System.out.printf("The %s flag was given but "+
					"is unsupported.\n", flag);
			return index ;
		}
		if( ++index == args.length ){
			System.out.printf("The %s flag was given but "+
						"no integer value for %s.\n", flag, variable);
		} else {
			int value ;
			try{
				value = Integer.parseInt(args[index]);
			} catch(java.lang.NumberFormatException e){
				System.out.printf("The %s flag was given but "+
						"no integer value for %s.\n", flag, variable);
				return --index;
			}
			if( value <= 0 ){
				System.out.printf("The value for %s must be positive.\n", variable);
			} else {
				if(flag.equals("-s")){
					PuzzleGraphic.moveStep = value;
				} else {
					PuzzleGraphic.framerate = value;
				}
			}
		}
		return index ;
	}
	// solves given stack of puzzles and displays information about time taken to do so
	public static PuzzlePriorityQueue benchmark( PuzzleStack puzzles ){
		long time = 0 ;
		int total = puzzles.size();
		PuzzlePriorityQueue queue = new PuzzlePriorityQueue(Math.max(100,total),Math.max(100,total), false);
		
		JFrame frame = null ;
		if( enableGraphics ){
			frame = new JFrame();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setResizable(true);
		}
		while(!puzzles.isEmpty()){
			time += solvePuzzle( frame, queue, puzzles.pop() );
		}
		System.out.printf("Solving %d puzzles took %d ms.", total,  time );
		if( total != 0 )
			System.out.println("That gives us an average of " + (time/total)
								+ " ms per puzzle.");
		if( enableGraphics )	
			frame.dispose();
		return queue ;
	}
	//solves a single puzzle and returns the time it took to do so
	// time is returned in miliseconds
	public static long solvePuzzle( JFrame frame, PuzzlePriorityQueue queue, Puzzle puzzle ){
		
		PuzzleGraphic graphic = null ;
		if( frame != null )
			graphic = setUpFrame( frame, puzzle );
		
		long time = System.nanoTime();
		MoveStack stack = puzzle.solution();
		puzzle.setName("Puzzle "+ (queue.size() +1) );
		queue.push(puzzle, stack.size());
		time = System.nanoTime() - time ;
		time = time/1000000 ;
		if( puzzle.solved() ){
			System.out.printf( "%s is already solved! Time spent %d ms.\n",
								puzzle.getName(), time);
		} else if( stack.isEmpty() ){
			System.out.printf( "%s is unsolvable! Time spent %d ms.\n",
								puzzle.getName(), time);
			
		} else {
			System.out.printf( "%s is solved in %d steps. Time taken %d ms.\n",
								puzzle.getName(), stack.size(), time );
			if( frame != null )
				graphic.move(stack);
		}
		if( frame != null )
			closeFrame( frame, graphic );
		return time ;
	}
	//sets up frame and graphic for the graphic solving of the puzzle
	// returns PuzzleGraphic ready to be solved
	private static PuzzleGraphic setUpFrame( JFrame frame, Puzzle puzzle ){
		frame.setVisible(false);
		PuzzleGraphic graphic = puzzle.getGraphic();
		frame.setSize(graphic.graphicWidth() + 16, graphic.graphicHeight() + 38);
		frame.setTitle(puzzle.getName());
		frame.add(graphic);
		frame.setVisible(true);
		return graphic ;
	}
	// closes frame and makes it ready for next puzzle
	private static void closeFrame( JFrame frame, PuzzleGraphic graphic ){
		frame.setVisible(false);
		frame.remove( graphic );
	}
}
