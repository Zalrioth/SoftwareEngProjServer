package gameserver;

public class Enemy {

    protected int idNum;
    protected static int idCounter = 0;

    protected int x;
    protected int y;

    protected boolean wasHit;
    protected boolean isAlive;

    public int countDown = 120;

    Enemy(int x, int y) {
        this.x = x;
        this.y = y;

        wasHit = false;
        isAlive = true;

        idNum = idCounter;
        idCounter++;
    }

    public void tick() {
        y += 2;

        if (y > 700) {
            isAlive = false;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getID() {
        return idNum;
    }

    public boolean wasHit() {
        return wasHit;
    }

    public void setHit(boolean wasHit) {
        this.wasHit = wasHit;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }
}
