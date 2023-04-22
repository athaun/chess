package network.responses;

import scenes.pieces.Tile;

/*
 * This class is used to send the initial setup of the board to the client on join request along with some other information about the client's permissions.
 */
public class InitialSetup extends KryoResponse {
    public Tile[][] board = new Tile[8][8];
    public boolean isClientObserver = false;
    public boolean isClientAllowed = true;

    public InitialSetup(Tile[][] board, boolean isClientObserver) {
        this.board = board;
    }
}
