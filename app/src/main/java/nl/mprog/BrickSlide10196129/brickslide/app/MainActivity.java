package nl.mprog.BrickSlide10196129.brickslide.app;

import android.os.Bundle;
import android.widget.Toast;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.sprite.batch.SpriteGroup;
import org.andengine.entity.util.FPSLogger;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.engine.options.EngineOptions;
import org.andengine.entity.scene.Scene;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;

import org.andengine.util.debug.Debug;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import org.andengine.entity.sprite.Sprite;


import RushHourSolver.Board;
import RushHourSolver.Car;
import RushHourSolver.Move;
import RushHourSolver.MoveStack;
import RushHourSolver.Puzzle;
import nl.mprog.BrickSlide10196129.brickslide.app.ResourceLoader.Values;

public class MainActivity extends SimpleBaseGameActivity {

    private static int CAMERA_WIDTH = 720;
    private static int CAMERA_HEIGHT = 1280;

    private Sound exit ;
    private Sound bash ;

    private int puzzleIndex = 0 ;

    private String[] puzzles = {"Level 1:8:1,2H2;0,0H2;5,0V3;0,1V3;3,1V3;0,4V2;4,4H2;2,5H3;",
            "Level 2:8:0,2H2;0,0V2;3,0H3;3,1V2;5,1V3;4,2V2;0,3H3;2,4V2;4,4H2;0,5H2;3,5H2;",
            "Level 3:14:1,2H2;3,2V3;1,3H2;5,3V3;1,4V2;2,5H2;",
            "Level 4:9:1,2H2;0,0V3;3,0V3;2,3V2;3,3H3;5,4V2;2,5H3;",
            "Level 5:9:1,2H2;0,0H2;3,0V3;5,0V2;0,1V3;4,1V3;5,2V2;1,3H3;0,4V2;4,4H2;4,5H2;",
            "Level 6:9:1,2H2;0,0H2;3,0V2;0,1H2;4,1V3;5,1V3;3,2V3;0,3H2;2,3V2;0,4V2;3,5H3;",
            "Level 7:13:1,2H2;1,0V2;2,0H2;4,0V2;5,0V2;3,1V2;5,2V2;2,3H2;3,4V2;",
            "Level 8:12:0,2H2;3,0H2;5,0V3;2,1H2;4,1V2;2,2V2;3,2V2;0,3H2;4,3H2;0,4H2;2,4V2;3,4H3;0,5H2;3,5H3;",
            "Level 9:12:0,2H2;1,0V2;2,0H2;4,0H2;3,1V2;4,1H2;4,2V3;5,2V2;0,3V3;1,3H3;2,4V2;5,4V2;",
            "Level 10:14:1,2H2;0,0H2;2,0V2;4,0H2;0,1H2;4,1H2;0,2V3;5,2V3;1,3H3;3,4V2;0,5H2;4,5H2;",
            "Level 11:25:1,2H2;0,0V3;1,0H2;3,0V3;2,3V2;3,3H3;5,4V2;2,5H3;",
            "Level 12:17:0,2H2;0,0V2;1,0H2;5,0V3;2,1V3;3,3H3;4,4V2;0,5H3;",
            "Level 13:16:3,2H2;0,0H2;2,0H2;4,0V2;2,1V2;5,1V3;1,2V2;0,3V3;3,3H2;3,4V2;4,4H2;1,5H2;4,5H2;",
            "Level 14:17:2,2H2;0,0H2;2,0V2;4,1H2;0,2V2;1,2V2;4,2V2;5,2V2;2,3H2;2,4V2;4,4H2;0,5H2;",
            "Level 15:23:2,2H2;1,0H2;3,0H2;0,1H2;2,1H2;4,1V3;5,1V3;0,2V3;1,2V3;2,3V2;3,3V2;4,4H2;1,5H2;3,5H2;"};


    private ArrayList<Brick> carSprites ;
    private TouchHandler handler;

    private Puzzle puzzle ;

    private Sprite upSlide ;
    private Sprite downSlide ;
    private ButtonSprite undo, restart, skip ;


    private ResourceLoader resourceLoader ;

