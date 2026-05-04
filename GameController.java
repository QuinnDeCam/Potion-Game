import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

/**
 * GameController - Main controller for the MVC architecture
 * Handles user input and coordinates between Model and View
 */
public class GameController {

    private GameModel model;
    private GameView view;
    private JFrame frame;
    private Timer forestTimer;
    private Timer caveTimer;
    private Timer mountainTimer;

    public GameController() {
        model = new GameModel();
        view = new GameView(model);

        setupFrame();
        setupMouseListener();
        setupBrewListener();
        setupSpawnTimer();
    }

    private void setupFrame() {
        frame = new JFrame("Potion Finder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create button panel for room navigation
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.DARK_GRAY);

        JButton forestBtn = new JButton("Forest");
        JButton caveBtn = new JButton("Cave");
        JButton mountainBtn = new JButton("Mountain");
        JButton brewingBtn = new JButton("Brewing Room");

        forestBtn.addActionListener(e -> {
            model.setCurrentRoom(GameModel.Room.FOREST);
            view.updateUIVisibility(GameModel.Room.FOREST);
            view.repaint();
        });

        caveBtn.addActionListener(e -> {
            model.setCurrentRoom(GameModel.Room.CAVE);
            view.updateUIVisibility(GameModel.Room.CAVE);
            view.repaint();
        });

        mountainBtn.addActionListener(e -> {
            model.setCurrentRoom(GameModel.Room.MOUNTAIN);
            view.updateUIVisibility(GameModel.Room.MOUNTAIN);
            view.repaint();
        });

        brewingBtn.addActionListener(e -> {
            model.setCurrentRoom(GameModel.Room.BREWING_ROOM);
            view.updateUIVisibility(GameModel.Room.BREWING_ROOM);
            view.repaint();
        });

        buttonPanel.add(forestBtn);
        buttonPanel.add(caveBtn);
        buttonPanel.add(mountainBtn);
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

                // If journal is open, handle pagination or close it
                if (model.isJournalOpen()) {
                    if (view.getPrevPageBounds().contains(click)) {
                        view.prevPage();
                    } else if (view.getNextPageBounds().contains(click)) {
                        view.nextPage();
                    } else {
                        model.setJournalOpen(false);
                        view.updateUIVisibility(model.getCurrentRoom());
                    }
                    view.repaint();
                    return;
                }

                GameModel.Room currentRoom = model.getCurrentRoom();

                if (currentRoom == GameModel.Room.FOREST || currentRoom == GameModel.Room.CAVE
                        || currentRoom == GameModel.Room.MOUNTAIN) {
                    for (GameModel.SpawnedIngredient ingredient : model.getActiveIngredients(currentRoom)) {
                        if (ingredient.bounds.contains(click)) {
                            model.collectIngredient(currentRoom, ingredient);
                            view.repaint();
                            break;
                        }
                    }
                } else if (currentRoom == GameModel.Room.BREWING_ROOM) {
                    for (GameModel.Ingredient type : GameModel.Ingredient.values()) {
                        Rectangle bounds = view.getInvBounds(type);
                        if (bounds != null && isClickInStack(click, bounds) && model.getIngredientCount(type) > 0) {
                            view.toggleIngredientSelection(type, model.getIngredientCount(type));
                            view.repaint();
                            break;
                        }
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

    private void setupSpawnTimer() {
        // Every 4 seconds, staggered by 1.3 seconds per room
        int interval = 4000;

        forestTimer = new Timer(interval, e -> {
            model.spawnForRoom(GameModel.Room.FOREST);
            view.repaint();
        });
        forestTimer.setInitialDelay(0);
        forestTimer.setRepeats(true);
        forestTimer.start();

        caveTimer = new Timer(interval, e -> {
            model.spawnForRoom(GameModel.Room.CAVE);
            view.repaint();
        });
        caveTimer.setInitialDelay(1333);
        caveTimer.setRepeats(true);
        caveTimer.start();

        mountainTimer = new Timer(interval, e -> {
            model.spawnForRoom(GameModel.Room.MOUNTAIN);
            view.repaint();
        });
        mountainTimer.setInitialDelay(2666);
        mountainTimer.setRepeats(true);
        mountainTimer.start();
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
            if (result == GameModel.Potion.UNKNOWN_MIXTURE) {
                resultText = "Unknown Mixture";
            } else {
                resultText = result.name().toLowerCase().replace("_", " ");
                resultText = resultText.substring(0, 1).toUpperCase() + resultText.substring(1) + "!";
            }

            String finalResultText = resultText;
            view.updateBrewResult(""); // clear text while brewing
            view.startBrewAnimation(result, model.wasLastBrewNewDiscovery(), () -> {
                view.clearSelections();
                view.updateBrewResult(finalResultText);

                if (model.hasWon()) {
                    view.updateBrewResult("You discovered all recipes!");
                    view.getBrewButton().setEnabled(false);
                } else {
                    view.getBrewButton().setEnabled(true);
                }
                view.repaint();
            });
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GameController();
        });
    }
}