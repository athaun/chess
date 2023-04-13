package scenes.chess;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;

import com.esotericsoftware.kryonet.Client;

import graphics.Camera;
import graphics.Graphics;
import input.Keyboard;
import network.KryoRegister;
import network.KryoRequest;
import scene.Scene;

public class GameClient {

    Client client;
    String name;

    public void join (String name, String ip) {

        this.name = name;

        client = new Client();
        client.start();
        try {
            client.connect(5000, ip, 54555, 54777);
        } catch (IOException e) {
            e.printStackTrace();
        }

        KryoRegister.register(client);
        
        KryoRequest request = new KryoRequest();
        request.text = "Request sent from " + name + " on join!";
        
        client.sendTCP(request);
    }

    int i = 0;
    public void send_i () {
            i ++;
            KryoRequest request = new KryoRequest();
            request.text = "Request from " + name + " | " + i;
            
            client.sendTCP(request);
    }
}
