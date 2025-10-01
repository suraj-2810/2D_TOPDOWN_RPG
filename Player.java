// import javax.imageio.ImageIO;
// import java.awt.*;
// import java.awt.image.BufferedImage;
// import java.io.IOException;

// public class Player {
//     enum State { IDLE, RUNNING, ATTACK1, ATTACK2 }
//     private State state = State.IDLE;

//     private int x, y, speed;
//     private Animation idleAnim, runAnim, attack1Anim, attack2Anim, currentAnim;

//     private static final int FRAME_SIZE = 192; 
//     public Player(int startX, int startY, int speed) {
//         this.x = startX;
//         this.y = startY;
//         this.speed = speed;

//         loadAnimations();
//         currentAnim = idleAnim; //idle
//     }

//     private void loadAnimations() {//har spritesheet ka frame count wise animation set kiya hai
//         try {
//             BufferedImage idleSheet = ImageIO.read(getClass().getResource("assets/Warrior_Idle.png"));
//             idleAnim = new Animation(sliceSpriteSheet(idleSheet, 8), 10);

//             BufferedImage runSheet = ImageIO.read(getClass().getResource("assets/Warrior_Run.png"));
//             runAnim = new Animation(sliceSpriteSheet(runSheet, 6), 12);

//             BufferedImage atk1Sheet = ImageIO.read(getClass().getResource("assets/Warrior_Attack1.png"));
//             attack1Anim = new Animation(sliceSpriteSheet(atk1Sheet, 4), 8);

//             BufferedImage atk2Sheet = ImageIO.read(getClass().getResource("assets/Warrior_Attack2.png"));
//             attack2Anim = new Animation(sliceSpriteSheet(atk2Sheet, 4), 8);

//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

//     private Image[] sliceSpriteSheet(BufferedImage sheet, int frameCount) {//splicing
//         Image[] frames = new Image[frameCount];
//         for (int i = 0; i < frameCount; i++) {
//             frames[i] = sheet.getSubimage(i * FRAME_SIZE, 0, FRAME_SIZE, FRAME_SIZE);
//         }
//         return frames;
//     }

//     public void update(boolean up, boolean down, boolean left, boolean right,
//                        boolean attack1, boolean attack2) {// Movement & animation update

//         if (attack1) {
//             state = State.ATTACK1;
//             currentAnim = attack1Anim;
//         } else if (attack2) {
//             state = State.ATTACK2;
//             currentAnim = attack2Anim;
//         } else if (up || down || left || right) {
//             state = State.RUNNING;
//             currentAnim = runAnim;

//             if (up) y -= speed;
//             if (down) y += speed;
//             if (left) x -= speed;
//             if (right) x += speed;
//         } else {
//             state = State.IDLE;
//             currentAnim = idleAnim;
//         }

//         currentAnim.update();
//     }

//     public void draw(Graphics2D g2) {
//         g2.drawImage(currentAnim.getCurrentFrame(), x, y, 96, 96, null); 
//         // reducing size
//     }
// }
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
    
    public Player(int startX, int startY, int speed) {
        this.x = startX;
        this.y = startY;
        this.speed = speed;

        loadAnimations();
        currentAnim = idleAnim;
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
                // Attack animation finished lopp back to idle to idle
                state = State.IDLE;
                currentAnim = idleAnim;
            }
        } else {
            // new inputs if not attacking
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

                int dx = 0, dy = 0;//finding delta
                if (up) dy -= speed;
                if (down) dy += speed;
                if (left) dx -= speed;
                if (right) dx += speed;

                if (dx != 0 && dy != 0) {//diagonal speed balancinging
                    dx = (int) (dx / Math.sqrt(2));
                    dy = (int) (dy / Math.sqrt(2));
                }

                x += dx;
                y += dy;

                //bounds not below y=0,x=0and x=800-96 ,y=800-96
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