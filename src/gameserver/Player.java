package gameserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

class Player implements Runnable {

    private static int totalPlayers;

    private Socket server;
    private String line, input;
    private PrintWriter output;
    private JSONObject send;
    private JSONObject recieve;

    private int idNum;
    private long xPos;
    private long yPos;

    Player(Socket server, int idNum) {
        this.server = server;
        this.idNum = idNum;
        try {
            output = new PrintWriter(this.server.getOutputStream(), true);
        } catch (IOException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
        send = new JSONObject();
        recieve = new JSONObject();
        input = "";
        totalPlayers++;
    }

    public void run() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(server.getInputStream()));
            while ((input = bufferedReader.readLine()) != null) {
                recieveData(input);
                sendData();
            }
            System.out.println("Made it");
            server.close();
        } catch (IOException ioe) {
            System.out.println("Player " + idNum + " Quit");
            totalPlayers--;
            GameServer.playerList.remove(this);
        }
    }

    public void recieveData(String input) {
        JSONParser parser = new JSONParser();
        try {
            recieve = (JSONObject) parser.parse(input);
        } catch (ParseException ex) {
        }
        xPos = ((Long) recieve.get("xPos")).intValue();
        yPos = ((Long) recieve.get("yPos")).intValue();

        JSONArray msg = (JSONArray) recieve.get("enemies");

        Iterator<JSONObject> iterator = msg.iterator();
        while (iterator.hasNext()) {
            JSONObject updateEnemy = iterator.next();
            for (Enemy tEnemy : Logic.enemyList) {
                if ((Long) updateEnemy.get("id") == tEnemy.getID()) {
                    tEnemy.isAlive = false;
                }
            }
        }

        JSONArray msg2 = (JSONArray) recieve.get("bullets");

        Iterator<JSONObject> iterator2 = msg2.iterator();
        while (iterator2.hasNext()) {
            boolean foundBullet = false;
            JSONObject updateBullet = iterator2.next();
            for (Bullet tBullet : Logic.bulletList) {
                if (idNum == tBullet.getPlayerID()) {
                    //System.out.println(tBullet.getPlayerID());
                    if ((Long) updateBullet.get("id") == tBullet.getID()) {
                        foundBullet = true;
                        tBullet.setAlive((Boolean) updateBullet.get("alive"));
                        break;
                    }
                }
            }

            if (!foundBullet) {
                if ((Boolean) updateBullet.get("alive") == false) {
                } else {
                    System.out.println("Making new " + idNum + " " + (Long) updateBullet.get("id"));
                    Bullet newBullet = new Bullet(idNum, ((Long) updateBullet.get("id")).intValue());
                    Logic.bulletList.add(newBullet);
                }
            }
        }
    }

    public void sendData() {
        send.clear();
        send.put("totalPlayers", totalPlayers);
        send.put("playerID", idNum);
        JSONArray players = new JSONArray();
        for (Player tPlayer : GameServer.playerList) {
            if (!tPlayer.equals(this)) {
                JSONObject playerDetails = new JSONObject();
                playerDetails.put("id", tPlayer.idNum);
                playerDetails.put("xPos", tPlayer.xPos);
                playerDetails.put("yPos", tPlayer.yPos);
                playerDetails.put("alive", true);
                players.add(playerDetails);
            }
        }
        send.put("players", players);

        JSONArray enemies = new JSONArray();
        for (Enemy tEnemy : Logic.enemyList) {
            JSONObject enemyDetails = new JSONObject();
            enemyDetails.put("id", tEnemy.idNum);
            enemyDetails.put("xPos", tEnemy.getX());
            enemyDetails.put("yPos", tEnemy.getY());
            enemyDetails.put("alive", tEnemy.isAlive);
            enemies.add(enemyDetails);
        }
        send.put("enemies", enemies);

        JSONArray bullets = new JSONArray();
        for (Bullet tBullet : Logic.bulletList) {
            if (tBullet.playerID != idNum) {
                //System.out.println("bullet id " + tBullet.idNum);
                //System.out.println("player id " + tBullet.playerID);
                //System.out.println("actual id " + idNum);
                JSONObject bulletDetails = new JSONObject();
                bulletDetails.put("id", tBullet.idNum);
                bulletDetails.put("playerID", tBullet.playerID);
                bulletDetails.put("alive", tBullet.isAlive);
                bullets.add(bulletDetails);
            } else {
                //System.out.println("Not sending");
            }
        }
        send.put("bullets", bullets);
        output.println(send);
    }
}
