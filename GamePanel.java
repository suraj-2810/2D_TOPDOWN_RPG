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
        
        lastSpawnTime = System.currentTimeMillis();
        
        System.out.println("GamePanel: Starting game thread");
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
            }
        }
    }

    private void update() {
        try {
            player.update(upPressed, downPressed, leftPressed, rightPressed, attack1Pressed, attack2Pressed);
        } catch (Exception e) {
            System.err.println("GamePanel: Error updating player: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Spawn enemies
        spawnEnemies();

        // Update enemies
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            try {
                enemy.update(player, fireballs);
            } catch (Exception e) {
                System.err.println("GamePanel: Error updating enemy: " + e.getMessage());
                e.printStackTrace();
                enemyIterator.remove();
            }
            
            // Remove enemies that go too far off-screen
            if (Math.abs(enemy.getX() - player.getX()) > 1500 || 
                Math.abs(enemy.getY() - player.getY()) > 1500) {
                enemyIterator.remove();
            }
        }

        // Update fireballs and check collisions
        Iterator<Fireball> fireballIterator = fireballs.iterator();
        while (fireballIterator.hasNext()) {
            Fireball fireball = fireballIterator.next();
            fireball.update();
            
            boolean shouldRemove = false;
            
            // Remove if off-screen
            if (fireball.isOffScreen(SCREEN_WIDTH, SCREEN_HEIGHT)) {
                shouldRemove = true;
            }
            
            // Check collision with player
            Rectangle playerBounds = new Rectangle(
                player.getX() - 48, 
                player.getY() - 48, 
                96, 
                96
            );
            if (fireball.getBounds().intersects(playerBounds)) {
                shouldRemove = true;
                System.out.println("Player hit by fireball!");
            }
            
            if (shouldRemove) {
                fireballIterator.remove();
            }
        }
        
        // Limit maximum enemies
        if (enemies.size() > 20) {
            enemies.remove(0);
        }
    }

    private void spawnEnemies() {
        // Don't spawn if we already have 10 enemies
        if (enemies.size() >= 10) {
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSpawnTime > SPAWN_INTERVAL) {
            int spawnX, spawnY;
            double distance;
            int attempts = 0;

            // Find spawn position away from player
            do {
                spawnX = random.nextInt(SCREEN_WIDTH);
                spawnY = random.nextInt(SCREEN_HEIGHT);
                distance = Math.sqrt(
                    Math.pow(spawnX - player.getX(), 2) + 
                    Math.pow(spawnY - player.getY(), 2)
                );
                attempts++;
            } while (distance < SPAWN_RADIUS && attempts < 50);
            
            if (attempts < 50) {
                enemies.add(new Enemy(spawnX, spawnY, 2));
                System.out.println("Enemy spawned at (" + spawnX + ", " + spawnY + ")");
            }
            lastSpawnTime = currentTime;
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        try {
            // Draw background
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
            g2.drawString("Enemies: " + enemies.size(), 10, 20);
            g2.drawString("Fireballs: " + fireballs.size(), 10, 40);
            if (player != null) {
                g2.drawString("Player: (" + player.getX() + ", " + player.getY() + ")", 10, 60);
            }

        } catch (Exception e) {
            System.err.println("GamePanel: Error in paint: " + e.getMessage());
            e.printStackTrace();
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