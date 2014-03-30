package nl.mprog.BrickSlide10196129.brickslide.app.game;

import org.andengine.audio.sound.Sound;
import org.andengine.input.touch.TouchEvent;

import RushHourSolver.Car;
import RushHourSolver.Move;

/**
 * Handles the dragging and dropping of the bricks.
 * Created by hroosterhuis on 3/20/14.
 */
public class TouchHandler {

    public static int BLOCK_SIDE = 101;
    public static int FIELD_X = 60 ;
    public static int FIELD_Y = 305 ;

    // place where touch started
    private float startX, startY;
    private boolean moving ;
    private Brick brick;
    private MainActivity activity ;

    TouchHandler(MainActivity activity){
        this.activity = activity ;
    }

    /**
     * Method to start dragging sequence, returns false if another brick is already being dragged.
     */
    public synchronized boolean startMove(Brick brick, TouchEvent pSceneTouchEvent){
        if(moving && brick != brick)
            return false;
        moving        = true ;
        this.brick    =  brick;

        startX = pSceneTouchEvent.getX();
        startY = pSceneTouchEvent.getY();

        return true ;
    }

    public void playBump(){
        activity.getSoundHandler().bump.play();
    }

    /**
     * Returns true if brick is being moved.
     */
    public boolean hasMove(Brick brick){
        return moving && this.brick == brick ;
    }

    public synchronized void stopMove(Brick brick){
        if(this.brick == brick)
            moving = false ;
    }

    public float relativeX(TouchEvent pSceneTouchEvent){
        return (pSceneTouchEvent.getX() - startX) + gridX(brick.getCar().getx());
    }
    public float relativeY(TouchEvent pSceneTouchEvent){
        return (pSceneTouchEvent.getY() - startY) + gridY(brick.getCar().gety());
    }

    public boolean onTouched(Brick brick, TouchEvent pSceneTouchEvent) {

        if(pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN){
            startMove(brick,pSceneTouchEvent);
        } else if(hasMove(brick)) {
            Car car = brick.getCar();
            if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
                int endX = Math.round((brick.getX() - FIELD_X) / BLOCK_SIDE);
                int endY = Math.round((brick.getY() - FIELD_Y) / BLOCK_SIDE);

                int mov = endX - car.getx() ;
                if (car.isVertical())
                    mov = endY - car.gety();

                Move move = new Move(brick.getCarIndex(), mov);
                if (mov != 0 && activity.getPuzzle().move(move)) {
                    brick.snapSprite(endX, endY);
                    activity.updateMoveCounter();
                    if(activity.getPuzzle().winningMovePossible()){
                        activity.finishPuzzle();
                    }
                } else {
                    brick.snapSprite();
                }
                stopMove(brick);
            } else if(pSceneTouchEvent.getAction() == TouchEvent.ACTION_MOVE){
                // Max/Min structure for movement of bricks
                brick.setPosition(
                        Math.min(gridX(maxGridX(car)), Math.max(gridX(minGridX(car)),
                                relativeX(pSceneTouchEvent) )),
                        Math.min(gridY(maxGridY(car)), Math.max(gridY(minGridY(car)),
                                relativeY(pSceneTouchEvent) ))
                );
            }
        }
        return true;
    }
    // minimal x a car can be dragged
    public int minGridX(Car car){
        if(car.isVertical())
            return car.getx();

        int[][] grid = activity.getPuzzle().getBoard().getBoardArray();
        for(int i = car.getx()-1 ; i >= 0 ; i--)
            if(grid[i][car.gety()] != 0)
                return i+1;

        return 0 ;
    }
    // maximal x a car can be dragged
    public int maxGridX(Car car){
        if(car.isVertical())
            return car.getx();

        int[][] grid = activity.getPuzzle().getBoard().getBoardArray();
        for(int i = car.getx() + car.getSize() ; i < 6 ; i++)
            if(grid[i][car.gety()] != 0)
                return i-car.getSize();

        return 6-car.getSize();
    }
    // minimal y a car can be dragged
    public int minGridY(Car car){
        if(car.isHorizontal())
            return car.gety();

        int[][] grid = activity.getPuzzle().getBoard().getBoardArray();
        for(int i = car.gety()-1 ; i >= 0 ; i--)
            if(grid[car.getx()][i] != 0)
                return i+1;

        return 0 ;
    }
    // maximal y a car can be dragged
    public int maxGridY(Car car){
        if(car.isHorizontal())
            return car.gety();

        int[][] grid = activity.getPuzzle().getBoard().getBoardArray();
        for(int i = car.gety() + car.getSize() ; i < 6 ; i++)
            if(grid[car.getx()][i] != 0)
                return i-car.getSize();

        return 6-car.getSize();
    }
    // converts x on grid to screen x
    public static int gridX(int x){
        return FIELD_X + x*BLOCK_SIDE ;
    }
    // converts y on grid to screen y
    public static int gridY(int y){
        return FIELD_Y + y*BLOCK_SIDE ;
    }
    // x of nearest grid block
    public static float nearestXSnap(float x){
        return Math.round((x - FIELD_X) / BLOCK_SIDE)*BLOCK_SIDE + FIELD_X ;
    }
    // y of nearest grid block
    public static float nearestYSnap(float y){
        return Math.round((y - FIELD_Y) / BLOCK_SIDE)*BLOCK_SIDE + FIELD_Y ;
    }

}
