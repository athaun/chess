package scenes.chess;

import java.io.IOException;
import java.util.ArrayList;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import network.KryoRegister;
import network.KryoRequest;
import network.KryoResponse;

public class GameServer {

    Server server = new Server();
    public ArrayList<String> messages = new ArrayList<String>();

    public void start () {
        
        server.start();
        try {
            server.bind(54553, 5477);
        } catch (IOException e) {
            e.printStackTrace();
        }

        KryoRegister.register(server);

        System.out.println("Server started");

        server.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof KryoRequest) {
                    KryoRequest request = (KryoRequest) object;
                    
                    System.out.println(request.text);
                    messages.add(request.text);

                    KryoResponse response = new KryoResponse();
                    response.text = "Hello World!";
                    connection.sendTCP(response);
                }
            }
        });        
    }
}
