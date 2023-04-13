package scenes.chess;

import java.io.IOException;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import ecs.GameObject;
import ecs.SpriteRenderer;
import graphics.Camera;
import graphics.Color;
import graphics.Graphics;
import input.Keyboard;
import network.KryoRegister;
import network.SomeRequest;
import network.SomeResponse;
import scene.Scene;
import ui.Text;

public class GameServer extends Scene {

    Server server = new Server();
    Text requestText;
    
    GameObject ship;

    public void awake () {
        camera = new Camera();
        Graphics.setDefaultBackground(graphics.Color.BLUE);
        
        server.start();
        try {
            server.bind(54555, 54777);
        } catch (IOException e) {
            e.printStackTrace();
        }

        KryoRegister.register(server);

        requestText = new Text("No requests read", Color.WHITE, 20, 20);

        server.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof SomeRequest) {
                    SomeRequest request = (SomeRequest) object;
                    
                    System.out.println(request.text);
                    requestText.change(request.text);

                    SomeResponse response = new SomeResponse();
                    response.text = "Hello World!";
                    connection.sendTCP(response);
                }
            }
        });

        ship = new GameObject(new Vector2f(700, 300), 3);
        ship.addComponent(new SpriteRenderer("src/assets/images/ships/ShipCarrierHull.png", new Vector2f(57 / 2, 189 / 2)));
        
    }

    public void update () {
        if (Keyboard.getKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
            System.exit(1);
        }
    }    
}
