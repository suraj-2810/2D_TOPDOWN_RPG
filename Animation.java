// import java.awt.Image;

// public class Animation {
//     private Image[] frames;
//     private int currentFrame;
//     private long lastTime, frameDelay;

//     public Animation(Image[] frames, int fps) {
//         this.frames = frames;
//         this.frameDelay = 1000 / fps; // 1ms
//         this.currentFrame = 0;
//         this.lastTime = System.currentTimeMillis();
//     }

//     public void update() {
//         long now = System.currentTimeMillis();
//         if (now - lastTime >= frameDelay) {
//             currentFrame = (currentFrame + 1) % frames.length;
//             lastTime = now;
//         }
//     }

//     public Image getCurrentFrame() {
//         return frames[currentFrame];
//     }

//     public void reset() {
//         currentFrame = 0;
//         lastTime = System.currentTimeMillis();
//     }
// }
import java.awt.Image;

public class Animation {
    private Image[] frames;
    private int currentFrame;
    private long lastTime, frameDelay;
    private boolean loop;
    private boolean finished;

    public Animation(Image[] frames, int fps, boolean loop) {
        this.frames = frames;
        this.frameDelay = 1000 / fps;
        this.currentFrame = 0;
        this.lastTime = System.currentTimeMillis();
        this.loop = loop;
        this.finished = false;
    }

    public void update() {
        long now = System.currentTimeMillis();
        if (now - lastTime >= frameDelay) {
            currentFrame++;
            
            if (currentFrame >= frames.length) {
                if (loop) {
                    currentFrame = 0;
                } else {
                    currentFrame = frames.length - 1;
                    finished = true;
                }
            }
            
            lastTime = now;
        }
    }

    public Image getCurrentFrame() {
        return frames[currentFrame];
    }

    public void reset() {
        currentFrame = 0;
        finished = false;
        lastTime = System.currentTimeMillis();
    }
    
    public boolean isFinished() {
        return finished;
    }
}