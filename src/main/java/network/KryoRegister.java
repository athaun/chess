package network;

import org.joml.Vector4f;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;

import ecs.GameObject;
import ecs.SpriteRenderer;
import graphics.Color;
import network.requests.JoinRequest;
import network.requests.KryoRequest;
import network.requests.Message;
import network.requests.Probe;
import network.responses.InitialSetup;
import network.responses.KryoResponse;
import network.responses.ProbeResponse;
import scenes.pieces.NetData;
import scenes.pieces.Tile;
import util.OrderPreservingList;

public class KryoRegister {
    private static void registerWithKryo (Kryo kryo) {
        kryo.register(KryoRequest.class);
        kryo.register(KryoResponse.class);

        // Probes
        kryo.register(Probe.class);
        kryo.register(ProbeResponse.class);

        // General Messages
        kryo.register(Message.class);

        // Game
        kryo.register(JoinRequest.class);
        kryo.register(InitialSetup.class);
        kryo.register(NetData.class);
        kryo.register(char[][].class);
        kryo.register(char[].class);
        
    }

    public static void register (Client client) {
        Kryo kryo = client.getKryo();
        registerWithKryo(kryo);
    }

    public static void register (Server server) {
        Kryo kryo = server.getKryo();
        registerWithKryo(kryo);
    }
}
