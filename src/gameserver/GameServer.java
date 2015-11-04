package gameserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.ArrayList;

public class GameServer {

    public static ArrayList<Player> playerList = new ArrayList<Player>();

    private static int port = 63400, maxConnections = 10;

    // Listen for incoming connections and handle them
    public static void main(String[] args) {
        int i = 0;
        int idNum = 0;

        try {
            ServerSocket listener = new ServerSocket(port);
            Socket server;

            while ((i++ < maxConnections) || (maxConnections == 0)) {
                Player connection;

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
