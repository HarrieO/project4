package nl.mprog.BrickSlide10196129.brickslide.app.game;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveModifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import RushHourSolver.Move;

/**
 * Class for the animation of several on screen moves.
 * Created by hroosterhuis on 3/22/14.
 */
public class MoveSequencer {

    private MainActivity     activity ;
    private ArrayList<Brick> bricks   ;
    private Queue<Move> moves    ;
    private boolean active, dropSound;

    public MoveSequencer(MainActivity activity, ArrayList<Brick> bricks, Queue<Move> moves){
        this.activity = activity ;
        this.bricks   = bricks   ;
        this.moves    = moves;
        this.active   = false;
        this.dropSound = true ;
    }

    public MoveSequencer(MainActivity activity, ArrayList<Brick> bricks){
       this(activity,bricks,new LinkedList<Move>());
    }

    /**
     * Adds a move at the end of the sequence
     */
    public void addMove(Move move){
        moves.offer(move);
    }

    /**
     * Disables sound when brick is dropped. (this is done for the final move)
     */
    public void setDropSound(Boolean dropSound){
        this.dropSound = dropSound;
    }

    public synchronized void start(){
        if(!active) {
            active = true;
            activity.disableBrickTouching();
            nextMove();
        }
    }

    private void nextMove(){
        if(!moves.isEmpty()){
            Move move = moves.poll();
            Brick brick = bricks.get(move.getCarNumber());
            float startx = brick.getX();
            float starty = brick.getY();

            float endx   = startx;
            float endy   = starty;
            if(brick.getCar().isHorizontal())
                endx = TouchHandler.nearestXSnap(startx + move.getMovement()*TouchHandler.BLOCK_SIDE);
            else
                endy = TouchHandler.nearestYSnap(starty + move.getMovement() * TouchHandler.BLOCK_SIDE);

            double distance = Math.sqrt(Math.pow(endx-startx,2)+Math.pow(endy-starty,2));
            float duration = (float)Math.max(0.05,distance/800);

            SingleMove next = new SingleMove(duration,startx,endx,starty,endy);
            brick.registerEntityModifier(next);

        // upon finishing control is given back to the player
        } else if(!activity.finishPuzzle())
            activity.enableBrickTouching();
    }

    /**
     * Class for a single move
     */
    private class SingleMove extends MoveModifier{

        public SingleMove(float pDuration, float pFromX, float pToX, float pFromY, float pToY) {
            super(pDuration, pFromX, pToX, pFromY, pToY);
        }

        /**
         * Upon finishing the next move is initiated.
         */
        @Override
        protected void onModifierFinished(IEntity pItem)
        {
            super.onModifierFinished(pItem);
            nextMove();
            if(dropSound && activity.getSoundHandler().bump != null) {
                activity.getSoundHandler().bump.play();
            }
        }
    }

}
