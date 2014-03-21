package nl.mprog.BrickSlide10196129.brickslide.app;

import android.util.Log;

import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import RushHourSolver.Move;
import RushHourSolver.Puzzle;
import nl.mprog.BrickSlide10196129.brickslide.app.ResourceLoader.Values;

import RushHourSolver.Car;

/**
 * Created by hroosterhuis on 3/20/14.
 */
public class Brick extends Sprite {

    private Car car ;
    private int index ;
    private TouchHandler handler ;

    Brick(int index, Car car, Values colour, Puzzle puzzle, ResourceLoader resourceLoader, TouchHandler handler, VertexBufferObjectManager vertexBufferObjectManager){
        super(handler.gridX(car.getx()), handler.gridY(car.gety()), resourceLoader.getCar(colour, alignment(car), car.getSize()),
                vertexBufferObjectManager);
        this.car    = car ;
        this.index  = index  ;
        this.handler= handler ;
    }

    public Car getCar(){
        return  car ;
    }
    public int getCarIndex(){
        return  index ;
    }

    @Override
    public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
        return handler.onTouched(this,pSceneTouchEvent);
    }

    public static Values alignment(Car car){
        if(car.isHorizontal())
            return Values.HORIZONTAL ;
        return Values.VERTICAL ;
    }

    /**
     * Moves brick according to move, does not check whether move is a legal move.
     * No other bricks are blocked during movement, used for magnetic touching.
     * @param move (Legal) Move to be performed.
     */
    public void snapSprite(MainActivity activity, Move move){

       /*MoveModifier entityModifier = new BrickSequenceModifier(activity, this, move);

       registerEntityModifier(entityModifier);*/
    }

}
