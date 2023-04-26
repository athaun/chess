package scenes;

import static graphics.Graphics.setDefaultBackground;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import util.Log;
import graphics.Camera;
import graphics.Window;
import input.Keyboard;
import input.Keys;
import scenes.chess.GameClient;
import scenes.chess.GameClient.GameHost;
import ui.EventHandler.Event;
import ui.Frame;
import ui.Text;
import ui.element.Button;
import ui.element.TextField;
import ui.fonts.Font;
import util.Engine;

class joinGame extends Chess {
    
    // UI Elements
    Text availableText;
    TextField ipField;
    Button joinButton;

    // Buttons for the available games
    ArrayList<Button> hosts = new ArrayList<Button>();

    // Thread for discovering hosts.
    Thread discoveryThread;

    /*
     * Start a seperate thread to discover hosts.
     */
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

    /*
     * Set up the UI elements and start the discovery thread.
     */
    @Override
    public void awake() {
        camera = new Camera();
        setDefaultBackground(30, 30, 30);

        availableText = new Text("Available Games:", new Font("src/assets/fonts/AnimeAce.ttf", 20, true), PRIMARY_LIGHT, Window.getWidth() / 2, 20, 1, true, true);

        client = new GameClient();

        // Text field. Then display the IP on the chess game window.
        ipField = new TextField("127.0.0.1", new Frame(250, 75, 200, 70));
        ipField.tintColor = SECONDARY_DARK.toNormalizedVec4f();
        joinButton = new Button("JOIN", PRIMARY_DARK, PRIMARY_LIGHT, new Frame(455, 75, 95, 70));
        joinButton.tintColor = SECONDARY_DARK.toNormalizedVec4f();
        joinButton.getEventHandler().registerListener(Event.MOUSE_CLICK, (e) -> {
            try {
                Engine.scenes().switchScene(new ChessBoard().defer(() -> {
                    if (client.join("Client :O", ipField.getText().strip())) {
                        // If the join was successful, switch to the chess board scene.
                        // Stop the discovery thread.
                        discoveryThread.stop();
                    } else {
                        // If the join was unsuccessful, restart the discovery thread.
                        Log.warn("CLIENT - Failed to join game.");
                        discoveryThread.start();
                        Engine.scenes().switchScene(new joinGame());
                    }
                    // Rewrite as a try catch
                }));
            } catch (Exception e1) {
                // If the join was unsuccessful, go back to the join scene. 
                // Trying to enter the game will cause it to crash since there is no server.
                Engine.scenes().switchScene(new joinGame());
            }
        });        
        
        startDiscovery();
    }

    private List<InetAddress> addressList;
    /*
     * Discover games hosted on the local network by sending probes to all and listening for game IDs.
     */
    private void discoverHosts () {
        Log.debug("CLIENT - Discovering hosts...");
        addressList = client.kryo().discoverHosts(54777, 5000);

        for (InetAddress ip : addressList) {
            Log.debug("CLIENT - Probing " + ip.getHostAddress());
            if (client.probe(ip.getHostAddress())) {
                Log.debug("CLIENT - Found game at " + ip.getHostAddress());
            } else {
                Log.debug("CLIENT - No game found at " + ip.getHostAddress());
            }
        }            
    }

    /*
     * Display the list of hosts in the UI.
     */
    private void displayHosts () {

        if (client.getGameHosts().isEmpty()) {
            availableText.change("Searching for games...");
        } else {
            // map each host in the list to a button in the buttons list.
            int hostIndex = 0;
            for (GameHost h : client.getGameHosts()) {
                String label = h.hostName + " at " + h.address.getHostName();

                if (hostIndex >= hosts.size()) {
                    Button newButton = new Button(label, PRIMARY_DARK, PRIMARY_LIGHT, new Frame(250, 175 + (hostIndex * 75), 300, 70));
                    newButton.tintColor = SECONDARY_DARK.toNormalizedVec4f();
                    hosts.add(newButton);

                    
                    // Register a listener for the button that will attempt to join the game.
                    hosts.get(hostIndex).getEventHandler().registerListener(Event.MOUSE_CLICK, e -> {
                        Log.debug("CLIENT - Attempting to join " + h.hostName + " at " + h.address.getHostAddress());

                        try {
                            Engine.scenes().switchScene(new ChessBoard().defer(() -> {
                                if (client.join("Client :O", h.address.getHostAddress())) {
                                    // If the join was successful, switch to the chess board scene.
                                    // Stop the discovery thread.
                                    discoveryThread.stop();
                                } else {
                                    // If the join was unsuccessful, restart the discovery thread.
                                    Log.warn("CLIENT - Failed to join game.");
                                    discoveryThread.start();
                                    Engine.scenes().switchScene(new joinGame());
                                }
                            }));
                        } catch (Exception e1) {
                            // If the join was unsuccessful, go back to the join scene. 
                            // Trying to enter the game will cause it to crash since there is no server.
                            Engine.scenes().switchScene(new joinGame());
                        }
                    });
                }             

                // This does not clean up old hosts that are no longer available due to a bug with the rendering system that doesn't allow too many Buttons in the same render context
                // and because of the event system which currently does not have a cleanup/remove method
                // TODO: Fix this if time is available, for now the basic functionality is here, but if a host disapears and someone tries to join, it will cause a crash.                 

                hostIndex++;
            }
        }
    }

    /*
     * Scene update loop.
     */
    @Override
    public void update() {
        if (Keyboard.getKeyDown(Keys.KEY_ESCAPE)) {
            System.exit(0);
        }

        displayHosts();
    }
}