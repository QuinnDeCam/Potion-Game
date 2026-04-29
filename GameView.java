import javax.swing.*;
import java.awt.*;

/**
 * GameView - Handles all UI rendering
 * Displays the game title screen and game elements
 */
public class GameView extends JPanel {
    
    private GameModel model;
    
    // Brewing UI components
    private JComboBox<String> ingredient1Combo;
    private JComboBox<String> ingredient2Combo;
    private JButton brewButton;
    private JLabel resultLabel;
    
    // Ingredient positions in Forest
    private Rectangle mushroomBounds = new Rectangle(200, 420, 50, 50);
    private Rectangle leafBounds = new Rectangle(450, 440, 50, 50);
    private Rectangle crystalBounds = new Rectangle(650, 400, 50, 50);
    
    public GameView(GameModel model) {
        this.model = model;
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.DARK_GRAY);
        setLayout(null);
        
        // Setup brewing UI
        setupBrewingUI();
    }
    
    private void setupBrewingUI() {
        // Ingredient 1 dropdown
        ingredient1Combo = new JComboBox<>(new String[]{"Mushroom", "Leaf", "Crystal"});
        ingredient1Combo.setBounds(250, 200, 120, 30);
        add(ingredient1Combo);
        
        // Plus sign
        JLabel plusLabel = new JLabel("+");
        plusLabel.setForeground(Color.WHITE);
        plusLabel.setFont(new Font("Arial", Font.BOLD, 24));
        plusLabel.setBounds(380, 195, 30, 30);
        add(plusLabel);
        
        // Ingredient 2 dropdown
        ingredient2Combo = new JComboBox<>(new String[]{"Mushroom", "Leaf", "Crystal"});
        ingredient2Combo.setBounds(420, 200, 120, 30);
        add(ingredient2Combo);
        
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
    public JComboBox<String> getIngredient1Combo() { return ingredient1Combo; }
    public JComboBox<String> getIngredient2Combo() { return ingredient2Combo; }
    public JButton getBrewButton() { return brewButton; }
    public JLabel getResultLabel() { return resultLabel; }
    
    // Getters for ingredient bounds (used by controller for click detection)
    public Rectangle getMushroomBounds() { return mushroomBounds; }
    public Rectangle getLeafBounds() { return leafBounds; }
    public Rectangle getCrystalBounds() { return crystalBounds; }
    
    // Helper to convert combo box selection to Ingredient
    public GameModel.Ingredient getSelectedIngredient1() {
        return stringToIngredient((String) ingredient1Combo.getSelectedItem());
    }
    
    public GameModel.Ingredient getSelectedIngredient2() {
        return stringToIngredient((String) ingredient2Combo.getSelectedItem());
    }
    
    private GameModel.Ingredient stringToIngredient(String name) {
        switch (name) {
            case "Mushroom": return GameModel.Ingredient.MUSHROOM;
            case "Leaf": return GameModel.Ingredient.LEAF;
            case "Crystal": return GameModel.Ingredient.CRYSTAL;
            default: return null;
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
        
        // Draw inventory display
        drawInventory(g);
        
        // Draw room-specific visual
        drawRoomVisual(g, currentRoom);
    }
    
    private void drawInventory(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.setColor(Color.WHITE);
        
        int inventoryX = 20;
        int inventoryY = 80;
        
        g.drawString("Inventory:", inventoryX, inventoryY);
        
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Mushrooms: " + model.getIngredientCount(GameModel.Ingredient.MUSHROOM), inventoryX, inventoryY + 25);
        g.drawString("Leaves: " + model.getIngredientCount(GameModel.Ingredient.LEAF), inventoryX, inventoryY + 50);
        g.drawString("Crystals: " + model.getIngredientCount(GameModel.Ingredient.CRYSTAL), inventoryX, inventoryY + 75);
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
        
        // Draw collectible ingredients (if not collected)
        drawIngredient(g, GameModel.Ingredient.MUSHROOM, mushroomBounds);
        drawIngredient(g, GameModel.Ingredient.LEAF, leafBounds);
        drawIngredient(g, GameModel.Ingredient.CRYSTAL, crystalBounds);
    }
    
    private void drawIngredient(Graphics g, GameModel.Ingredient ingredient, Rectangle bounds) {
        if (model.isIngredientCollected(ingredient)) {
            return; // Don't draw if already collected
        }
        
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