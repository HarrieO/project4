package nl.mprog.BrickSlide10196129.brickslide.app;

import android.app.Application;

import nl.mprog.BrickSlide10196129.brickslide.app.database.HighscoreDatabase;
import nl.mprog.BrickSlide10196129.brickslide.app.database.MovesDatabase;
import nl.mprog.BrickSlide10196129.brickslide.app.database.PuzzleDatabase;

/**
 * Application class for shared databases.
 */
public class BrickSlideApplication extends Application
{

    PuzzleDatabase    puzzleDatabase ;
    HighscoreDatabase highscoreDatabase;
    MovesDatabase     movesDatabase ;
    @Override
    public void onCreate()
    {
        super.onCreate();

        puzzleDatabase    = new PuzzleDatabase(this);
        highscoreDatabase = new HighscoreDatabase(this);
        movesDatabase     = new MovesDatabase(this);
    }

    public HighscoreDatabase getHighscoreDatabase(){
        return  highscoreDatabase ;
    }

    public MovesDatabase getMovesDatabase(){
        return  movesDatabase ;
    }

    public PuzzleDatabase getPuzzleDatabase(){
        return  puzzleDatabase ;
    }

    public void onTerminate(){
        puzzleDatabase.close();
        highscoreDatabase.close();
        movesDatabase.close();
    }

}