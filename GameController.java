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
    
    public GameController() {
        model = new GameModel();
        view = new GameView(model);
        
        setupFrame();
        setupMouseListener();
    }
    
    private void setupFrame() {
        frame = new JFrame("Potion Finder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create button panel for room navigation
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.DARK_GRAY);
        
        JButton forestBtn = new JButton("Forest");
        JButton cupboardBtn = new JButton("Ingredient Cupboard");
        JButton brewingBtn = new JButton("Brewing Room");
        
        forestBtn.addActionListener(e -> {
            model.setCurrentRoom(GameModel.Room.FOREST);
            view.repaint();
        });
        
        cupboardBtn.addActionListener(e -> {
            model.setCurrentRoom(GameModel.Room.INGREDIENT_CUPBOARD);
            view.repaint();
        });
        
        brewingBtn.addActionListener(e -> {
            model.setCurrentRoom(GameModel.Room.BREWING_ROOM);
            view.repaint();
        });
        
        buttonPanel.add(forestBtn);
        buttonPanel.add(cupboardBtn);
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
            public void mouseClicked(MouseEvent e) {
                if (model.getCurrentRoom() == GameModel.Room.FOREST) {
                    Point click = e.getPoint();
                    
                    // Check if clicked on mushroom
                    if (view.getMushroomBounds().contains(click)) {
                        model.collectIngredient(GameModel.Ingredient.MUSHROOM);
                        view.repaint();
                    }
                    // Check if clicked on leaf
                    else if (view.getLeafBounds().contains(click)) {
                        model.collectIngredient(GameModel.Ingredient.LEAF);
                        view.repaint();
                    }
                    // Check if clicked on crystal
                    else if (view.getCrystalBounds().contains(click)) {
                        model.collectIngredient(GameModel.Ingredient.CRYSTAL);
                        view.repaint();
                    }
                }
            }
        });
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GameController();
        });
    }
}