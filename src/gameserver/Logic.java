package gameserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class Logic implements Runnable {

    static private boolean running = true;
    static private double fpsCap = 60;
    static private int fps;

    public static List<Enemy> enemyList;
    public static List<Enemy> deleteEnemyList;
    public static List<Bullet> bulletList;
    public static List<Bullet> deleteBulletList;

    private static final int SPAWN_COUNTDOWN = 60;
    private static int spawnCountdown = SPAWN_COUNTDOWN;

    Logic() {
        //bulletList = Collections.synchronizedList(new ArrayList<Bullet>());
        bulletList = new CopyOnWriteArrayList<Bullet>();
        deleteBulletList = new ArrayList<Bullet>();
        //enemyList = Collections.synchronizedList(new ArrayList<Enemy>());
        enemyList = new CopyOnWriteArrayList<Enemy>();
        deleteEnemyList = new ArrayList<Enemy>();
    }

    @Override
    public void run() {
        int frames = 0;

        double unprocessedSeconds = 0;
        long lastTime = System.nanoTime();
        double secondsPerTick = 1 / fpsCap;
        int tickCount = 0;

        while (running) {
            long now = System.nanoTime();
            long passedTime = now - lastTime;
            lastTime = now;
            if (passedTime < 0) {
                passedTime = 0;
            }
            if (passedTime > 100000000) {
                passedTime = 100000000;
            }

            unprocessedSeconds += passedTime / 1000000000.0;

            boolean ticked = false;
            while (unprocessedSeconds > secondsPerTick) {
                tick();
                unprocessedSeconds -= secondsPerTick;
                ticked = true;

                tickCount++;
                if (tickCount % fpsCap == 0) {
                    //System.out.println(frames + " fps");
                    fps = frames;
                    lastTime += 1000;
                    frames = 0;
                }
            }

            if (ticked) {
                frames++;
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void tick() {
        spawnCountdown--;
        if (spawnCountdown < 0) {
            Random rand = new Random();
            Enemy temp = new Enemy(rand.nextInt(500), 0);
            enemyList.add(temp);
            spawnCountdown = SPAWN_COUNTDOWN;
        }
        for (Enemy enemy : enemyList) {
            enemy.tick();
            if (enemy.isAlive == false) {
                enemy.countDown--;
                if (enemy.countDown < 0) {
                    deleteEnemyList.add(enemy);
                }
            }
        }
        for (Enemy enemy : deleteEnemyList) {
            enemyList.remove(enemy);
        }
        deleteEnemyList.clear();

        for (Bullet bullet : bulletList) {
            //bullet.tick();
            if (bullet.isAlive == false) {
                bullet.countDown--;
                if (bullet.countDown < 0) {
                    //System.out.println("Dead bullet");
                    deleteBulletList.add(bullet);
                }
            }
        }
        //System.out.println("size " + bulletList.size());
        for (Bullet bullet : deleteBulletList) {
            bulletList.remove(bullet);
        }
        deleteBulletList.clear();
    }
}
