package nl.mprog.BrickSlide10196129.brickslide.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import RushHourSolver.Puzzle;
import nl.mprog.BrickSlide10196129.brickslide.app.database.HighscoreDatabase;
import nl.mprog.BrickSlide10196129.brickslide.app.database.MovesDatabase;
import nl.mprog.BrickSlide10196129.brickslide.app.database.PuzzleDatabase;
import nl.mprog.BrickSlide10196129.brickslide.app.game.BrickSlideApplication;
import nl.mprog.BrickSlide10196129.brickslide.app.game.MainActivity;


public class LevelSelectionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_selection);

        PuzzleDatabase.PuzzleCursor c = ((BrickSlideApplication)getApplication()).getPuzzleDatabase().getCursor();
        HighscoreDatabase db = ((BrickSlideApplication)getApplication()).getHighscoreDatabase();
        boolean enabled = true ;
        enabled = addPuzzle(c, db, enabled);
        while(c.hasNext()) {
            c.next();
            enabled = addPuzzle(c, db, enabled);
        }

    }

    public boolean addPuzzle(PuzzleDatabase.PuzzleCursor c, HighscoreDatabase db, boolean enabled){
        Puzzle puzzle = c.get();
        TextView v = new TextView(this);
        v.setText(puzzle.getName());
        v.setTextSize(28);
        LinearLayout hor = new LinearLayout(this);
        hor.setOrientation(LinearLayout.HORIZONTAL);
        Button b = new Button(this);

        boolean last = false ;


        if(enabled && db.hasScore(puzzle.getId())) {
            int score = db.get(puzzle.getId());
            if (score != -1)
                b.setText(String.valueOf(puzzle.getStars(db.get(puzzle.getId()))));
        } else {
            if(enabled)
                b.setText("-");
            last = true;
        }
        b.setEnabled(enabled);
        if(enabled){
            final int id = puzzle.getId();
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openPuzzle(id);
                }
            });
        }

        hor.addView(b);
        hor.addView(v);
        ((LinearLayout) findViewById(R.id.level_list)).addView(hor);
        return !last ;
    }

    public void openPuzzle(int puzzleID){
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("Level", puzzleID);
        finish();
        startActivity(intent);
    }



}
