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
    Tile currentSelectedTile = null;
    Tile futureSelectedTile = null;

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

        //board is displayed with y rows first then x columns
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                board[y][x].update();

                if(board[y][x].isPieceClicked() && currentSelectedTile != null) {
                    // If the currently selected tile is not null, then move the piece to the new tile because the user already clicked a tile before
                    System.out.println("Board " + x + ", " + y + " has been moved to new location!");
                    futureSelectedTile = board[y][x];

                    // Move the piece to the new tile
                    futureSelectedTile.setPiece(currentSelectedTile.getPiece());
                    currentSelectedTile.setPiece(null);

                    // Reset the currently selected tile and the future selected tile
                    currentSelectedTile.setIsPieceClicked(false);
                    futureSelectedTile.setIsPieceClicked(false);

                    // Reset the currently selected tile and the future selected tile
                    currentSelectedTile = null;
                    futureSelectedTile = null;
                }              
                
                if(board[y][x].isPieceClicked() && currentSelectedTile == null) {
                    // If the currently selected tile is null, then set the currently selected tile to the tile that was clicked
                    currentSelectedTile = board[y][x];
                    System.out.println("Board " + x + ", " + y + " has been selected to move!");
                }

                board[y][x].setIsPieceClicked(false);                
            }
        }
    }
}
