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
        UNKNOWN_MIXTURE
    }
    
    // Spawned ingredient class
    private static class SpawnedIngredient {
        Ingredient type;
        int x, y;
        boolean collected;
        
        SpawnedIngredient(Ingredient type, int x, int y) {
            this.type = type;
            this.x = x;
            this.y = y;
            this.collected = false;
        }
    }
    
    private Room currentRoom = Room.FOREST;
    
    // Ingredient inventory
    private int mushroomCount = 0;
    private int leafCount = 0;
    private int crystalCount = 0;
    
    // Spawned ingredients in forest
    private java.util.List<SpawnedIngredient> spawnedIngredients = new java.util.ArrayList<>();
    private long lastSpawnTime = 0;
    private static final long RESET_DELAY = 60000; // 1 minute in milliseconds
    
    // Brewing result
    private Potion lastBrewedPotion = null;
    
    public GameModel() {
        spawnRandomIngredients();
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
    
    // Random ingredient spawning
    private void spawnRandomIngredients() {
        spawnedIngredients.clear();
        int count = 2 + new java.util.Random().nextInt(4); // 2-5 ingredients
        java.util.Random rand = new java.util.Random();
        
        for (int i = 0; i < count; i++) {
            Ingredient[] ingredients = Ingredient.values();
            Ingredient type = ingredients[rand.nextInt(ingredients.length)];
            int x = 100 + rand.nextInt(600);
            int y = 350 + rand.nextInt(120);
            spawnedIngredients.add(new SpawnedIngredient(type, x, y));
        }
        lastSpawnTime = System.currentTimeMillis();
    }
    
    public java.util.List<SpawnedIngredient> getSpawnedIngredients() {
        return spawnedIngredients;
    }
    
    public void collectSpawnedIngredient(int index) {
        if (index >= 0 && index < spawnedIngredients.size()) {
            SpawnedIngredient ingredient = spawnedIngredients.get(index);
            if (!ingredient.collected) {
                ingredient.collected = true;
                addIngredient(ingredient.type);
                
                // Check if all collected
                if (areAllIngredientsCollected()) {
                    scheduleReset();
                }
            }
        }
    }
    
    private boolean areAllIngredientsCollected() {
        for (SpawnedIngredient ing : spawnedIngredients) {
            if (!ing.collected) return false;
        }
        return true;
    }
    
    private void scheduleReset() {
        new Thread(() -> {
            try {
                Thread.sleep(RESET_DELAY);
                spawnRandomIngredients();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    public boolean shouldCheckReset() {
        if (areAllIngredientsCollected() && 
            System.currentTimeMillis() - lastSpawnTime > RESET_DELAY) {
            spawnRandomIngredients();
            return true;
        }
        return false;
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