    @Override
    protected void onCreate(Bundle pSavedInstanceState){
        super.onCreate(pSavedInstanceState);
        carSprites = new ArrayList<Brick>();
        puzzle = new Puzzle("Level 1:8:1,2H2;0,0H2;5,0V3;0,1V3;3,1V3;0,4V2;4,4H2;2,5H3;");
        handler = new TouchHandler(this);
    }

    @Override
    protected void onCreateResources() {
        resourceLoader = new ResourceLoader(this.getTextureManager(), this.getAssets());
        try {
            resourceLoader.loadAll();
        } catch (IOException e) {
            Debug.e(e);
        }

        SoundFactory.setAssetBasePath("mfx/");
        try {
           handler.setBump(SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "Thip.ogg"));
           handler.bump.setVolume(50);
        } catch (final IOException e) {
            //Debug.e(e);
        }
        try {
           exit = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "Exit.ogg");
        } catch (final IOException e) {
           exit = null ;
            //Debug.e(e);
        }
        try {
            bash = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "shortbang.ogg");
        } catch (final IOException e) {
            bash = null ;
            //Debug.e(e);
        }
    }

    @Override
    protected Scene onCreateScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());
        carSprites = new ArrayList<Brick>();

        final Scene scene = new Scene();
        Sprite backgroundSprite = new Sprite(0, 0, resourceLoader.getValue(Values.BACKGROUND), getVertexBufferObjectManager());
        scene.attachChild(backgroundSprite);

        undo = ButtonSprite.undoSprite(this, resourceLoader, getVertexBufferObjectManager());
        scene.attachChild(undo);
        scene.registerTouchArea(undo);
        restart = ButtonSprite.restartSprite(this, resourceLoader, getVertexBufferObjectManager());
        scene.attachChild(restart);
        scene.registerTouchArea(restart);
        skip = ButtonSprite.skipSprite(this, resourceLoader, getVertexBufferObjectManager());
        scene.attachChild(skip);
        scene.registerTouchArea(skip);

        initBricks(scene);



        upSlide = new Sprite(0, -650, resourceLoader.getValue("slide"), getVertexBufferObjectManager());

        downSlide = new Sprite(0, 1290, resourceLoader.getValue("slide"), getVertexBufferObjectManager());
        scene.attachChild(upSlide);
        scene.attachChild(downSlide);


        scene.setTouchAreaBindingOnActionDownEnabled(true);

        return scene;
    }

    public void initBricks(final Scene scene) {
        Board board = puzzle.getBoard();

        Values colours[] = {Values.BLUE, Values.GREEN, Values.PURPLE};

        carSprites.add(createBrick(0, board.getCar(0), Values.RED));
        for (int i = 1; i < board.getTotalCars(); i++) {
            carSprites.add(createBrick(i, board.getCar(i), colours[i % colours.length]));
        }

        for (Sprite car : carSprites) {
            scene.attachChild(car);
        }


    }
    public void setPuzzle(final Scene scene){
        this.runOnUpdateThread(new Runnable()
        {
            @Override
            public void run()
            {
                while(!carSprites.isEmpty()){
                    Brick rem =  carSprites.remove(carSprites.size() - 1);
                    scene.detachChild(rem);
                    scene.unregisterTouchArea(rem);
                    rem.detachSelf();
                    rem.dispose();

                }
                initBricks(scene);
                scene.detachChild(upSlide);
                scene.detachChild(downSlide);
                scene.attachChild(upSlide);
                scene.attachChild(downSlide);
            }
        });

    }

    public void onGameCreated(){
        super.onGameCreated();
        enableBrickTouching();
        /*MoveSequencer moves = new MoveSequencer(this,carSprites);
        for(int i = 0 ; i < 2000 ; i++){
            Move move = puzzle.getBoard().possibleMoves().randomPeek();
            moves.addMove(move);
            puzzle.move(move);
        }
        moves.start();
        */

    }

    public Brick createBrick(int index, Car car, Values colour){
        return new Brick(index, car,colour, puzzle,resourceLoader,handler,getVertexBufferObjectManager());
    }

    @Override
    public EngineOptions onCreateEngineOptions() {
        final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        EngineOptions options = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED,
                new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
        options.getAudioOptions().setNeedsSound(true);
        return options ;
    }

    public void disableBrickTouching(){
        Scene scene = getEngine().getScene();
        for(Brick brick : carSprites){
            scene.unregisterTouchArea(brick);
        }
        scene.unregisterTouchArea(undo);
        scene.unregisterTouchArea(restart);
        scene.unregisterTouchArea(skip);
    }

    public void enableBrickTouching(){

        Scene scene = getEngine().getScene();
        for(Brick brick : carSprites){
            scene.registerTouchArea(brick);
        }
        scene.registerTouchArea(undo);
        scene.registerTouchArea(restart);
        scene.registerTouchArea(skip);
    }


    /**
     * Does not disable touching.
     * @param move
     * @return
     */
    public boolean move(Move move){
        if(puzzle.move(move)){
            //carSprites.get(move.getCarNumber()).snapSprite(this, move);
            return true ;
        }
        return false ;
    }

    public Puzzle getPuzzle(){
        return puzzle ;
    }

    public boolean finishPuzzle(){
        if(!puzzle.solved()) {
            Move winningMove = puzzle.winningMove();
            if (puzzle.getBoard().move(winningMove)) {
                disableBrickTouching();
                MoveSequencer moves = new MoveSequencer(this,carSprites);
                Move epicEndingMove = new Move(winningMove.getCarNumber(), winningMove.getMovement()+3);
                moves.addMove(epicEndingMove);
                exit.play();
                moves.setDropSound(false);
                moves.start();

                return true;
            } else
                return false;
        } else {
            DelayHandler.delayed(1000, new Runnable() {
                @Override
                public void run() {
                    startTransition();

                }
            });
        }
        return true ;
    }

    public void undo(){
        undo(1);
    }

    public void restart(){
        undo(puzzle.moveCount());
    }

    public void undo(int steps){
        MoveSequencer seq = new MoveSequencer(this,carSprites);
        Move undone = puzzle.undo() ;
        while(undone != null && steps-- > 0){
            seq.addMove(undone);
            if(steps > 0)
                undone = puzzle.undo() ;
        }
        seq.start();
    }

    public void skip(){
        disableBrickTouching();
        DelayHandler.delayed(0, new Runnable() {
            @Override
            public void run() {
                safeSkip();
            }
        });

    }

    private void safeSkip(){
        Toast message = Toast.makeText(this,"Calculating solution.", Toast.LENGTH_LONG);
        MoveStack sol = puzzle.solution();
        MoveSequencer seq = new MoveSequencer(this, carSprites);
        if(sol.isEmpty())
            return;
        while(!puzzle.winningMovePossible()) {
            Move mov = sol.pop();
            seq.addMove(mov);
            puzzle.move(mov);
        }
        seq.start();
        message.cancel();
    }

    private void startTransition(){
        disableBrickTouching();
        upSlide.registerEntityModifier(new MoveModifier(0.3f,0,0,-650,0) {
            @Override
            protected void onModifierFinished(final IEntity pItem)
            {
                super.onModifierFinished(pItem);
                DelayHandler.delayed(100, new Runnable() {
                    @Override
                    public void run() {
                        puzzle = new Puzzle(puzzles[++puzzleIndex % puzzles.length]);
                        setPuzzle(getEngine().getScene());
                    }
                });
                DelayHandler.delayed(1000, new Runnable() {
                    @Override
                    public void run() {
                        pItem.registerEntityModifier(new MoveModifier(1f, 0, 0, 0, -650));
                    }
                });
            }
        });
        downSlide.registerEntityModifier(new MoveModifier(0.3f,0,0,1290,640){
            @Override
            protected void onModifierFinished(final IEntity pItem)
            {
                super.onModifierFinished(pItem);
                DelayHandler.delayed(1000, new Runnable() {
                    @Override
                    public void run() {
                        pItem.registerEntityModifier(new MoveModifier(1f,0,0,640,1290){
                            protected void onModifierFinished(final IEntity pItem) {
                                super.onModifierFinished(pItem);
                                enableBrickTouching();
                            }
                        });
                    }
                });

            }
        });
        
        DelayHandler.delayed((int)(1000*0.17),new Runnable() {
            @Override
            public void run() {
                bash.play();
            }
        });
    }
}
