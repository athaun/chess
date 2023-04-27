package scenes.chess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import network.KryoRegister;
import network.requests.JoinRequest;
import network.requests.MoveData;
import network.requests.Probe;
import network.requests.Winner;
import network.responses.ProbeResponse;
import scenes.ChessBoard;
import scenes.pieces.NetData;
import scenes.pieces.Tile;
import util.Engine;
import util.Log;

public class GameClient {

    Client client;
    String name;

    /*
     * Information about a game host.
     */
    public class GameHost {
        public int gameID;
        public InetAddress address;
        public String hostName;

        public GameHost (InetAddress address) {
            this.address = address;
        }

        public GameHost (InetAddress address, int id, String hostName) {
            this.address = address;
            this.gameID = id;
            this.hostName = hostName;
        }
    }

    /*
     * Lists of game hosts for display in the join menu.
     */
    private List<GameHost> gameHosts = new ArrayList<>();
    private List<GameHost> inactiveHosts = new ArrayList<>();

    /*
     * Creates a new client with a listener for moves.
     */
    public GameClient () {
        client = new Client();
        client.start();

        // Create a move listener
        client.addListener(new Listener() {
            public void received (Connection connection, Object req) {
                if (req instanceof MoveData) {
                    MoveData data = (MoveData) req;
                    Log.debug("CLIENT - Move received: " + data.oldX + ", " + data.oldY + " to " + data.newX + ", " + data.newY);
                    ChessBoard.movePiece(data.oldX, data.oldY, data.newX, data.newY, data.type);
                }
            }
        });
    }

    /*
     * Joins a game with the given name and IP address.
     */
    public boolean join(String name, String ip) {
        this.name = name;

        try { 
            client.connect(10 * 1000, ip, 54553, 54777);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        KryoRegister.register(client);
        
        JoinRequest request = new JoinRequest();
        request.name = name;
        
        client.sendTCP(request);

        // Create a listener for the initial board setup
        client.addListener(new Listener() {
            public void received (Connection connection, Object req) {
                if (req instanceof NetData) {
                    NetData data = (NetData) req;
                    Log.debug("CLIENT - Initial board setup received.");

                    // Create a new client side board that will be rendered
                    Tile[][] newBoard = new Tile[8][8];
                    for (int x = 0; x < 8; x++) {
                        for (int y = 0; y < 8; y++) {
                            newBoard[x][y] = new Tile(x, y, data.board[x][y]);
                        }
                    }
                    ChessBoard.board = newBoard;                    
                }
                if (req instanceof Winner){
                    Winner win = (Winner) req;
                    Log.p("Won: " + win.whiteWon);
                    if(win.whiteWon){
                        //((ChessBoard)Engine.scenes().currentScene()).currentTurn.change("White won!");
                        ((ChessBoard)Engine.scenes().currentScene()).turn = "White won!";
                    }
                    else{
                        ((ChessBoard)Engine.scenes().currentScene()).turn = "Black won!";
                        //((ChessBoard)Engine.scenes().currentScene()).currentTurn.change("Black won!");
                    }
                }
            }
        });

        return true;
    }

    /*
     * Sends a move to the server.
     */
    public void sendMove (Tile old, Tile _new) {
        char type = old.getPiece().getCharFromType();

        client.sendTCP(new MoveData(
            old.getX(), old.getY(), type,
            _new.getX(), _new.getY(), type
        ));
    }

    /*
     * Adds a host to the list of inactive hosts.
     * Not really used right now.
     */
    public void addInactiveHost (InetAddress address) {
        inactiveHosts.add(new GameHost(address));
    }

    /*
     * Adds a host to the list of hosts and updates the IP address if it already exists for the join menu.
     */
    public void probeListener () {
        client.addListener(new Listener() {
            public void received (Connection connection, Object req) {
                if (req instanceof ProbeResponse) {
                    ProbeResponse response = (ProbeResponse) req;
                    Log.debug("CLIENT - Probe response with game ID: " + response.gameID + " from " + response.hostName);

                    // Only save a single IP for games with the same gameID
                    if (gameHosts.stream().noneMatch(gameHost -> gameHost.gameID == response.gameID)) {
                        GameHost gameHost = new GameHost(connection.getRemoteAddressTCP().getAddress(), response.gameID, response.hostName);
                        gameHosts.add(gameHost);
                    } else {
                        gameHosts.stream().filter(gameHost -> gameHost.gameID == response.gameID).forEach(gameHost -> {
                            // Update the IP address if it has changed
                            gameHost.address = connection.getRemoteAddressTCP().getAddress();
                        });
                    }      
                                 
                    // TODO: remove the host if inactive
                }
            }
        });
    }

    /*
     * Returns the list of available game hosts.
     */
    public List<GameHost> getGameHosts () {
        return gameHosts;
    }

    /*
     * Send a probe request to the host to get game information.
     */
    public boolean probe (String ip) {
        try {
            client.connect(5000, ip, 54553, 54777);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        KryoRegister.register(client);
        
        Probe request = new Probe();
        
        client.sendTCP(request);

        return true;
    }

    /*
     * Returns the name of the computer.
     */
    public static String getHostName () {
        try {
			return new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("hostname").getInputStream())).readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return "HOSTNAME";
    }

    public void stop() {
        client.stop();
    }

    public Client kryo() {
        return client;
    }
}
