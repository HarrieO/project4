package nl.mprog.BrickSlide10196129.brickslide.app;

import android.content.res.AssetManager;

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
        HORIZONTAL("H"), VERTICAL("V"), BRICK("Brick"), BACKGROUND("BrickslideBackground"),
        EXTENSION("jpg"), FOLDER("gfx/");

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
        return Values.FOLDER.txt + colour.txt + Values.BRICK.txt + alignment.txt
                + length + "." + Values.EXTENSION.txt ;
    }

    public TextureRegion loadCar(Values colour, Values alignment, int length) throws IOException{
        return load(carPath(colour, alignment, length));
    }

    public TextureRegion getCar(Values colour, Values alignment, int length){
        return get(carPath(colour, alignment, length));
    }

    public TextureRegion loadValue(Values value) throws IOException {
        return load(Values.FOLDER.txt + value.txt + "." + Values.EXTENSION.txt);
    }

    public TextureRegion getValue(Values value){
        return get(Values.FOLDER.txt + value.txt + "." + Values.EXTENSION.txt);
    }

    public TextureRegion get(String key) {
        return textureLibrary.get(key);
    }

}
