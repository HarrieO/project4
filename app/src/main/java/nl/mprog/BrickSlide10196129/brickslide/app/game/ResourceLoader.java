package nl.mprog.BrickSlide10196129.brickslide.app.game;

import android.content.res.AssetManager;
import android.util.Log;

import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.util.adt.io.in.IInputStreamOpener;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by hroosterhuis on 3/20/14.
 */
public class ResourceLoader {

    public static enum Values {
        RED("Red"), BLUE("Blue"), GREEN("Green"), PURPLE("Purple"),
        HORIZONTAL("H"), VERTICAL("V"), BRICK("Brick"), BACKGROUND("BrickslideBackground.jpg"),
        EXTENSION("jpg"), FOLDER("gfx/"), BRICKFOLDER("Bricks/"), UNDO("undo.jpg"), SKIP("skip.jpg"),
        NOSKIP("skipcancelled.jpg"), RESTART("restart.jpg"), SLIDE("slide.jpg"), STAR("star.gif"), NOSTAR("nostar.gif");

        private String txt ;
        private Values(String txt){
            this.txt = txt ;
        }
        public String string(){
            return txt ;
        }
    }

    private final HashMap<String, TextureRegion> textureLibrary = new HashMap<String, TextureRegion>();

    private TextureManager textureManager ;
    private AssetManager   assets ;

    public ResourceLoader(TextureManager textureManager, AssetManager assets){
        this.textureManager = textureManager ;
        this.assets         = assets ;
    }

    public void unload(String key){
        textureLibrary.remove(key);
    }

    public boolean isLoaded(String key){
        return textureLibrary.containsKey(key);
    }

    public void loadAll() throws IOException {
        loadValue(Values.BACKGROUND);
        loadValue(Values.UNDO);
        loadValue(Values.RESTART);
        loadValue(Values.SKIP);
        loadValue(Values.NOSKIP);
        loadValue(Values.SLIDE);
        loadValue(Values.STAR);
        loadValue(Values.NOSTAR);
        Values colours[] = {Values.RED,Values.BLUE,Values.GREEN,Values.PURPLE};
        Values alignments[] = {Values.HORIZONTAL, Values.VERTICAL};
        for(int length = 1 ; length < 7 ; length++){
            for(Values colour : colours){
                for(Values alignment : alignments){
                    loadCar(colour, alignment, length);
                }
            }
        }
    }

    public TextureRegion load(final String key) throws IOException {
        TextureRegion region = textureLibrary.get(key);
        if(region != null)
            return  region ;
        ITexture texture = new BitmapTexture(textureManager, new IInputStreamOpener() {
            @Override
            public InputStream open() throws IOException {
                return assets.open(key);
            }
        });
        texture.load();
        region = TextureRegionFactory.extractFromTexture(texture);
        textureLibrary.put(key,region);
        return region ;
    }

    private String carPath(Values colour, Values alignment, int length){
        return Values.FOLDER.txt + Values.BRICKFOLDER.txt + colour.txt + Values.BRICK.txt + alignment.txt
                + length + "." + Values.EXTENSION.txt ;
    }

    public TextureRegion loadCar(Values colour, Values alignment, int length) throws IOException{
        return load(carPath(colour, alignment, length));
    }

    public TextureRegion getCar(Values colour, Values alignment, int length){
        return get(carPath(colour, alignment, length));
    }

    public TextureRegion loadValue(Values value) throws IOException {
        return loadValue(value.txt);
    }

    public TextureRegion getValue(Values value){
        return getValue(value.txt);
    }

    public TextureRegion loadValue(String value) throws IOException {
        return load(Values.FOLDER.txt + value );
    }

    public TextureRegion getValue(String value){
        return get(Values.FOLDER.txt + value );
    }

    public TextureRegion get(String key) {
        return textureLibrary.get(key);
    }

}
