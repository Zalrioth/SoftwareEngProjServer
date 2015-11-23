package gameserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

class ServerCommunication implements Runnable {

    private Socket socket;
    private String line, input;
    private PrintWriter output;
    private JSONObject send;
    private JSONObject recieve;

    ServerCommunication() {
        try {
            socket = new Socket("ec2-54-68-103-36.us-west-2.compute.amazonaws.com", 63400);
            //socket = new Socket("68.8.238.186", 63400);
            output = new PrintWriter(this.socket.getOutputStream(), true);
        } catch (IOException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
        send = new JSONObject();
        send.put("type", "server");
        send.put("ip", GameServer.ip);
        send.put("port", GameServer.port);
        output.println(send);
        recieve = new JSONObject();
        input = "";
    }

    public void run() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while ((input = bufferedReader.readLine()) != null) {
                recieveData(input);
                sendData();
            }
            socket.close();
        } catch (IOException ioe) {
            System.out.println("Server Manager Down!");
        }
    }

    public void recieveData(String input) {
        JSONParser parser = new JSONParser();
        try {
            recieve = (JSONObject) parser.parse(input);
        } catch (ParseException ex) {
        }
        //xPos = ((Long) recieve.get("xPos")).intValue();
    }

    public void sendData() {
        send.clear();
        send.put("totalPlayers", GameServer.totalPlayers);
        output.println(send);
    }
}
