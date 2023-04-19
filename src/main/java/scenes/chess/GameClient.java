package scenes.chess;

import java.io.IOException;

import com.esotericsoftware.kryonet.Client;

import network.KryoProbe;
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
