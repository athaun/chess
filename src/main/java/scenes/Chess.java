package scenes;

import graphics.Camera;
import graphics.Color;
import graphics.Window;
import scene.Scene;
import ui.Frame;
import ui.Text;
import ui.EventHandler.Event;
import ui.element.Button;
import ui.element.TextField;
import ui.fonts.Font;
import util.Engine;
import util.Log;

import static graphics.Graphics.setDefaultBackground;


/**
 * Minimal usage example of the AudioListener and AudioSource components.
 */
public class Chess extends Scene {

    Font animeAceFont;
    Text titleText;

    TextField tf;

    Button hostButton;
    Button joinButton;
    Button exitButton;

    Color offWhite = new Color(252, 234, 201); 
    Color black = new Color(58,54,51);
    public static void main (String[] args) {
        Engine.init(1080, 720, "Chess");
        Engine.scenes().switchScene(new Chess());
        Engine.showWindow();
        Log.setLogLevel(Log.ALL);
    }

    public void awake() {
        camera = new Camera();
        setDefaultBackground(Color.GRAY);

        animeAceFont = new Font("src/assets/fonts/AnimeAce.ttf", 48, true);
        titleText = new Text("CHESS", animeAceFont, Color.WHITE, Window.getWidth() / 2, 5, 1, true, true);
    
        //tf = new TextField(":)", new Frame(10, 10, 200, 25));
        
        hostButton = new Button("HOST A GAME", offWhite, black, new Frame(450, 100, 200, 75));
        hostButton.getEventHandler().registerListener(Event.MOUSE_CLICK, (e) -> {
            System.out.println("Host button clicked!");
        });

        joinButton = new Button("JOIN A GAME", black, offWhite, new Frame(450, 200, 200, 75));
        joinButton.getEventHandler().registerListener(Event.MOUSE_CLICK, (e) -> {
            System.out.println("Join button clicked!");
        });

        exitButton = new Button("EXIT", offWhite, black, new Frame(450, 300, 200, 75));
        exitButton.getEventHandler().registerListener(Event.MOUSE_CLICK, (e) -> {
            System.out.println("Exit button clicked!");
        });
        
    }

    public void update() {
        
    }
}
