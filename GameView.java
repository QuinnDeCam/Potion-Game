import javax.swing.*;
import java.awt.*;

/**
 * GameView - Handles all UI rendering
 * Displays the game title screen and game elements
 */
public class GameView extends JPanel {
    
    private GameModel model;
    
    // Brewing UI components
    private JButton brewButton;
    private JLabel resultLabel;
    
    // Selected ingredients for brewing
    private GameModel.Ingredient selectedIngredient1 = null;
    private GameModel.Ingredient selectedIngredient2 = null;
    
    // Inventory positions in Brewing Room
    private Rectangle invMushroomBounds = new Rectangle(50, 200, 50, 50);
    private Rectangle invLeafBounds = new Rectangle(50, 280, 50, 50);
    private Rectangle invCrystalBounds = new Rectangle(50, 360, 50, 50);
    
    public GameView(GameModel model) {
        this.model = model;
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.DARK_GRAY);
        setLayout(null);
        
        // Setup brewing UI
        setupBrewingUI();
    }
    
    private void setupBrewingUI() {
        // Brew button
        brewButton = new JButton("Brew");
        brewButton.setBounds(320, 250, 100, 35);
        brewButton.setFont(new Font("Arial", Font.BOLD, 16));
        add(brewButton);
        
        // Result label
        resultLabel = new JLabel("");
        resultLabel.setForeground(Color.YELLOW);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 20));
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        resultLabel.setBounds(200, 300, 400, 30);
        add(resultLabel);
    }
    
    // Getters for brewing components (used by controller)
    public JButton getBrewButton() { return brewButton; }
    public JLabel getResultLabel() { return resultLabel; }
    
    // Getters for inventory bounds (used by controller for click detection)
    public Rectangle getInvMushroomBounds() { return invMushroomBounds; }
    public Rectangle getInvLeafBounds() { return invLeafBounds; }
    public Rectangle getInvCrystalBounds() { return invCrystalBounds; }
    
    public GameModel.Ingredient getSelectedIngredient1() { return selectedIngredient1; }
    public GameModel.Ingredient getSelectedIngredient2() { return selectedIngredient2; }
    
    public void clearSelections() {
        selectedIngredient1 = null;
        selectedIngredient2 = null;
    }
    
    public void toggleIngredientSelection(GameModel.Ingredient ingredient) {
        if (selectedIngredient1 == ingredient) {
            selectedIngredient1 = null;
        } else if (selectedIngredient2 == ingredient) {
            selectedIngredient2 = null;
        } else if (selectedIngredient1 == null) {
            selectedIngredient1 = ingredient;
        } else if (selectedIngredient2 == null) {
            selectedIngredient2 = ingredient;
        } else {
            // Replace the first one if both are full
            selectedIngredient1 = ingredient;
        }
    }
    
    public void updateBrewResult(String result) {
        resultLabel.setText(result);
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
        
        // Draw active collectible ingredients
        for (GameModel.SpawnedIngredient ingredient : model.getActiveIngredients()) {
            drawIngredient(g, ingredient.type, ingredient.bounds);
        }
    }
    
    private void drawIngredient(Graphics g, GameModel.Ingredient ingredient, Rectangle bounds) {
        
        switch (ingredient) {
            case MUSHROOM:
                // Red mushroom cap
                g.setColor(new Color(200, 50, 50));
                g.fillOval(bounds.x, bounds.y, bounds.width, bounds.height / 2);
                // White stem
                g.setColor(Color.WHITE);
                g.fillRect(bounds.x + 15, bounds.y + 25, 20, 25);
                break;
            case LEAF:
                // Green leaf
                g.setColor(new Color(50, 200, 50));
                g.fillOval(bounds.x, bounds.y, bounds.width, bounds.height);
                g.setColor(new Color(100, 150, 50));
                g.drawLine(bounds.x + 25, bounds.y + 10, bounds.x + 25, bounds.y + 40);
                break;
            case CRYSTAL:
                // Blue crystal
                g.setColor(new Color(100, 150, 255));
                int[] crystalX = {bounds.x + 25, bounds.x + 45, bounds.x + 25, bounds.x + 5};
                int[] crystalY = {bounds.y, bounds.y + 25, bounds.y + 50, bounds.y + 25};
                g.fillPolygon(crystalX, crystalY, 4);
                // Crystal shine
                g.setColor(Color.WHITE);
                g.fillOval(bounds.x + 20, bounds.y + 15, 8, 8);
                break;
        }
        
        // Draw label
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        String name = ingredient.name().toLowerCase();
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        g.drawString(name, bounds.x + 5, bounds.y - 5);
    }
    
    // Removed ingredient cupboard
    
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
        
        // Draw selected ingredients in the top area (plus sign visual)
        if (selectedIngredient1 != null) {
            drawIngredient(g, selectedIngredient1, new Rectangle(300, 190, 50, 50));
        }
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("+", 380, 220);
        if (selectedIngredient2 != null) {
            drawIngredient(g, selectedIngredient2, new Rectangle(420, 190, 50, 50));
        }
        
        // Draw Inventory Stacks
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Inventory", 40, 180);
        
        drawInventoryStack(g, GameModel.Ingredient.MUSHROOM, invMushroomBounds);
        drawInventoryStack(g, GameModel.Ingredient.LEAF, invLeafBounds);
        drawInventoryStack(g, GameModel.Ingredient.CRYSTAL, invCrystalBounds);
    }
    
    private void drawInventoryStack(Graphics g, GameModel.Ingredient type, Rectangle bounds) {
        int count = model.getIngredientCount(type);
        if (count > 0) {
            // Draw highlight if selected
            if (selectedIngredient1 == type || selectedIngredient2 == type) {
                g.setColor(new Color(255, 255, 0, 100)); // semi-transparent yellow
                g.fillRect(bounds.x - 5, bounds.y - 5, bounds.width + 10, bounds.height + 30);
                g.setColor(Color.YELLOW);
                g.drawRect(bounds.x - 5, bounds.y - 5, bounds.width + 10, bounds.height + 30);
            }
            
            // Draw multiple to look like a stack
            for (int i = 0; i < Math.min(count, 3); i++) {
                Rectangle stackBounds = new Rectangle(bounds.x + (i*5), bounds.y - (i*5), bounds.width, bounds.height);
                drawIngredient(g, type, stackBounds);
            }
            
            // Draw count
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString("x" + count, bounds.x + 15, bounds.y + bounds.height + 15);
        }
    }
}