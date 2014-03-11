/*********************************
 * H.R. Oosterhuis - 10196129
 * Marysia Winkels - 10163727
 * Kunstmatige Intelligentie
 * Datastructuren 2012-2013
 * Project Opdracht
 *********************************/
import java.awt.*;
/*
 * implements graphic interface for puzzle
 * this class should be considered an extension of the PuzzleSolver
 * and is not part of the main Solver
 */
public class PuzzleGraphic extends Canvas{
	// default value to avoid warnings
	private static final long serialVersionUID = 1L;
	// variables used for drawing and animating
	public static int blockSize = 80, wallSize = 15, carDiff = 5, moveStep = 4,
					   framerate = 36 ;
	public static Color colourCycle[] = { Color.blue, Color.cyan, Color.green,
		Color.magenta, Color.orange, Color.pink, Color.white, Color.yellow },
		exitCarColor = Color.red ;
	
	private int height, width, exit, amountOfCars, progress, startColour ;
	private Board.Wall exitWall ;
	private Car cars[] ;
	private Board board ;
	private Move move ;
	
	public PuzzleGraphic( Board board ){
		this.height 		= board.getHeight() ;
		this.width 			= board.getWidth() ;
		this.exitWall 		= board.getExitWall() ;
		this.exit 			= board.getExit() ;
		this.cars			= board.getCars() ;
		this.amountOfCars 	= board.getTotalCars();
		this.board			= board ;
		this.move			= null ;
		this.progress		= 0 ;
		this.startColour	= (int) Math.floor(Math.random()*(double)colourCycle.length);
	}
	
	public int fieldHeight() 	{ return height ; 											}
	public int fieldWidth() 	{ return width ; 											}
	public int graphicHeight() 	{ return blockSize * height + wallSize * 2 ; 				}
	public int graphicWidth() 	{ return blockSize * width  + wallSize * 2 ; 				}
	public Dimension dimension(){ return new Dimension(graphicWidth(), graphicHeight());	}
	
	
	
	/*
	 * paints graphic if animation is to be played
	 * repaint() is called appropriately
	 */
	public void paint( Graphics g ){
		setSize(graphicWidth(), graphicHeight());
		setBackground(Color.gray);
		drawBorders( g );
		drawCars( g );
		if( move != null) {
			drawMovingCar( g );
			wait(1);
			if( progress == moveStep * Math.abs(move.getMovement()) ){
				if( board.won()){
					move = null;
				} else {
					board.move(move);
					if(board.won()){
						move = new Move(move.getCarNumber(),
								4 * Integer.signum(move.getMovement()) );
						progress = 0 ;
						repaint();
					} else
						move = null ;
				}
			} else
				repaint();
		}
	}
	// returns the colar a car should have
	private Color cycle( int carNumber ){
		if( carNumber == board.getExitCar() )
			return exitCarColor ;
		return colourCycle[ (startColour+carNumber)%colourCycle.length ];
	}
	
	private void drawBorders( Graphics g ){
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, graphicWidth(), wallSize);
		g.fillRect(0, graphicHeight() - wallSize, graphicWidth(), wallSize);
		g.fillRect(0, 0, wallSize, graphicHeight());
		g.fillRect(graphicWidth()-wallSize, 0, wallSize, graphicHeight());
		g.setColor(getBackground());
		if ( exitWall.equals(Board.Wall.RIGHT) ){
			g.fillRect(graphicWidth()-wallSize, wallSize+exit*blockSize, wallSize, blockSize);
		} else if( exitWall.equals(Board.Wall.LEFT) ){
			g.fillRect(0, wallSize+exit*blockSize, wallSize, blockSize);
		} else if( exitWall.equals(Board.Wall.TOP) ){
			g.fillRect(wallSize+exit*blockSize, 0, blockSize, wallSize);
		} else if( exitWall.equals(Board.Wall.BOTTOM) ){
			g.fillRect(wallSize+exit*blockSize, graphicHeight() - wallSize, blockSize, wallSize);
		} 
	}
	
	private void drawCars(Graphics g){
		int expt ;
		if( move != null){
			expt = move.getCarNumber() ;
		} else {
			expt = -1 ;
		}
		for( int i = 0; i < amountOfCars ; i++ ){
			if( i != expt && !(board.won() && move == null && i == board.getExitCar()) )
				drawCar( g, i );
		}
	}
	private void drawCar( Graphics g, int carNumber ){
		g.setColor( cycle(carNumber) );
		Car car = cars[carNumber];
		int x = wallSize + carDiff/2 + car.getx() * blockSize ;
		int y = wallSize + carDiff/2 + car.gety() * blockSize ;
		int carHeight = blockSize - carDiff ;
		int	carWidth  = blockSize - carDiff ;
		if(car.isHorizontal()){
			carWidth  = blockSize * car.getSize() - carDiff ;
		} else {
			carHeight = blockSize * car.getSize() - carDiff ;
		}
		g.fillRoundRect( x, y, carWidth, carHeight, 10, 10 );
	}
	
	private void wait( int steps ){
		try {
			Thread.sleep(Math.max(1, 1000/framerate * steps));
		} catch (InterruptedException e) {
		}
	}
	private void waitForMoveToFinish(){
		Move safe;
		while(move!=null){
			safe = move ;
			if( safe != null ){
				wait(moveStep * Math.abs(safe.getMovement()) - progress );
			}
		}
	}
	public void move( MoveStack moves ){
		while( !moves.isEmpty() ){
			move(moves.pop());
		}
	}
	public boolean move( Move move ){
		if(board.legalMove(move)){
			waitForMoveToFinish();
			this.move = move ;
			this.progress = 0 ;
			repaint();
			waitForMoveToFinish();
			return true ;
		}
		return false ;
	}
	
	private void drawMovingCar(Graphics g){
		g.setColor( cycle(move.getCarNumber()) );
		Car car = cars[move.getCarNumber()];
		double perc = (double)progress/(double)(moveStep * Math.abs(move.getMovement())) ;
		int x = wallSize + carDiff/2 + car.getx() * blockSize ;
		int y = wallSize + carDiff/2 + car.gety() * blockSize ;
		int carHeight = blockSize - carDiff ;
		int	carWidth  = blockSize - carDiff ;
		if(car.isHorizontal()){
			carWidth  = blockSize * car.getSize() - carDiff ;
			x 		 += blockSize * move.getMovement() * perc ;
		} else {
			carHeight = blockSize * car.getSize() - carDiff ;
			y 		 += blockSize * move.getMovement() * perc ;
		}
		g.fillRoundRect( x, y, carWidth, carHeight, 10, 10 );
		progress++;
	}
}
