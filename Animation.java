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
    private Image[] frames; // all animation frames
    private int currentFrame; // current frame index
    private long lastTime, frameDelay; // timing control
    private boolean loop; // repeat animation
    private boolean finished; // animation complete

    public Animation(Image[] frames, int fps, boolean loop) {
        this.frames = frames;
        this.frameDelay = 1000 / fps; // ms per frame
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
                    currentFrame = 0; // restart animation
                } else {
                    currentFrame = frames.length - 1; // stay on last frame
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