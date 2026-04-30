import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;

/**
 * GameModel - Placeholder for game data
 * 
 * This class will hold all game state and data including:
 * - Player information (position, inventory, stats)
 * - Game world/level data
 * - Potion locations and properties
 * - Score and progress tracking
 * - Game settings and configuration
 */
public class GameModel {
    
    // Room types
    public enum Room {
        FOREST,
        BREWING_ROOM
    }
    
    // Ingredient types
    public enum Ingredient {
        MUSHROOM,
        LEAF,
        CRYSTAL
    }
    
    // Potion types
    public enum Potion {
        HEALING_POTION,
        POISON_POTION,
        STRENGTH_POTION,
        UNKNOWN_MIXTURE
    }
    
    private Room currentRoom = Room.FOREST;
    
    // Ingredient inventory
    private int mushroomCount = 0;
    private int leafCount = 0;
    private int crystalCount = 0;
    
    public static class SpawnedIngredient {
        public Ingredient type;
        public Rectangle bounds;
        
        public SpawnedIngredient(Ingredient type, Rectangle bounds) {
            this.type = type;
            this.bounds = bounds;
        }
    }
    
    // Forest ingredients
    private List<SpawnedIngredient> activeIngredients = new ArrayList<>();
    
    // Brewing result
    private Potion lastBrewedPotion = null;
    
    // Discovered potions
    private Set<Potion> discoveredPotions = new HashSet<>();
    
    // UI State
    private boolean journalOpen = false;

    public GameModel() {
        spawnIngredients();
    }
    
    // TODO: Player data
    // - Player position (x, y coordinates)
    // - Player inventory (potions collected)
    // - Player health/stats
    
    // TODO: Level/World data
    // - Map/grid representation
    // - Obstacles and walkable areas
    // - Potion spawn locations
    
    // TODO: Game state
    // - Current score
    // - Level number
    // - Game status (playing, paused, game over)
    
    // TODO: Game configuration
    // - Window dimensions
    // - Tile size
    // - Game speed/difficulty
    
    // Room management
    public Room getCurrentRoom() {
        return currentRoom;
    }
    
    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }
    
    // Ingredient management
    public int getIngredientCount(Ingredient ingredient) {
        switch (ingredient) {
            case MUSHROOM: return mushroomCount;
            case LEAF: return leafCount;
            case CRYSTAL: return crystalCount;
            default: return 0;
        }
    }
    
    public void addIngredient(Ingredient ingredient) {
        switch (ingredient) {
            case MUSHROOM: mushroomCount++; break;
            case LEAF: leafCount++; break;
            case CRYSTAL: crystalCount++; break;
        }
    }
    
    // Forest ingredient collection
    public List<SpawnedIngredient> getActiveIngredients() {
        return activeIngredients;
    }
    
    public void spawnIngredients() {
        activeIngredients.clear();
        Random random = new Random();
        int numIngredients = random.nextInt(4) + 2; // 2 to 5 ingredients
        
        Ingredient[] allTypes = Ingredient.values();
        
        for (int i = 0; i < numIngredients; i++) {
            Ingredient type = allTypes[random.nextInt(allTypes.length)];
            
            // Random bounds in the forest grass area (approx x: 50-700, y: 400-500)
            // Subtracting width/height (50) to keep them fully visible
            int x = 50 + random.nextInt(600);
            int y = 400 + random.nextInt(100);
            
            // Note: Not doing complex collision detection here for simplicity,
            // but in a real game we would ensure they don't overlap.
            Rectangle bounds = new Rectangle(x, y, 50, 50);
            
            activeIngredients.add(new SpawnedIngredient(type, bounds));
        }
    }
    
    public void collectIngredient(SpawnedIngredient ingredient) {
        if (activeIngredients.remove(ingredient)) {
            addIngredient(ingredient.type);
        }
    }
    
    // Brewing system
    public Potion brew(Ingredient ingredient1, Ingredient ingredient2) {
        // Check if player has enough ingredients
        if (getIngredientCount(ingredient1) < 1 || getIngredientCount(ingredient2) < 1) {
            lastBrewedPotion = Potion.UNKNOWN_MIXTURE;
            return lastBrewedPotion;
        }
        
        // Use ingredients
        useIngredient(ingredient1);
        useIngredient(ingredient2);
        
        // Determine recipe (order doesn't matter)
        if ((ingredient1 == Ingredient.LEAF && ingredient2 == Ingredient.CRYSTAL) ||
            (ingredient1 == Ingredient.CRYSTAL && ingredient2 == Ingredient.LEAF)) {
            lastBrewedPotion = Potion.HEALING_POTION;
        } else if ((ingredient1 == Ingredient.CRYSTAL && ingredient2 == Ingredient.MUSHROOM) ||
                   (ingredient1 == Ingredient.MUSHROOM && ingredient2 == Ingredient.CRYSTAL)) {
            lastBrewedPotion = Potion.POISON_POTION;
        } else if ((ingredient1 == Ingredient.MUSHROOM && ingredient2 == Ingredient.LEAF) ||
                   (ingredient1 == Ingredient.LEAF && ingredient2 == Ingredient.MUSHROOM)) {
            lastBrewedPotion = Potion.STRENGTH_POTION;
        } else {
            lastBrewedPotion = Potion.UNKNOWN_MIXTURE;
        }
        
        if (lastBrewedPotion != Potion.UNKNOWN_MIXTURE) {
            discoveredPotions.add(lastBrewedPotion);
        }
        
        return lastBrewedPotion;
    }
    
    public Set<Potion> getDiscoveredPotions() {
        return discoveredPotions;
    }
    
    public int getTotalDiscoverablePotions() {
        int count = 0;
        for (Potion p : Potion.values()) {
            if (p != Potion.UNKNOWN_MIXTURE) count++;
        }
        return count;
    }
    
    public boolean isJournalOpen() {
        return journalOpen;
    }
    
    public void setJournalOpen(boolean journalOpen) {
        this.journalOpen = journalOpen;
    }
    
    private void useIngredient(Ingredient ingredient) {
        switch (ingredient) {
            case MUSHROOM: 
                if (mushroomCount > 0) mushroomCount--; 
                break;
            case LEAF: 
                if (leafCount > 0) leafCount--; 
                break;
            case CRYSTAL: 
                if (crystalCount > 0) crystalCount--; 
                break;
        }
    }
    
    public Potion getLastBrewedPotion() {
        return lastBrewedPotion;
    }
}