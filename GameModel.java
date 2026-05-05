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
        BREWING_ROOM,
        START_SCREEN
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
        FUR
    }
    
    // Potion types
    public enum Potion {
        STICKY_LIQUID,
        TINY_VIAL,
        MUDDLED_MIXTURE,
        HEAVY_POTION,
        NIGHT_VISION_POTION,
        GLOW_POTION,
        FUZZY_POTION,
        BUG_JUICE,
        FRIEND_POTION,
        SPECIAL_FRIEND_POTION,
        HOPPING_TONIC,
        LEAF_JUICE,
        SPIDERMANS_BREW,
        ALLERGIC_REACTION_IN_A_BOTTLE,
        GROWING_POTION,
        RAMUNE,
        ROCKS_ON_THE_ROCKS,
        COLD_BLOODED,
        DRUG_DOSE,
        SPECKLED_SKIN_SERUM,
        GLASS_SKIN_SERUM,
        YELLOW_SNOW_CONE_CONCOCTION,
        WATER,
        UNKNOWN_MIXTURE
    }
    
    private Room currentRoom = Room.START_SCREEN;
    
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
            validTypes = new Ingredient[]{Ingredient.LEAF, Ingredient.TREE_SAP, Ingredient.FROG};
        } else if (room == Room.CAVE) {
            validTypes = new Ingredient[]{Ingredient.MUSHROOM, Ingredient.CRYSTAL, Ingredient.BUG};
        } else if (room == Room.MOUNTAIN) {
            validTypes = new Ingredient[]{Ingredient.ICE, Ingredient.PEBBLE, Ingredient.FUR};
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
    
    private boolean matches(Ingredient i1, Ingredient i2, Ingredient a, Ingredient b) {
        return (i1 == a && i2 == b) || (i1 == b && i2 == a);
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
        if (matches(ingredient1, ingredient2, Ingredient.TREE_SAP, Ingredient.TREE_SAP) ||
            matches(ingredient1, ingredient2, Ingredient.TREE_SAP, Ingredient.ICE) ||
            matches(ingredient1, ingredient2, Ingredient.TREE_SAP, Ingredient.LEAF)) {
            lastBrewedPotion = Potion.STICKY_LIQUID;
        } else if (matches(ingredient1, ingredient2, Ingredient.TREE_SAP, Ingredient.CRYSTAL)) {
            lastBrewedPotion = Potion.TINY_VIAL;
        } else if (matches(ingredient1, ingredient2, Ingredient.TREE_SAP, Ingredient.FUR)) {
            lastBrewedPotion = Potion.MUDDLED_MIXTURE;
        } else if (matches(ingredient1, ingredient2, Ingredient.PEBBLE, Ingredient.PEBBLE) ||
                   matches(ingredient1, ingredient2, Ingredient.PEBBLE, Ingredient.CRYSTAL)) {
            lastBrewedPotion = Potion.HEAVY_POTION;
        } else if (matches(ingredient1, ingredient2, Ingredient.BUG, Ingredient.FROG)) {
            lastBrewedPotion = Potion.NIGHT_VISION_POTION;
        } else if (matches(ingredient1, ingredient2, Ingredient.BUG, Ingredient.MUSHROOM) ||
                   matches(ingredient1, ingredient2, Ingredient.BUG, Ingredient.CRYSTAL)) {
            lastBrewedPotion = Potion.GLOW_POTION;
        } else if (matches(ingredient1, ingredient2, Ingredient.FUR, Ingredient.FUR)) {
            lastBrewedPotion = Potion.FUZZY_POTION;
        } else if (matches(ingredient1, ingredient2, Ingredient.BUG, Ingredient.BUG)) {
            lastBrewedPotion = Potion.BUG_JUICE;
        } else if (matches(ingredient1, ingredient2, Ingredient.PEBBLE, Ingredient.FUR)) {
            lastBrewedPotion = Potion.FRIEND_POTION;
        } else if (matches(ingredient1, ingredient2, Ingredient.CRYSTAL, Ingredient.FUR)) {
            lastBrewedPotion = Potion.SPECIAL_FRIEND_POTION;
            lastBrewedPotion = Potion.HOPPING_TONIC;
        } else if (matches(ingredient1, ingredient2, Ingredient.LEAF, Ingredient.LEAF)) {
            lastBrewedPotion = Potion.LEAF_JUICE;
        } else if (matches(ingredient1, ingredient2, Ingredient.BUG, Ingredient.TREE_SAP) ||
                   matches(ingredient1, ingredient2, Ingredient.BUG, Ingredient.FUR)) {
            lastBrewedPotion = Potion.SPIDERMANS_BREW;
        } else if (matches(ingredient1, ingredient2, Ingredient.FUR, Ingredient.FROG)) {
            lastBrewedPotion = Potion.ALLERGIC_REACTION_IN_A_BOTTLE;
        } else if (matches(ingredient1, ingredient2, Ingredient.LEAF, Ingredient.MUSHROOM)) {
            lastBrewedPotion = Potion.GROWING_POTION;
        } else if (matches(ingredient1, ingredient2, Ingredient.PEBBLE, Ingredient.LEAF)) {
            lastBrewedPotion = Potion.RAMUNE;
        } else if (matches(ingredient1, ingredient2, Ingredient.ICE, Ingredient.PEBBLE)) {
            lastBrewedPotion = Potion.ROCKS_ON_THE_ROCKS;
        } else if (matches(ingredient1, ingredient2, Ingredient.FROG, Ingredient.ICE)) {
            lastBrewedPotion = Potion.COLD_BLOODED;
        } else if (matches(ingredient1, ingredient2, Ingredient.MUSHROOM, Ingredient.CRYSTAL)) {
            lastBrewedPotion = Potion.DRUG_DOSE;
        } else if (matches(ingredient1, ingredient2, Ingredient.MUSHROOM, Ingredient.FROG)) {
            lastBrewedPotion = Potion.SPECKLED_SKIN_SERUM;
        } else if (matches(ingredient1, ingredient2, Ingredient.LEAF, Ingredient.FROG)) {
            lastBrewedPotion = Potion.GLASS_SKIN_SERUM;
        } else if (matches(ingredient1, ingredient2, Ingredient.FUR, Ingredient.ICE)) {
            lastBrewedPotion = Potion.YELLOW_SNOW_CONE_CONCOCTION;
        } else if (matches(ingredient1, ingredient2, Ingredient.ICE, Ingredient.ICE)) {
            lastBrewedPotion = Potion.WATER;
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
            case STICKY_LIQUID: return new Ingredient[]{Ingredient.TREE_SAP, Ingredient.TREE_SAP};
            case TINY_VIAL: return new Ingredient[]{Ingredient.TREE_SAP, Ingredient.CRYSTAL};
            case MUDDLED_MIXTURE: return new Ingredient[]{Ingredient.TREE_SAP, Ingredient.FUR};
            case HEAVY_POTION: return new Ingredient[]{Ingredient.PEBBLE, Ingredient.PEBBLE};
            case NIGHT_VISION_POTION: return new Ingredient[]{Ingredient.BUG, Ingredient.FROG};
            case GLOW_POTION: return new Ingredient[]{Ingredient.BUG, Ingredient.MUSHROOM};
            case FUZZY_POTION: return new Ingredient[]{Ingredient.FUR, Ingredient.FUR};
            case BUG_JUICE: return new Ingredient[]{Ingredient.BUG, Ingredient.BUG};
            case FRIEND_POTION: return new Ingredient[]{Ingredient.PEBBLE, Ingredient.FUR};
            case SPECIAL_FRIEND_POTION: return new Ingredient[]{Ingredient.CRYSTAL, Ingredient.FUR};
            case HOPPING_TONIC: return new Ingredient[]{Ingredient.FROG, Ingredient.FROG};
            case LEAF_JUICE: return new Ingredient[]{Ingredient.LEAF, Ingredient.LEAF};
            case SPIDERMANS_BREW: return new Ingredient[]{Ingredient.BUG, Ingredient.TREE_SAP};
            case ALLERGIC_REACTION_IN_A_BOTTLE: return new Ingredient[]{Ingredient.FUR, Ingredient.FROG};
            case GROWING_POTION: return new Ingredient[]{Ingredient.LEAF, Ingredient.MUSHROOM};
            case RAMUNE: return new Ingredient[]{Ingredient.PEBBLE, Ingredient.LEAF};
            case ROCKS_ON_THE_ROCKS: return new Ingredient[]{Ingredient.ICE, Ingredient.PEBBLE};
            case COLD_BLOODED: return new Ingredient[]{Ingredient.FROG, Ingredient.ICE};
            case DRUG_DOSE: return new Ingredient[]{Ingredient.MUSHROOM, Ingredient.CRYSTAL};
            case SPECKLED_SKIN_SERUM: return new Ingredient[]{Ingredient.MUSHROOM, Ingredient.FROG};
            case GLASS_SKIN_SERUM: return new Ingredient[]{Ingredient.LEAF, Ingredient.FROG};
            case YELLOW_SNOW_CONE_CONCOCTION: return new Ingredient[]{Ingredient.FUR, Ingredient.ICE};
            case WATER: return new Ingredient[]{Ingredient.ICE, Ingredient.ICE};
            default: return null;
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