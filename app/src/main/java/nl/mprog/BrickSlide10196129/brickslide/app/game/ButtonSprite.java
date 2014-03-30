package nl.mprog.BrickSlide10196129.brickslide.app.game;

import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Class for onscreen buttons, all wrapper functions.
 * Created by hroosterhuis on 3/22/14.
 */
public abstract class ButtonSprite extends Sprite {

    private static int FIRSTX = 50 ;
    private static int FIRSTY = 960 ;
    private static int SPACE = 214 ;

    private MainActivity activity ;

    public abstract void touchAction();

    public MainActivity getActivity(){
        return activity ;
    }

    /**
     * Wrapper function to ease programming.
     */
    public ButtonSprite(MainActivity activity, float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
        this.activity = activity ;
    }

    public static ButtonSprite undoSprite(MainActivity activity,ResourceLoader resourceLoader, VertexBufferObjectManager vbom){
        ButtonSprite backgroundSprite = new ButtonSprite(activity, FIRSTX + SPACE, FIRSTY, resourceLoader.getValue(ResourceLoader.Values.UNDO), vbom) {
            @Override
            public void touchAction() {
                getActivity().undo();
            }
        };
        return backgroundSprite ;
    }

    public static ButtonSprite restartSprite(MainActivity activity,ResourceLoader resourceLoader, VertexBufferObjectManager vbom){
        ButtonSprite backgroundSprite = new ButtonSprite(activity, FIRSTX, FIRSTY, resourceLoader.getValue(ResourceLoader.Values.RESTART), vbom) {
            @Override
            public void touchAction() {
                getActivity().restart();
            }
        };
        return backgroundSprite ;
    }

    public static ButtonSprite skipSprite(MainActivity activity,ResourceLoader resourceLoader, VertexBufferObjectManager vbom){
        ButtonSprite backgroundSprite = new ButtonSprite(activity, FIRSTX + SPACE * 2, FIRSTY, resourceLoader.getValue(ResourceLoader.Values.SKIP), vbom) {
            @Override
            public void touchAction() {
                getActivity().skip();
            }
        };
        return backgroundSprite ;
    }

    public static ButtonSprite noskipSprite(MainActivity activity,ResourceLoader resourceLoader, VertexBufferObjectManager vbom){
        ButtonSprite backgroundSprite = new ButtonSprite(activity, FIRSTX + SPACE * 2, FIRSTY, resourceLoader.getValue(ResourceLoader.Values.NOSKIP), vbom) {
            @Override
            public void touchAction() {
                getActivity().noskip();
            }
        };
        return backgroundSprite ;
    }

    @Override
    public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
        if(!activity.getPuzzle().solved() && pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP){
            touchAction();
        }
        return  true ;
    }


}
