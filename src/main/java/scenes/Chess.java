package scenes;

import graphics.Camera;
import graphics.Color;
import graphics.Window;
import input.Keyboard;
import input.Keys;
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

import java.util.List;
import java.net.InetAddress;
import java.util.ArrayList;

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

    static GameServer server;
    static GameClient client;
    
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

        // Title
        animeAceFont = new Font("src/assets/fonts/AnimeAce.ttf", 72, true);
        titleText = new Text("CHESS", animeAceFont, offWhite, Window.getWidth() / 2, 90, 1, true, true);

        // Host button
        hostButton = new Button("HOST A GAME", offWhite, black, new Frame(300, 225, 200, 75));
        hostButton.getEventHandler().registerListener(Event.MOUSE_CLICK, (e) -> {
            System.out.println("Host button clicked!");

            server = new GameServer();
            server.start();

            client = new GameClient();
            client.join("Server's client", "127.0.0.1");

            Engine.scenes().switchScene(new ChessBoard());
        });
    
        // Join button
        joinButton = new Button("JOIN A GAME", black, offWhite, new Frame(300, 350, 200, 75));
        joinButton.getEventHandler().registerListener(Event.MOUSE_CLICK, (e) -> {
            System.out.println("Join button clicked!");
            
            client = new GameClient();
            client.join("Not The Server's Client", "127.0.0.1");
            
            Engine.scenes().switchScene(new ChessBoard());
        });
        
        // Exit button
        exitButton = new Button("EXIT", offWhite, black, new Frame(300, 475, 200, 75));
        exitButton.getEventHandler().registerListener(Event.MOUSE_CLICK, (e) -> {
            System.exit(0);
        });
        
        // Credits
        creditText = new Text("Created by Asher Haun, Sylvia Flores, Ellie Walser and Younus Syed", new Font(), offWhite, Window.getWidth() / 2, 800, 1, true, true);
    }

    public void update() {
        if (Keyboard.getKeyDown(Keys.KEY_S) || Keyboard.getKeyDown(Keys.KEY_H)) {
            System.out.println("[HOTKEY] S|H: Joining self-hosted game...");

            server = new GameServer();
            server.start();

            client = new GameClient();
            client.join("Server's client", "127.0.0.1");

            Engine.scenes().switchScene(new ChessBoard());
        }

        if(Keyboard.getKeyDown(Keys.KEY_J) || Keyboard.getKeyDown(Keys.KEY_C)) {
            System.out.println("[HOTKEY] J|C: Client Join screen");
            Engine.scenes().switchScene(new joinGame());
        }

        if (Keyboard.getKeyDown(Keys.KEY_ESCAPE)) {
            System.exit(0);
        }
    }

    class joinGame extends Scene {

        Text availableText;
        TextField tf;

        ArrayList<Button> hosts = new ArrayList<Button>();

        public joinGame() {

        }

        public void awake() {
            Button enter;
            camera = new Camera();
            setDefaultBackground(30, 30, 30);

            availableText = new Text("Available Games:", new Font("src/assets/fonts/AnimeAce.ttf", 20, true), offWhite, Window.getWidth() / 2, 20, 1, true, true);

            client = new GameClient();
            

            // Text field. Then display the IP on the chess game window.
            // enter = new Button("JOIN A GAME", black, offWhite, new Frame(300, 350, 200, 75));

            // Figure out a way to click enter on text field and take player to the chess
            // board.

            // enter.getEventHandler().registerListener(Event.MOUSE_CLICK, (e) -> {
            //     System.out.println("Enter button clicked!");
            //     Engine.scenes().switchScene(new ChessBoard());
            // });

            new Thread(() -> {
                while (true) {
                    discoverHosts();
                    try {
                        Thread.sleep(1000 / 60);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        List<InetAddress> hosts_addresses;
        private void discoverHosts () {
            System.out.println("[CLIENT] Discovering hosts...");
            // client.join("Not The Server's Client", "127.0.0.1");
            hosts_addresses = client.kryo().discoverHosts(54777, 5000);

            for (InetAddress host : hosts_addresses) {
                client.probe(host.getHostAddress());
            }
        }

        private void displayHosts () {
            if (hosts_addresses == null) {
                availableText.change("No games available.");
            } else {
                // map each host in the list to a button in the buttons list. 

                int hostIndex = 0;
                for (InetAddress host : hosts_addresses) {
                    if (hostIndex >= hosts.size()) {
                        hosts.add(new Button(host.getHostAddress(), black, offWhite, new Frame(300, 50 + (hostIndex * 75), 200, 70)));
                    } else {
                        hosts.get(hostIndex).setText(host.getHostAddress());
                    }
                    hostIndex++;
                }
            }
        }

        public void update() {
            if (Keyboard.getKeyDown(Keys.KEY_ESCAPE)) {
                System.exit(0);
            }

            displayHosts();
        }
    }
}
