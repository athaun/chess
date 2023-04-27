package scenes.pieces;

import org.joml.Vector2f;

import ecs.GameObject;
import ecs.SpriteRenderer;
import graphics.Color;
import graphics.Window;
import input.Mouse;
import scenes.pieces.Piece.PieceColor;
import scenes.pieces.Piece.PieceType;

public class Tile {

    public static final char EMPTY_TILE = 0;
    // Starting layout of the board with black on top and white on bottom, null means empty
    private static Piece[][] startingLayout = {
        {new Piece(0, 0, PieceType.ROOK, PieceColor.BLACK), new Piece(1, 0, PieceType.KNIGHT, PieceColor.BLACK), new Piece(2, 0, PieceType.BISHOP, PieceColor.BLACK), new Piece(3, 0, PieceType.QUEEN, PieceColor.BLACK), new Piece(4, 0, PieceType.KING, PieceColor.BLACK), new Piece(5, 0, PieceType.BISHOP, PieceColor.BLACK), new Piece(6, 0, PieceType.KNIGHT, PieceColor.BLACK), new Piece(7, 0, PieceType.ROOK, PieceColor.BLACK)},
        {new Piece(0, 1, PieceType.PAWN, PieceColor.BLACK), new Piece(1, 1, PieceType.PAWN, PieceColor.BLACK),   new Piece(2, 1, PieceType.PAWN, PieceColor.BLACK),   new Piece(3, 1, PieceType.PAWN, PieceColor.BLACK),  new Piece(4, 1, PieceType.PAWN, PieceColor.BLACK), new Piece(5, 1, PieceType.PAWN, PieceColor.BLACK),   new Piece(6, 1, PieceType.PAWN, PieceColor.BLACK),   new Piece(7, 1, PieceType.PAWN, PieceColor.BLACK)},
        {null,                                              null,                                                null,                                                null,                                               null,                                              null,                                                null,                                                null},
        {null,                                              null,                                                null,                                                null,                                               null,                                              null,                                                null,                                                null},
        {null,                                              null,                                                null,                                                null,                                               null,                                              null,                                                null,                                                null},
        {null,                                              null,                                                null,                                                null,                                               null,                                              null,                                                null,                                                null},
        {new Piece(0, 6, PieceType.PAWN, PieceColor.WHITE), new Piece(1, 6, PieceType.PAWN, PieceColor.WHITE),   new Piece(2, 6, PieceType.PAWN, PieceColor.WHITE),   new Piece(3, 6, PieceType.PAWN, PieceColor.WHITE),  new Piece(4, 6, PieceType.PAWN, PieceColor.WHITE), new Piece(5, 6, PieceType.PAWN, PieceColor.WHITE),   new Piece(6, 6, PieceType.PAWN, PieceColor.WHITE),   new Piece(7, 6, PieceType.PAWN, PieceColor.WHITE)},
        {new Piece(0, 7, PieceType.ROOK, PieceColor.WHITE), new Piece(1, 7, PieceType.KNIGHT, PieceColor.WHITE), new Piece(2, 7, PieceType.BISHOP, PieceColor.WHITE), new Piece(3, 7, PieceType.QUEEN, PieceColor.WHITE), new Piece(4, 7, PieceType.KING, PieceColor.WHITE), new Piece(5, 7, PieceType.BISHOP, PieceColor.WHITE), new Piece(6, 7, PieceType.KNIGHT, PieceColor.WHITE), new Piece(7, 7, PieceType.ROOK, PieceColor.WHITE)}
    };

    public static char[][] getStartingLayout() {
        char[][] layout = new char[8][8];
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (startingLayout[y][x] == null) {
                    layout[x][y] = ' ';
                    continue;
                }
                layout[x][y] = startingLayout[y][x].getCharFromType();
            }
        }

        return layout;
    }
    
    private int x, y;
    private int renderX, renderY;
    private int size;
    private Piece piece;

    private boolean light = false;
    private boolean isPieceClicked = false;

    private Color black = new Color(193, 114, 86);
    private Color blackHovered = new Color(170, 104, 76);
    private Color white = new Color(251, 223, 188);
    private Color whiteHovered = new Color(230, 210, 170);

    private GameObject gameObject;
    private SpriteRenderer spriteRenderer;

    public Tile (int x, int y, char piece) {
        this.x = x;
        this.y = y;
        this.size = Window.getHeight() / 9;

        this.renderX = x * size;
        this.renderY = y * size + size;

        this.light = (x + y) % 2 == 0;

        // Log.p("Creating tile at " + x + ", " + y + " with size " + size + " and render position " + renderX + ", " + renderY + "");

        this.gameObject = new GameObject("tile " + x + ", " + y, new Vector2f(renderX, renderY), 1);
        spriteRenderer = new SpriteRenderer(this.light ? white : black, new Vector2f(size));
        this.gameObject.addComponent(spriteRenderer);

        if (piece != ' ') {
            this.piece = Piece.getPieceFromChar(piece, x, y);
            this.piece.calculateSprite(x, y, size);
        }
    }

    public String getTurn(){
        return nextTurn;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
        if (piece != null) {
            this.piece.calculateSprite(x, y, size);
        }
    }

    public boolean isOccupied() {
        return piece != null;
    }

    public boolean isPieceClicked(){
        return isPieceClicked;
    }

    public void setIsPieceClicked(boolean isPieceClicked){
        this.isPieceClicked = isPieceClicked;
    }

    public boolean isOccupiedBy(PieceColor color) {
        return isOccupied() && piece.getColor() == color;
    }

    public void update() {
        checkClick();
    }

    public GameObject getGameObject() {
        return gameObject;
    }

    private boolean pMouseDown = false;

    public boolean checkClick () {
        if (Mouse.mouseX > renderX && Mouse.mouseY > renderY && Mouse.mouseX < renderX + size && Mouse.mouseY < renderY + size) {
            // Mouse is inside the tile
            if (light) {
                spriteRenderer.setColor(whiteHovered);
            } else {
                spriteRenderer.setColor(blackHovered);
            }

            if (Mouse.mouseButtonDown(0) && !pMouseDown) {
                // Left mouse button is pressed
                boolean isPieceClicked = true;
                pMouseDown = true;
                return true;
            } else if (!Mouse.mouseButtonDown(0) && pMouseDown) {
                // Left mouse button is released
                pMouseDown = false;
            }
        } else {
            if (light) {
                spriteRenderer.setColor(white);
            } else {
                spriteRenderer.setColor(black);
            }
        }
        return false;
    }

    public boolean isPieceClicked() {
        return false;
    }
    public void setIsPieceClicked(boolean b) {
               isPieceClicked = b;
            }
         
        }

