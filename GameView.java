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

    // Animation states
    private boolean isAnimating = false;
    private int animPhase = 0; // 0=none, 1=drop, 2=brew, 3=emerge
    private int animDropY = 0;
    private int animPotionY = 0;
    private GameModel.Potion resultPotion = null;

    // Inventory positions in Brewing Room
    private Rectangle invMushroomBounds = new Rectangle(50, 200, 50, 50);
    private Rectangle invLeafBounds = new Rectangle(50, 280, 50, 50);
    private Rectangle invCrystalBounds = new Rectangle(50, 360, 50, 50);
    private Rectangle journalButtonBounds = new Rectangle(700, 30, 60, 80);
    private Image leafImg;
    private Image mushroomImg;
    private Image crystalImg;
    private Image cauldronImg;
    private Image journalImg;

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
        journalImg = new ImageIcon("Journal.png").getImage();
    }

    public Rectangle getJournalButtonBounds() {
        return journalButtonBounds;
    }

    private void setupBrewingUI() {
        // Brew button
        brewButton = new JButton("Brew");
        brewButton.setBounds(630, 450, 100, 35);
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
        if (brewButton != null) {
            brewButton.setVisible(isBrewing);
            if (model.hasWon()) {
                brewButton.setEnabled(false);
            }
        }
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

    public void toggleIngredientSelection(GameModel.Ingredient ingredient, int availableCount) {
        if (isAnimating) return; // Disable clicking during animation
        
        int currentlySelected = 0;
        if (selectedIngredient1 == ingredient) currentlySelected++;
        if (selectedIngredient2 == ingredient) currentlySelected++;

        if (currentlySelected == 0) {
            // None selected, add to first available slot
            if (selectedIngredient1 == null) selectedIngredient1 = ingredient;
            else if (selectedIngredient2 == null) selectedIngredient2 = ingredient;
            else selectedIngredient1 = ingredient; // overwrite slot 1 if both full
        } else if (currentlySelected == 1) {
            // One selected. Can we select a second?
            if (availableCount >= 2 && (selectedIngredient1 == null || selectedIngredient2 == null)) {
                if (selectedIngredient1 == null) selectedIngredient1 = ingredient;
                else selectedIngredient2 = ingredient;
            } else {
                // Deselect it
                if (selectedIngredient1 == ingredient) selectedIngredient1 = null;
                if (selectedIngredient2 == ingredient) selectedIngredient2 = null;
            }
        } else {
            // Two selected. Deselect both.
            if (selectedIngredient1 == ingredient) selectedIngredient1 = null;
            if (selectedIngredient2 == ingredient) selectedIngredient2 = null;
        }
    }

    public void startBrewAnimation(GameModel.Potion result, Runnable onComplete) {
        if (isAnimating) return;
        
        isAnimating = true;
        animPhase = 1;
        animDropY = 0;
        animPotionY = 0;
        resultPotion = result;
        if (brewButton != null) brewButton.setEnabled(false);

        Timer timer = new Timer(33, null); // ~30 fps
        timer.addActionListener(e -> {
            if (animPhase == 1) {
                animDropY += 5; // Drop speed
                if (animDropY >= 110) { // Reached cauldron (190 -> 300)
                    animPhase = 2; // Brewing phase
                }
            } else if (animPhase == 2) {
                // 3 second wait (3000ms / 33ms = 90 frames roughly)
                // Let's use animDropY as a frame counter for phase 2 to avoid extra vars
                animDropY++;
                if (animDropY >= 200) { // 90 frames of waiting
                    animPhase = 3;
                    animPotionY = -50; // start slightly inside cauldron
                }
            } else if (animPhase == 3) {
                animPotionY += 3; // emerge speed
                if (animPotionY >= 60) { // fully emerged
                    timer.stop();
                    
                    // Reset animation state
                    isAnimating = false;
                    animPhase = 0;
                    animDropY = 0;
                    animPotionY = 0;
                    resultPotion = null;
                    
                    // Execute completion logic
                    if (onComplete != null) onComplete.run();
                }
            }
            repaint();
        });
        timer.start();
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
            if (journalImg != null && journalImg.getWidth(null) > 0) {
                int imgW = journalImg.getWidth(null);
                int imgH = journalImg.getHeight(null);
                double scale = Math.min((double) journalButtonBounds.width / imgW, (double) journalButtonBounds.height / imgH);
                int drawW = (int) (imgW * scale);
                int drawH = (int) (imgH * scale);
                int drawX = journalButtonBounds.x + (journalButtonBounds.width - drawW) / 2;
                int drawY = journalButtonBounds.y + (journalButtonBounds.height - drawH) / 2;
                g.drawImage(journalImg, drawX, drawY, drawW, drawH, this);
            } else {
                g.setColor(new Color(139, 69, 19)); // Brown cover
                g.fillRect(journalButtonBounds.x, journalButtonBounds.y, journalButtonBounds.width, journalButtonBounds.height);
                g.setColor(new Color(210, 180, 140)); // Pages edge
                g.fillRect(journalButtonBounds.x + 45, journalButtonBounds.y + 5, 10, 70);
                g.setColor(new Color(255, 215, 0)); // Gold trim
                g.drawRect(journalButtonBounds.x + 5, journalButtonBounds.y + 5, journalButtonBounds.width - 20,
                        journalButtonBounds.height - 10);
            }

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

            // Draw icon (gray rectangle)
            g.setColor(Color.GRAY);
            g.fillRect(jx + 40, textY - 15, 30, 30);

            if (discoveredSet.contains(p)) {
                g.setColor(new Color(0, 100, 0));
                g.setFont(new Font("Georgia", Font.BOLD, 18));
                g.drawString(potionName, jx + 80, textY);
                
                // Draw recipe
                g.setColor(Color.DARK_GRAY);
                g.setFont(new Font("Georgia", Font.ITALIC, 14));
                GameModel.Ingredient[] recipe = model.getRecipe(p);
                if (recipe != null && recipe.length == 2) {
                    String ing1 = recipe[0].name().toLowerCase();
                    ing1 = ing1.substring(0, 1).toUpperCase() + ing1.substring(1);
                    String ing2 = recipe[1].name().toLowerCase();
                    ing2 = ing2.substring(0, 1).toUpperCase() + ing2.substring(1);
                    g.drawString("Recipe: " + ing1 + " + " + ing2, jx + 80, textY + 15);
                }
            } else {
                g.setColor(Color.GRAY);
                g.setFont(new Font("Georgia", Font.BOLD, 18));
                g.drawString("Unknown Recipe", jx + 80, textY);
                
                // Draw unknown recipe
                g.setColor(Color.LIGHT_GRAY);
                g.setFont(new Font("Georgia", Font.ITALIC, 14));
                g.drawString("Recipe: ? + ?", jx + 80, textY + 15);
            }
            textY += 50;
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
        if (selectedIngredient1 != null && animPhase < 2) {
            drawIngredient(g, selectedIngredient1, new Rectangle(300, 190 + animDropY, 50, 50));
        }
        if (!isAnimating) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("+", 380, 220);
        }
        if (selectedIngredient2 != null && animPhase < 2) {
            drawIngredient(g, selectedIngredient2, new Rectangle(420, 190 + animDropY, 50, 50));
        }

        // Draw emerging potion
        if (animPhase == 3 && resultPotion != null) {
            // Placeholder: gray rectangle with name, or just a colored box
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(360, 300 - animPotionY, 80, 80);
            g.setColor(Color.BLACK);
            g.drawRect(360, 300 - animPotionY, 80, 80);
            
            // Text to show what it is
            g.setFont(new Font("Arial", Font.BOLD, 12));
            String name = resultPotion.name().replace("_POTION", "");
            if (resultPotion == GameModel.Potion.UNKNOWN_MIXTURE) name = "UNKNOWN";
            g.drawString(name, 365, 300 - animPotionY + 45);
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