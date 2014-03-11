/*********************************
 * H.R. Oosterhuis - 10196129
 * Marysia Winkels - 10163727
 * Kunstmatige Intelligentie
 * Datastructuren 2012-2013
 * Project Opdracht
 *********************************/

import java.io.BufferedReader;
import java.io.FileReader;


/*
 * PuzzleReader contains the single static function read
 * that takes a textfile and reads puzzles from it
 * 
 * returns found puzzles in a PuzzleStack
 * 
 * if an error occurs it is printed to the user
 * if it disables further reading all the puzzles read so far
 * are returned in a PuzzleStack
 */
public abstract class PuzzleReader {
	public static PuzzleStack read( String textFile ){
		PuzzleStack stack = new PuzzleStack();
		try {
        	BufferedReader in = new BufferedReader(new FileReader(textFile));
        	String str, name, temp;
        	int height, width, read, exit, exitCar ;
        	Board.Wall exitWall ;
        	Board board ;
            while ((str = in.readLine()) != null) {
            	read = 0 ;
            	name = str;
            	str = in.readLine();
            	temp = "";
            	while(String.valueOf(str.charAt(read)).matches("\\d")){
            		temp = temp + String.valueOf(str.charAt(read++));
            	}
            	height = Integer.parseInt(temp);
            	read++;
            	temp = "";
            	while(String.valueOf(str.charAt(read)).matches("\\d")){
            		temp = temp + String.valueOf(str.charAt(read++));
            	}
            	width = Integer.parseInt(temp);
            	read++;
            	temp = String.valueOf(str.charAt(read));
            	if(temp.contentEquals("B")){
            		exitWall = Board.Wall.BOTTOM ;
            		read += 6;
            	} else if(temp.contentEquals("T")){
            		exitWall = Board.Wall.TOP ;
            		read += 3 ;
            	} else if(temp.contentEquals("L")){
            		exitWall = Board.Wall.LEFT ;
            		read += 4 ;
            	} else {
            		exitWall = Board.Wall.RIGHT ;
            		read += 5 ;
            	} 
            	temp = "";
            	while(String.valueOf(str.charAt(read)).matches("\\d")){
            		temp = temp + String.valueOf(str.charAt(read++));
            	}
            	exit = Integer.parseInt(temp);
            	read++;
            	temp = "";
            	while(read < str.length() && String.valueOf(str.charAt(read)).matches("\\d")){
            		temp = temp + String.valueOf(str.charAt(read++));
            	}
            	exitCar = Integer.parseInt(temp) + 1;
            	
            	
            	if( exit < 0
                || (exit > width
            	&& (exitWall.equals(Board.Wall.TOP) || exitWall.equals(Board.Wall.BOTTOM)))
                || (exit > height
                && (exitWall.equals(Board.Wall.LEFT)|| exitWall.equals(Board.Wall.RIGHT))))
            	{
            		System.out.printf("%s has an invalid exit.\n", name);
            		break;
            	} else if( height < 0 || width < 0 ){
            		System.out.printf("%s has an invalid size.\n", name);
            	}
            	
            	board = new Board(height,width,exit,exitWall);
            	
            	
            	int field[][] = new int[height][width];
            	for( int y = 0 ; y < height ; y++ ){
            		str = in.readLine();
            		temp = "" ;
            		read = 0 ;
            		int x = 0 ;
            		while( x < width ){
            			if(String.valueOf(str.charAt(read)).matches("\\d")){
            				temp = temp + str.charAt(read);
            			} else if(temp.matches("\\d+")){
            				field[x][y] = Integer.parseInt(temp) + 1;
            				temp = "";
            			}
            			if(String.valueOf(str.charAt(read)).contentEquals(".")){
            				x++;
            			}
            			read++;
            		}
            	}
            	int size ;
            	boolean horizontal = false ;
            	for( int y = 0 ; y < height ; y++ ){
            		for( int x = 0 ; x < width ; x++ ){
            			size = 0;
            			if(field[x][y] != 0 ){
                			if(x > 0 && field[x-1][y] == field[x][y]){
                				continue;
                			} else if(y > 0 && field[x][y-1] == field[x][y]){
                				continue ;
                			} else if(x < width-1 && field[x+1][y] == field[x][y]){
                				size = 2;
                				horizontal = true ;
                				while(x + size  < width   && field[x+size][y] == field[x][y]){
                					size++;
                				}
                			} else if(y < height-1 && field[x][y+1] == field[x][y]){
                				size = 2;
                				horizontal = false ;
                				while(y + size < height   && field[x][y+size] == field[x][y]){
                					size++;
                				}
                			}
                			if( field[x][y] == exitCar ){
                				board.addExitCar(new Car(x,y,size,horizontal));
                			} else {
                				board.addCar(new Car(x,y,size,horizontal));
                			}
                		}
                	}
            	}
            	if(board.getTotalCars() <= 0 ){
            		System.out.printf("%s is an empty board.\n", name);
            	} else if(board.getExitCar() < 0 ||
            			  board.getExitCar() >= board.getTotalCars()){
            		System.out.printf("%s has no car that can exit.\n", name);
            	} else {
            		stack.push(new Puzzle(board, name));
            	}
            	
            }
            
            in.close();
        } catch ( java.lang.Exception e){
        	System.out.println("\nThere was a problem while reading your file!");
            if( stack.size() > 0 ){
            	System.out.println("The program was able to read " + stack.size() + " puzzles before an error occured.");
            } else {
            	System.out.println("The program was unable to read any puzzles.");
            }
        }
		
		return stack ;
	}
	public static PuzzlePriorityQueue readAndQueue( String textFile ){
		PuzzleStack stack = read(textFile);
		return RushHourSolver.benchmark(stack);
	}
}
