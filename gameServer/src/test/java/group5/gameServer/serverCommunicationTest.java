package group5.gameServer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class serverCommunicationTest {
    private static int ready = 0;
    private static List<int []> sessions = new ArrayList<int[]>();
    private static boolean sessionIsCreated = false;

    public static void main(String[] args) {

        try {
            final Server server = new Server();

            Kryo kryo = server.getKryo();
            kryo.register(String.class);

            server.addListener(new Listener() {

                @Override
                public void received (Connection connection, Object object) {
                    if (object instanceof String) {
                        String request = (String) object;
                        //System.out.println("Msg: " + request + " from " + connection);

                        if (request.startsWith("PLAYER")) {
                            int[] session = sessions.get((connection.getID()-1)/2);

                            if (session[0] == connection.getID()) {
                                server.sendToTCP(session[1], request);
                            }
                            else if (session[1] == connection.getID()) {
                                server.sendToTCP(session[0], request);
                            }
                        }
                        else if (request.startsWith("READY")) {
                            int[] session = sessions.get((connection.getID()-1)/2);
                            session[2] = session[2] + 1;

                            if (session[2] == 2) {
                                try {
                                    Thread.sleep(4000);
                                }
                                catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                                server.sendToTCP(session[0], "START L");
                                server.sendToTCP(session[1], "START R");
                            }

                            sessions.set((connection.getID()-1)/2, session);
                        }
                    }
                }

                @Override
                public void connected(Connection connection) {
                    System.out.println(connection + " connected! " + server.getConnections().length);

                    if (!sessionIsCreated) {
                        int[] session = new int[3];
                        session[0] = connection.getID();
                        session[1] = -1;
                        session[2] = 0;
                        sessions.add(session);

                        sessionIsCreated = true;
                    }
                    else {
                        sessions.get(sessions.size()-1)[1] = connection.getID();
                        sessionIsCreated = false;
                    }

                    for (int[] session: sessions) {
                        System.out.println(session[0] + ", " + session[1] + ", " + session[2]);
                    }
                }

                @Override
                public void disconnected(Connection connection) {
                    super.disconnected(connection);

                    int[] session = sessions.get((connection.getID()-1)/2);
                    session[2] = session[2] - 1;
                    // TODO: DC or END OF GAME Messages
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
