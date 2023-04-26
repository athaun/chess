package scenes;

import graphics.Camera;
import input.Keyboard;
import scenes.chess.GameServer;
import scenes.pieces.Piece;
import scenes.pieces.Tile;
import ui.Text;
import ui.fonts.Font;
import static graphics.Graphics.setDefaultBackground;
import org.lwjgl.glfw.GLFW;

import util.Log;

public class ChessBoard extends Chess {

    // Drawable board
    public static Tile[][] board = new Tile[8][8];
    // Board data for networking
    public static char[][] boardData = new char[8][8];

    Tile currentSelectedTile = null;
    Tile futureSelectedTile = null;

    Text info;
    Text currentTurn;
    static boolean whiteTurn = true;

    String ipText = "IP: " + GameServer.getIp();
    String turn = "Turn: " + currentTurn; //Tile.getTurn();


    public static boolean isServer = false;
    
    /*
     * Called when the scene is first loaded.
     */
    public void awake() {
        camera = new Camera();
        setDefaultBackground(100, 100, 100);

        Log.setLogLevel(Log.ALL);

        Piece.loadSprites("src/assets/images/pack.png");

        info = new Text(ipText, new Font("src/assets/fonts/AnimeAce.ttf", 20, true), PRIMARY_LIGHT, 10, 10);
        currentTurn = new Text(turn, new Font("src/assets/fonts/AnimeAce.ttf", 20, true), PRIMARY_LIGHT, 545, 10);

        isServer = server != null;

        if (isServer) {
            ipText = ipText + " as server.";
        } else {
            ipText = ipText + " as client.";
        }
        info.change(ipText);

        if (isServer) {
            boardData = createBoardData();
            Log.info("SERVER - Board created!");
        } else {
            Log.info("CLIENT - Waiting for board to be recieved from server!");
        }
    }

    /*
     * Called by the server to create the board data which is sent to the client on join.
     */
    public char[][] createBoardData () {
        return Tile.getStartingLayout();
    }

    /*
     * This method is called when the client receives a move from the server.
     */
    public static void movePiece (int oldX, int oldY, int newX, int newY, char type) {
        // Log.debug("CLIENT - Moving piece from " + oldX + ", " + oldY + " to " + newX + ", " + newY);
        if (board[newX][newY].isOccupied()) {
            board[newX][newY].getPiece().removeGameObject();        
        }

        board[newX][newY].setPiece(board[oldX][oldY].getPiece());
        board[oldX][oldY].setPiece(null);

        whiteTurn = !whiteTurn;
        
    }

    private void clientUpdate () {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (board[y][x] == null) continue;

                board[y][x].update();

                if(board[y][x].isPieceClicked() && currentSelectedTile != null) {
                    // If the currently selected tile is not null, then move the piece to the new tile because the user already clicked a tile before
                    // Log.p("Board " + x + ", " + y + " has been moved to new location!");
                    futureSelectedTile = board[y][x];

                    // Request the server to update the board
                    client.sendMove(currentSelectedTile, futureSelectedTile);

                    // // Reset the currently selected tile and the future selected tile
                    currentSelectedTile.setIsPieceClicked(false);
                    futureSelectedTile.setIsPieceClicked(false);

                    // // Reset the currently selected tile and the future selected tile
                    currentSelectedTile = null;
                    futureSelectedTile = null;
                }
                
                if(board[y][x].isPieceClicked() && currentSelectedTile == null && board[y][x].isOccupied()) {
                    // If the currently selected tile is null, then set the currently selected tile to the tile that was clicked
                    currentSelectedTile = board[y][x];
                    // Log.p("Board " + x + ", " + y + " has been selected to move!");
                }

                board[y][x].setIsPieceClicked(false);     
                
                
            }

        }
        if (whiteTurn){
            turn = "Turn: " + "White's turn";
            currentTurn.setColor(PRIMARY_LIGHT);
        }
        else{
            turn = "Turn: " + "Black's turn";
            currentTurn.setColor(PRIMARY_DARK);
        }

        currentTurn.change(turn);
    }

    public void update () {
        // exit the chessboard by pressing escape
        if(Keyboard.getKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
            Log.p("Client shutting down!");
            System.exit(0);
        }
        
        clientUpdate();        
    }
}
