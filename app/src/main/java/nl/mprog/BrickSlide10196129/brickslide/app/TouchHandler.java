package nl.mprog.BrickSlide10196129.brickslide.app;

import org.andengine.input.touch.TouchEvent;

import RushHourSolver.Car;
import RushHourSolver.Move;
import RushHourSolver.Puzzle;

/**
 * Created by hroosterhuis on 3/20/14.
 */
public class TouchHandler {

    private static int BLOCK_SIDE = 101;
    private static int FIELD_X = 60 ;
    private static int FIELD_Y = 305 ;

    private float startX, startY;
    private boolean moving ;
    private Brick brick;
    private Puzzle puzzle ;

    TouchHandler(Puzzle puzzle){
        this.puzzle = puzzle ;
    }

    public synchronized boolean startMove(Brick brick, TouchEvent pSceneTouchEvent){
        if(moving && brick != brick)
            return false;
        moving        = true ;
        this.brick    =  brick;

        startX = pSceneTouchEvent.getX();
        startY = pSceneTouchEvent.getY();

        return true ;
    }

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

                if (puzzle.move(new Move(brick.getCarIndex(), mov))) {
                    brick.setPosition(gridX(endX), gridY(endY));
                } else {
                    brick.setPosition(gridX(car.getx()), gridY(car.gety()));
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

    public int minGridX(Car car){
        if(car.isVertical())
            return car.getx();

        int[][] grid = puzzle.getBoard().getBoardArray();
        for(int i = car.getx()-1 ; i >= 0 ; i--)
            if(grid[i][car.gety()] != 0)
                return i+1;

        return 0 ;
    }

    public int maxGridX(Car car){
        if(car.isVertical())
            return car.getx();

        int[][] grid = puzzle.getBoard().getBoardArray();
        for(int i = car.getx() + car.getSize() ; i < 6 ; i++)
            if(grid[i][car.gety()] != 0)
                return i-car.getSize();

        return 6-car.getSize();
    }

    public int minGridY(Car car){
        if(car.isHorizontal())
            return car.gety();

        int[][] grid = puzzle.getBoard().getBoardArray();
        for(int i = car.gety()-1 ; i >= 0 ; i--)
            if(grid[car.getx()][i] != 0)
                return i+1;

        return 0 ;
    }

    public int maxGridY(Car car){
        if(car.isHorizontal())
            return car.gety();

        int[][] grid = puzzle.getBoard().getBoardArray();
        for(int i = car.gety() + car.getSize() ; i < 6 ; i++)
            if(grid[car.getx()][i] != 0)
                return i-car.getSize();

        return 6-car.getSize();
    }

    public static int gridX(int x){
        return FIELD_X + x*BLOCK_SIDE ;
    }
    public static int gridY(int y){
        return FIELD_Y + y*BLOCK_SIDE ;
    }



}
