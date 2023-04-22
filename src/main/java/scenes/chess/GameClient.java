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

    public void join(String name, String ip) {
        this.name = name;

        try {
            client.connect(5000, ip, 54553, 54777);
        } catch (IOException e) {
            e.printStackTrace();
        }

        KryoRegister.register(client);
        
        KryoRequest request = new KryoRequest();
        request.text = "Request sent from " + name + " on join!";
        
        client.sendTCP(request);
    }

    public class GameHost {
        public int gameID;
        public List<InetAddress> addressList;
    }

    private List<GameHost> gameHosts = new ArrayList<>();
    public void probeListener () {
        client.addListener(new Listener() {
            public void received (Connection connection, Object req) {
                if (req instanceof KryoProbeResponse) {
                    KryoProbeResponse response = (KryoProbeResponse) req;
                    System.out.println("Response with game ID: " + response.gameID);

                    // Only save a single IP for games with the same gameID
                    if (gameHosts.stream().noneMatch(gameHost -> gameHost.gameID == response.gameID)) {
                        GameHost gameHost = new GameHost();
                        gameHost.gameID = response.gameID;
                        gameHost.addressList = new ArrayList<>();
                        gameHost.addressList.add(connection.getRemoteAddressTCP().getAddress());
                        gameHosts.add(gameHost);
                    }
                    for (GameHost gameHost : gameHosts) {
                        if (gameHost.gameID == response.gameID) {
                            gameHost.addressList.add(connection.getRemoteAddressTCP().getAddress());
                            return;
                        }
                    }                    
                }
            }
        });
    }

    public List<GameHost> getGameHosts () {
        return gameHosts;
    }

    public void probe (String ip) {
        try {
            client.connect(5000, ip, 54553, 54777);
        } catch (IOException e) {
            e.printStackTrace();
        }

        KryoRegister.register(client);
        
        KryoProbe request = new KryoProbe();
        
        client.sendTCP(request);
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
