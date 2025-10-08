import javax.swing.*;
import java.awt.*;

public class Gamewindow {
    public static void main(String[] args) {
        // create main window
        JFrame window = new JFrame("Top-Down RPG");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(800, 600);
        window.setResizable(false);

        // add game panel
        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);

        window.setLocationRelativeTo(null); // center on screen
        window.setVisible(true);
    }
}