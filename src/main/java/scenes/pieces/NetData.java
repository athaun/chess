package scenes.pieces;

public class NetData {
    public char[][] board;

    public NetData() {
    
    }
    
    public NetData(Tile[][] board) {
        // Convert a tile array to a char array
        this.board = new char[8][8];
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (board[x][y] == null) {
                    this.board[x][y] = ' ';
                    continue;
                }

                if (board[x][y].getPiece() != null) {
                    this.board[x][y] = board[x][y].getPiece().getCharFromType();
                } else {
                    this.board[x][y] = ' ';
                }
            }
        }
    }

    public NetData (char[][] board) {
        this.board = board;
    }
}
