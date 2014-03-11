import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
 * 
 * it uses BoardGenerator1 to generate puzzles and writes them to files
 */

public abstract class PuzzleWriter {
	public static boolean generateLevelFile( String fileName, int totalLevels, int height, int width, int tries){
		PuzzlePriorityQueue queue = BoardGenerator1.generatePuzzles( totalLevels, height, width, 0);
		return generateLevelFile( fileName, queue, height, width, tries) ;
	}
	public static boolean generateLevelFile( String fileName, int totalLevels, PuzzleStack stack, int height, int width, int tries){
		System.out.println("Starting with " + stack.size() + " puzzles from last time.");
		PuzzlePriorityQueue queue = BoardGenerator1.generatePuzzles( stack, totalLevels, height, width, 0);
		return generateLevelFile( fileName, queue, height, width, tries) ;
	}
	public static boolean generateLevelFile( String fileName, PuzzlePriorityQueue queue, int height, int width, int tries){
		int i = 0;
		while(tries > 1000){
			queue = BoardGenerator1.generatePuzzles( queue, height, width, 1000);
			System.out.print( i++ + " - range: " + queue.bottomPrior() + " -> " + queue.topPrior()  + " writing...");
			if(writeToFile( queue.copy(), fileName ))
				System.out.println( "done.");
			else
				System.out.println( "!! backup could not be made !!!");
			tries -= 1000;
		}
		queue = BoardGenerator1.generatePuzzles( queue, height, width, tries);
		System.out.println( i + " - range: " + queue.bottomPrior() + " -> " + queue.topPrior() );
		
		return writeToFile( queue, fileName ) ;
	}
	
	public static boolean writeToFile( PuzzlePriorityQueue queue, String fileName ){
		BufferedWriter writer = null;
		if( queue == null)
			return false ;
	
		try
		{
			writer = new BufferedWriter( new FileWriter( fileName ));
			while(!queue.isEmpty()){
				writer.write( queue.pop().savingFormat());
			}
		}
		catch ( IOException e){}
		finally
		{
			try
			{
				if ( writer != null)
					writer.close( );
			}
			catch ( IOException e)
			{
			}
	    }
		return queue.isEmpty() ;
	}
	
	public static void generateLevelFileInfinite( String fileName, int totalLevels, int height, int width){
		PuzzlePriorityQueue queue = BoardGenerator1.generatePuzzles( totalLevels, height, width, 0);
		generateLevelFileInfinite( fileName, queue, height, width) ;
	}
	public static void generateLevelFileInfinite( String fileName, int totalLevels, PuzzleStack stack, int height, int width){
		System.out.println("Starting with " + stack.size() + " puzzles from last time.");
		PuzzlePriorityQueue queue = BoardGenerator1.generatePuzzles( stack, totalLevels, height, width, 0);
		generateLevelFileInfinite( fileName, queue, height, width) ;
	}
	public static void generateLevelFileInfinite( String fileName, PuzzlePriorityQueue queue, int height, int width){
		int i = 0;
		while( true ){
			queue = BoardGenerator1.generatePuzzles( queue, height, width, 10000);
			System.out.print( i++ + " - range: " + queue.bottomPrior() + " -> " + queue.topPrior() + " ; average: " + queue.averagePrior()  + " writing...");
			if(writeToFile( queue.copy(), fileName ))
				System.out.println( "done.");
			else
				System.out.println( "!! backup could not be made !!!");
			
		}
	}
}
