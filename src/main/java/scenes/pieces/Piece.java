package scenes.pieces;

import org.joml.Vector2f;
import org.joml.Vector2i;

import ecs.GameObject;
import ecs.SpriteRenderer;
import graphics.Spritesheet;
import graphics.Texture;
import util.Engine;

public class Piece {
    public enum PieceType {
        PAWN, ROOK, KNIGHT, BISHOP, QUEEN, KING
    }

    public enum PieceColor {
        WHITE, BLACK
    }

    private Vector2i position;
    private PieceType type;
    private PieceColor color;
    private GameObject gameObject;

    private static Spritesheet spritesheet;

    public static void loadSprites (String path) {
        spritesheet = new Spritesheet(new Texture(path), 280, 600, 12, 0);
    }

    public Piece(int x, int y, PieceType type, PieceColor color) {
        this.position = new Vector2i(x, y);
        this.type = type;
        this.color = color;
    }

    public void calculateSprite (int x, int y, int tileSize) {
        float scalar = (float)(graphics.Window.getWidth() / (tileSize * 1.9));
        this.gameObject = new GameObject(this.color == PieceColor.WHITE ? "White " : "Black " + this.type.toString(), new Vector2f(x * tileSize + scalar * 4, y * tileSize + tileSize / 3), 100);

        SpriteRenderer spriteRenderer = null;
        int spriteIndex = 0;

        // Calculate sprite index based on color and type
        if (color == PieceColor.WHITE) {
            switch (type) {
                case BISHOP:
                    spriteIndex = 6;
                    break;
                case KING:
                    spriteIndex = 7;
                    break;
                case KNIGHT:
                    spriteIndex = 8;
                    break;
                case PAWN:
                    spriteIndex = 9;
                    break;
                case QUEEN:
                    spriteIndex = 10;
                    break;
                case ROOK:
                    spriteIndex = 11;
                    break;
            }
        } else {
            switch (type) {
                case BISHOP:
                    spriteIndex = 0;
                    break;
                case KING:
                    spriteIndex = 1;
                    break;
                case KNIGHT:
                    spriteIndex = 2;
                    break;
                case PAWN:
                    spriteIndex = 3;
                    break;
                case QUEEN:
                    spriteIndex = 4;
                    break;
                case ROOK:
                    spriteIndex = 5;
                    break;
            }
        }

        // Create sprite renderer with calculated sprite index and position
        spriteRenderer = new SpriteRenderer(spritesheet.getSprite(spriteIndex), new Vector2f(280, 600));
        // Scale sprite
        spriteRenderer.setSize(new Vector2f(280 / scalar, 600 / scalar));
        // Add sprite renderer to game object
        this.gameObject.addComponent(spriteRenderer);
    }

    public Vector2i getPosition() {
        return position;
    }

    public void setPosition(Vector2i position) {
        this.position = position;
    }

    public PieceType getType() {
        return type;
    }

    public PieceColor getColor () {
        return color;
    }
}
