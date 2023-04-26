package network.requests;

/*
 * This class is used to send a move request to the server.
 */
public class MoveData extends KryoRequest {
    public int oldX, oldY;
    public int newX, newY;
    public char type;

    public MoveData () {

    }

    public MoveData (int oldX, int oldY, char oldType, int newX, int newY, char newType) {
        this.oldX = oldX;
        this.oldY = oldY;

        this.newX = newX;
        this.newY = newY;
        
        this.type = oldType;
    }
}
