package nl.mprog.BrickSlide10196129.brickslide.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import nl.mprog.BrickSlide10196129.brickslide.app.game.MainActivity;


public class MenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    protected void onStart(){
        final Activity activity = this ;
        super.onStart();
       ((Button) findViewById(R.id.play_button)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity,MainActivity.class);
                    startActivity(intent);
                }
            }
        );
        ((Button) findViewById(R.id.level_button)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(activity,LevelSelectionActivity.class);
                        startActivity(intent);
                    }
                }
        );
    }

}
