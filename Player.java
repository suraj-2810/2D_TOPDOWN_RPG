import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Player {
    enum State { IDLE, RUNNING, ATTACK1, ATTACK2 }
    private State state = State.IDLE;

    private int x, y, speed;
    private Animation idleAnim, runAnim, attack1Anim, attack2Anim, currentAnim;

    private static final int FRAME_SIZE = 192;
    private static final int SPRITE_WIDTH = 96;
    private static final int SPRITE_HEIGHT = 96;
    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;
    
    // Health properties
    private int maxHealth;
    private int currentHealth;
    
    public Player(int startX, int startY, int speed) {
        this.x = startX;
        this.y = startY;
        this.speed = speed;
        
        // Initialize health
        this.maxHealth = 100;
        this.currentHealth = 100;

        loadAnimations();
        currentAnim = idleAnim;
    }
    
    // Health getters
    public int getMaxHealth() {
        return maxHealth;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public void takeDamage(int damage) {
        this.currentHealth -= damage;
        if (this.currentHealth < 0) {
            this.currentHealth = 0;
        }
        System.out.println("Player took " + damage + " damage. Health: " + currentHealth + "/" + maxHealth);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, SPRITE_WIDTH, SPRITE_HEIGHT);
    }
    
    public int getX() { 
        return x + SPRITE_WIDTH / 2; 
    }
    
    public int getY() { 
        return y + SPRITE_HEIGHT / 2; 
    }

    private void loadAnimations() {
        try {
            BufferedImage idleSheet = ImageIO.read(getClass().getResource("assets/Warrior_Idle.png"));
            idleAnim = new Animation(sliceSpriteSheet(idleSheet, 8), 10, true);

            BufferedImage runSheet = ImageIO.read(getClass().getResource("assets/Warrior_Run.png"));
            runAnim = new Animation(sliceSpriteSheet(runSheet, 6), 12, true);

            BufferedImage atk1Sheet = ImageIO.read(getClass().getResource("assets/Warrior_Attack1.png"));
            attack1Anim = new Animation(sliceSpriteSheet(atk1Sheet, 4), 8, false);

            BufferedImage atk2Sheet = ImageIO.read(getClass().getResource("assets/Warrior_Attack2.png"));
            attack2Anim = new Animation(sliceSpriteSheet(atk2Sheet, 4), 8, false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Image[] sliceSpriteSheet(BufferedImage sheet, int frameCount) {
        Image[] frames = new Image[frameCount];
        for (int i = 0; i < frameCount; i++) {
            frames[i] = sheet.getSubimage(i * FRAME_SIZE, 0, FRAME_SIZE, FRAME_SIZE);
        }
        return frames;
    }

    public void update(boolean up, boolean down, boolean left, boolean right,
                       boolean attack1, boolean attack2) {
        
        // If currently in an attack state, check if animation is finished
        if (state == State.ATTACK1 || state == State.ATTACK2) {
            if (currentAnim.isFinished()) {
                state = State.IDLE;
                currentAnim = idleAnim;
            }
        } else {
            // Handle new inputs if not attacking
            if (attack1) {
                state = State.ATTACK1;
                currentAnim = attack1Anim;
                currentAnim.reset();
            } else if (attack2) {
                state = State.ATTACK2;
                currentAnim = attack2Anim;
                currentAnim.reset();
            } else if (up || down || left || right) {
                state = State.RUNNING;
                currentAnim = runAnim;

                int dx = 0, dy = 0;
                if (up) dy -= speed;
                if (down) dy += speed;
                if (left) dx -= speed;
                if (right) dx += speed;

                // Diagonal speed normalization
                if (dx != 0 && dy != 0) {
                    dx = (int) (dx / Math.sqrt(2));
                    dy = (int) (dy / Math.sqrt(2));
                }

                x += dx;
                y += dy;

                // Keep player within screen bounds
                if (x < 0) x = 0;
                if (x > SCREEN_WIDTH - SPRITE_WIDTH) x = SCREEN_WIDTH - SPRITE_WIDTH;
                if (y < 0) y = 0;
                if (y > SCREEN_HEIGHT - SPRITE_HEIGHT) y = SCREEN_HEIGHT - SPRITE_HEIGHT;
            } else {
                state = State.IDLE;
                currentAnim = idleAnim;
            }
        }

        currentAnim.update();
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(currentAnim.getCurrentFrame(), x, y, 96, 96, null);
    }
}