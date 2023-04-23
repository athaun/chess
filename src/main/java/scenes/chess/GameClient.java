package scenes.chess;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import network.KryoRegister;
import network.requests.JoinRequest;
import network.requests.Probe;
import network.responses.InitialSetup;
import network.responses.ProbeResponse;
import scenes.ChessBoard;
import scenes.pieces.NetData;
import scenes.pieces.Tile;
import util.Log;

public class GameClient {

    Client client;
    String name;

    public GameClient () {
        client = new Client();
        client.start();
    }

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

        client.addListener(new Listener() {
            public void received (Connection connection, Object req) {
                if (req instanceof InitialSetup) {
                    NetData data = (NetData) req;
                    Log.debug(" Initial setup received.");

                    Tile[][] newBoard = new Tile[8][8];
                    for (int x = 0; x < 8; x++) {
                        for (int y = 0; y < 8; y++) {
                            if (data.board[x][y] == ' ') {
                                newBoard[x][y] = new Tile(x, y);
                            } else {
                                newBoard[x][y] = new Tile(x, y, data.board[x][y]);
                            }
                        }
                    }
                    ChessBoard.board = newBoard;                    
                }
            }
        });

        return true;
    }

    public class GameHost {
        public int gameID;
        public InetAddress address;

        public GameHost (InetAddress address) {
            this.address = address;
        }

        public GameHost (InetAddress address, int id) {
            this.address = address;
            this.gameID = id;
        }
    }

    private List<GameHost> gameHosts = new ArrayList<>();
    private List<GameHost> inactiveHosts = new ArrayList<>();

    public void addInactiveHost (InetAddress address) {
        inactiveHosts.add(new GameHost(address));
    }
    public void probeListener () {
        client.addListener(new Listener() {
            public void received (Connection connection, Object req) {
                if (req instanceof ProbeResponse) {
                    ProbeResponse response = (ProbeResponse) req;
                    Log.debug(" Probe response with game ID: " + response.gameID);

                    // Only save a single IP for games with the same gameID
                    if (gameHosts.stream().noneMatch(gameHost -> gameHost.gameID == response.gameID)) {
                        GameHost gameHost = new GameHost(connection.getRemoteAddressTCP().getAddress(), response.gameID);
                        gameHosts.add(gameHost);
                    } else {
                        gameHosts.stream().filter(gameHost -> gameHost.gameID == response.gameID).forEach(gameHost -> {
                            // Update the IP address if it has changed
                            gameHost.address = connection.getRemoteAddressTCP().getAddress();
                        });
                    }      
                                 
                    // remove the host is inactive
                    if (inactiveHosts.stream().anyMatch(gameHost -> gameHost.address == connection.getRemoteAddressTCP().getAddress())) {
                        // TODO @Asher: make this work later
                        inactiveHosts.removeIf(gameHost -> gameHost.address == connection.getRemoteAddressTCP().getAddress());
                        gameHosts.removeIf(gameHost -> gameHost.address == connection.getRemoteAddressTCP().getAddress());
                    }
                    
                } else {
                    Log.debug(" Probe response with invalid game ID: " + Object.class);
                }
            }
        });
    }

    public List<GameHost> getGameHosts () {
        return gameHosts;
    }

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

    public void stop() {
        client.stop();
    }

    public Client kryo() {
        return client;
    }
}
