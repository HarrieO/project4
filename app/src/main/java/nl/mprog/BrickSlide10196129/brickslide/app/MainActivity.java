package nl.mprog.BrickSlide10196129.brickslide.app;

import android.os.Bundle;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
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


    private ArrayList<Brick> carSprites ;
    private TouchHandler handler;

    private Puzzle puzzle ;


    private ResourceLoader resourceLoader ;

    @Override
    protected void onCreate(Bundle pSavedInstanceState){
        super.onCreate(pSavedInstanceState);
        carSprites = new ArrayList<Brick>();
        puzzle = new Puzzle("Level 1:8:1,2H2;0,0H2;5,0V3;0,1V3;3,1V3;0,4V2;4,4H2;2,5H3;");
        handler = new TouchHandler(this,puzzle);
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
    }

    @Override
    protected Scene onCreateScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());
        carSprites = new ArrayList<Brick>();

        final Scene scene = new Scene();
        Sprite backgroundSprite = new Sprite(0, 0, resourceLoader.getValue(Values.BACKGROUND), getVertexBufferObjectManager());
        scene.attachChild(backgroundSprite);

        Board board = puzzle.getBoard();

        Values colours[] = {Values.BLUE,Values.GREEN,Values.PURPLE};

        carSprites.add(createBrick(0,board.getCar(0), Values.RED));
        for(int i = 1 ; i < board.getTotalCars() ; i++){
            carSprites.add(createBrick(i, board.getCar(i), colours[i % colours.length]));
        }

        for(Sprite car : carSprites){
            scene.attachChild(car);
            scene.registerTouchArea(car);
        }

        scene.setTouchAreaBindingOnActionDownEnabled(true);

        return scene;
    }

    public void onGameCreated(){

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
    }

    public void enableBrickTouching(){
        Scene scene = getEngine().getScene();
        for(Brick brick : carSprites){
            scene.registerTouchArea(brick);
        }
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
                moves.start();
                return true;
            } else
                return false;
        }
        return true ;
    }
}
