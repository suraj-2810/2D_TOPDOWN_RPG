import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public class Enemy {
    enum State {FLYING, ATTACK}
    private State state = State.FLYING;

    private int x, y, speed;
    private Animation flyingAnim, attackAnim, currentAnim;

    private static final int FRAME_WIDTH = 80;
    private static final int FRAME_HEIGHT = 128;
    private static final int SPRITE_WIDTH = 80;
    private static final int SPRITE_HEIGHT = 128;

    private long lastShotTime;
    private final long shootCooldown = 2500; // 2.5s

    public Enemy(int startX, int startY, int speed) {
        this.x = startX;
        this.y = startY;
        this.speed = speed;
        this.lastShotTime = System.currentTimeMillis();

        loadAnimations();
        this.currentAnim = flyingAnim;
    }

    private void loadAnimations() {
        try {
            BufferedImage flyingSheet = ImageIO.read(getClass().getResource("assets/FLYING.png"));
            flyingAnim = new Animation(sliceSpriteSheet(flyingSheet, 4), 10, true);

            BufferedImage attackSheet = ImageIO.read(getClass().getResource("assets/ATTACK.png"));
            attackAnim = new Animation(sliceSpriteSheet(attackSheet, 8), 12, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Image[] sliceSpriteSheet(BufferedImage sheet, int frameCount) {
        Image[] frames = new Image[frameCount];
        for (int i = 0; i < frameCount; i++) {
            frames[i] = sheet.getSubimage(i * FRAME_WIDTH, 0, FRAME_WIDTH, FRAME_HEIGHT);
        }
        return frames;
    }

    public void update(Player player, List<Fireball> fireballs) {
        if (state == State.ATTACK) {
            // Update animation and check if finished
            if (currentAnim.isFinished()) {
                state = State.FLYING;
                currentAnim = flyingAnim;
                currentAnim.reset(); // Reset flying animation
            }
        } else if (state == State.FLYING) {
            int playerX = player.getX();
            int playerY = player.getY();
            double angle = Math.atan2(playerY - y, playerX - x);
            x += speed * Math.cos(angle);
            y += speed * Math.sin(angle);

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShotTime > shootCooldown) {
                state = State.ATTACK;
                currentAnim = attackAnim;
                currentAnim.reset();
                
                // Only add fireball if list isn't getting too large
                if (fireballs.size() < 50) {
                    fireballs.add(new Fireball(x + SPRITE_WIDTH / 2, y + SPRITE_HEIGHT / 2, playerX, playerY, 6));
                }
                lastShotTime = currentTime;
            }
        }

        currentAnim.update();
    }
    
    public int getX() { return x; }
    public int getY() { return y; }

    public void draw(Graphics2D g2) {
        g2.drawImage(currentAnim.getCurrentFrame(), x, y, SPRITE_WIDTH, SPRITE_HEIGHT, null);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, SPRITE_WIDTH, SPRITE_HEIGHT);
    }
}