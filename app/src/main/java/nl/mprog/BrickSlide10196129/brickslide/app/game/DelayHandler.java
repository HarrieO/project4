package nl.mprog.BrickSlide10196129.brickslide.app.game;

import android.os.*;
import android.os.Process;

import RushHourSolver.Puzzle;

/**
 * Created by hroosterhuis on 3/22/14.
 */
public class DelayHandler {

    public static void delayed(final int count, final Runnable runnable){
        final int fcount = Math.max(count,1);
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(fcount);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runnable.run();
            }
        }).start();
    }

    public static void priorityThread(final Runnable runnable){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                runnable.run();
            }
        });
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();
        
    }
}
