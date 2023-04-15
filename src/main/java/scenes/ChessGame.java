/*package scenes;

import graphics.Camera;
import graphics.Color;
import graphics.Window;
import input.Keyboard;
import scene.Scene;
import scenes.chess.GameClient;
import scenes.chess.GameServer;
import ui.Frame;
import ui.Text;
import ui.EventHandler.Event;
import ui.element.Button;
import ui.element.TextField;
import ui.fonts.Font;
import util.Engine;
import util.Log;

import static graphics.Graphics.setDefaultBackground;

import java.util.Vector;

import org.joml.Vector2f;

import ecs.GameObject;
import ecs.SpriteRenderer;

import org.lwjgl.glfw.GLFW;

class ChessGame extends Scene{

        GameObject[][] board = new GameObject[8][8];

        GameServer server;
        GameClient client;

        Text info;
        
        public void awake() {
            camera = new Camera();
            setDefaultBackground(Color.GRAY);

            int size = Window.getWidth()/8;

            for (int x = 0; x < 8; x++){
                for (int y = 0; y < 8; y++){
                    board[x][y] = new GameObject(new Vector2f(size* x, size * y + 100));
                    board[x][y].addComponent(new SpriteRenderer(Color.randomColor(), new Vector2f(size, size)));
                }
            }

            info = new Text("Chess Board :D", new Font("src/assets/fonts/AnimeAce.ttf", 20, true), Color.BLACK, 10, 10);
        }

        public void update () {
            if (Keyboard.getKeyDown(GLFW.GLFW_KEY_SPACE)) {
                client.send_i();
                System.out.println("Client sending!");
            }
            if (server != null) {
                String s = "";
                for (String i : server.messages) {
                    s = i;
                }
                info.change(s);
            }
        }
    }
    */