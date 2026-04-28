import javax.swing.*;

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
        view = new GameView();
        
        setupFrame();
    }
    
    private void setupFrame() {
        frame = new JFrame("Potion Finder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(view);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GameController();
        });
    }
}