package group5.game;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;

public class clientCommunicationTest {

    public static void main(String[] args) {

        try {
            Client client = new Client();
            client.start();
            client.connect(5000, "localhost", 54555, 54777);

            Kryo kryo = client.getKryo();
            kryo.register(String.class);

            client.addListener(new Listener() {

                public void received (Connection connection, Object object) {

                    if (object instanceof String) {
                        String response = (String) object;
                        System.out.println(response);

                        if (response.startsWith("BOSS")) {
                            client.sendTCP("PLAYER <x> <y>");
                            client.sendTCP("BULLET <x> <y>");
                        }
                    }
                }
            });
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
