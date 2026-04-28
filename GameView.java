import javax.swing.*;
import java.awt.*;

/**
 * GameView - Handles all UI rendering
 * Displays the game title screen and game elements
 */
public class GameView extends JPanel {
    
    private GameModel model;
    
    public GameView(GameModel model) {
        this.model = model;
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.DARK_GRAY);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        GameModel.Room currentRoom = model.getCurrentRoom();
        
        // Draw room name at top
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        String roomName = getRoomDisplayName(currentRoom);
        FontMetrics fm = g.getFontMetrics();
        int titleWidth = fm.stringWidth(roomName);
        int centerX = (getWidth() - titleWidth) / 2;
        g.drawString(roomName, centerX, 60);
        
        // Draw room-specific visual
        drawRoomVisual(g, currentRoom);
    }
    
    private String getRoomDisplayName(GameModel.Room room) {
        switch (room) {
            case FOREST:
                return "Forest";
            case INGREDIENT_CUPBOARD:
                return "Ingredient Cupboard";
            case BREWING_ROOM:
                return "Brewing Room";
            default:
                return "Unknown";
        }
    }
    
    private void drawRoomVisual(Graphics g, GameModel.Room room) {
        switch (room) {
            case FOREST:
                drawForest(g);
                break;
            case INGREDIENT_CUPBOARD:
                drawIngredientCupboard(g);
                break;
            case BREWING_ROOM:
                drawBrewingRoom(g);
                break;
        }
    }
    
    private void drawForest(Graphics g) {
        // Draw grass ground
        g.setColor(new Color(34, 139, 34));
        g.fillRect(50, 400, 700, 150);
        
        // Draw trees (triangles)
        g.setColor(new Color(139, 69, 19)); // brown trunk
        g.fillRect(150, 350, 30, 100);
        g.fillRect(400, 320, 30, 130);
        g.fillRect(600, 360, 30, 90);
        
        g.setColor(new Color(0, 100, 0)); // green foliage
        g.fillPolygon(new int[] {135, 165, 195}, new int[] {250, 350, 250}, 3);
        g.fillPolygon(new int[] {385, 435, 485}, new int[] {200, 320, 200}, 3);
        g.fillPolygon(new int[] {585, 630, 675}, new int[] {270, 360, 270}, 3);
        
        // Draw sun
        g.setColor(Color.YELLOW);
        g.fillOval(650, 50, 80, 80);
    }
    
    private void drawIngredientCupboard(Graphics g) {
        // Draw wooden shelves
        g.setColor(new Color(139, 69, 19));
        g.fillRect(100, 150, 600, 30);
        g.fillRect(100, 280, 600, 30);
        g.fillRect(100, 410, 600, 30);
        
        // Draw jars on shelves
        g.setColor(new Color(255, 200, 200)); // pink jar
        g.fillOval(150, 180, 40, 50);
        g.setColor(new Color(200, 255, 200)); // green jar
        g.fillOval(250, 180, 40, 50);
        g.setColor(new Color(200, 200, 255)); // blue jar
        g.fillOval(350, 180, 40, 50);
        g.setColor(new Color(255, 255, 200)); // yellow jar
        g.fillOval(450, 180, 40, 50);
        g.setColor(new Color(255, 200, 100)); // orange jar
        g.fillOval(550, 180, 40, 50);
        
        g.setColor(new Color(255, 200, 200));
        g.fillOval(180, 310, 40, 50);
        g.setColor(new Color(200, 255, 200));
        g.fillOval(300, 310, 40, 50);
        g.setColor(new Color(200, 200, 255));
        g.fillOval(420, 310, 40, 50);
        
        g.setColor(new Color(255, 255, 200));
        g.fillOval(200, 440, 40, 50);
        g.setColor(new Color(255, 200, 100));
        g.fillOval(350, 440, 40, 50);
        g.setColor(new Color(200, 255, 255));
        g.fillOval(500, 440, 40, 50);
    }
    
    private void drawBrewingRoom(Graphics g) {
        // Draw cauldron
        g.setColor(new Color(80, 80, 80)); // dark gray cauldron
        g.fillOval(250, 350, 300, 100);
        g.fillRect(250, 300, 300, 100);
        
        // Cauldron rim
        g.setColor(new Color(100, 100, 100));
        g.fillOval(240, 340, 320, 40);
        
        // Bubbling liquid
        g.setColor(new Color(128, 0, 128)); // purple
        g.fillOval(270, 360, 260, 70);
        
        // Bubbles
        g.setColor(new Color(200, 100, 200));
        g.fillOval(320, 340, 20, 20);
        g.fillOval(400, 350, 15, 15);
        g.fillOval(480, 340, 25, 25);
        
        // Fire under cauldron
        g.setColor(Color.ORANGE);
        g.fillOval(320, 450, 40, 40);
        g.fillOval(380, 460, 50, 50);
        g.fillOval(440, 450, 40, 40);
        g.setColor(Color.RED);
        g.fillOval(350, 460, 30, 30);
        g.fillOval(420, 465, 25, 25);
        
        // Shelf with bottles
        g.setColor(new Color(139, 69, 19));
        g.fillRect(50, 150, 150, 20);
        g.fillRect(600, 150, 150, 20);
        
        // Bottles
        g.setColor(Color.CYAN);
        g.fillOval(80, 120, 30, 40);
        g.setColor(Color.GREEN);
        g.fillOval(120, 130, 25, 30);
        g.setColor(Color.RED);
        g.fillOval(650, 120, 30, 40);
        g.setColor(Color.BLUE);
        g.fillOval(700, 130, 25, 30);
    }
}