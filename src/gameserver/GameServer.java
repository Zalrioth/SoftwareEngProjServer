package gameserver;

import java.io.BufferedReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameServer {

    public static List<Player> playerList;

    //private static int port = 63400, maxConnections = 10;
    private static int maxConnections = 10;
    public static String ip;
    public static int port;
    
    public static int totalPlayers = 0;

    // Listen for incoming connections and handle them
    public static void main(String[] args) {
        playerList = Collections.synchronizedList(new ArrayList<Player>());
        int i = 0;
        int idNum = 0;
        
        try {
            ip = getIp();
        } catch (Exception ex) {
            Logger.getLogger(GameServer.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (args.length != 0) {
            port = Integer.parseInt(args[0]);
            System.out.println("Set port as: " + port);
        } else {
            port = 63400;
        }


        ServerCommunication serverManager = new ServerCommunication();
        
        Thread mainServer = new Thread(serverManager);
        mainServer.start();

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
     public static String getIp() throws Exception {
        URL whatismyip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));
            String ip = in.readLine();
            return ip;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
