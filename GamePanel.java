import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable, KeyListener, MouseListener {
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
    private final long SPAWN_INTERVAL = 3000; // 3s between spawns
    private final int SPAWN_RADIUS = 250; // minimum spawn distance
    
    // health bar dimensions
    private static final int HEALTH_BAR_HEIGHT = 20;
    private static final int HEALTH_BAR_Y = 10;
    private static final int HEALTH_BAR_X = 10;
    private static final int HEALTH_BAR_WIDTH = 200;
    
    // game over state
    private boolean gameOver = false;
    private float fadeAlpha = 0f; // 0 = transparent, 1 = opaque
    private final float FADE_SPEED = 0.02f; // fade speed per frame
    
    // restart button
    private Rectangle restartButton;
    private final int BUTTON_WIDTH = 200;
    private final int BUTTON_HEIGHT = 60;

    public GamePanel() {
        System.out.println("GamePanel: Constructor starting");
        setBackground(new Color(124, 252, 0));
        setDoubleBuffered(true);
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);

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
        
        // initialize restart button position
        restartButton = new Rectangle(
            SCREEN_WIDTH / 2 - BUTTON_WIDTH / 2,
            SCREEN_HEIGHT / 2 + 50,
            BUTTON_WIDTH,
            BUTTON_HEIGHT
        );
        
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
                update(); // update game state
                repaint(); // redraw screen

                // sleep until next frame
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
        if (gameOver) {
            // fade to black
            if (fadeAlpha < 1f) {
                fadeAlpha += FADE_SPEED;
                if (fadeAlpha > 1f) {
                    fadeAlpha = 1f;
                }
            }
            return; // stop updating game
        }
        
        // update player
        try {
            player.update(upPressed, downPressed, leftPressed, rightPressed, attack1Pressed, attack2Pressed);
        } catch (Exception e) {
            System.err.println("GamePanel: Error updating player: " + e.getMessage());
            e.printStackTrace();
        }
        
        // check if player is dead
        if (player.getCurrentHealth() <= 0) {
            gameOver = true;
            System.out.println("Game Over!");
            return;
        }
        
        // check player attack hits enemies
        if (player.isAttacking()) {
            Rectangle attackHitbox = player.getAttackHitbox();
            Iterator<Enemy> enemyIter = enemies.iterator();
            while (enemyIter.hasNext()) {
                Enemy enemy = enemyIter.next();
                if (attackHitbox.intersects(enemy.getBounds())) {
                    enemy.takeDamage(player.getAttackDamage()); // deal damage
                    if (enemy.getCurrentHealth() <= 0) {
                        enemyIter.remove(); // remove dead enemy
                        System.out.println("Enemy killed!");
                    }
                }
            }
        }
        
        spawnEnemies(); // spawn new enemies

        // update all enemies
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
            
            // remove distant enemies
            if (Math.abs(enemy.getX() - player.getX()) > 1500 || 
                Math.abs(enemy.getY() - player.getY()) > 1500) {
                enemyIterator.remove();
            }
        }

        // update fireballs and collisions
        Iterator<Fireball> fireballIterator = fireballs.iterator();
        while (fireballIterator.hasNext()) {
            Fireball fireball = fireballIterator.next();
            fireball.update();
            
            boolean shouldRemove = false;
            
            // check if offscreen
            if (fireball.isOffScreen(SCREEN_WIDTH, SCREEN_HEIGHT)) {
                shouldRemove = true;
            }
            
            // check player collision
            Rectangle playerBounds = new Rectangle(
                player.getX() - 20, 
                player.getY() - 30, 
                36, 
                36
            );
            if (fireball.getBounds().intersects(playerBounds)) {
                shouldRemove = true;
                player.takeDamage(10); // deal 10 damage
                System.out.println("Player hit by fireball!");
            }
            
            if (shouldRemove) {
                fireballIterator.remove();
            }
        }
        
        // limit max enemies
        if (enemies.size() > 20) {
            enemies.remove(0);
        }
    }

    private void spawnEnemies() {
        // limit to 10 enemies
        if (enemies.size() >= 10) {
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSpawnTime > SPAWN_INTERVAL) {
            int spawnX, spawnY;
            double distance;
            int attempts = 0;

            // find spawn away from player
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
            // draw background tiles
            if (tileManager != null) {
                tileManager.draw(g2);
            }

            // draw player sprite
            if (player != null) {
                player.draw(g2);
            }

            // draw all enemies
            for (Enemy enemy : enemies) {
                enemy.draw(g2);
            }

            // draw all fireballs
            for (Fireball fireball : fireballs) {
                fireball.draw(g2);
            }

            // draw UI elements
            if (player != null) {
                drawHealthBar(g2);
            }
            
            // draw debug info
            g2.setColor(Color.WHITE);
            g2.drawString("Enemies: " + enemies.size(), 10, HEALTH_BAR_Y + HEALTH_BAR_HEIGHT + 20);
            g2.drawString("Fireballs: " + fireballs.size(), 10, HEALTH_BAR_Y + HEALTH_BAR_HEIGHT + 40);
            g2.drawString("Player: (" + player.getX() + ", " + player.getY() + ")", 10, HEALTH_BAR_Y + HEALTH_BAR_HEIGHT + 60);
            g2.drawString("Health: " + player.getCurrentHealth() + "/" + player.getMaxHealth(), 10, HEALTH_BAR_Y + HEALTH_BAR_HEIGHT + 80);
            
            // draw game over screen with fade
            if (gameOver) {
                drawGameOverScreen(g2);
            }

        } catch (Exception e) {
            System.err.println("GamePanel: Error in paint: " + e.getMessage());
            e.printStackTrace();
        }

        g2.dispose();
    }

    private void drawHealthBar(Graphics2D g) {
        // calculate health percentage
        double healthRatio = (double) player.getCurrentHealth() / player.getMaxHealth();
        int fillWidth = (int) (HEALTH_BAR_WIDTH * healthRatio);
        
        // draw red background (lost health)
        g.setColor(Color.RED);
        g.fillRect(HEALTH_BAR_X, HEALTH_BAR_Y, HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT);
        
        // draw green foreground (current health)
        g.setColor(Color.GREEN);
        g.fillRect(HEALTH_BAR_X, HEALTH_BAR_Y, fillWidth, HEALTH_BAR_HEIGHT);
        
        // draw black border
        g.setColor(Color.BLACK);
        g.drawRect(HEALTH_BAR_X, HEALTH_BAR_Y, HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT);
    }
    
    private void drawGameOverScreen(Graphics2D g2) {
        // draw semi-transparent black overlay
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)); // reset alpha
        
        // only show text and button when fade is complete
        if (fadeAlpha >= 0.8f) {
            // draw "GAME OVER" text
            g2.setColor(Color.RED);
            g2.setFont(new Font("Arial", Font.BOLD, 60));
            FontMetrics fm = g2.getFontMetrics();
            String gameOverText = "GAME OVER";
            int textX = (SCREEN_WIDTH - fm.stringWidth(gameOverText)) / 2;
            g2.drawString(gameOverText, textX, SCREEN_HEIGHT / 2 - 50);
            
            // draw restart button
            g2.setColor(Color.GRAY);
            g2.fillRect(restartButton.x, restartButton.y, restartButton.width, restartButton.height);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(3));
            g2.drawRect(restartButton.x, restartButton.y, restartButton.width, restartButton.height);
            
            // draw button text
            g2.setFont(new Font("Arial", Font.BOLD, 24));
            String buttonText = "RESTART";
            FontMetrics bmFm = g2.getFontMetrics();
            int buttonTextX = restartButton.x + (restartButton.width - bmFm.stringWidth(buttonText)) / 2;
            int buttonTextY = restartButton.y + ((restartButton.height - bmFm.getHeight()) / 2) + bmFm.getAscent();
            g2.drawString(buttonText, buttonTextX, buttonTextY);
        }
    }
    
    private void restartGame() {
        System.out.println("Restarting game...");
        gameOver = false;
        fadeAlpha = 0f;
        
        // reset player
        player = new Player(100, 100, 4);
        
        // clear enemies and fireballs
        enemies.clear();
        fireballs.clear();
        
        lastSpawnTime = System.currentTimeMillis();
        System.out.println("Game restarted!");
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
    
    @Override
    public void mouseClicked(MouseEvent e) {
        if (gameOver && restartButton.contains(e.getPoint())) {
            restartGame();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}