import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.image.BufferedImage;
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

        double angle = Math.atan2(targetY - startY, targetX - startX);
        this.dx = speed * Math.cos(angle);
        this.dy = speed * Math.sin(angle);
    }
    
    private void loadSprite() {
        try {
            BufferedImage projectileSheet = ImageIO.read(getClass().getResource("assets/projectile.png"));
            System.out.println("projectile.png loaded: " + projectileSheet.getWidth() + "x" + projectileSheet.getHeight());
            
            // Validate dimensions before slicing
            if (projectileSheet.getWidth() < 32 || projectileSheet.getHeight() < 32) {
                System.err.println("WARNING: projectile.png is smaller than expected (32x32)");
                throw new RuntimeException("Invalid projectile dimensions");
            }
            
            // You said it's 96x32, so we take the first 32x32 section
            fireballImage = projectileSheet.getSubimage(0, 0, 32, 32);
            
        } catch (Exception e) {
            System.err.println("Error loading fireball sprite: " + e.getMessage());
            e.printStackTrace();
            
            // Create a fallback red circle
            BufferedImage fallback = new BufferedImage(SPRITE_WIDTH, SPRITE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = fallback.createGraphics();
            g.setColor(Color.ORANGE);
            g.fillOval(0, 0, SPRITE_WIDTH, SPRITE_HEIGHT);
            g.setColor(Color.RED);
            g.fillOval(8, 8, 16, 16);
            g.dispose();
            fireballImage = fallback;
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