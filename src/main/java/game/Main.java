package game;

import ecs.GameObject;
import ecs.SpriteRenderer;
import graphics.Camera;
import graphics.Color;
import graphics.Graphics;
import input.Keyboard;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import scene.Scene;
import ui.EventHandler;
import ui.Frame;
import ui.Text;
import ui.element.Button;
import util.Engine;
import util.Log;

public class Main extends Scene {
    public static void main(String[] args) {
        Engine.init(400, 600, "Game", 1);
        Engine.scenes().switchScene(new Main());
        Engine.showWindow();
    }

    float[][] terrain;

    GameObject ship;
    EarthMap map;

    Text menuText;
    Button serverBtn, clientBtn;

    public void awake() {
        camera = new Camera();
        Graphics.setDefaultBackground(new Color(30));

        ship = new GameObject(new Vector2f(700, 300), 3);
        ship.addComponent(new SpriteRenderer("src/assets/images/ships/ShipCarrierHull.png", new Vector2f(57 / 2, 189 / 2)));

        // map = new EarthMap("src/assets/images/worldMapSpiral.png");

        menuText = new Text("Select server or client", Color.WHITE, 20, 20);
        
        serverBtn = new Button("Server", Color.BLUE, Color.WHITE, new Frame(20, 50, 150, 25));
        serverBtn.tintColor = Color.GRAY.toNormalizedVec4f();
        uiRenderer.add(serverBtn);
        addUIElement(serverBtn);
        
        serverBtn.getEventHandler().registerListener(EventHandler.Event.MOUSE_CLICK, e -> {
            Log.info("Starting server.");
            Engine.scenes().switchScene(new GameServer());
        });


        clientBtn = new Button("Client", Color.RED, Color.WHITE, new Frame(20, 100, 150, 25));
        clientBtn.tintColor = Color.GRAY.toNormalizedVec4f();
        uiRenderer.add(clientBtn);
        addUIElement(clientBtn);

        clientBtn.getEventHandler().registerListener(EventHandler.Event.MOUSE_CLICK, e -> {
            Log.info("Starting client.");
            Engine.scenes().switchScene(new GameClient());
        });      
    }

    public void update() {
        // map.mapImage.getComponent(SpriteRenderer.class).setSize(new Vector2f(Window.getWidth(), Window.getHeight()));

        if (Keyboard.getKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
            System.exit(1);
        }
    }
}


