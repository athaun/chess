package scenes;

import graphics.Camera;
import graphics.Color;
import graphics.Window;
import scene.Scene;
import ui.Frame;
import ui.Text;
import ui.element.TextField;
import ui.fonts.Font;
import util.Engine;
import util.Log;

import static graphics.Graphics.setDefaultBackground;


/**
 * Minimal usage example of the AudioListener and AudioSource components.
 */
public class Chess extends Scene {

    Font openSans;
    Text titleText;

    TextField tf;

    public static void main (String[] args) {
        Engine.init(1080, 720, "Chess");
        Engine.scenes().switchScene(new Chess());
        Engine.showWindow();
        Log.setLogLevel(Log.ALL);
    }

    public void awake() {
        camera = new Camera();
        setDefaultBackground(Color.CYAN);

        openSans = new Font("src/assets/fonts/OpenSans.ttf", 20, true);
        titleText = new Text("YEET", openSans, Color.WHITE, Window.getWidth() / 2, 5, 1, true, true);
    
        tf = new TextField(":)", new Frame(10, 10, 200, 25));
        //hiii
    }

    public void update() {
        
    }
}
