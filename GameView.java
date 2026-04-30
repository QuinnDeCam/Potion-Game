import javax.swing.*;
import java.awt.*;
import java.util.Set;

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
    private Rectangle journalButtonBounds = new Rectangle(700, 30, 60, 80);
    private Image leafImg;
    private Image mushroomImg;
    private Image crystalImg;
    private Image cauldronImg;

    public GameView(GameModel model) {
        this.model = model;
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.DARK_GRAY);
        setLayout(null);

        // Setup brewing UI
        setupBrewingUI();

        cauldronImg = new ImageIcon("Cauldron.png").getImage();
        leafImg = new ImageIcon("Leaf.png").getImage();
        mushroomImg = new ImageIcon("Mushroom.png").getImage();
        crystalImg = new ImageIcon("Crystal.png").getImage();
    }

    public Rectangle getJournalButtonBounds() {
        return journalButtonBounds;
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
        
        updateUIVisibility(model.getCurrentRoom());
    }
    
    public void updateUIVisibility(GameModel.Room currentRoom) {
        boolean isBrewing = (currentRoom == GameModel.Room.BREWING_ROOM) && !model.isJournalOpen();
        if (brewButton != null) brewButton.setVisible(isBrewing);
        if (resultLabel != null) resultLabel.setVisible(isBrewing);
    }

    // Getters for brewing components (used by controller)
    public JButton getBrewButton() {
        return brewButton;
    }

    public JLabel getResultLabel() {
        return resultLabel;
    }

    // Getters for inventory bounds (used by controller for click detection)
    public Rectangle getInvMushroomBounds() {
        return invMushroomBounds;
    }

    public Rectangle getInvLeafBounds() {
        return invLeafBounds;
    }

    public Rectangle getInvCrystalBounds() {
        return invCrystalBounds;
    }

    public GameModel.Ingredient getSelectedIngredient1() {
        return selectedIngredient1;
    }

    public GameModel.Ingredient getSelectedIngredient2() {
        return selectedIngredient2;
    }

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

        if (currentRoom == GameModel.Room.BREWING_ROOM) {
            // Draw journal button
            g.setColor(new Color(139, 69, 19)); // Brown cover
            g.fillRect(journalButtonBounds.x, journalButtonBounds.y, journalButtonBounds.width, journalButtonBounds.height);
            g.setColor(new Color(210, 180, 140)); // Pages edge
            g.fillRect(journalButtonBounds.x + 45, journalButtonBounds.y + 5, 10, 70);
            g.setColor(new Color(255, 215, 0)); // Gold trim
            g.drawRect(journalButtonBounds.x + 5, journalButtonBounds.y + 5, journalButtonBounds.width - 20,
                    journalButtonBounds.height - 10);

            // Draw X of Y above the book
            int discovered = model.getDiscoveredPotions().size();
            int total = model.getTotalDiscoverablePotions();
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString(discovered + "/" + total, journalButtonBounds.x + 10, journalButtonBounds.y - 10);

            // Draw journal overlay if open
            if (model.isJournalOpen()) {
                drawJournalOverlay(g);
            }
        }
    }

    private void drawJournalOverlay(Graphics g) {
        // Semi-transparent background
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, getWidth(), getHeight());

        // Journal pages
        int jx = 200, jy = 100, jw = 400, jh = 400;
        g.setColor(new Color(245, 222, 179)); // Wheat/parchment color
        g.fillRect(jx, jy, jw, jh);
        g.setColor(new Color(139, 69, 19)); // Dark brown border
        g.drawRect(jx, jy, jw, jh);
        g.drawRect(jx + 2, jy + 2, jw - 4, jh - 4);

        // Title
        g.setFont(new Font("Georgia", Font.BOLD, 24));
        g.setColor(Color.BLACK);
        g.drawString("Potions Journal", jx + 40, jy + 40);

        Set<GameModel.Potion> discoveredSet = model.getDiscoveredPotions();
        int totalPotions = model.getTotalDiscoverablePotions();

        g.setFont(new Font("Georgia", Font.ITALIC, 16));
        g.drawString("Discovered: " + discoveredSet.size() + " of " + totalPotions, jx + 40, jy + 70);

        int textY = jy + 110;
        for (GameModel.Potion p : GameModel.Potion.values()) {
            if (p == GameModel.Potion.UNKNOWN_MIXTURE)
                continue;

            String potionName = p.name().toLowerCase().replace("_", " ");
            potionName = potionName.substring(0, 1).toUpperCase() + potionName.substring(1);

            g.setFont(new Font("Georgia", Font.BOLD, 18));
            if (discoveredSet.contains(p)) {
                g.setColor(new Color(0, 100, 0));
                g.drawString("✓ " + potionName, jx + 40, textY);
            } else {
                g.setColor(Color.GRAY);
                g.drawString("? Unknown Recipe", jx + 40, textY);
            }
            textY += 30;
        }
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
        g.fillPolygon(new int[] { 135, 165, 195 }, new int[] { 250, 350, 250 }, 3);
        g.fillPolygon(new int[] { 385, 435, 485 }, new int[] { 200, 320, 200 }, 3);
        g.fillPolygon(new int[] { 585, 630, 675 }, new int[] { 270, 360, 270 }, 3);

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
                if (mushroomImg != null) {
                    g.drawImage(mushroomImg, bounds.x, bounds.y, bounds.width, bounds.height, this);
                }
                break;

            case LEAF:
                if (leafImg != null) {
                    g.drawImage(leafImg, bounds.x, bounds.y, bounds.width, bounds.height, this);
                }
                break;

            case CRYSTAL:
                if (crystalImg != null) {
                    g.drawImage(crystalImg, bounds.x, bounds.y, bounds.width, bounds.height, this);
                }
                break;
        }

        // Draw label
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 12));

        String name = ingredient.name()
                .toLowerCase();
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        g.drawString(name, bounds.x + 5, bounds.y - 5);
    }

    // Removed ingredient cupboard

    private void drawBrewingRoom(Graphics g) {
        // Draw cauldron image
        if (cauldronImg != null) {
            g.drawImage(cauldronImg, 184, 300, 432, 360, this);
        }

        // Shelf with bottles
        // g.setColor(new Color(139, 69, 19));
        // g.fillRect(50, 150, 150, 20);
        // g.fillRect(600, 150, 150, 20);

        // Bottles
        // g.setColor(Color.CYAN);
        // g.fillOval(80, 120, 30, 40);
        // g.setColor(Color.GREEN);
        // g.fillOval(120, 130, 25, 30);
        // g.setColor(Color.RED);
        // g.fillOval(650, 120, 30, 40);
        // g.setColor(Color.BLUE);
        // g.fillOval(700, 130, 25, 30);

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

            // Draw single item
            drawIngredient(g, type, bounds);

            // Draw count
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString("x" + count, bounds.x + 15, bounds.y + bounds.height + 15);
        }
    }
}