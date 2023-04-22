package scenes;

import static graphics.Graphics.setDefaultBackground;

import org.joml.Vector2f;

import ecs.GameObject;
import ecs.SpriteRenderer;
import graphics.Camera;
import graphics.Color;
import graphics.Window;
import input.Keyboard;
import input.Keys;
import scene.Scene;
import scenes.chess.GameClient;
import scenes.chess.GameServer;
import ui.EventHandler.Event;
import ui.Frame;
import ui.Text;
import ui.element.Button;
import ui.fonts.Font;
import util.Engine;
import util.Log;

/*
 * This is the main menu scene.
 * It contains the title, buttons to host/join a game, and credits.
 * 
 * Hotkeys:
 * - S|H: Host a game and join it
 * - J|C: Go to the join screen
 * - L: Join Localhost game
 * - ESC: Exit the game
 * 
 */
public class Chess extends Scene {
    
    // Constant colors
    public static Color PRIMARY_LIGHT = new Color(252, 234, 201); 
    public static Color PRIMARY_DARK = new Color(58, 54, 51);

    // Networking
    public static GameServer server;
    public static GameClient client;

    // Text elements
    Font animeAceFont;
    Text titleText;
    Text creditText;

    // Menu Buttons
    Button hostButton;
    Button joinButton;
    Button exitButton;

    // Knight sprites for the title
    GameObject blackKnight;
    GameObject whiteKnight;
    
    public static void main (String[] args) {
        Engine.init(800, 900, "Chess");
        Engine.scenes().switchScene(new Chess());
        Engine.showWindow();
        Log.setLogLevel(Log.ALL);
    }

    /*
     * Awake is called once when the scene is first loaded.
     * Sets up the UI elements and logic for buttons.
     */
    @Override
    public void awake() {
        camera = new Camera();
        setDefaultBackground(30, 30, 30);

        // Knight sprites
        blackKnight = new GameObject(new Vector2f((Window.getWidth() / 2) - 325, 25)).addComponent(new SpriteRenderer("src/assets/images/black_knight.png", new Vector2f(135, 230)));
        whiteKnight = new GameObject(new Vector2f((Window.getWidth() / 2) + 175, 25)).addComponent(new SpriteRenderer("src/assets/images/white_knight.png", new Vector2f(135, 230)));

        // Title
        animeAceFont = new Font("src/assets/fonts/AnimeAce.ttf", 72, true);
        titleText = new Text("CHESS", animeAceFont, PRIMARY_LIGHT, Window.getWidth() / 2, 90, 1, true, true);

        // Host button
        hostButton = new Button("HOST A GAME", PRIMARY_LIGHT, PRIMARY_DARK, new Frame(300, 225, 200, 75));
        hostButton.getEventHandler().registerListener(Event.MOUSE_CLICK, (e) -> {
            System.out.println("Host button clicked!");

            server = new GameServer();
            server.start();

            client = new GameClient();
            client.join("Server's client", "127.0.0.1");

            Engine.scenes().switchScene(new ChessBoard());
        });
    
        // Join button
        joinButton = new Button("JOIN A GAME", PRIMARY_DARK, PRIMARY_LIGHT, new Frame(300, 350, 200, 75));
        joinButton.getEventHandler().registerListener(Event.MOUSE_CLICK, (e) -> {
            System.out.println("Join button clicked!");
            
            Engine.scenes().switchScene(new joinGame());
        });
        
        // Exit button
        exitButton = new Button("EXIT", PRIMARY_LIGHT, PRIMARY_DARK, new Frame(300, 475, 200, 75));
        exitButton.getEventHandler().registerListener(Event.MOUSE_CLICK, (e) -> {
            System.exit(0);
        });
        
        // Credits
        creditText = new Text("Created by Asher Haun, Sylvia Flores, Ellie Walser and Younus Syed", new Font(), PRIMARY_LIGHT, Window.getWidth() / 2, 800, 1, true, true);
    }

    /*
     * Primary scene loop:
     * Checks for hotkeys
     */
    @Override
    public void update() {
        // Host a game and join it
        if (Keyboard.getKeyDown(Keys.KEY_S) || Keyboard.getKeyDown(Keys.KEY_H)) {
            System.out.println("[HOTKEY] S|H: Joining self-hosted game...");

            server = new GameServer();
            server.start();

            client = new GameClient();
            client.join("Server's client", "127.0.0.1");

            Engine.scenes().switchScene(new ChessBoard());
        }

        // Go to the join screen
        if(Keyboard.getKeyDown(Keys.KEY_J) || Keyboard.getKeyDown(Keys.KEY_C)) {
            System.out.println("[HOTKEY] J|C: Client Join screen");
            Engine.scenes().switchScene(new joinGame());
        }

        // Join Localhost game
        if (Keyboard.getKeyDown(Keys.KEY_L)) {
            System.out.println("[HOTKEY] L: Joining localhost game...");

            client = new GameClient();
            client.join("Not The Server's Client", "127.0.0.1");
            
            Engine.scenes().switchScene(new ChessBoard());
        }

        // Exit the game
        if (Keyboard.getKeyDown(Keys.KEY_ESCAPE)) {
            System.exit(0);
        }
    }
}