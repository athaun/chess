package scenes.chess;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import network.KryoProbe;
import network.KryoProbeResponse;
import network.KryoRegister;
import network.KryoRequest;
import network.KryoResponse;
import util.MathUtils;

public class GameServer {

    Server server = new Server();
    public ArrayList<String> messages = new ArrayList<String>();

    private static int gameID = 0;

    public static String getIp () {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("google.com", 80));
            String ip = socket.getLocalAddress().getHostAddress();
            socket.close();
            return ip;
        } catch (IOException e) {
            e.printStackTrace();
            return "Error Finding IP";
        }
    }

    public void start () {

        gameID = MathUtils.randomInt(100000, 999999);
        
        server.start();
        try {
            server.bind(54553, 54777);
        } catch (IOException e) {
            e.printStackTrace();
        }

        KryoRegister.register(server);

        System.out.println("Server started");

        server.addListener(new Listener() {
            public void received(Connection connection, Object req) {
                // If the request doesn't extend KryoRequest, ignore it.
                if (!(req instanceof KryoRequest)) return;

                if (req instanceof KryoProbe) {
                    KryoProbeResponse response = new KryoProbeResponse();
                    response.gameID = gameID;
                    response.open = true;

                    System.out.println("[SERVER] Responding to probe with ID: " + response.gameID);

                    connection.sendTCP(response);
                }
            }
        });        
    }
}
