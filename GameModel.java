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
        INGREDIENT_CUPBOARD,
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
        UNKNOWN_MIXTURE
    }
    
    private Room currentRoom = Room.FOREST;
    
    // Ingredient inventory
    private int mushroomCount = 0;
    private int leafCount = 0;
    private int crystalCount = 0;
    
    // Forest ingredients (position and collected state)
    private boolean mushroomCollected = false;
    private boolean leafCollected = false;
    private boolean crystalCollected = false;
    
    // Brewing result
    private Potion lastBrewedPotion = null;
    
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
    public boolean isIngredientCollected(Ingredient ingredient) {
        switch (ingredient) {
            case MUSHROOM: return mushroomCollected;
            case LEAF: return leafCollected;
            case CRYSTAL: return crystalCollected;
            default: return false;
        }
    }
    
    public void collectIngredient(Ingredient ingredient) {
        switch (ingredient) {
            case MUSHROOM: 
                if (!mushroomCollected) {
                    mushroomCollected = true;
                    addIngredient(Ingredient.MUSHROOM);
                }
                break;
            case LEAF: 
                if (!leafCollected) {
                    leafCollected = true;
                    addIngredient(Ingredient.LEAF);
                }
                break;
            case CRYSTAL: 
                if (!crystalCollected) {
                    crystalCollected = true;
                    addIngredient(Ingredient.CRYSTAL);
                }
                break;
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
        } else {
            lastBrewedPotion = Potion.UNKNOWN_MIXTURE;
        }
        
        return lastBrewedPotion;
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