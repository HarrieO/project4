package nl.mprog.BrickSlide10196129.brickslide.app.game;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Toast;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.engine.options.EngineOptions;
import org.andengine.entity.scene.Scene;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;

import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import java.io.IOException;
import java.util.ArrayList;

import org.andengine.entity.sprite.Sprite;


import RushHourSolver.Board;
import RushHourSolver.Car;
import RushHourSolver.Move;
import RushHourSolver.MoveStack;
import RushHourSolver.Puzzle;
import nl.mprog.BrickSlide10196129.brickslide.app.BrickSlideApplication;
import nl.mprog.BrickSlide10196129.brickslide.app.R;
import nl.mprog.BrickSlide10196129.brickslide.app.database.HighscoreDatabase;
import nl.mprog.BrickSlide10196129.brickslide.app.database.MovesDatabase;
import nl.mprog.BrickSlide10196129.brickslide.app.database.PuzzleDatabase.PuzzleCursor;

import nl.mprog.BrickSlide10196129.brickslide.app.game.ResourceLoader.Values;

/**
 * Main game activity
 */
public class MainActivity extends SimpleBaseGameActivity {

    private static int CAMERA_WIDTH = 720;
    private static int CAMERA_HEIGHT = 1280;
    private static long SKIP_TIME   = 1000*60*5;

    private HighscoreDatabase highscoredb ;
    private PuzzleCursor puzzleCursor ;
    private Thread solveThread ;

    private ArrayList<Brick> carSprites ;
    private TouchHandler handler;
    private SoundHandler soundHandler ;

    private Puzzle puzzle ;
    private Toast solveMessage, waitMessage ;

    private Sprite upSlide, downSlide ;
    private ButtonSprite undo, restart, skip, noskip ;

    private BitmapTextureAtlas mFontTexture;
    private Font mFont ;
    private Text puzzleTitle, moveCounter ;

    private long puzzleStarted, lastToast ;

    private boolean skipped ;

    private ResourceLoader resourceLoader ;

    private Sprite[] stars, nostars;

    @Override
    // Suppres string format matches because of a bug in android studio (v0.5.1)
    @SuppressLint("StringFormatMatches")
    protected void onCreate(Bundle pSavedInstanceState){
        super.onCreate(pSavedInstanceState);
        carSprites   = new ArrayList<Brick>();
        puzzleCursor = ((BrickSlideApplication)getApplication()).getPuzzleDatabase().getCursor();
        highscoredb  = ((BrickSlideApplication)getApplication()).getHighscoreDatabase();

        skipped = false ;

        lastToast = 0;

        //Load puzzle
        Intent intent = getIntent() ;
        //intent from level selection
        if(intent.hasExtra("Level")) {
            int id = intent.getIntExtra("Level", 0);
            puzzle = puzzleCursor.get(id);
            puzzleStarted = System.currentTimeMillis();
        //intent from main menu
        } else {
            SharedPreferences pref = getPreferences(MODE_PRIVATE);
            puzzleStarted = pref.getLong(getString(R.string.time_started), System.currentTimeMillis());
            int id = pref.getInt(getString(R.string.level_id_pref_key), -1);
            int moves = pref.getInt(getString(R.string.no_moves_pref_key), 0);
            if (id == -1)
                puzzle = puzzleCursor.get();
            else if (moves == 0)
                puzzle = puzzleCursor.get(id);
            else {
                puzzle = puzzleCursor.get(id);
                String state = pref.getString(getString(R.string.state_pref_key), "");
                if (state != "")
                    puzzle.setState(state, moves);
                MovesDatabase db = ((BrickSlideApplication)getApplication()).getMovesDatabase();
                if (db.movesSaved())
                    puzzle.setMoves(db.get());
            }
        }
        stars   = new Sprite[5];
        nostars = new Sprite[5];

        solveMessage = Toast.makeText(this,"Calculating solution...", Toast.LENGTH_LONG);
        waitMessage  = Toast.makeText(this,"", Toast.LENGTH_LONG);
    }

    @Override
    /**
     * Saves game state and stops solving thread
     */
    protected void onPause(){
        super.onPause();
        SharedPreferences.Editor pref = getPreferences(MODE_PRIVATE).edit();
        pref.putInt(getString(R.string.level_id_pref_key), puzzle.getId());
        pref.putInt(getString(R.string.no_moves_pref_key), puzzle.moveCount());
        pref.putString(getString(R.string.state_pref_key), puzzle.getBoard().getState());
        pref.putLong(getString(R.string.time_started), puzzleStarted);
        pref.commit();

        MovesDatabase db = ((BrickSlideApplication)getApplication()).getMovesDatabase();
        db.put(puzzle.movesString());

        if(solveThread != null)
            solveThread.interrupt();

    }

