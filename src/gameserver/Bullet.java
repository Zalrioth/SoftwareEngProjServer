package gameserver;

public class Bullet {

    protected int idNum;

    protected boolean wasHit;
    protected boolean isAlive = true;

    protected int playerID;

    public int countDown = 120;

    Bullet(int playerID, int idNum) {
        this.playerID = playerID;
        this.idNum = idNum;
    }

    public int getID() {
        return idNum;
    }

    public boolean wasHit() {
        return wasHit;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public int getPlayerID() {
        return playerID;
    }
}
