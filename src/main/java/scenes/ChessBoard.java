package scenes;

import graphics.Camera;
import graphics.Window;
import input.Keyboard;
import scenes.chess.GameServer;
import scenes.pieces.Piece;
import scenes.pieces.Tile;
import scenes.pieces.Piece.PieceColor;
import ui.Text;
import ui.fonts.Font;
import static graphics.Graphics.setDefaultBackground;
import org.lwjgl.glfw.GLFW;

class ChessBoard extends Chess {

    Tile[][] board = new Tile[8][8];

    Text info;
    Text turn;

    String ipText = "IP: " + GameServer.getIp();
    
    public void awake() {
        camera = new Camera();
        setDefaultBackground(100, 100, 100);

        Piece.loadSprites("src/assets/images/pack.png");

        int tileSize = Window.getHeight() / 9;

        // Create the board
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                // Alternate the color of the tiles
                PieceColor color = (x + y) % 2 == 0 ? PieceColor.WHITE : PieceColor.BLACK;
                // Create the tile
                board[x][y] = new Tile(x, y, tileSize, color);
            }
        }    
        
        info = new Text(ipText, new Font("src/assets/fonts/AnimeAce.ttf", 20, true), offWhite, 10, 10);
    }

    public void update () {
        if (Keyboard.getKeyDown(GLFW.GLFW_KEY_SPACE)) {
            client.send_i();
            System.out.println("Client sending!");
        }

        // exit the chessboard by pressing escape
        if(Keyboard.getKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
            System.out.println("Client shutting down!");
            System.exit(0);
        }

        if (server != null) {
            String s = "";
            for (String i : server.messages) {
                s = i;
            }
            info.change(s);
        }

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                board[x][y].update();
            }
        }
    }
}
