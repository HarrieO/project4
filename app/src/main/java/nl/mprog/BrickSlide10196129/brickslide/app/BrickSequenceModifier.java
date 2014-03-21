package nl.mprog.BrickSlide10196129.brickslide.app;

import android.text.method.Touch;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveModifier;

import java.util.ArrayList;
import java.util.Queue;
import java.util.Stack;
import java.util.logging.Handler;

import RushHourSolver.Move;

/**
 * Created by hroosterhuis on 3/21/14.
 */
public class BrickSequenceModifier extends MoveModifier{


   private MainActivity        activity ;
    private boolean             disable ;
    private BrickSequenceModifier next ;
    private Brick               nextBrick ;
    private Move move ;


    /**
     * Single brick move, that does not disable other bricks.
     * Meant for half moves, to enable magnetic movements.
     * @param brick
     * @param move
     */
    BrickSequenceModifier(MainActivity activity, Brick brick, Move move){
        super(duration(brick,move),brick.getX(),moveEndX(brick,move),brick.getY(),moveEndY(brick, move));
        this.disable = false ;
        this.next    = null ;
        this.activity = activity ;
        this.nextBrick = null ;
        this.move      = move ;
    }

    /**
     * Last brick move, or complete single brick move ;
     * @param activity
     * @param brick
     * @param move
     * @return
     */
    public static BrickSequenceModifier blockingModifier(MainActivity activity, Brick brick, Move move){
        BrickSequenceModifier mod = new BrickSequenceModifier(activity, brick, move);
        mod.disable = true ;
        return mod ;
    }

    private static BrickSequenceModifier firstModifier(MainActivity activity, Brick nextBrick, BrickSequenceModifier next, Brick brick, Move move){
        BrickSequenceModifier mod = new BrickSequenceModifier(activity, nextBrick, next, brick, move);
        mod.disable = true ;
        return mod ;
    }

    /**
     * Chain move of sequence, that is followed by another move.
     * @param brick
     * @param move
     */
    private BrickSequenceModifier(MainActivity activity, Brick nextBrick, BrickSequenceModifier next, Brick brick, Move move){
        this(activity, brick, move);
        this.next    = next ;
        this.nextBrick = nextBrick ;
    }

    public void start(Brick brick){
        brick.registerEntityModifier(this);
    }

    @Override
    protected void onModifierStarted(IEntity pItem)
    {
        super.onModifierStarted(pItem);
        if(disable)
            activity.disableBrickTouching();

    }

    @Override
    protected void onModifierFinished(IEntity pItem)
    {
        super.onModifierFinished(pItem);

        if(next != null)
            nextBrick.registerEntityModifier(next);
        else if(disable){
            if(!activity.finishPuzzle()){
                activity.enableBrickTouching();
            }
        }
    }

    /**
     * Moves in stack should be in reverse order, (last move on top)
     * @param activity
     * @param bricks
     * @param moves
     * @return
     */
    public static SequenceStarter makeModifier(MainActivity activity, ArrayList<Brick> bricks, Stack<Move> moves){

        Move move = moves.pop();
        Brick lastBrick = bricks.get(move.getCarNumber());
        BrickSequenceModifier last = blockingModifier(activity, lastBrick, move) ;

        if(moves.isEmpty())
            return new SequenceStarter(bricks.get(move.getCarNumber()), last);

        while(moves.size() > 1){
            move = moves.pop();
            last = new BrickSequenceModifier(activity, lastBrick, last, bricks.get(move.getCarNumber()), move) ;
            lastBrick = bricks.get(move.getCarNumber());
        }

        move = moves.pop();
        last = firstModifier(activity, lastBrick, last, bricks.get(move.getCarNumber()), move) ;
        lastBrick = bricks.get(move.getCarNumber());

        return new SequenceStarter(lastBrick, last);

    }

    public static float moveEndX(Brick brick, Move move){
        if(brick.getCar().isVertical())
            return brick.getX();
        else
            return TouchHandler.gridX(brick.getCar().getx() + move.getMovement());
    }

    public static float moveEndY(Brick brick, Move move){
        if(brick.getCar().isHorizontal())
            return brick.getY();
        else
            return TouchHandler.gridY(brick.getCar().gety() + move.getMovement());
    }

    public static float duration(Brick brick, Move move){
        double total = Math.sqrt(Math.pow(brick.getX()-moveEndX(brick,move),2) +
                                 Math.pow(brick.getY()-moveEndY(brick,move),2));
        return (float) Math.max(0.05,total/800);
    }

    public static class SequenceStarter{
        private Brick car ;
        private BrickSequenceModifier sequenceModifier ;

        private SequenceStarter(Brick firstCar, BrickSequenceModifier firstModifiers){
            car = firstCar ;
            sequenceModifier = firstModifiers ;
        }

        public void run(){
            car.registerEntityModifier(sequenceModifier);
        }
    }


}
