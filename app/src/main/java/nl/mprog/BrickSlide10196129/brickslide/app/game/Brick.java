package nl.mprog.BrickSlide10196129.brickslide.app.game;

import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import RushHourSolver.Puzzle;

import RushHourSolver.Car;

/**
 * Created by hroosterhuis on 3/20/14.
 */
public class Brick extends Sprite {

    private Car car ;
    private int index ;
    private TouchHandler handler ;



    Brick(int index, Car car, ResourceLoader.Values colour, Puzzle puzzle, ResourceLoader resourceLoader, TouchHandler handler, VertexBufferObjectManager vertexBufferObjectManager){
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

    public static ResourceLoader.Values alignment(Car car){
        if(car.isHorizontal())
            return ResourceLoader.Values.HORIZONTAL ;
        return ResourceLoader.Values.VERTICAL ;
    }

    public void snapSprite(){
        snapSprite(getCar().getx(),getCar().gety());
    }
    /**
     * Snaps brick to x or y in pixels.
     */
    public void snapSprite(int x, int y){
        float startx = getX();
        float starty = getY();
        float endx   = TouchHandler.gridX(x);
        float endy   = TouchHandler.gridY(y);
        double distance = Math.sqrt(Math.pow(endx-startx,2)+Math.pow(endy-starty,2));
        float duration = (float)Math.max(0.05,distance/800);
        MoveModifier entityModifier = new MoveModifier(duration,startx,endx,starty,endy);
        registerEntityModifier(entityModifier);
        handler.playBump();
    }

}
