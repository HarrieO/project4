package nl.mprog.BrickSlide10196129.brickslide.app;

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
}