    @Override
    protected void onCreateResources() {
        resourceLoader = new ResourceLoader(this.getTextureManager(), this.getAssets());
        try {
            resourceLoader.loadAll();
        } catch (IOException e) {
            Debug.e(e);
        }

        handler = new TouchHandler(this);

        soundHandler = new SoundHandler(this);



        this.mFontTexture = new BitmapTextureAtlas(getTextureManager(),256, 256);

        this.mFont = new Font(getFontManager(),mFontTexture,Typeface.create(Typeface.DEFAULT, Typeface.BOLD),96, true, Color.WHITE);

        this.mEngine.getTextureManager().loadTexture(this.mFontTexture);

        this.getFontManager().loadFont(this.mFont);

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
        noskip = ButtonSprite.noskipSprite(this, resourceLoader, getVertexBufferObjectManager());
        scene.attachChild(noskip);
        scene.registerTouchArea(noskip);

        // make sure the skip will be available later
        setNoskipTimer();

        initBricks(scene);

        puzzleTitle = new Text(45, 170, this.mFont, puzzle.getName(), getVertexBufferObjectManager());
        moveCounter = new Text(500,170, this.mFont, String.valueOf(puzzle.moveCount()), 6, getVertexBufferObjectManager());
        scene.attachChild(puzzleTitle);
        scene.attachChild(moveCounter);

        moveCounter.setText(String.valueOf(Math.min(puzzle.moveCount(),999)));
        moveCounter.setX(680-moveCounter.getWidth());

        for(int i = 0; i < 5; i++){
            nostars[i] = new Sprite(0, 0, resourceLoader.getValue(Values.NOSTAR), getVertexBufferObjectManager());
            nostars[i].setScaleCenter(0, 0);
            nostars[i].setScale(0.065f);
            nostars[i].setPosition((CAMERA_WIDTH - nostars[i].getWidthScaled()*5)/2 + nostars[i].getWidthScaled()*i,
                    1170 - nostars[i].getHeightScaled()/2);
            scene.attachChild(nostars[i]);
        }

        int highscore = 0 ;
        if(highscoredb.hasScore(puzzle.getId())) {
            highscore = puzzle.getStars(highscoredb.get(puzzle.getId()));
        }

        for(int i = 0; i < 5; i++){
            stars[i] = new Sprite(0, 0, resourceLoader.getValue(Values.STAR), getVertexBufferObjectManager());
            stars[i].setScaleCenter(0, 0);
            stars[i].setScale(0.065f);
            stars[i].setPosition((CAMERA_WIDTH - stars[i].getWidthScaled()*5)/2 + stars[i].getWidthScaled()*i, 1170 - stars[i].getHeightScaled()/2);
            if(i < highscore)
                scene.attachChild(stars[i]);
        }

        upSlide   = new Sprite(0, -650, resourceLoader.getValue(Values.SLIDE), getVertexBufferObjectManager());
        downSlide = new Sprite(0, 1290, resourceLoader.getValue(Values.SLIDE), getVertexBufferObjectManager());
        scene.attachChild(upSlide);
        scene.attachChild(downSlide);

        scene.setTouchAreaBindingOnActionDownEnabled(true);

        return scene;
    }

    /**
     * places bricks of puzzle on scene
     * @param scene
     */
    public void initBricks(final Scene scene) {
        Board board = puzzle.getBoard();

        ResourceLoader.Values colours[] = {ResourceLoader.Values.BLUE, ResourceLoader.Values.GREEN, ResourceLoader.Values.PURPLE};

        carSprites.add(createBrick(0, board.getCar(0), ResourceLoader.Values.RED));
        for (int i = 1; i < board.getTotalCars(); i++) {
            carSprites.add(createBrick(i, board.getCar(i), colours[i % colours.length]));
        }

        for (Sprite car : carSprites) {
            scene.attachChild(car);
        }


    }

    /**
     * Changes on screen puzzle to private puzzle, used to change levels
     * @param scene
     */
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


                puzzleTitle.setText(puzzle.getName());

                updateMoveCounter();

