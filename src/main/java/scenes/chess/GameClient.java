package scenes.chess;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;

import com.esotericsoftware.kryonet.Client;

import graphics.Camera;
import graphics.Graphics;
import input.Keyboard;
import network.KryoRegister;
import network.SomeRequest;
import scene.Scene;

public class GameClient extends Scene {

    Client client;

    public void awake () {
        camera = new Camera();
        Graphics.setDefaultBackground(graphics.Color.RED);

        client = new Client();
        client.start();
        try {
            client.connect(5000, "0.0.0.0", 54555, 54777);
        } catch (IOException e) {
            e.printStackTrace();
        }

        KryoRegister.register(client);
        
        SomeRequest request = new SomeRequest();
        request.text = "Here is the request";
        
        client.sendTCP(request);
    }

    int i = 0;
    public void update () {
        if (Keyboard.getKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
            System.exit(1);
        }

        if (Keyboard.getKeyUp(GLFW.GLFW_KEY_SPACE)) {
            i ++;
            SomeRequest request = new SomeRequest();
            request.text = "Request " + i;
            
            client.sendTCP(request);
        }
    }
}
