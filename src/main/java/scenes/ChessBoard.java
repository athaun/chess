package scenes;

import graphics.Camera;
import graphics.Color;
import graphics.Spritesheet;
import graphics.Window;
import input.Keyboard;
import ui.Text;
import ui.fonts.Font;
import static graphics.Graphics.setDefaultBackground;
import org.joml.Vector2f;
import ecs.GameObject;
import ecs.PointLight;
import ecs.SpriteRenderer;
import org.lwjgl.glfw.GLFW;

class ChessBoard extends Chess{

    GameObject[][] board = new GameObject[8][8];

    GameObject[] blackPawns = new GameObject[8];
    GameObject [] whitePawns = new GameObject[8];

    GameObject blackQueen;
    GameObject whiteQueen;

    GameObject blackKing;
    GameObject whiteKing;

    GameObject blackRookOne;
    GameObject blackRookTwo;
    GameObject whiteRookOne;
    GameObject whiteRookTwo;

    GameObject blackKnightOne;
    GameObject blackKnightTwo;
    GameObject whiteKnightOne;
    GameObject whiteKnightTwo;

    GameObject blackBishopOne;
    GameObject blackBishopTwo;
    GameObject whiteBishopOne;
    GameObject whiteBishopTwo;

    Spritesheet pieces; 

    Text info;
    Text turn;
    
    public void awake() {
        camera = new Camera();
        setDefaultBackground(Color.GRAY);

        int sizeHori = Window.getWidth()/8;
        int sizeVert = Window.getHeight()/9;

        //For loops place the chess board down.
        for (int x = 0; x < 8; x++){
            for (int y = 0; y < 8; y++){
                board[x][y] = new GameObject(new Vector2f(sizeHori* x, sizeHori* y + 100));

                if ((x + y) % 2 == 0)
                board[x][y].addComponent(new SpriteRenderer(tan, new Vector2f(sizeHori, sizeHori)));
                
                else
                board[x][y].addComponent(new SpriteRenderer(brown, new Vector2f(sizeHori, sizeHori)));
            }
    }
    //Adds black pawn pieces.
    for (int x = 0; x < 8; ++x){
        blackPawns[x] = new GameObject(new Vector2f(sizeHori * x + 20 ,sizeVert + 100), 0); 
        blackPawns[x].addComponent(new SpriteRenderer("src/assets/images/black_pawn.png", new Vector2f(56, 96))); //Pawn divided by 5.
    }

    //Adds white pawn pieces. 
    for (int x = 0; x < 8; ++x){
        whitePawns[x] = new GameObject(new Vector2f(sizeHori * x + 20 ,sizeVert + 600), 0); 
        whitePawns[x].addComponent(new SpriteRenderer("src/assets/images/white_pawn.png", new Vector2f(56, 96))); //Pawn divided by 5.
    }

    //Rook Pieces
    blackRookOne = new GameObject(new Vector2f(20 ,sizeVert), 0); 
    blackRookOne.addComponent(new SpriteRenderer("src/assets/images/black_rook.png", new Vector2f(56, 96)));
    blackRookTwo = new GameObject(new Vector2f(720 ,sizeVert), 0); 
    blackRookTwo.addComponent(new SpriteRenderer("src/assets/images/black_rook.png", new Vector2f(60, 100)));

    whiteRookOne = new GameObject(new Vector2f(20 ,sizeVert + 700), 0); 
    whiteRookOne.addComponent(new SpriteRenderer("src/assets/images/white_rook.png", new Vector2f(56, 96)));
    whiteRookTwo = new GameObject(new Vector2f(720 ,sizeVert + 700), 0); 
    whiteRookTwo.addComponent(new SpriteRenderer("src/assets/images/white_rook.png", new Vector2f(60, 100)));

    //Knight Pieces
    blackKnightOne = new GameObject(new Vector2f(120,sizeVert)); 
    blackKnightOne.addComponent(new SpriteRenderer("src/assets/images/black_knight.png", new Vector2f(56, 96)));
    blackKnightTwo = new GameObject(new Vector2f(620,sizeVert)); 
    blackKnightTwo.addComponent(new SpriteRenderer("src/assets/images/black_knight.png", new Vector2f(56, 96)));

    whiteKnightOne = new GameObject(new Vector2f(120,sizeVert + 700)); 
    whiteKnightOne.addComponent(new SpriteRenderer("src/assets/images/white_knight.png", new Vector2f(56, 96)));
    whiteKnightTwo = new GameObject(new Vector2f(620, sizeVert + 700)); 
    whiteKnightTwo.addComponent(new SpriteRenderer("src/assets/images/white_knight.png", new Vector2f(56, 96)));
    
    //Goes after knight on board. 
    blackBishopOne = new GameObject(new Vector2f(220,sizeVert)); 
    blackBishopOne.addComponent(new SpriteRenderer("src/assets/images/black_bishop.png", new Vector2f(56, 100)));
    blackBishopTwo = new GameObject(new Vector2f(520,sizeVert)); 
    blackBishopTwo.addComponent(new SpriteRenderer("src/assets/images/black_bishop.png", new Vector2f(56, 100)));
    
    whiteBishopTwo = new GameObject(new Vector2f(0,0)); 
    whiteBishopTwo.addComponent(new SpriteRenderer("src/assets/images/black_bishop.png", new Vector2f(56, 100)));
    
    /*
    blackQueen = new GameObject(new Vector2f(320,sizeVert)); 
    blackQueen.addComponent(new SpriteRenderer("src/assets/images/black_queen.png", new Vector2f(56, 100)));
    whiteBishopOne = new GameObject(new Vector2f(0,0)); 
    whiteBishopOne.addComponent(new SpriteRenderer("src/assets/images/white_bishop.png", new Vector2f(56, 96)));//56,100
    whiteBishopTwo = new GameObject(new Vector2f(520,sizeVert + 600)); 
    whiteBishopTwo.addComponent(new SpriteRenderer("src/assets/images/white_bishop.png", new Vector2f(56, 100)));
    //Queen Pieces
    whiteQueen = new GameObject(new Vector2f(320,sizeVert+700)); 
    whiteQueen.addComponent(new SpriteRenderer("src/assets/images/black_queen.png", new Vector2f(56,96 )));
    
    whiteQueen = new GameObject(new Vector2f(320,sizeVert)); 
    whiteQueen.addComponent(new SpriteRenderer("src/assets/images/white_queen.png", new Vector2f(10, 10)));
    
    blackKing = new GameObject(new Vector2f(420,sizeVert)); 
    blackKing.addComponent(new SpriteRenderer("src/assets/images/black_king.png", new Vector2f(56, 100)));
    */
    
        info = new Text("Your IP is:", new Font("src/assets/fonts/AnimeAce.ttf", 20, true), offWhite, 10, 10);
        turn = new Text("Turn:", new Font("src/assets/fonts/AnimeAce.ttf", 20, true), offWhite, Window.getWidth()/2, 10);
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
