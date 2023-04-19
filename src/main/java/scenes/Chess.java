package scenes;

import graphics.Camera;
import graphics.Color;
import graphics.Window;
import scene.Scene;
import scenes.chess.GameClient;
import scenes.chess.GameServer;
import ui.Frame;
import ui.Text;
import ui.EventHandler.Event;
import ui.element.Button;
import ui.element.TextField;
import ui.fonts.Font;
import util.Engine;
import util.Log;
import static graphics.Graphics.setDefaultBackground;
import org.joml.Vector2f;
import ecs.GameObject;
import ecs.SpriteRenderer;

public class Chess extends Scene {
    Font animeAceFont;
    Text titleText;
    Text creditText;

    Button hostButton;
    Button joinButton;
    Button exitButton;

    Color offWhite = new Color(252, 234, 201); 
    Color black = new Color(58,54,51);

    GameObject blackKnight;
    GameObject whiteKnight;

    GameServer server;
    GameClient client;
    public static void main (String[] args) {
        Engine.init(800, 900, "Chess");
        Engine.scenes().switchScene(new Chess());
        Engine.showWindow();
        Log.setLogLevel(Log.ALL);
    }


    public void awake() {
        camera = new Camera();
        setDefaultBackground(30, 30, 30);

        // Knight sprites
        blackKnight = new GameObject(new Vector2f((Window.getWidth()/2) - 325, 25)).addComponent(new SpriteRenderer("src/assets/images/black_knight.png", new Vector2f(135, 230)));
        whiteKnight = new GameObject(new Vector2f((Window.getWidth()/2) + 175, 25)).addComponent(new SpriteRenderer("src/assets/images/white_knight.png", new Vector2f(135, 230)));


        animeAceFont = new Font("src/assets/fonts/AnimeAce.ttf", 72, true);
        titleText = new Text("CHESS", animeAceFont, offWhite, Window.getWidth() / 2, 90, 1, true, true);
        // animeAceFont = new Font("src/assets/fonts/AnimeAce.ttf", 48, true);
        animeAceFont.changeSize(48);

        hostButton = new Button("HOST A GAME", offWhite, black, new Frame(300, 225, 200, 75));
        hostButton.getEventHandler().registerListener(Event.MOUSE_CLICK, (e) -> {
            System.out.println("Host button clicked!");

            server = new GameServer();
            server.start();

            client = new GameClient();
            client.join("Server's client", "127.0.0.1");

            Engine.scenes().switchScene(new ChessBoard());
        });
    
        joinButton = new Button("JOIN A GAME", black, offWhite, new Frame(300, 350, 200, 75));
        joinButton.getEventHandler().registerListener(Event.MOUSE_CLICK, (e) -> {
            System.out.println("Join button clicked!");
            
            client = new GameClient();
            client.join("Not The Server's Client", "127.0.0.1");
            
            Engine.scenes().switchScene(new ChessBoard());
        });
        
        exitButton = new Button("EXIT", offWhite, black, new Frame(300, 475, 200, 75));
        exitButton.getEventHandler().registerListener(Event.MOUSE_CLICK, (e) -> {
            System.exit(0);
        });
        
        creditText = new Text("Created by Asher Haun, Sylvia Flores, Ellie Walser and Younus Syed", new Font(), offWhite, Window.getWidth() / 2, 800, 1, true, true);
    }

    class joinGame extends Scene {

        TextField tf;

        public joinGame() {

        }

        public void awake() {
            Button enter;
            camera = new Camera();
            setDefaultBackground(Color.GRAY);

            new Text("Enter an IP address.", new Font("src/assets/fonts/AnimeAce.ttf", 20, true), Color.BLACK, 10, 10);

            // Text field. Then display the IP on the chess game window.

            enter = new Button("ENTER", offWhite, black, new Frame(450, 300, 200, 75));

            // Figure out a way to click enter on text field and take player to the chess
            // board.

            enter.getEventHandler().registerListener(Event.MOUSE_CLICK, (e) -> {
                System.out.println("Enter button clicked!");
                Engine.scenes().switchScene(new ChessBoard());
            });

        }
    }

    public void update() {

    }
}
