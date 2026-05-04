import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Set;
import java.util.Map;
import java.util.EnumMap;

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

    // Discovery Animation states
    private boolean animIsNewDiscovery = false;
    private double animRotation = 0;
    private double animScale = 1.0;
    private double animCurrentX = 0;
    private double animCurrentY = 0;
    private int animTargetX = 0;
    private int animTargetY = 0;
    private int lingerFrames = 0;

    // Inventory bounds map
    private Map<GameModel.Ingredient, Rectangle> invBoundsMap = new EnumMap<>(GameModel.Ingredient.class);

    private Rectangle journalButtonBounds = new Rectangle(700, 30, 60, 80);
    private Image leafImg;
    private Image mushroomImg;
    private Image crystalImg;
    private Image bugImg;
    private Image treeSapImg;
    private Image frogImg;
    private Image iceImg;
    private Image pebbleImg;
    private Image furImg;
    private Image cauldronImg;
    private Image journalImg;
    private Image forestImg;
    private Image caveImg;
    private Image mountainImg;
    private Image bubbleImg;

    public GameView(GameModel model) {
        this.model = model;
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.DARK_GRAY);
        setLayout(null);

        // Setup inventory bounds
        // Left Column: Forest (x=20)
        invBoundsMap.put(GameModel.Ingredient.TREE_SAP, new Rectangle(20, 80, 50, 50));
        invBoundsMap.put(GameModel.Ingredient.FROG, new Rectangle(20, 180, 50, 50));
        invBoundsMap.put(GameModel.Ingredient.LEAF, new Rectangle(20, 280, 50, 50));
        invBoundsMap.put(GameModel.Ingredient.RARE_CRYSTAL_FOREST, new Rectangle(20, 380, 50, 50));

        // Middle Column: Cave (x=90)
        invBoundsMap.put(GameModel.Ingredient.MUSHROOM, new Rectangle(90, 80, 50, 50));
        invBoundsMap.put(GameModel.Ingredient.BUG, new Rectangle(90, 180, 50, 50));
        invBoundsMap.put(GameModel.Ingredient.CRYSTAL, new Rectangle(90, 280, 50, 50));
        invBoundsMap.put(GameModel.Ingredient.RARE_CRYSTAL_CAVE, new Rectangle(90, 380, 50, 50));

        // Right Column: Mountain (x=160)
        invBoundsMap.put(GameModel.Ingredient.ICE, new Rectangle(160, 80, 50, 50));
        invBoundsMap.put(GameModel.Ingredient.PEBBLE, new Rectangle(160, 180, 50, 50));
        invBoundsMap.put(GameModel.Ingredient.FUR, new Rectangle(160, 280, 50, 50));
        invBoundsMap.put(GameModel.Ingredient.RARE_CRYSTAL_MOUNTAIN, new Rectangle(160, 380, 50, 50));

        // Setup brewing UI
        setupBrewingUI();

        cauldronImg = new ImageIcon("Cauldron.png").getImage();
        leafImg = new ImageIcon("Leaf.png").getImage();
        mushroomImg = new ImageIcon("Mushroom.png").getImage();
        crystalImg = new ImageIcon("Crystal.png").getImage();
        bugImg = new ImageIcon("Bug.png").getImage();
        treeSapImg = new ImageIcon("TreeSap.png").getImage();
        frogImg = new ImageIcon("Frog.png").getImage();
        iceImg = new ImageIcon("Ice.png").getImage();
        pebbleImg = new ImageIcon("Pebble.png").getImage();
        furImg = new ImageIcon("Fur.png").getImage();
        journalImg = new ImageIcon("Journal.png").getImage();
        forestImg = new ImageIcon("Forest.png").getImage();
        caveImg = new ImageIcon("Cave.png").getImage();
        mountainImg = new ImageIcon("Mountain.png").getImage();
        bubbleImg = new ImageIcon("Bubble.png").getImage();
    }

    public Rectangle getJournalButtonBounds() {
        return journalButtonBounds;
    }

    private static final int[][] POTION_PIXELS = {
            { 0, 0, 0, 0, 7, 6, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 7, 6, 0, 0, 0, 0 },
            { 0, 0, 0, 1, 7, 6, 1, 0, 0, 0 },
            { 0, 0, 0, 0, 1, 1, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 1, 1, 0, 0, 0, 0 },
            { 0, 0, 0, 1, 1, 1, 1, 0, 0, 0 },
            { 0, 0, 0, 1, 2, 1, 1, 0, 0, 0 },
            { 0, 0, 1, 2, 2, 3, 4, 1, 0, 0 },
            { 0, 0, 1, 2, 3, 4, 4, 1, 0, 0 },
            { 0, 1, 2, 2, 4, 4, 4, 4, 1, 0 },
            { 0, 1, 2, 3, 4, 4, 5, 5, 1, 0 },
            { 1, 3, 2, 4, 4, 4, 5, 5, 5, 1 },
            { 1, 3, 3, 4, 5, 5, 5, 5, 5, 1 },
            { 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }
    };

    private void drawPotionIcon(Graphics g, GameModel.Potion potion, int x, int y, int pixelSize) {
        Color brightColor = new Color(150, 150, 150);
        Color midColor = new Color(100, 100, 100);
        Color darkColor = new Color(50, 50, 50);

        switch (potion) {
            case STICKY_LIQUID: brightColor=new Color(255,200,0); midColor=new Color(200,150,0); darkColor=new Color(150,100,0); break;
            case TINY_VIAL: brightColor=new Color(150,200,255); midColor=new Color(100,150,200); darkColor=new Color(50,100,150); break;
            case MUDDLED_MIXTURE: brightColor=new Color(139,101,8); midColor=new Color(100,70,0); darkColor=new Color(60,40,0); break;
            case HEAVY_POTION: brightColor=new Color(120,120,120); midColor=new Color(80,80,80); darkColor=new Color(40,40,40); break;
            case NIGHT_VISION_POTION: brightColor=new Color(100,255,100); midColor=new Color(50,200,50); darkColor=new Color(0,150,0); break;
            case GLOW_POTION: brightColor=new Color(0,255,255); midColor=new Color(0,200,200); darkColor=new Color(0,150,150); break;
            case FUZZY_POTION: brightColor=new Color(255,150,50); midColor=new Color(200,100,0); darkColor=new Color(150,50,0); break;
            case BUG_JUICE: brightColor=new Color(200,50,200); midColor=new Color(150,0,150); darkColor=new Color(100,0,100); break;
            case FRIEND_POTION: brightColor=new Color(255,150,200); midColor=new Color(255,100,150); darkColor=new Color(200,50,100); break;
            case SPECIAL_FRIEND_POTION: brightColor=new Color(255,50,150); midColor=new Color(200,0,100); darkColor=new Color(150,0,50); break;
            case SPICY_WATER: brightColor=new Color(255,50,50); midColor=new Color(200,0,0); darkColor=new Color(150,0,0); break;
            case HOPPING_TONIC: brightColor=new Color(150,255,50); midColor=new Color(100,200,0); darkColor=new Color(50,150,0); break;
            case LEAF_JUICE: brightColor=new Color(50,200,50); midColor=new Color(0,150,0); darkColor=new Color(0,100,0); break;
            case SPIDERMANS_BREW: brightColor=new Color(255,0,0); midColor=new Color(150,0,0); darkColor=new Color(0,0,150); break;
            case ALLERGIC_REACTION_IN_A_BOTTLE: brightColor=new Color(200,255,0); midColor=new Color(150,200,0); darkColor=new Color(100,150,0); break;
            case GROWING_POTION: brightColor=new Color(0,255,100); midColor=new Color(0,200,50); darkColor=new Color(0,150,0); break;
            case RAMUNE: brightColor=new Color(200,255,255); midColor=new Color(150,200,255); darkColor=new Color(100,150,200); break;
            case ROCKS_ON_THE_ROCKS: brightColor=new Color(255,255,255); midColor=new Color(200,200,200); darkColor=new Color(150,150,150); break;
            case COLD_BLOODED: brightColor=new Color(50,100,255); midColor=new Color(0,50,200); darkColor=new Color(0,0,150); break;
            case DRUG_DOSE: brightColor=new Color(150,0,255); midColor=new Color(100,0,200); darkColor=new Color(50,0,150); break;
            case SPECKLED_SKIN_SERUM: brightColor=new Color(255,255,100); midColor=new Color(200,200,50); darkColor=new Color(150,150,0); break;
            case GLASS_SKIN_SERUM: brightColor=new Color(220,255,220); midColor=new Color(180,220,180); darkColor=new Color(150,200,150); break;
            case YELLOW_SNOW_CONE_CONCOCTION: brightColor=new Color(255,255,0); midColor=new Color(200,200,0); darkColor=new Color(150,150,0); break;
            case WATER: brightColor=new Color(100,150,255); midColor=new Color(50,100,200); darkColor=new Color(0,50,150); break;
            default: break; // Unknown remains gray
        }

        Color[] palette = {
                new Color(0, 0, 0, 0), // 0 transparent
                new Color(179, 179, 179), // 1 outline
                new Color(255, 255, 255), // 2 white highlight
                brightColor, // 3 bright color
                midColor, // 4 mid color
                darkColor, // 5 dark color
                new Color(142, 94, 74), // 6 cork base
                new Color(176, 139, 122) // 7 cork highlight
        };

        for (int r = 0; r < POTION_PIXELS.length; r++) {
            for (int c = 0; c < POTION_PIXELS[r].length; c++) {
                int colorIndex = POTION_PIXELS[r][c];
                if (colorIndex != 0) {
                    g.setColor(palette[colorIndex]);
                    g.fillRect(x + c * pixelSize, y + r * pixelSize, pixelSize, pixelSize);
                }
            }
        }
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
        if (resultLabel != null)
            resultLabel.setVisible(isBrewing);
    }

    // Getters for brewing components (used by controller)
    public JButton getBrewButton() {
        return brewButton;
    }

    public JLabel getResultLabel() {
        return resultLabel;
    }

    // Getters for inventory bounds (used by controller for click detection)
    public Rectangle getInvBounds(GameModel.Ingredient type) {
        return invBoundsMap.get(type);
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
        if (isAnimating)
            return; // Disable clicking during animation

        int currentlySelected = 0;
        if (selectedIngredient1 == ingredient)
            currentlySelected++;
        if (selectedIngredient2 == ingredient)
            currentlySelected++;

        if (currentlySelected == 0) {
            // None selected, add to first available slot
            if (selectedIngredient1 == null)
                selectedIngredient1 = ingredient;
            else if (selectedIngredient2 == null)
                selectedIngredient2 = ingredient;
            else
                selectedIngredient1 = ingredient; // overwrite slot 1 if both full
        } else if (currentlySelected == 1) {
            // One selected. Can we select a second?
            if (availableCount >= 2 && (selectedIngredient1 == null || selectedIngredient2 == null)) {
                if (selectedIngredient1 == null)
                    selectedIngredient1 = ingredient;
                else
                    selectedIngredient2 = ingredient;
            } else {
                // Deselect it
                if (selectedIngredient1 == ingredient)
                    selectedIngredient1 = null;
                if (selectedIngredient2 == ingredient)
                    selectedIngredient2 = null;
            }
        } else {
            // Two selected. Deselect both.
            if (selectedIngredient1 == ingredient)
                selectedIngredient1 = null;
            if (selectedIngredient2 == ingredient)
                selectedIngredient2 = null;
        }
    }

    public void startBrewAnimation(GameModel.Potion result, boolean isNewDiscovery, Runnable onComplete) {
        if (isAnimating)
            return;

        isAnimating = true;
        animPhase = 1;
        animDropY = 0;
        animPotionY = 0;
        resultPotion = result;
        animIsNewDiscovery = isNewDiscovery;

        animRotation = 0;
        animScale = 1.0;
        animCurrentX = 360;
        animCurrentY = 240;

        // Target is the center of the journal button
        animTargetX = journalButtonBounds.x + journalButtonBounds.width / 2 - 40; // offset for center
        animTargetY = journalButtonBounds.y + journalButtonBounds.height / 2 - 40;
        lingerFrames = 0;

        if (brewButton != null)
            brewButton.setEnabled(false);

        Timer timer = new Timer(33, null); // ~30 fps
        timer.addActionListener(e -> {
            if (animPhase == 1) {
                animDropY += 5; // Drop speed
                if (animDropY >= 70) { // Reached cauldron (230 -> 300)
                    animPhase = 2; // Brewing phase
                }
            } else if (animPhase == 2) {
                // 3 second wait (3000ms / 33ms = 90 frames roughly)
                animDropY++;
                if (animDropY >= 160) { // 90 frames of waiting (70 + 90)
                    animPhase = 3;
                    animPotionY = -50; // start slightly inside cauldron
                }
            } else if (animPhase == 3) {
                animPotionY += 3; // emerge speed
                if (animPotionY >= 60) { // fully emerged
                    if (animIsNewDiscovery) {
                        animPhase = 4;
                    } else {
                        timer.stop();
                        resetAnimation();
                        if (onComplete != null)
                            onComplete.run();
                    }
                }
            } else if (animPhase == 4) {
                lingerFrames++;
                if (lingerFrames >= 45) { // 1.5 seconds linger
                    animPhase = 5;
                }
            } else if (animPhase == 5) {
                // Fly to journal
                double dx = animTargetX - animCurrentX;
                double dy = animTargetY - animCurrentY;
                double dist = Math.sqrt(dx * dx + dy * dy);

                if (dist < 10) {
                    timer.stop();
                    resetAnimation();
                    if (onComplete != null)
                        onComplete.run();
                } else {
                    animCurrentX += dx * 0.1; // Ease in
                    animCurrentY += dy * 0.1;
                    animRotation += 0.2; // Spin
                    animScale = Math.max(0.1, animScale * 0.95); // Shrink
                }
            }
            repaint();
        });
        timer.start();
    }

    private void resetAnimation() {
        isAnimating = false;
        animPhase = 0;
        animDropY = 0;
        animPotionY = 0;
        resultPotion = null;
        animIsNewDiscovery = false;
    }

    public void updateBrewResult(String result) {
        resultLabel.setText(result);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        GameModel.Room currentRoom = model.getCurrentRoom();

        // Draw room-specific visual
        drawRoomVisual(g, currentRoom);

        if (currentRoom == GameModel.Room.BREWING_ROOM) {
            // Draw journal button
            if (journalImg != null && journalImg.getWidth(null) > 0) {
                int imgW = journalImg.getWidth(null);
                int imgH = journalImg.getHeight(null);
                double scale = Math.min((double) journalButtonBounds.width / imgW,
                        (double) journalButtonBounds.height / imgH);
                int drawW = (int) (imgW * scale);
                int drawH = (int) (imgH * scale);
                int drawX = journalButtonBounds.x + (journalButtonBounds.width - drawW) / 2;
                int drawY = journalButtonBounds.y + (journalButtonBounds.height - drawH) / 2;
                g.drawImage(journalImg, drawX, drawY, drawW, drawH, this);
            } else {
                g.setColor(new Color(139, 69, 19)); // Brown cover
                g.fillRect(journalButtonBounds.x, journalButtonBounds.y, journalButtonBounds.width,
                        journalButtonBounds.height);
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

    private int journalPage = 0;
    private Rectangle prevPageBounds = new Rectangle();
    private Rectangle nextPageBounds = new Rectangle();

    public Rectangle getPrevPageBounds() { return prevPageBounds; }
    public Rectangle getNextPageBounds() { return nextPageBounds; }
    public void nextPage() { journalPage++; }
    public void prevPage() { if (journalPage > 0) journalPage--; }
    public void setJournalPage(int p) { journalPage = p; }

    private void drawJournalOverlay(Graphics g) {
        // Semi-transparent background
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, getWidth(), getHeight());

        // Journal pages - wider for 2 columns
        int jx = 100, jy = 50, jw = 600, jh = 500;
        g.setColor(new Color(245, 222, 179)); // Wheat/parchment color
        g.fillRect(jx, jy, jw, jh);
        g.setColor(new Color(139, 69, 19)); // Dark brown border
        g.drawRect(jx, jy, jw, jh);
        g.drawRect(jx + 2, jy + 2, jw - 4, jh - 4);
        
        // Draw center spine line
        g.drawLine(jx + jw/2, jy, jx + jw/2, jy + jh);

        // Title
        g.setFont(new Font("Georgia", Font.BOLD, 24));
        g.setColor(Color.BLACK);
        g.drawString("Potions Journal", jx + 40, jy + 40);

        Set<GameModel.Potion> discoveredSet = model.getDiscoveredPotions();
        int totalPotions = model.getTotalDiscoverablePotions();

        g.setFont(new Font("Georgia", Font.ITALIC, 16));
        g.drawString("Discovered: " + discoveredSet.size() + " of " + totalPotions, jx + jw/2 + 40, jy + 40);

        int itemsPerPage = 10; // 5 per column
        
        // Collect valid potions
        java.util.List<GameModel.Potion> allPotions = new java.util.ArrayList<>();
        for (GameModel.Potion p : GameModel.Potion.values()) {
            if (p != GameModel.Potion.UNKNOWN_MIXTURE) {
                allPotions.add(p);
            }
        }
        
        int totalPages = (int)Math.ceil((double)allPotions.size() / itemsPerPage);
        if (journalPage >= totalPages) journalPage = totalPages - 1;
        if (journalPage < 0) journalPage = 0;
        
        int startIdx = journalPage * itemsPerPage;
        int endIdx = Math.min(startIdx + itemsPerPage, allPotions.size());
        
        int col1X = jx + 30;
        int col2X = jx + jw/2 + 30;
        
        for (int i = startIdx; i < endIdx; i++) {
            GameModel.Potion p = allPotions.get(i);
            boolean isCol2 = (i - startIdx) >= 5;
            int drawX = isCol2 ? col2X : col1X;
            int itemIdx = (i - startIdx) % 5;
            int drawY = jy + 80 + (itemIdx * 75); // Vertical space between items
            
            String potionName = p.name().toLowerCase().replace("_", " ");
            potionName = potionName.substring(0, 1).toUpperCase() + potionName.substring(1);

            // Draw icon larger (pixel size 3 -> 30x42 roughly)
            if (discoveredSet.contains(p)) {
                drawPotionIcon(g, p, drawX, drawY - 10, 3);
            } else {
                g.setColor(Color.GRAY);
                g.fillRect(drawX, drawY - 5, 30, 42);
            }

            if (discoveredSet.contains(p)) {
                g.setColor(new Color(0, 100, 0));
                g.setFont(new Font("Georgia", Font.BOLD, 14));
                g.drawString(potionName, drawX + 45, drawY + 10);

                // Draw recipe
                g.setColor(Color.DARK_GRAY);
                g.setFont(new Font("Georgia", Font.ITALIC, 12));
                GameModel.Ingredient[] recipe = model.getRecipe(p);
                if (recipe != null && recipe.length >= 2) {
                    String ing1 = recipe[0].name().toLowerCase().replace("rare_", "").replace("_forest", "").replace("_cave", "").replace("_mountain", "").replace("_", " ");
                    ing1 = ing1.substring(0, 1).toUpperCase() + ing1.substring(1);
                    String ing2 = recipe[1].name().toLowerCase().replace("rare_", "").replace("_forest", "").replace("_cave", "").replace("_mountain", "").replace("_", " ");
                    ing2 = ing2.substring(0, 1).toUpperCase() + ing2.substring(1);
                    g.drawString("Recipe: " + ing1 + " + " + ing2, drawX + 45, drawY + 28);
                }
            } else {
                g.setColor(Color.GRAY);
                g.setFont(new Font("Georgia", Font.BOLD, 14));
                g.drawString("Unknown Recipe", drawX + 45, drawY + 10);

                // Draw unknown recipe
                g.setColor(Color.LIGHT_GRAY);
                g.setFont(new Font("Georgia", Font.ITALIC, 12));
                g.drawString("Recipe: ? + ?", drawX + 45, drawY + 28);
            }
        }
        
        // Draw pagination arrows
        prevPageBounds.setBounds(jx + 20, jy + jh - 40, 70, 30);
        nextPageBounds.setBounds(jx + jw - 90, jy + jh - 40, 70, 30);
        
        if (journalPage > 0) {
            g.setColor(new Color(139, 69, 19));
            g.fillRect(prevPageBounds.x, prevPageBounds.y, prevPageBounds.width, prevPageBounds.height);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString("< Prev", prevPageBounds.x + 10, prevPageBounds.y + 20);
        }
        
        if (journalPage < totalPages - 1) {
            g.setColor(new Color(139, 69, 19));
            g.fillRect(nextPageBounds.x, nextPageBounds.y, nextPageBounds.width, nextPageBounds.height);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString("Next >", nextPageBounds.x + 10, nextPageBounds.y + 20);
        }
        
        // Draw page indicator
        g.setColor(Color.BLACK);
        g.setFont(new Font("Georgia", Font.PLAIN, 14));
        g.drawString("Page " + (journalPage + 1) + " of " + totalPages, jx + jw/2 - 35, jy + jh - 20);
    }

    private String getRoomDisplayName(GameModel.Room room) {
        switch (room) {
            case FOREST:
                return "Forest";
            case CAVE:
                return "Cave";
            case MOUNTAIN:
                return "Mountain";
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
            case CAVE:
                drawCave(g);
                break;
            case MOUNTAIN:
                drawMountain(g);
                break;
            case BREWING_ROOM:
                drawBrewingRoom(g);
                break;
        }
    }

    private void drawForest(Graphics g) {
        if (forestImg != null && forestImg.getWidth(null) > 0) {
            g.drawImage(forestImg, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(new Color(34, 139, 34)); // Fallback forest green
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // Draw active collectible ingredients
        for (GameModel.SpawnedIngredient ingredient : model.getActiveIngredients(GameModel.Room.FOREST)) {
            drawIngredient(g, ingredient.type, ingredient.bounds, false);
        }
    }

    private void drawCave(Graphics g) {
        if (caveImg != null && caveImg.getWidth(null) > 0) {
            g.drawImage(caveImg, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(new Color(70, 70, 80)); // Fallback cave dark gray
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // Draw active collectible ingredients
        for (GameModel.SpawnedIngredient ingredient : model.getActiveIngredients(GameModel.Room.CAVE)) {
            drawIngredient(g, ingredient.type, ingredient.bounds, false);
        }
    }

    private void drawMountain(Graphics g) {
        if (mountainImg != null && mountainImg.getWidth(null) > 0) {
            g.drawImage(mountainImg, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(new Color(220, 220, 230)); // Fallback mountain light gray/blue
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // Draw active collectible ingredients
        for (GameModel.SpawnedIngredient ingredient : model.getActiveIngredients(GameModel.Room.MOUNTAIN)) {
            drawIngredient(g, ingredient.type, ingredient.bounds, false);
        }
    }

    private void drawIngredient(Graphics g, GameModel.Ingredient ingredient, Rectangle bounds, boolean showLabel) {

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
                if (crystalImg != null && crystalImg.getWidth(null) > 0) {
                    g.drawImage(crystalImg, bounds.x, bounds.y, bounds.width, bounds.height, this);
                } else {
                    g.setColor(Color.CYAN);
                    g.fillRect(bounds.x + 10, bounds.y + 10, bounds.width - 20, bounds.height - 20);
                }
                break;

            case BUG:
                if (bugImg != null && bugImg.getWidth(null) > 0) {
                    g.drawImage(bugImg, bounds.x, bounds.y, bounds.width, bounds.height, this);
                } else {
                    g.setColor(new Color(139, 0, 139)); // Dark magenta
                    g.fillOval(bounds.x + 10, bounds.y + 15, bounds.width - 20, bounds.height - 30);
                    g.setColor(Color.BLACK);
                    g.drawLine(bounds.x + 10, bounds.y + 25, bounds.x + bounds.width - 10, bounds.y + 25);
                }
                break;

            case TREE_SAP:
                if (treeSapImg != null && treeSapImg.getWidth(null) > 0) {
                    g.drawImage(treeSapImg, bounds.x, bounds.y, bounds.width, bounds.height, this);
                } else {
                    g.setColor(new Color(255, 165, 0)); // Orange
                    int[] xPoints = { bounds.x + 25, bounds.x + 10, bounds.x + 40 };
                    int[] yPoints = { bounds.y + 10, bounds.y + 40, bounds.y + 40 };
                    g.fillPolygon(xPoints, yPoints, 3);
                }
                break;

            case FROG:
                if (frogImg != null && frogImg.getWidth(null) > 0) {
                    g.drawImage(frogImg, bounds.x, bounds.y, bounds.width, bounds.height, this);
                } else {
                    g.setColor(new Color(34, 139, 34)); // Forest green
                    g.fillOval(bounds.x + 10, bounds.y + 20, bounds.width - 20, bounds.height - 25);
                    g.setColor(Color.BLACK);
                    g.fillOval(bounds.x + 15, bounds.y + 25, 5, 5); // Eye
                    g.fillOval(bounds.x + 30, bounds.y + 25, 5, 5); // Eye
                }
                break;

            case ICE:
                if (iceImg != null && iceImg.getWidth(null) > 0) {
                    g.drawImage(iceImg, bounds.x, bounds.y, bounds.width, bounds.height, this);
                } else {
                    g.setColor(new Color(173, 216, 230)); // Light blue
                    g.fillRect(bounds.x + 10, bounds.y + 10, bounds.width - 20, bounds.height - 20);
                }
                break;

            case PEBBLE:
                if (pebbleImg != null && pebbleImg.getWidth(null) > 0) {
                    g.drawImage(pebbleImg, bounds.x, bounds.y, bounds.width, bounds.height, this);
                } else {
                    g.setColor(Color.GRAY);
                    g.fillOval(bounds.x + 15, bounds.y + 15, bounds.width - 30, bounds.height - 30);
                }
                break;

            case FUR:
                if (furImg != null && furImg.getWidth(null) > 0) {
                    g.drawImage(furImg, bounds.x, bounds.y, bounds.width, bounds.height, this);
                } else {
                    g.setColor(new Color(139, 69, 19)); // Saddle brown
                    g.fillRoundRect(bounds.x + 10, bounds.y + 10, bounds.width - 20, bounds.height - 20, 15, 15);
                }
                break;

            case RARE_CRYSTAL_FOREST:
                drawTintedCrystal(g, bounds, new Color(255, 117, 177, 150)); // Pink
                break;

            case RARE_CRYSTAL_CAVE:
                drawTintedCrystal(g, bounds, new Color(255, 142, 13, 150)); // Orange
                break;

            case RARE_CRYSTAL_MOUNTAIN:
                drawTintedCrystal(g, bounds, new Color(58, 134, 189, 150)); // Blue
                break;
        }

        if (showLabel) {
            // Draw label
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 12));

            String name = ingredient.name().toLowerCase();
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            g.drawString(name, bounds.x + 5, bounds.y - 5);
        }
    }

    private void drawTintedCrystal(Graphics g, Rectangle bounds, Color tint) {
        if (crystalImg != null && crystalImg.getWidth(null) > 0) {
            BufferedImage img = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = img.createGraphics();
            g2.drawImage(crystalImg, 0, 0, bounds.width, bounds.height, null);
            g2.setComposite(AlphaComposite.SrcAtop);
            g2.setColor(tint);
            g2.fillRect(0, 0, bounds.width, bounds.height);
            g2.dispose();
            g.drawImage(img, bounds.x, bounds.y, null);
        } else {
            // Fallback geometry
            g.setColor(new Color(tint.getRed(), tint.getGreen(), tint.getBlue())); // solid color
            g.fillRect(bounds.x + 10, bounds.y + 10, bounds.width - 20, bounds.height - 20);
        }
    }

    // Removed ingredient cupboard

    private void drawBrewingRoom(Graphics g) {
        // Draw cauldron image
        if (cauldronImg != null) {
            g.drawImage(cauldronImg, 250, 300, 432, 360, this);
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
            drawIngredient(g, selectedIngredient1, new Rectangle(380, 230 + animDropY, 50, 50), true);
        }
        if (!isAnimating) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("+", 455, 260); // 380+50=430, 490... center is ~460
        }
        if (selectedIngredient2 != null && animPhase < 2) {
            drawIngredient(g, selectedIngredient2, new Rectangle(490, 230 + animDropY, 50, 50), true);
        }

        // Draw bubbles during brewing phase
        if (animPhase == 2 && bubbleImg != null) {
            // Use animDropY as a frame counter (goes from ~110 to 200)
            int bubble1Y = 320 - ((animDropY * 2) % 100);
            int bubble2Y = 340 - ((animDropY * 3) % 120);
            int bubble3Y = 330 - ((animDropY * 4) % 110);
            g.drawImage(bubbleImg, 446, bubble1Y, 30, 30, this);
            g.drawImage(bubbleImg, 486, bubble2Y, 20, 20, this);
            g.drawImage(bubbleImg, 416, bubble3Y, 25, 25, this);
        }

        // Draw emerging potion
        if (animPhase >= 3 && resultPotion != null) {
            Graphics2D g2d = (Graphics2D) g.create();

            if (animPhase == 3 || animPhase == 4) {
                drawPotionIcon(g2d, resultPotion, 426, 300 - animPotionY, 5);

                if (animPhase == 4) {
                    // Draw "New Discovery!" above
                    g2d.setColor(Color.YELLOW);
                    g2d.setFont(new Font("Georgia", Font.BOLD, 20));
                    g2d.drawString("New Discovery!", 310, 300 - animPotionY - 20);
                } else {
                    // Text to show what it is
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Arial", Font.BOLD, 12));
                    String name = resultPotion.name().replace("_POTION", "");
                    if (resultPotion == GameModel.Potion.UNKNOWN_MIXTURE)
                        name = "UNKNOWN";
                    g2d.drawString(name, 365, 300 - animPotionY + 95);
                }
            } else if (animPhase == 5) {
                // Apply transforms
                int cx = (int) animCurrentX + 40;
                int cy = (int) animCurrentY + 40;
                g2d.translate(cx, cy);
                g2d.rotate(animRotation);
                g2d.scale(animScale, animScale);
                g2d.translate(-cx, -cy);
                drawPotionIcon(g2d, resultPotion, (int) animCurrentX, (int) animCurrentY, 5);
            }

            g2d.dispose();
        }

        // Draw Inventory Stacks
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Inventory", 40, 40);

        for (GameModel.Ingredient type : GameModel.Ingredient.values()) {
            Rectangle bounds = invBoundsMap.get(type);
            if (bounds != null) {
                drawInventoryStack(g, type, bounds);
            }
        }
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
            drawIngredient(g, type, bounds, false);

            // Draw count
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString("x" + count, bounds.x + 15, bounds.y + bounds.height + 15);
        }
    }
}