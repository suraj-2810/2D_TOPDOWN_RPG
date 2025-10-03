// import javax.swing.*;
// import java.awt.*;
// import java.awt.event.*;
// // import javax.imageio.ImageIO;
// // import java.io.IOException;

// public class GamePanel extends JPanel implements Runnable, KeyListener {
//     private Thread gameThread;
//     private final int FPS = 60;

//     private Player player;
//     private boolean upPressed, downPressed, leftPressed, rightPressed, attack1Pressed, attack2Pressed;

//     public GamePanel() {
//         setBackground(Color.BLACK);
//         setDoubleBuffered(true);
//         setFocusable(true);
//         addKeyListener(this);

//         player = new Player(100, 100, 4);
//     }

//     public void run() {
//         double drawInterval = 1_000_000_000.0 / FPS;
//         double nextDrawTime = System.nanoTime() + drawInterval;

//         while (gameThread != null) {
//             update();
//             repaint();

//             try {
//                 double remaining = nextDrawTime - System.nanoTime();
//                 long sleepMillis = (long) (remaining / 1_000_000);
//                 if (sleepMillis > 0) Thread.sleep(sleepMillis);
//                 nextDrawTime += drawInterval;
//             } catch (InterruptedException e) { e.printStackTrace(); }
//         }
//     }

//     private void update() {
//         player.update(upPressed, downPressed, leftPressed, rightPressed, attack1Pressed, attack2Pressed);
//     }

//     protected void paintComponent(Graphics g) {
//         super.paintComponent(g);
//         Graphics2D g2 = (Graphics2D) g;

//         player.draw(g2);

//         g2.dispose();
//     }

//     public void keyPressed(KeyEvent e) {// key handling
//         switch (e.getKeyCode()) {
//             case KeyEvent.VK_W: upPressed = true; break;
//             case KeyEvent.VK_S: downPressed = true; break;
//             case KeyEvent.VK_A: leftPressed = true; break;
//             case KeyEvent.VK_D: rightPressed = true; break;
//             case KeyEvent.VK_J: attack1Pressed = true; break;
//             case KeyEvent.VK_K: attack2Pressed = true; break;
//         }
//     }

//     public void keyReleased(KeyEvent e) {
//         switch (e.getKeyCode()) {
//             case KeyEvent.VK_W: upPressed = false; break;
//             case KeyEvent.VK_S: downPressed = false; break;
//             case KeyEvent.VK_A: leftPressed = false; break;
//             case KeyEvent.VK_D: rightPressed = false; break;
//             case KeyEvent.VK_J: attack1Pressed = false; break;
//             case KeyEvent.VK_K: attack2Pressed = false; break;
//         }
//     }

//     public void keyTyped(KeyEvent e) {}
// }
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable, KeyListener {
    private Thread gameThread;
    private final int FPS = 60;

    private Player player;
    private TileManager tileManager;
    private boolean upPressed, downPressed, leftPressed, rightPressed, attack1Pressed, attack2Pressed;

    private List<Enemy> enemies = new ArrayList<>();
    private List<Fireball> fireballs = new ArrayList<>();
    private Random random = new Random();

    private long lastSpawnTime;//spawner
    private final long SPAWN_INTERVAL = 3000; 
    private final int SPAWN_RADIUS = 250;     

    public GamePanel() {
        setBackground(new Color(124, 252, 0));
        setDoubleBuffered(true);
        setFocusable(true);
        addKeyListener(this);

        player = new Player(100, 100, 4);
        tileManager = new TileManager(this); 
        // START THE GAME THREAD
        startGameThread();
    }
    
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void run() {
        double drawInterval = 1_000_000_000.0 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (gameThread != null) {
            update();
            repaint();

            try {
                double remaining = nextDrawTime - System.nanoTime();
                long sleepMillis = (long) (remaining / 1_000_000);
                if (sleepMillis > 0) Thread.sleep(sleepMillis);
                nextDrawTime += drawInterval;
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    private void update() {
        player.update(upPressed, downPressed, leftPressed, rightPressed, attack1Pressed, attack2Pressed);
        
        spawnEnemies();

        for (Enemy enemy : enemies) {
            enemy.update(player, fireballs);
        }

        Iterator<Fireball> fireballIterator = fireballs.iterator();
        while (fireballIterator.hasNext()) {
            Fireball fireball = fireballIterator.next();
            fireball.update();
            if (fireball.isOffScreen(SCREEN_WIDTH, SCREEN_HEIGHT)) {
                fireballIterator.remove(); 
            }
        }
    }

    private void spawnEnemies() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSpawnTime > SPAWN_INTERVAL) {
            int spawnX, spawnY;
            double distance;

            do {
                spawnX = random.nextInt(SCREEN_WIDTH);
                spawnY = random.nextInt(SCREEN_HEIGHT);
                distance = Math.sqrt(Math.pow(spawnX - player.getX(), 2) + Math.pow(spawnY - player.getY(), 2));
            } while (distance < SPAWN_RADIUS);
            
            enemies.add(new Enemy(spawnX, spawnY, 2)); // 2 is enemy speed
            lastSpawnTime = currentTime;
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        tileManager.draw(g2);

        player.draw(g2);

        for (Enemy enemy : enemies) {
            enemy.draw(g2);
        }

        for (Fireball fireball : fireballs) {
            fireball.draw(g2);
        }

        g2.dispose();
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP: upPressed = true; break;
            case KeyEvent.VK_DOWN: downPressed = true; break;
            case KeyEvent.VK_LEFT: leftPressed = true; break;
            case KeyEvent.VK_RIGHT: rightPressed = true; break;
            case KeyEvent.VK_W: upPressed = true; break;
            case KeyEvent.VK_S: downPressed = true; break;
            case KeyEvent.VK_A: leftPressed = true; break;
            case KeyEvent.VK_D: rightPressed = true; break;
            case KeyEvent.VK_J: attack1Pressed = true; break;
            case KeyEvent.VK_K: attack2Pressed = true; break;
        }
    }

    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP: upPressed = false; break;
            case KeyEvent.VK_DOWN: downPressed = false; break;
            case KeyEvent.VK_LEFT: leftPressed = false; break;
            case KeyEvent.VK_RIGHT: rightPressed = false; break;
            case KeyEvent.VK_W: upPressed = false; break;
            case KeyEvent.VK_S: downPressed = false; break;
            case KeyEvent.VK_A: leftPressed = false; break;
            case KeyEvent.VK_D: rightPressed = false; break;
            case KeyEvent.VK_J: attack1Pressed = false; break;
            case KeyEvent.VK_K: attack2Pressed = false; break;
        }
    }

    public void keyTyped(KeyEvent e) {}
}