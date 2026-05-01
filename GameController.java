import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * GameController - Main controller for the MVC architecture
 * Handles user input and coordinates between Model and View
 */
public class GameController {

    private GameModel model;
    private GameView view;
    private JFrame frame;
    private Timer respawnTimer;

    public GameController() {
        model = new GameModel();
        view = new GameView(model);

        setupFrame();
        setupMouseListener();
        setupBrewListener();
    }

    private void setupFrame() {
        frame = new JFrame("Potion Finder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create button panel for room navigation
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.DARK_GRAY);

        JButton forestBtn = new JButton("Forest");
        JButton brewingBtn = new JButton("Brewing Room");

        forestBtn.addActionListener(e -> {
            model.setCurrentRoom(GameModel.Room.FOREST);
            view.updateUIVisibility(GameModel.Room.FOREST);
            view.repaint();
        });

        brewingBtn.addActionListener(e -> {
            model.setCurrentRoom(GameModel.Room.BREWING_ROOM);
            view.updateUIVisibility(GameModel.Room.BREWING_ROOM);
            view.repaint();
        });

        buttonPanel.add(forestBtn);
        buttonPanel.add(brewingBtn);

        // Add components to frame
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.add(view, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void setupMouseListener() {
        view.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point click = e.getPoint();

                // Toggle journal if button clicked
                if (view.getJournalButtonBounds().contains(click)) {
                    model.setJournalOpen(!model.isJournalOpen());
                    view.updateUIVisibility(model.getCurrentRoom());
                    view.repaint();
                    return;
                }

                // If journal is open, clicking anywhere else closes it
                if (model.isJournalOpen()) {
                    model.setJournalOpen(false);
                    view.updateUIVisibility(model.getCurrentRoom());
                    view.repaint();
                    return;
                }

                GameModel.Room currentRoom = model.getCurrentRoom();

                if (currentRoom == GameModel.Room.FOREST) {
                    for (GameModel.SpawnedIngredient ingredient : model.getActiveIngredients()) {
                        if (ingredient.bounds.contains(click)) {
                            model.collectIngredient(ingredient);
                            view.repaint();
                            checkRespawnTimer();
                            break;
                        }
                    }
                } else if (currentRoom == GameModel.Room.BREWING_ROOM) {
                    if (isClickInStack(click, view.getInvMushroomBounds())
                            && model.getIngredientCount(GameModel.Ingredient.MUSHROOM) > 0) {
                        view.toggleIngredientSelection(GameModel.Ingredient.MUSHROOM,
                                model.getIngredientCount(GameModel.Ingredient.MUSHROOM));
                        view.repaint();
                    } else if (isClickInStack(click, view.getInvLeafBounds())
                            && model.getIngredientCount(GameModel.Ingredient.LEAF) > 0) {
                        view.toggleIngredientSelection(GameModel.Ingredient.LEAF,
                                model.getIngredientCount(GameModel.Ingredient.LEAF));
                        view.repaint();
                    } else if (isClickInStack(click, view.getInvCrystalBounds())
                            && model.getIngredientCount(GameModel.Ingredient.CRYSTAL) > 0) {
                        view.toggleIngredientSelection(GameModel.Ingredient.CRYSTAL,
                                model.getIngredientCount(GameModel.Ingredient.CRYSTAL));
                        view.repaint();
                    }
                }
            }
        });
    }

    private boolean isClickInStack(Point click, Rectangle bounds) {
        // Expand bounds to match the visual selection highlight and text area
        Rectangle expanded = new Rectangle(bounds.x - 5, bounds.y - 5, bounds.width + 10, bounds.height + 30);
        return expanded.contains(click);
    }

    private void checkRespawnTimer() {
        if (model.getActiveIngredients().isEmpty()) {
            if (respawnTimer == null || !respawnTimer.isRunning()) {
                respawnTimer = new Timer(60000, e -> {
                    model.spawnIngredients();
                    view.repaint();
                });
                respawnTimer.setRepeats(false);
                respawnTimer.start();
            }
        }
    }

    private void setupBrewListener() {
        view.getBrewButton().addActionListener(e -> {
            GameModel.Ingredient ing1 = view.getSelectedIngredient1();
            GameModel.Ingredient ing2 = view.getSelectedIngredient2();

            if (ing1 == null || ing2 == null) {
                view.updateBrewResult("Select 2 ingredients!");
                view.repaint();
                return;
            }

            GameModel.Potion result = model.brew(ing1, ing2);

            String resultText;
            switch (result) {
                case HEALING_POTION:
                    resultText = "Healing Potion!";
                    break;
                case POISON_POTION:
                    resultText = "Poison Potion!";
                    break;
                case STRENGTH_POTION:
                    resultText = "Strength Potion!";
                    break;
                default:
                    resultText = "Unknown Mixture";
            }
            
            if (model.hasWon()) {
                resultText = "You discovered all recipes!";
                view.getBrewButton().setEnabled(false);
            }

            view.clearSelections();
            view.updateBrewResult(resultText);
            view.repaint();
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GameController();
        });
    }
}