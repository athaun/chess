package scenes;

import graphics.Camera;
import graphics.Color;
import graphics.Window;
import scene.Scene;
import ui.Text;
import ui.fonts.Font;
import util.Engine;

import static graphics.Graphics.setDefaultBackground;


/**
 * Minimal usage example of the AudioListener and AudioSource components.
 */
public class Chess extends Scene {

    Font openSans;
    Text titleText;

    public static void main (String[] args) {
        Engine.init(1080, 720, "Chess");
        Engine.scenes().switchScene(new Chess());
        Engine.showWindow();
    }

    public void awake() {
        camera = new Camera();
        setDefaultBackground(0);

        openSans = new Font("src/assets/fonts/OpenSans-Regular.ttf", 20, true);
        titleText = new Text("Chess :O", openSans, Color.WHITE, Window.getWidth() / 2, 5, 1, true, true);
    }

    public void update() {
        
    }
}
