package scenes.chess;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import network.KryoProbe;
import network.KryoProbeResponse;
import network.KryoRegister;
import network.KryoRequest;

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
            client.connect(5000, ip, 54553, 54777);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        KryoRegister.register(client);
        
        KryoRequest request = new KryoRequest();
        request.text = "Request sent from " + name + " on join!";
        
        client.sendTCP(request);

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
                if (req instanceof KryoProbeResponse) {
                    KryoProbeResponse response = (KryoProbeResponse) req;
                    System.out.println("[CLIENT] Probe response with game ID: " + response.gameID);

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
                    System.out.println("[CLIENT] Probe response with invalid game ID: " + Object.class);
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
        
        KryoProbe request = new KryoProbe();
        
        client.sendTCP(request);

        return true;
    }

    int i = 0;
    public void send_i() {
        i ++;
        KryoRequest request = new KryoRequest();
        request.text = "Request from " + name + " | " + i;
        
        client.sendTCP(request);
    }

    public void stop() {
        client.stop();
    }

    public Client kryo() {
        return client;
    }
}
