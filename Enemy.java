import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Enemy {
    enum State {MOVING,ATTACK,IDLE}
    private State state = State.IDLE;

    private int x, y, speed;
    private Animation idleAnim, runAnim, attackAnim, currentAnim;
}