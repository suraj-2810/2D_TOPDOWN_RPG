import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.IOException;

public class Fireball {
    private int x, y;
    private double dx, dy; 
    private int speed;
    
    private Image fireballImage;
    private final int SPRITE_WIDTH = 32;
    private final int SPRITE_HEIGHT = 32;

    public Fireball(int startX, int startY, int targetX, int targetY, int speed) {
        this.x = startX - SPRITE_WIDTH / 2; 
        this.y = startY - SPRITE_HEIGHT / 2;
        this.speed = speed;

        loadSprite();

        double angle = Math.atan2(targetY - this.y, targetX - this.x);
        this.dx = speed * Math.cos(angle);
        this.dy = speed * Math.sin(angle);
    }
    
    private void loadSprite() {
        try {
            fireballImage = ImageIO.read(getClass().getResource("assets/projectile.png")).getSubimage(0, 0, 32, 32);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        x += dx;
        y += dy;
    }

    public void draw(Graphics2D g2) {
        if (fireballImage != null) {
            g2.drawImage(fireballImage, x, y, SPRITE_WIDTH, SPRITE_HEIGHT, null);
        }
    }
    
    public boolean isOffScreen(int screenWidth, int screenHeight) {
        return x < -SPRITE_WIDTH || x > screenWidth || y < -SPRITE_HEIGHT || y > screenHeight;
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, SPRITE_WIDTH, SPRITE_HEIGHT);
    }
}