package network.requests;

public class Winner extends KryoRequest {
    public boolean whiteWon;
    public Winner (){

    }
    public Winner(boolean w){
        whiteWon = w;
    }
}