                for(int i = 0; i < 5; i++){
                    nostars[i].clearEntityModifiers();
                    nostars[i].setPosition((CAMERA_WIDTH - nostars[i].getWidthScaled()*5)/2 + nostars[i].getWidthScaled()*i,
                            1170 - nostars[i].getHeightScaled()/2);

                }

                int highscore = 0 ;
                if(highscoredb.hasScore(puzzle.getId()))
                    highscore = puzzle.getStars(highscoredb.get(puzzle.getId()));
                for(int i = 0 ; i < 5 ; i++){
                    stars[i].clearEntityModifiers();
                    stars[i].detachSelf();
                    stars[i].setPosition((CAMERA_WIDTH - stars[i].getWidthScaled()*5)/2 + stars[i].getWidthScaled()*i,
                            1170 - nostars[i].getHeightScaled()/2);
                    if(i < highscore)
                        scene.attachChild(stars[i]);
                }
                scene.attachChild(upSlide);
                scene.attachChild(downSlide);


            }
        });

    }

    /**
     * Enable play when game finished creating
     */
    public void onGameCreated(){
        super.onGameCreated();
        enableBrickTouching();
        updateMoveCounter();

    }

    public Brick createBrick(int index, Car car, ResourceLoader.Values colour){
        return new Brick(index, car,colour, puzzle,resourceLoader,handler,getVertexBufferObjectManager());
    }

    public SoundHandler getSoundHandler(){
        return soundHandler ;
    }

    @Override
    public EngineOptions onCreateEngineOptions() {
        final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        EngineOptions options = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED,
                new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
        options.getAudioOptions().setNeedsSound(true);
        return options ;
    }

    /**
     * Disables touch of buttons and bricks
     */
    public void disableBrickTouching(){
        Scene scene = getEngine().getScene();
        for(Brick brick : carSprites){
            scene.unregisterTouchArea(brick);
        }
        scene.unregisterTouchArea(undo);
        scene.unregisterTouchArea(restart);
        scene.unregisterTouchArea(skip);
    }
    /**
     * Enables touch of buttons and bricks
     */
    public void enableBrickTouching(){

        Scene scene = getEngine().getScene();
        for(Brick brick : carSprites){
            scene.registerTouchArea(brick);
        }
        scene.registerTouchArea(undo);
        scene.registerTouchArea(restart);
        scene.registerTouchArea(skip);

    }

    public Puzzle getPuzzle(){
        return puzzle ;
    }

    /**
     * Performs final move if possible, and initiates transition to next puzzle if solved.
     */
    public boolean finishPuzzle(){
        if(!puzzle.solved()) {
            Move winningMove = puzzle.winningMove();
            if (puzzle.move(winningMove)) {
                disableBrickTouching();
                MoveSequencer moves = new MoveSequencer(this,carSprites);
                Move epicEndingMove = new Move(winningMove.getCarNumber(), winningMove.getMovement()+3);
                moves.addMove(epicEndingMove);
                soundHandler.exit.play();
                moves.setDropSound(false);
                moves.start();
                if(!skipped)
                    highscoredb.put(puzzle.getId(), puzzle.moveCount());
                else
                    highscoredb.put(puzzle.getId(), -1);

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

    /**
     * Undos moves of the puzzle, animation shown on screen.
     */
    public void undo(int steps){
        MoveSequencer seq = new MoveSequencer(this,carSprites);
        Move undone = puzzle.undo() ;
        while(undone != null && steps-- > 0){
            seq.addMove(undone);
            if(steps > 0)
                undone = puzzle.undo() ;
            updateMoveCounter();
        }
        seq.start();
    }

    /**
     * Safe method to update on screen counter
     */
    public void updateMoveCounter(){
        this.runOnUpdateThread(new Runnable()
        {
            @Override
            public void run()
            {
                moveCounter.setText(String.valueOf(Math.min(puzzle.moveCount(),999)));
                moveCounter.setX(680-moveCounter.getWidth());
            }
        });
    }

    /**
     * sets timer for enabling skip button
     */
    public void setNoskipTimer(){
        DelayHandler.delayed((int)(SKIP_TIME - (System.currentTimeMillis() - puzzleStarted))+100, new Runnable() {
            @Override
            public void run() {
                noskip();
            }
        });
    }

    /**
     * method for press of skip button, starts a safe thread to calculate solution.
     */
    public void skip(){
        if(System.currentTimeMillis() - puzzleStarted > SKIP_TIME) {
            disableBrickTouching();

            solveThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    safeSkip();
                }
            });
            solveThread.setPriority(Thread.MAX_PRIORITY);
            solveThread.start();
        } else
            noskip();

    }

    /**
     * method for press of noskip button
     */
    public void noskip(){
        if(System.currentTimeMillis() - puzzleStarted > SKIP_TIME) {
            enableSkip();
        // to avoid changing toast text when its active, a 10 second counter is placed
        } else if(System.currentTimeMillis() - lastToast > 1000*10) {
            waitMessage.setText(getString(R.string.skip_message_start)+ " " + (SKIP_TIME - (System.currentTimeMillis() - puzzleStarted))/1000 + " " + getString(R.string.skip_message_end));
            waitMessage.show();
            lastToast = System.currentTimeMillis();
        }
    }

     // Enable skip button
    public void enableSkip(){
        this.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                Scene scene = getEngine().getScene();
                noskip.detachSelf();
                scene.unregisterTouchArea(noskip);
                scene.attachChild(skip);
                scene.registerTouchArea(skip);
            }
        });
    }

    // disable skip button
    public void disableSkip(){
        this.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                Scene scene = getEngine().getScene();
                skip.detachSelf();
                scene.unregisterTouchArea(skip);
                noskip.detachSelf();
                scene.attachChild(noskip);
                scene.registerTouchArea(noskip);

            }
        });
    }

    // safe skip is performed in a safethread of skip()
    // method can be interrupted
    private void safeSkip(){
        solveMessage.show();
        MoveStack sol = puzzle.solution();
        if(!Thread.interrupted()) {
            MoveSequencer seq = new MoveSequencer(this, carSprites);
            if (sol.isEmpty())
                return;
            while (!puzzle.winningMovePossible() && !sol.isEmpty()) {
                Move mov = sol.pop();
                seq.addMove(mov);
                puzzle.move(mov);
            }
            skipped = true ;
            seq.start();
        }
        solveThread = null ;
    }

    // safe method for displaying star
    private void enableStar(final int index){
        this.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                getEngine().getScene().attachChild(stars[index]);
            }
        });
    }

    // safe method for displaying slides
    private void attachSlides(){
        this.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                upSlide.detachSelf();
                downSlide.detachSelf();
                getEngine().getScene().attachChild(upSlide);
                getEngine().getScene().attachChild(downSlide);
            }
        });
    }

    /**
     * Method starts the transition from one puzzle to the other.
     */
    private void startTransition(){
        for(int i = 0 ; i < 5 ; i++)
            stars[i].detachSelf();
        // initiate star sequence
        for(int i = 0 ; i < 5 ; i++){
            final int index = i ;
            if(index < puzzle.getStars(skipped)) {
                DelayHandler.delayed(250 * i, new Runnable() {
                    @Override
                    public void run() {
                        enableStar(index);
                        soundHandler.starwon.setVolume(10);
                        soundHandler.starwon.play();
                    }
                });
            } else if(index >= puzzle.getStars(false) || skipped){
                DelayHandler.delayed(250 * i, new Runnable() {
                    @Override
                    public void run() {
                        soundHandler.starlost.setVolume(10);
                        soundHandler.starlost.play();
                    }
                });
            }

        }
        skipped = false ;
        // initiate slide transition
        DelayHandler.delayed(1500,new Runnable() {
            @Override
            public void run() {
                slideTransition();
            }
        } );
    }

    /**
     * Plays animation of falling slides, to change levels
     */
    private void slideTransition(){
        disableBrickTouching();
        attachSlides();
        upSlide.registerEntityModifier(new MoveModifier(0.3f,0,0,-650,0) {
            @Override
            protected void onModifierFinished(final IEntity pItem)
            {
                // slides closed, drawing new puzzle behind them
                super.onModifierFinished(pItem);
                DelayHandler.delayed(100, new Runnable() {
                    @Override
                    public void run() {
                        puzzle = puzzleCursor.next();
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
                                puzzleStarted = System.currentTimeMillis();
                                disableSkip();
                                setNoskipTimer();
                            }
                        });
                    }
                });

            }
        });
        // Starting the bash sound just before the slides collide
        DelayHandler.delayed((int)(1000*0.17),new Runnable() {
            @Override
            public void run() {
                soundHandler.bash.play();
            }
        });
    }
}
