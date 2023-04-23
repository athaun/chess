package scenes;

import graphics.Camera;
import graphics.Window;
import input.Keyboard;
import scenes.chess.GameClient;
import scenes.chess.GameServer;
import scenes.pieces.Piece;
import scenes.pieces.Tile;
import scenes.pieces.Piece.PieceColor;
import ui.Text;
import ui.fonts.Font;
import static graphics.Graphics.setDefaultBackground;
import org.lwjgl.glfw.GLFW;
import util.Log;

public class ChessBoard extends Chess {

    public static Tile[][] board = new Tile[8][8];
    Tile currentSelectedTile = null;
    Tile futureSelectedTile = null;

    Text info;
    Text turn;

    String ipText = "IP: " + GameServer.getIp();

    boolean isServer = false;
    
    public void awake() {
        camera = new Camera();
        setDefaultBackground(100, 100, 100);

        Log.setLogLevel(Log.ALL);

        Piece.loadSprites("src/assets/images/pack.png");

        info = new Text(ipText, new Font("src/assets/fonts/AnimeAce.ttf", 20, true), PRIMARY_LIGHT, 10, 10);

        isServer = server != null;

        if (isServer) {
            ipText = ipText + " as server.";
        } else {
            ipText = ipText + " as client.";
        }
        info.change(ipText);

        if (isServer) {
            board = createBoard();
            Log.info("SERVER - Board created!");
        }
    }

    public Tile[][] createBoard () {
        Tile[][] board = new Tile[8][8];

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

        return board;
    }

    public void update () {

        // exit the chessboard by pressing escape
        if(Keyboard.getKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
            Log.p("Client shutting down!");
            System.exit(0);
        }

        if (isServer) {
            for (int x = 0; x < 8; x++) {
                for (int y = 0; y < 8; y++) {
                    board[x][y].update();
                }
            }
        }

        //board is displayed with y rows first then x columns
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                board[y][x].update();

                if(board[y][x].isPieceClicked() && currentSelectedTile != null) {
                    // If the currently selected tile is not null, then move the piece to the new tile because the user already clicked a tile before
                    Log.p("Board " + x + ", " + y + " has been moved to new location!");
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
                
                if(board[y][x].isPieceClicked() && currentSelectedTile == null && board[y][x].isOccupied()) {
                    // If the currently selected tile is null, then set the currently selected tile to the tile that was clicked
                    currentSelectedTile = board[y][x];
                    Log.p("Board " + x + ", " + y + " has been selected to move!");
                }

                board[y][x].setIsPieceClicked(false);                
            }
        }
    }
}
