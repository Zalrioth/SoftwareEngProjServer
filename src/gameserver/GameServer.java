package gameserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameServer {

    public static List<Player> playerList;

    private static int port = 63400, maxConnections = 10;

    // Listen for incoming connections and handle them
    public static void main(String[] args) {
        playerList = Collections.synchronizedList(new ArrayList<Player>());
        int i = 0;
        int idNum = 0;

        Logic logicLoop = new Logic();

        Thread logic = new Thread(logicLoop);
        logic.start();

        try {
            ServerSocket listener = new ServerSocket(port);
            Socket server;

            while ((i++ < maxConnections) || (maxConnections == 0)) {
                server = listener.accept();
                Player conn_c = new Player(server, idNum);
                playerList.add(conn_c);
                Thread t = new Thread(conn_c);
                t.start();

                idNum++;
            }
        } catch (IOException ioe) {
            System.out.println("IOException on socket listen: " + ioe);
            ioe.printStackTrace();
        }
    }
}
