package nl.mprog.BrickSlide10196129.brickslide.app;

import android.os.Bundle;

import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.engine.options.EngineOptions;
import org.andengine.entity.scene.Scene;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;

import org.andengine.util.debug.Debug;

import java.io.IOException;
import java.util.ArrayList;

import org.andengine.entity.sprite.Sprite;

import RushHourSolver.Board;
import RushHourSolver.Car;
import RushHourSolver.Puzzle;
import nl.mprog.BrickSlide10196129.brickslide.app.ResourceLoader.Values;

public class MainActivity extends SimpleBaseGameActivity {

    private static int CAMERA_WIDTH = 720;
    private static int CAMERA_HEIGHT = 1280;

    private static int BLOCK_SIDE = 101;


    private ArrayList<Sprite> carSprites ;
    private TouchHandler handler;

    private Puzzle puzzle ;


    private ResourceLoader resourceLoader ;

    @Override
    protected void onCreate(Bundle pSavedInstanceState){
        super.onCreate(pSavedInstanceState);
        carSprites = new ArrayList<Sprite>();
        puzzle = new Puzzle("Level 1:8:1,2H2;0,0H2;5,0V3;0,1V3;3,1V3;0,4V2;4,4H2;2,5H3;");
        handler = new TouchHandler(puzzle);
    }

    @Override
    protected void onCreateResources() {
        resourceLoader = new ResourceLoader(this.getTextureManager(), this.getAssets());
        try {
            resourceLoader.loadAll();
        } catch (IOException e) {
            Debug.e(e);
        }
    }

    @Override
    protected Scene onCreateScene() {
        carSprites = new ArrayList<Sprite>();

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

    public Brick createBrick(int index, Car car, Values colour){
        return new Brick(index, car,colour, puzzle,resourceLoader,handler,getVertexBufferObjectManager());
    }



    @Override
    public EngineOptions onCreateEngineOptions() {
        final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED,
                new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
    }

}
