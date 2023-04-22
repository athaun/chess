package scenes;

import graphics.Camera;
import graphics.Color;
import graphics.Window;
import input.Keyboard;
import input.Keys;
import scene.Scene;
import scenes.chess.GameClient;
import scenes.chess.GameServer;
import scenes.chess.GameClient.GameHost;
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

        Thread discoveryThread;
        private void startDiscovery () {
            discoveryThread = new Thread(() -> {
                // Set up a listener to handle probe responses.
                client.probeListener();
                while (true) {
                    discoverHosts();
                }
            });
            discoveryThread.start();
        }

        public void awake() {
            Button enter;
            camera = new Camera();
            setDefaultBackground(30, 30, 30);

            availableText = new Text("Available Games:", new Font("src/assets/fonts/AnimeAce.ttf", 20, true), offWhite, Window.getWidth() / 2, 20, 1, true, true);

            client = new GameClient();

            // Text field. Then display the IP on the chess game window.
            tf = new TextField("127.0.0.1", new Frame(250, 75, 200, 70));
            enter = new Button("JOIN", black, offWhite, new Frame(455, 75, 95, 70));
            enter.getEventHandler().registerListener(Event.MOUSE_CLICK, (e) -> {
                if (client.join("Client :O", tf.getText().strip())) {
                    Engine.scenes().switchScene(new ChessBoard());
                } else {
                    System.out.println("[CLIENT] Failed to join game.");
                }
            });        
            
            startDiscovery();
        }

        private List<InetAddress> addressList;
        /*
         * Discover games hosted on the local network by sending probes to all and listening for game IDs.
         */
        private void discoverHosts () {
            System.out.println("[CLIENT] Discovering hosts...");
            addressList = client.kryo().discoverHosts(54777, 5000);

            for (InetAddress ip : addressList) {
                System.out.println("[CLIENT] Probing " + ip.getHostAddress());
                if (client.probe(ip.getHostAddress())) {
                    System.out.println("[CLIENT] Found game at " + ip.getHostAddress());
                } else {
                    System.out.println("[CLIENT] No game found at " + ip.getHostAddress());
                }
            }            
        }

        private void displayHosts () {

            if (client.getGameHosts().isEmpty()) {
                availableText.change("Searching for games...");
            } else {
                // map each host in the list to a button in the buttons list.
                int hostIndex = 0;
                for (GameHost h : client.getGameHosts()) {
                    String label = h.gameID + " at " + h.address.getHostName();

                    if (hostIndex >= hosts.size()) {
                        // Add a new button to the list.
                        hosts.add(new Button(label, black, offWhite, new Frame(250, 175 + (hostIndex * 75), 300, 70)));
                        
                        // Register a listener for the button that will attempt to join the game.
                        hosts.get(hostIndex).getEventHandler().registerListener(Event.MOUSE_CLICK, e -> {
                            System.out.println("Joining " + h.gameID + " at " + h.address.getHostAddress());
                            // Stop the discovery thread.
                            discoveryThread.stop();
                            if (client.join("Client :O", h.address.getHostAddress())) {
                                // If the join was successful, switch to the chess board scene.
                                Engine.scenes().switchScene(new ChessBoard());
                            } else {
                                // If the join was unsuccessful, restart the discovery thread.
                                System.out.println("[CLIENT] Failed to join game.");
                                discoveryThread.start();
                            }
                        });
                    }                
                    // This does not clean up old hosts that are no longer available due to a bug with the rendering system.
                    // TODO @Asher: Fix this if time is available, for now the basic functionality is here.                  

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
