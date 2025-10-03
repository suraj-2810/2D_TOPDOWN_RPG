// import javax.swing.*;
// import java.awt.*;
// import java.awt.event.*;
// import java.util.ArrayList;
// import java.util.Iterator;
// import java.util.List;
// import java.util.Random;

// public class GamePanel extends JPanel implements Runnable, KeyListener {
//     private Thread gameThread;
//     private final int FPS = 60;
    
//     // Define screen dimensions
//     private static final int SCREEN_WIDTH = 800;
//     private static final int SCREEN_HEIGHT = 600;

//     private Player player;
//     private TileManager tileManager;
//     private boolean upPressed, downPressed, leftPressed, rightPressed, attack1Pressed, attack2Pressed;

//     private List<Enemy> enemies = new ArrayList<>();
//     private List<Fireball> fireballs = new ArrayList<>();
//     private Random random = new Random();

//     private long lastSpawnTime;
//     private final long SPAWN_INTERVAL = 3000; 
//     private final int SPAWN_RADIUS = 250;     

//     public GamePanel() {
//         setBackground(new Color(124, 252, 0));
//         setDoubleBuffered(true);
//         setFocusable(true);
//         addKeyListener(this);

//         player = new Player(100, 100, 4);
//         tileManager = new TileManager(this);
        
//         // Initialize spawn timer
//         lastSpawnTime = System.currentTimeMillis();
        
//         // START THE GAME THREAD
//         startGameThread();
//     }
    
//     public void startGameThread() {
//         gameThread = new Thread(this);
//         gameThread.start();
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
//             } catch (InterruptedException e) { 
//                 e.printStackTrace(); 
//             }
//         }
//     }

//     private void update() {
//         player.update(upPressed, downPressed, leftPressed, rightPressed, attack1Pressed, attack2Pressed);
        
//         spawnEnemies();

//         // Update enemies
//         Iterator<Enemy> enemyIterator = enemies.iterator();
//         while (enemyIterator.hasNext()) {
//             Enemy enemy = enemyIterator.next();
//             enemy.update(player, fireballs);
            
//             // Remove enemies that go too far off-screen (optional cleanup)
//             if (Math.abs(enemy.getBounds().x - player.getX()) > 1000 || 
//                 Math.abs(enemy.getBounds().y - player.getY()) > 1000) {
//                 enemyIterator.remove();
//             }
//         }

//         // Update fireballs and check collisions
//         Iterator<Fireball> fireballIterator = fireballs.iterator();
//         while (fireballIterator.hasNext()) {
//             Fireball fireball = fireballIterator.next();
//             fireball.update();
            
//             boolean shouldRemove = false;
            
//             // Remove if off-screen
//             if (fireball.isOffScreen(SCREEN_WIDTH, SCREEN_HEIGHT)) {
//                 shouldRemove = true;
//             }
            
//             // Check collision with player (remove fireball on hit)
//             Rectangle playerBounds = new Rectangle(player.getX() - 48, player.getY() - 48, 96, 96);
//             if (fireball.getBounds().intersects(playerBounds)) {
//                 shouldRemove = true;
//                 // You could add player damage logic here
//             }
            
//             if (shouldRemove) {
//                 fireballIterator.remove();
//             }
//         }
        
//         // Limit maximum enemies to prevent memory issues
//         if (enemies.size() > 20) {
//             enemies.remove(0); // Remove oldest enemy
//         }
//     }

//     private void spawnEnemies() {
//         // Only spawn if we don't have too many enemies already
//         if (enemies.size() >= 10) {
//             return; // Don't spawn more if we already have 10 enemies
//         }
        
//         long currentTime = System.currentTimeMillis();
//         if (currentTime - lastSpawnTime > SPAWN_INTERVAL) {
//             int spawnX, spawnY;
//             double distance;
//             int attempts = 0;

//             // Find a spawn position away from the player (with safety limit)
//             do {
//                 spawnX = random.nextInt(SCREEN_WIDTH);
//                 spawnY = random.nextInt(SCREEN_HEIGHT);
//                 distance = Math.sqrt(Math.pow(spawnX - player.getX(), 2) + 
//                                    Math.pow(spawnY - player.getY(), 2));
//                 attempts++;
//             } while (distance < SPAWN_RADIUS && attempts < 50);
            
//             if (attempts < 50) { // Only spawn if we found a valid position
//                 enemies.add(new Enemy(spawnX, spawnY, 2)); // 2 is enemy speed
//             }
//             lastSpawnTime = currentTime;
//         }
//     }

//     protected void paintComponent(Graphics g) {
//         super.paintComponent(g);
//         Graphics2D g2 = (Graphics2D) g;

//         // Draw background tiles
//         tileManager.draw(g2);

//         // Draw player
//         player.draw(g2);

//         // Draw enemies
//         for (Enemy enemy : enemies) {
//             enemy.draw(g2);
//         }

//         // Draw fireballs
//         for (Fireball fireball : fireballs) {
//             fireball.draw(g2);
//         }
        
//         // Debug info (optional - remove in production)
//         g2.setColor(Color.WHITE);
//         g2.drawString("Enemies: " + enemies.size(), 10, 20);
//         g2.drawString("Fireballs: " + fireballs.size(), 10, 40);

