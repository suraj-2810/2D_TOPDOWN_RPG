// import javax.imageio.ImageIO;
// import java.awt.*;
// import java.awt.image.BufferedImage;
// import java.io.IOException;

// public class TileManager {
//     // private final int TILE_SIZE=(800);
//     private final int TILE_WIDTH = 800;
//     private final int TILE_HEIGHT = 600;
//     private BufferedImage grassTile;
//     private GamePanel gp;

//     public TileManager(GamePanel gp) {
//         this.gp = gp;
//         loadTiles();
//     }

//     private void loadTiles() {
//         try {
//             BufferedImage tilemap = ImageIO.read(getClass().getResource("assets/Ground/Tilemap_Flat.png"));
            
//             // Extract the grass tile from the correct position
//             // Based on the tilemap image, green grass appears to be at (0, 0) or nearby
//             // Adjust these coordinates to match the exact tile you want from your tilemap
//             grassTile = tilemap.getSubimage(0, 0, 180, 180);
            
//         } catch (IOException e) {
//             System.err.println("Error loading tilemap: " + e.getMessage());
//             e.printStackTrace();

//             // Fallback: create a solid green tile
//             grassTile = new BufferedImage(TILE_WIDTH, TILE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
//             Graphics2D g = grassTile.createGraphics();
//             g.setColor(new Color(145, 192, 59));
//             g.fillRect(0, 0, TILE_WIDTH, TILE_HEIGHT);
//             g.dispose();
//         }
//     }

//     public void draw(Graphics2D g2) {
//         if (grassTile != null) {
//             int screenWidth = gp.getWidth();
//             int screenHeight = gp.getHeight();

//             // Draw tiles to cover the entire screen
//             for (int y = 0; y < screenHeight; y += TILE_WIDTH) {
//                 for (int x = 0; x < screenWidth; x += TILE_HEIGHT) {
//                     g2.drawImage(grassTile, x, y, TILE_WIDTH, TILE_HEIGHT, null);
//                 }
//             }
//         }
//     }
// }
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class TileManager {
    private final int TILE_WIDTH = 800;
    private final int TILE_HEIGHT = 600;
    private BufferedImage grassTile; // background tile image
    private GamePanel gp;

    public TileManager(GamePanel gp) {
        this.gp = gp;
        loadTiles();
    }

    private void loadTiles() {
        try {
            // load tilemap spritesheet
            BufferedImage tilemap = ImageIO.read(getClass().getResource("assets/Ground/Tilemap_Flat.png"));
            
            // extract grass tile (180x180)
            grassTile = tilemap.getSubimage(0, 0, 180, 180);
            
        } catch (IOException e) {
            System.err.println("Error loading tilemap: " + e.getMessage());
            e.printStackTrace();

            // create green fallback
            grassTile = new BufferedImage(TILE_WIDTH, TILE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = grassTile.createGraphics();
            g.setColor(new Color(145, 192, 59));
            g.fillRect(0, 0, TILE_WIDTH, TILE_HEIGHT);
            g.dispose();
        }
    }

    public void draw(Graphics2D g2) {
        if (grassTile != null) {
            // stretch tile to screen
            g2.drawImage(grassTile, -30, -30, TILE_WIDTH+30, TILE_HEIGHT+30, null);
        }
    }
}