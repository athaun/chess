package scenes.chess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import network.KryoRegister;
import network.requests.JoinRequest;
import network.requests.KryoRequest;
import network.requests.MoveData;
import network.requests.Probe;
import network.responses.InitialSetup;
import network.responses.ProbeResponse;
import scenes.Chess;
import scenes.ChessBoard;
import scenes.pieces.NetData;
import util.Engine;
import util.Log;
import util.MathUtils;

import static scenes.ChessBoard.*;

public class GameServer {

    Server server = new Server();
    
    private List<Connection> clients = new ArrayList<Connection>();
    
    private static int gameID = 0;
    
    boolean whiteTurn = true;
    /*
     * Starts the server.
     */
    public void start () {
        // Generate a random game ID
        gameID = MathUtils.randomInt(100000, 999999);
        
        // Start the server on UDP and TCP ports 54553 and 54777 respectively.
        server.start();
        try {
            server.bind(54553, 54777);
        } catch (IOException e) {
            Log.info("SERVER - Failed to bind to ports 54553 and/or 54777. Check to make sure they are not in use by another program or instance of this program.");
            e.printStackTrace();
            System.exit(1);
        }

        KryoRegister.register(server);

        server.addListener(new Listener() {
            public void received(Connection connection, Object req) {
                // If the request doesn't extend KryoRequest, ignore it.
                if (!(req instanceof KryoRequest)) return;

                if (req instanceof Probe) {
                    probe(connection, (Probe) req);
                }

                if (req instanceof JoinRequest) {
                    addClient(connection, (JoinRequest) req);
                }

                if (req instanceof MoveData) {
                    validateMove(connection, (MoveData) req);
                }
            }
        });  
    }

    /*
     * Validates a move.
     */
    private void validateMove (Connection connection, MoveData move) {
        Log.info("SERVER - Validating move from client with ID " + gameID);

        if (clients.size() < 2) {
            Log.info("SERVER - Client attempted to move, but there are not enough players.");
            return;
        }

        // Check if the move is valid.
        // !!!

        // Send the move to all clients.
        for (Connection client : clients) {
            client.sendTCP(move);
        }
    }

    /*
     * Adds a client to the server.
     */
    private void addClient (Connection connection, JoinRequest request) {

        if (clients.size() >= 2) {
            Log.info("SERVER - Client " + request.name + " attempted to join, but the server is full.");
            // return;
            Log.warn("IF YOU SEE THIS, IT MEANS THE SERVER IS FULL BUT YOU ARE STILL ABLE TO JOIN. THIS IS TEMPORARY AND WILL BE CHANGED IN THE FUTURE.");
        }

        // Send the client the initial setup of the board.
        NetData setup = new NetData(ChessBoard.boardData);
        Log.info("SERVER - Sending initial setup to client " + request.name + " with ID " + gameID);
        
        for (int y = 0; y < 8; y ++) {
            for (int x = 0; x < 8; x ++) {
                System.out.print(" " + setup.board[x][y]);
            }
            System.out.println();
        }
        
        connection.sendTCP(setup);

        // Add the client to the list of clients.
        clients.add(connection);

        Log.info("SERVER - Client " + request.name + " has joined the server.");
    }

    /*
     * Responds to a probe request.
     */
    private void probe (Connection connection, Probe probe) {
        ProbeResponse response = new ProbeResponse();
        response.gameID = gameID;
        response.open = true;
        response.hostName = Chess.computerName;

        Log.info("SERVER - Responding to probe with ID: " + response.gameID);

        connection.sendTCP(response);
    }

    /*
     * Returns the IP of the computer running this program.
     */
    public static String getIp () {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("google.com", 80));
            String ip = socket.getLocalAddress().getHostAddress();
            socket.close();
            return ip;
        } catch (IOException e) {
            Log.warn("Error finding IP address.");
            e.printStackTrace();
            return "Error Finding IP";
        }
    }
}
