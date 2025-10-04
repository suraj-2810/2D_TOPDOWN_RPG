import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public class Enemy {
    enum State {FLYING, ATTACK, IDLE}
    private State state = State.FLYING;//starting at flying

    private int x, y, speed;
    private Animation flyingAnim, attackAnim, currentAnim;//animation object

    private static final int FRAME_WIDTH = 80;
    private static final int FRAME_HEIGHT = 70;
    private static final int SPRITE_WIDTH = 80;
    private static final int SPRITE_HEIGHT = 70;
    
    private static final int ATTACK_RANGE = 200;//start shooting at this
    private static final int MIN_DISTANCE = 150;//don't get closer than this

    private long lastShotTime;
    private final long shootCooldown = 2500;//2.5s cooldown

    public Enemy(int startX, int startY, int speed) {
        this.x = startX;
        this.y = startY;
        this.speed = speed;
        this.lastShotTime = System.currentTimeMillis();

        loadAnimations();
        this.currentAnim = flyingAnim;//start at flying
    }

    private void loadAnimations() {
        try {
            BufferedImage flyingSheet = ImageIO.read(getClass().getResource("assets/FLYING.png"));
            System.out.println("FLYING.png loaded: " + flyingSheet.getWidth() + "x" + flyingSheet.getHeight());
            
            if (flyingSheet.getHeight() != 70) {//error control
                System.err.println("WARNING: FLYING.png has unexpected height! Expected 70, got " + flyingSheet.getHeight());
            }
            flyingAnim = new Animation(sliceSpriteSheet(flyingSheet, 4, "FLYING"), 10, true);

            BufferedImage attackSheet = ImageIO.read(getClass().getResource("assets/ATTACK.png"));
            System.out.println("ATTACK.png loaded: " + attackSheet.getWidth() + "x" + attackSheet.getHeight());
            
            if (attackSheet.getHeight() != 70) {//error control
                System.err.println("WARNING: ATTACK.png has unexpected height! Expected 70, got " + attackSheet.getHeight());
            }
            attackAnim = new Animation(sliceSpriteSheet(attackSheet, 8, "ATTACK"), 12, false);
            
            System.out.println("Enemy animations loaded successfully");
            
        } catch (IOException e) {
            System.err.println("Error loading enemy sprites: " + e.getMessage());
            e.printStackTrace();
            
            Image[] fallbackFrames = new Image[1];//fallback for enemy 
            BufferedImage placeholder = new BufferedImage(FRAME_WIDTH, FRAME_HEIGHT, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = placeholder.createGraphics();
            g.setColor(Color.RED);
            g.fillRect(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
            g.dispose();
            fallbackFrames[0] = placeholder;
            
            flyingAnim = new Animation(fallbackFrames, 1, true);
            attackAnim = new Animation(fallbackFrames, 1, false);
        }
    }

    private Image[] sliceSpriteSheet(BufferedImage sheet, int frameCount, String sheetName) {
        Image[] frames = new Image[frameCount];
        
        try {
            for (int i = 0; i < frameCount; i++) {
                int xPos = i * FRAME_WIDTH;
                
                if (xPos + FRAME_WIDTH > sheet.getWidth()) {
                    System.err.println("ERROR: Frame " + i + " of " + sheetName + " exceeds sheet width!");
                    System.err.println("Trying to access x=" + xPos + " to " + (xPos + FRAME_WIDTH) + 
                                     " but sheet width is " + sheet.getWidth());
                    throw new RuntimeException("Invalid frame dimensions");
                }
                
                if (FRAME_HEIGHT > sheet.getHeight()) {
                    System.err.println("ERROR: Frame height " + FRAME_HEIGHT + " exceeds sheet height " + sheet.getHeight());
                    throw new RuntimeException("Invalid frame dimensions");
                }
                
                frames[i] = sheet.getSubimage(xPos, 0, FRAME_WIDTH, FRAME_HEIGHT);
            }
        } catch (Exception e) {
            System.err.println("Error slicing " + sheetName + ": " + e.getMessage());
            e.printStackTrace();
            
            for (int i = 0; i < frameCount; i++) {
                BufferedImage errorFrame = new BufferedImage(FRAME_WIDTH, FRAME_HEIGHT, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = errorFrame.createGraphics();
                g.setColor(Color.MAGENTA);
                g.fillRect(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
                g.dispose();
                frames[i] = errorFrame;
            }
        }
        
        return frames;
    }

    public void update(Player player, List<Fireball> fireballs) {//fireball direction
        int playerX = player.getX();
        int playerY = player.getY();
        
        double distanceToPlayer = Math.sqrt(
            Math.pow(playerX - (x + SPRITE_WIDTH / 2), 2) + 
            Math.pow(playerY - (y + SPRITE_HEIGHT / 2), 2)
        );
        
        if (state == State.ATTACK) {
            if (currentAnim.isFinished()) {//animation swithching based on player 
                if (distanceToPlayer > ATTACK_RANGE) {
                    state = State.FLYING;
                    currentAnim = flyingAnim;
                    currentAnim.reset();
                } else {
                    state = State.IDLE;
                    currentAnim = flyingAnim;
                }
            }
        } else if (state == State.IDLE) {
            if (distanceToPlayer > ATTACK_RANGE) {
                state = State.FLYING;
                currentAnim = flyingAnim;
            } else if (distanceToPlayer < MIN_DISTANCE) {
                double angle = Math.atan2(playerY - y, playerX - x);
                x -= speed * Math.cos(angle) * 0.5;
                y -= speed * Math.sin(angle) * 0.5;
            }
            
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShotTime > shootCooldown) {//animatiom switch for attack based on cooldown
                state = State.ATTACK;
                currentAnim = attackAnim;
                currentAnim.reset();
                
                if (fireballs.size() < 50) {
                    fireballs.add(new Fireball(x + SPRITE_WIDTH / 2, y + SPRITE_HEIGHT / 2, playerX, playerY, 6));
                }
                lastShotTime = currentTime;
            }
        } else if (state == State.FLYING) {
            if (distanceToPlayer <= ATTACK_RANGE && distanceToPlayer >= MIN_DISTANCE) {
                state = State.IDLE;
                currentAnim = flyingAnim;
            } else if (distanceToPlayer < MIN_DISTANCE) {
                double angle = Math.atan2(playerY - y, playerX - x);
                x -= speed * Math.cos(angle);
                y -= speed * Math.sin(angle);
            } else {
                double angle = Math.atan2(playerY - y, playerX - x);
                x += speed * Math.cos(angle);
                y += speed * Math.sin(angle);
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