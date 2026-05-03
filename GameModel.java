import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;

import java.util.Map;
import java.util.EnumMap;

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
        CAVE,
        MOUNTAIN,
        BREWING_ROOM
    }
    
    // Ingredient types
    public enum Ingredient {
        MUSHROOM,
        LEAF,
        CRYSTAL,
        BUG,
        TREE_SAP,
        FROG,
        ICE,
        PEBBLE,
        FUR,
        RARE_CRYSTAL_FOREST,
        RARE_CRYSTAL_CAVE,
        RARE_CRYSTAL_MOUNTAIN
    }
    
    // Potion types
    public enum Potion {
        HEALING_POTION,
        POISON_POTION,
        STRENGTH_POTION,
        UNKNOWN_MIXTURE
    }
    
    private Room currentRoom = Room.FOREST;
    
    // Ingredient inventory and spawning chances
    private Map<Ingredient, Integer> inventory = new EnumMap<>(Ingredient.class);
    private Map<Ingredient, Integer> spawnChances = new EnumMap<>(Ingredient.class);
    
    public static class SpawnedIngredient {
        public Ingredient type;
        public Rectangle bounds;
        
        public SpawnedIngredient(Ingredient type, Rectangle bounds) {
            this.type = type;
            this.bounds = bounds;
        }
    }
    
    // Forest and Cave ingredients
    private Map<Room, List<SpawnedIngredient>> activeIngredientsByRoom = new EnumMap<>(Room.class);
    
    // Brewing result
    private Potion lastBrewedPotion = null;
    private boolean lastBrewWasNewDiscovery = false;
    
    // Discovered potions
    private Set<Potion> discoveredPotions = new HashSet<>();
    
    // UI State
    private boolean journalOpen = false;

    public GameModel() {
        // Initialize inventory
        for (Ingredient i : Ingredient.values()) {
            inventory.put(i, 0);
        }
        
        // Spawn chances (1 in X chance)
        spawnChances.put(Ingredient.TREE_SAP, 8);
        spawnChances.put(Ingredient.FROG, 6);
        spawnChances.put(Ingredient.LEAF, 2);
        
        spawnChances.put(Ingredient.CRYSTAL, 6);
        spawnChances.put(Ingredient.BUG, 3);
        spawnChances.put(Ingredient.MUSHROOM, 4);
        
        spawnChances.put(Ingredient.ICE, 2);
        spawnChances.put(Ingredient.PEBBLE, 3);
        spawnChances.put(Ingredient.FUR, 8);
        
        spawnChances.put(Ingredient.RARE_CRYSTAL_FOREST, 32);
        spawnChances.put(Ingredient.RARE_CRYSTAL_CAVE, 32);
        spawnChances.put(Ingredient.RARE_CRYSTAL_MOUNTAIN, 32);
        
        activeIngredientsByRoom.put(Room.FOREST, new ArrayList<>());
        activeIngredientsByRoom.put(Room.CAVE, new ArrayList<>());
        activeIngredientsByRoom.put(Room.MOUNTAIN, new ArrayList<>());
        
        // Initial spawn
        spawnForRoom(Room.FOREST);
        spawnForRoom(Room.CAVE);
        spawnForRoom(Room.MOUNTAIN);
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
        return inventory.getOrDefault(ingredient, 0);
    }
    
    public void addIngredient(Ingredient ingredient) {
        inventory.put(ingredient, getIngredientCount(ingredient) + 1);
    }
    
    // Room ingredient collection
    public List<SpawnedIngredient> getActiveIngredients(Room room) {
        return activeIngredientsByRoom.getOrDefault(room, new ArrayList<>());
    }
    
    public void spawnForRoom(Room room) {
        Random random = new Random();
        Ingredient[] validTypes;
        
        if (room == Room.FOREST) {
            validTypes = new Ingredient[]{Ingredient.LEAF, Ingredient.TREE_SAP, Ingredient.FROG, Ingredient.RARE_CRYSTAL_FOREST};
        } else if (room == Room.CAVE) {
            validTypes = new Ingredient[]{Ingredient.MUSHROOM, Ingredient.CRYSTAL, Ingredient.BUG, Ingredient.RARE_CRYSTAL_CAVE};
        } else if (room == Room.MOUNTAIN) {
            validTypes = new Ingredient[]{Ingredient.ICE, Ingredient.PEBBLE, Ingredient.FUR, Ingredient.RARE_CRYSTAL_MOUNTAIN};
        } else {
            return;
        }

        List<SpawnedIngredient> roomList = activeIngredientsByRoom.get(room);
        if (roomList == null) return;
        
        // Each ingredient type rolls its own independent chance to spawn
        for (Ingredient t : validTypes) {
            int chance = spawnChances.getOrDefault(t, 10);
            if (random.nextInt(chance) == 0) { // 1 in X chance
                // Random bounds in the grass/ground area (approx x: 50-700, y: 400-500)
                int x = 50 + random.nextInt(600);
                int y = 400 + random.nextInt(100);
                
                roomList.add(new SpawnedIngredient(t, new Rectangle(x, y, 50, 50)));
            }
        }
    }
    
    public void collectIngredient(Room room, SpawnedIngredient ingredient) {
        List<SpawnedIngredient> roomList = activeIngredientsByRoom.get(room);
        if (roomList != null && roomList.remove(ingredient)) {
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
            lastBrewWasNewDiscovery = discoveredPotions.add(lastBrewedPotion);
        } else {
            lastBrewWasNewDiscovery = false;
        }
        
        return lastBrewedPotion;
    }
    
    public boolean wasLastBrewNewDiscovery() {
        return lastBrewWasNewDiscovery;
    }

    public Set<Potion> getDiscoveredPotions() {
        return discoveredPotions;
    }
    
    public Ingredient[] getRecipe(Potion p) {
        switch (p) {
            case HEALING_POTION: 
                return new Ingredient[]{Ingredient.LEAF, Ingredient.CRYSTAL};
            case POISON_POTION: 
                return new Ingredient[]{Ingredient.CRYSTAL, Ingredient.MUSHROOM};
            case STRENGTH_POTION: 
                return new Ingredient[]{Ingredient.MUSHROOM, Ingredient.LEAF};
            default: 
                return null;
        }
    }
    
    public int getTotalDiscoverablePotions() {
        int count = 0;
        for (Potion p : Potion.values()) {
            if (p != Potion.UNKNOWN_MIXTURE) count++;
        }
        return count;
    }
    
    public boolean hasWon() {
        return discoveredPotions.size() == getTotalDiscoverablePotions();
    }
    
    public boolean isJournalOpen() {
        return journalOpen;
    }
    
    public void setJournalOpen(boolean journalOpen) {
        this.journalOpen = journalOpen;
    }
    
    private void useIngredient(Ingredient ingredient) {
        int currentCount = inventory.getOrDefault(ingredient, 0);
        if (currentCount > 0) {
            inventory.put(ingredient, currentCount - 1);
        }
    }
    
    public Potion getLastBrewedPotion() {
        return lastBrewedPotion;
    }
}