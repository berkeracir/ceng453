package group5.gameServer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameServer {
    private static List<Integer> connections = new ArrayList<Integer>();
    private static int readyHosts = 0;

    public static void main(String[] args) {
        connections.add(-1);

        try {
            final Server server = new Server();

            Kryo kryo = server.getKryo();
            kryo.register(String.class);

            server.addListener(new Listener() {

                @Override
                public void received (Connection connection, Object object) {
                    if (object instanceof String) {
                        String request = (String) object;

                        if (request.startsWith("PLAYER")) {
                            if (connections.get(connection.getID()) > 0) {
                                server.sendToTCP(connections.get(connection.getID()), request);
                            }
                        }
                        else if (request.startsWith("READY")) {
                            readyHosts += 1;
                            connections.set(connection.getID(), 0);

                            if (readyHosts >= 2) {
                                for (int i=1; i<connections.size(); i++) {
                                    if (connections.get(i) == 0 && i != connection.getID()) {
                                        connections.set(connection.getID(), i);
                                        connections.set(i, connection.getID());
                                        readyHosts -= 2;

                                        try {
                                            Thread.sleep(4000);
                                        }
                                        catch (InterruptedException ex) {
                                            ex.printStackTrace();
                                        }
                                        server.sendToTCP(connection.getID(), "START R");
                                        server.sendToTCP(i, "START L");
                                        break;
                                    }
                                }
                            }

                            System.out.println(connection + " READY (" + readyHosts + "|" + connections + ")");
                        }
                        else if (request.startsWith("FINISH")) {
                            connections.set(connection.getID(), -1);

                            System.out.println(connection + " FINISH (" + readyHosts + "|" + connections + ")");
                        }
                    }
                }

                @Override
                public void connected(Connection connection) {
                    connections.add(0);

                    System.out.println(connection + " connected! (" + readyHosts + "|" + connections + ")");
                }

                @Override
                public void disconnected(Connection connection) {
                    super.disconnected(connection);
                    connections.set(connection.getID(), -1);
                    //inSessionCount -= 1;

                    System.out.println(connection + " disconnected! (" + readyHosts + "|" + connections + ")");
                }
            });

            server.bind(54555);
            server.start();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
