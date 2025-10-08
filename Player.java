import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Player {
    enum State { IDLE, RUNNING, ATTACK1, ATTACK2 }
    private State state = State.IDLE;

    private int x, y, speed; // position and movement
    private Animation idleAnim, runAnim, attack1Anim, attack2Anim, currentAnim;

    private static final int FRAME_SIZE = 192;
    private static final int SPRITE_WIDTH = 96;
    private static final int SPRITE_HEIGHT = 96;
    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;
    
    // health system
    private int maxHealth;
    private int currentHealth;
    
    public Player(int startX, int startY, int speed) {
        this.x = startX;
        this.y = startY;
        this.speed = speed;
        
        // initialize health values
        this.maxHealth = 100;
        this.currentHealth = 100;

        loadAnimations();
        currentAnim = idleAnim;
    }
    
    // get max health
    public int getMaxHealth() {
        return maxHealth;
    }

    // get current health
    public int getCurrentHealth() {
        return currentHealth;
    }

    // apply damage to player
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
    
    // get center x position
    public int getX() { 
        return x + SPRITE_WIDTH / 2; 
    }
    
    // get center y position
    public int getY() { 
        return y + SPRITE_HEIGHT / 2; 
    }

    private void loadAnimations() {
        try {
            // load idle animation
            BufferedImage idleSheet = ImageIO.read(getClass().getResource("assets/Warrior_Idle.png"));
            idleAnim = new Animation(sliceSpriteSheet(idleSheet, 8), 10, true);

            // load running animation
            BufferedImage runSheet = ImageIO.read(getClass().getResource("assets/Warrior_Run.png"));
            runAnim = new Animation(sliceSpriteSheet(runSheet, 6), 12, true);

            // load attack 1 animation
            BufferedImage atk1Sheet = ImageIO.read(getClass().getResource("assets/Warrior_Attack1.png"));
            attack1Anim = new Animation(sliceSpriteSheet(atk1Sheet, 4), 8, false);

            // load attack 2 animation
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
        
        // check if attack finished
        if (state == State.ATTACK1 || state == State.ATTACK2) {
            if (currentAnim.isFinished()) {
                state = State.IDLE; // return to idle
                currentAnim = idleAnim;
            }
        } else {
            // handle new input
            if (attack1) {
                state = State.ATTACK1; // start attack 1
                currentAnim = attack1Anim;
                currentAnim.reset();
            } else if (attack2) {
                state = State.ATTACK2; // start attack 2
                currentAnim = attack2Anim;
                currentAnim.reset();
            } else if (up || down || left || right) {
                state = State.RUNNING; // start running
                currentAnim = runAnim;

                int dx = 0, dy = 0;
                if (up) dy -= speed;
                if (down) dy += speed;
                if (left) dx -= speed;
                if (right) dx += speed;

                // normalize diagonal movement
                if (dx != 0 && dy != 0) {
                    dx = (int) (dx / Math.sqrt(2));
                    dy = (int) (dy / Math.sqrt(2));
                }

                x += dx;
                y += dy;

                // clamp to screen bounds
                if (x < 0) x = 0;
                if (x > SCREEN_WIDTH - SPRITE_WIDTH) x = SCREEN_WIDTH - SPRITE_WIDTH;
                if (y < 0) y = 0;
                if (y > SCREEN_HEIGHT - SPRITE_HEIGHT) y = SCREEN_HEIGHT - SPRITE_HEIGHT;
            } else {
                state = State.IDLE; // no input, idle
                currentAnim = idleAnim;
            }
        }

        currentAnim.update();
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(currentAnim.getCurrentFrame(), x, y, 96, 96, null);
    }
}