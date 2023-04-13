package scenes;

import graphics.Camera;
import graphics.Color;
import graphics.Window;
import input.Keyboard;
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

import org.lwjgl.glfw.GLFW;

public class Chess extends Scene {
    Font animeAceFont;
    Text titleText;
    Text hostText;

    TextField tf;

    Button hostButton;
    Button joinButton;
    Button exitButton;

    Color offWhite = new Color(252, 234, 201);
    Color black = new Color(58, 54, 51);

    GameServer server;
    GameClient client;

    public static void main(String[] args) {
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

        hostButton = new Button("HOST A GAME", offWhite, black, new Frame(450, 100, 200, 75));
        hostButton.getEventHandler().registerListener(Event.MOUSE_CLICK, (e) -> {
            System.out.println("Host button clicked!");

            server = new GameServer();
            server.start();

            client = new GameClient();
            client.join("Server's client", "0.0.0.0");

            Engine.scenes().switchScene(new ChessGame());
        });

        joinButton = new Button("JOIN A GAME", black, offWhite, new Frame(450, 200, 200, 75));
        joinButton.getEventHandler().registerListener(Event.MOUSE_CLICK, (e) -> {
            System.out.println("Join button clicked!");

            client = new GameClient();
            client.join("Not The Server's Client", "0.0.0.0");

            Engine.scenes().switchScene(new joinGame());
        });

        exitButton = new Button("EXIT", offWhite, black, new Frame(450, 300, 200, 75));
        exitButton.getEventHandler().registerListener(Event.MOUSE_CLICK, (e) -> {
            System.out.println("Exit button clicked!");
        });

    }

    // Class for opening actual game with chessboard.

    class ChessGame extends Scene {
        Text info;
        public ChessGame() {
            
        }

        public void awake() {
            camera = new Camera();
            setDefaultBackground(Color.GRAY);

            info = new Text("Chess Board :D", new Font("src/assets/fonts/AnimeAce.ttf", 20, true), Color.BLACK, 10, 10);
        }

        public void update () {
            if (Keyboard.getKeyDown(GLFW.GLFW_KEY_SPACE)) {
                client.send_i();
                System.out.println("Client sending!");
            }
            if (server != null) {
                String s = "";
                for (String i : server.messages) {
                    s = i;
                }
                info.change(s);
            }
        }
    }

    class joinGame extends Scene {

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
                Engine.scenes().switchScene(new ChessGame());
            });

        }
    }

    public void update() {

    }
}
