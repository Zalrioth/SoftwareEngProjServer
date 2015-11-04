package gameserver;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

class Player implements Runnable {

    private Socket server;
    private String line, input;
    private PrintWriter output;
    private boolean threadAlive;

    public int idNum;
    public int xPos;
    public int yPos;

    Player(Socket server, int idNum) {
        this.server = server;
        this.idNum = idNum;
        try {
            output = new PrintWriter(this.server.getOutputStream(), true);
        } catch (IOException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
        threadAlive = true;
    }

    public void run() {

        input = "";

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(server.getInputStream()));
            // Get the client message
            while ((input = bufferedReader.readLine()) != null) {
                int xIndex = input.indexOf("x:");
                int yIndex = input.indexOf("y:");
                System.out.println(input);
                //System.out.println("x: " + xIndex + "y: " + yIndex);
                xPos = Integer.parseInt(input.substring(xIndex + 2, yIndex));
                yPos = Integer.parseInt(input.substring(yIndex + 2, input.length()));
                System.out.println("id: " + idNum + "x: " + xPos + "y: " + yPos);

                for (Player tPlayer : GameServer.playerList) {
                    if (!tPlayer.equals(this)) {
                        output.println("id:" + tPlayer.idNum + "x:" + tPlayer.xPos + "y:" + tPlayer.yPos);
                        System.out.println("Sending player data");
                    }
                }
            }

            //PrintStream out = new PrintStream(server.getOutputStream());
            server.close();
        } catch (IOException ioe) {
            System.out.println("Player " + idNum + "Quit");
            threadAlive = false;
            //System.out.println("IOException on socket listen: " + ioe);
            //ioe.printStackTrace();
        }
    }
}
