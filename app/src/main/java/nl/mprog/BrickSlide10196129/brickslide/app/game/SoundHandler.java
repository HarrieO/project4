package nl.mprog.BrickSlide10196129.brickslide.app.game;

import android.service.wallpaper.WallpaperService;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.audio.sound.SoundManager;
import org.andengine.engine.Engine;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by hroosterhuis on 3/24/14.
 */
public class SoundHandler {


    public Sound exit, bash, bump, starwon, starlost ;

    SoundHandler(MainActivity activity){

        SoundFactory.setAssetBasePath("mfx/");

        bump = tryLoad("Thip.ogg", activity);
        if(bump != null)
            bump.setVolume(50);

        exit     = tryLoad("Exit.ogg", activity);
        bash     = tryLoad("shortbang.ogg", activity);
        starlost = tryLoad("TornadoMagic.ogg", activity);
        if(starlost != null)
            starlost.setVolume(40);
        starwon  = tryLoad("boom.ogg", activity);
    }

    private Sound tryLoad(String filename, MainActivity activity){
        try {
            return SoundFactory.createSoundFromAsset(activity.getEngine().getSoundManager(), activity, filename);
        } catch (final IOException e) {
            return null ;
            //Debug.e(e);
        }
    }
}