//         g2.dispose();
//     }

//     public void keyPressed(KeyEvent e) {
//         switch (e.getKeyCode()) {
//             case KeyEvent.VK_UP: upPressed = true; break;
//             case KeyEvent.VK_DOWN: downPressed = true; break;
//             case KeyEvent.VK_LEFT: leftPressed = true; break;
//             case KeyEvent.VK_RIGHT: rightPressed = true; break;
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
//             case KeyEvent.VK_UP: upPressed = false; break;
//             case KeyEvent.VK_DOWN: downPressed = false; break;
//             case KeyEvent.VK_LEFT: leftPressed = false; break;
//             case KeyEvent.VK_RIGHT: rightPressed = false; break;
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
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable, KeyListener {
    private Thread gameThread;
    private final int FPS = 60;
    
    // Define screen dimensions
    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;

    private Player player;
    private TileManager tileManager;
    private boolean upPressed, downPressed, leftPressed, rightPressed, attack1Pressed, attack2Pressed;

    private List<Enemy> enemies = new ArrayList<>();
    private List<Fireball> fireballs = new ArrayList<>();
    private Random random = new Random();

    private long lastSpawnTime;
    private final long SPAWN_INTERVAL = 3000; 
    private final int SPAWN_RADIUS = 250;
    
    // Diagnostic variables
    private int updateCount = 0;
    private long lastDiagnosticPrint = System.currentTimeMillis();

    public GamePanel() {
        System.out.println("GamePanel: Constructor starting");
        setBackground(new Color(124, 252, 0));
        setDoubleBuffered(true);
        setFocusable(true);
        addKeyListener(this);

        try {
            System.out.println("GamePanel: Creating Player");
            player = new Player(100, 100, 4);
            System.out.println("GamePanel: Player created successfully");
        } catch (Exception e) {
            System.err.println("GamePanel: Error creating player: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            System.out.println("GamePanel: Creating TileManager");
            tileManager = new TileManager(this);
            System.out.println("GamePanel: TileManager created successfully");
        } catch (Exception e) {
            System.err.println("GamePanel: Error creating TileManager: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Initialize spawn timer
        lastSpawnTime = System.currentTimeMillis();
        
        System.out.println("GamePanel: Starting game thread");
        // START THE GAME THREAD
        startGameThread();
    }
    
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void run() {
        System.out.println("GamePanel: Game loop started");
        double drawInterval = 1_000_000_000.0 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (gameThread != null) {
            try {
                update();
                repaint();

                double remaining = nextDrawTime - System.nanoTime();
                long sleepMillis = (long) (remaining / 1_000_000);
                if (sleepMillis > 0) Thread.sleep(sleepMillis);
                nextDrawTime += drawInterval;
            } catch (Exception e) {
                System.err.println("GamePanel: Error in game loop: " + e.getMessage());
                e.printStackTrace();
                break;
            }
        }
    }

    private void update() {
        updateCount++;
        
        // Print diagnostic info every second
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDiagnosticPrint > 1000) {
            System.out.println("GamePanel: Update #" + updateCount + " - Enemies: " + enemies.size() + " - Fireballs: " + fireballs.size());
            lastDiagnosticPrint = currentTime;
        }
        
        try {
            player.update(upPressed, downPressed, leftPressed, rightPressed, attack1Pressed, attack2Pressed);
        } catch (Exception e) {
            System.err.println("GamePanel: Error updating player: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Comment out enemy spawning for now to isolate the issue
        // spawnEnemies();

        // Update enemies
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            try {
                enemy.update(player, fireballs);
            } catch (Exception e) {
                System.err.println("GamePanel: Error updating enemy: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Update fireballs
        Iterator<Fireball> fireballIterator = fireballs.iterator();
        while (fireballIterator.hasNext()) {
            Fireball fireball = fireballIterator.next();
            fireball.update();
            if (fireball.isOffScreen(SCREEN_WIDTH, SCREEN_HEIGHT)) {
                fireballIterator.remove();
            }
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        try {
            // Draw background tiles
            if (tileManager != null) {
                tileManager.draw(g2);
            }

            // Draw player
            if (player != null) {
                player.draw(g2);
            }

            // Draw enemies
            for (Enemy enemy : enemies) {
                enemy.draw(g2);
            }

            // Draw fireballs
            for (Fireball fireball : fireballs) {
                fireball.draw(g2);
            }
            
            // Debug info
            g2.setColor(Color.WHITE);
            g2.drawString("Updates: " + updateCount, 10, 20);
            g2.drawString("Enemies: " + enemies.size(), 10, 40);
            g2.drawString("Fireballs: " + fireballs.size(), 10, 60);
            if (player != null) {
                g2.drawString("Player Pos: " + player.getX() + ", " + player.getY(), 10, 80);
            }

        } catch (Exception e) {
            System.err.println("GamePanel: Error in paint: " + e.getMessage());
            e.printStackTrace();
        }

        g2.dispose();
    }

    public void keyPressed(KeyEvent e) {
        System.out.println("Key pressed: " + e.getKeyCode());
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