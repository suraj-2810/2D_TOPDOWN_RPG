import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Enemy {
    enum State {FLYING,ATTACK}//possible states
    private State state = State.FLYING;

    private int x, y, speed;
    private Animation  flyingAnim, attackAnim, currentAnim;//animation object

    private static final int FRAME_WIDTH = 80;
    private static final int FRAME_HEIGHT = 128;
    private static final int SPRITE_WIDTH = 80;
    private static final int SPRITE_HEIGHT = 128;

    private long last_shot_time;
    private final long shootcooldown = 2500;//2.5s

    public Enemy(int startX, int startY, int speed) {
        this.x = startX;
        this.y = startY;
        this.speed = speed;
        this.last_shot_time = System.currentTimeMillis();

        loadAnimations();
        this.currentAnim = flyingAnim;
    }

    private void loadAnimations() {
        try{
            BufferedImage flyingsheet = ImageIO.read(getClass().getResource("assets/demon/FLYING.png"));
            flyingAnim = new Animation(spliceSpriteSheet(flyingsheet, 4), 10, true);

            BufferedImage attacksheet = ImageIO.read(getClass().getResource("assets/demon/ATTACK.png"));
            flyingAnim = new Animation(spliceSpriteSheet(attacksheet, 8), 12, false);
        }catch (IOException e) {
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

            if (currentAnim.isFinished()) {
                state = State.FLYING;
                currentAnim = flyingAnim;
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
                currentAnim.reset(); // Start the animation from the first frame
                
                fireballs.add(new Fireball(x + SPRITE_WIDTH / 2, y + SPRITE_HEIGHT / 2, playerX, playerY, 6));
                lastShotTime = currentTime;
            }
        }

        currentAnim.update();//animation update
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(currentAnim.getCurrentFrame(), x, y, SPRITE_WIDTH, SPRITE_HEIGHT, null);
    }

    public Rectangle getBounds() {//collision
        return new Rectangle(x, y, SPRITE_WIDTH, SPRITE_HEIGHT);
    }
}