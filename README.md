# Potion-Game
Potion Finder: Game, where you collect ingredients, and then combine them to discover potions. 


Potion Finder is a 2D Java Swing exploration and discovery game where the player collects ingredients from different environments and combines them to discover potion recipes.
The player can switch between three main rooms:
Forest (Tree Sap, Frog, Leaf, Rare Pink Crystal)
Cave (Crystal, Mushroom, Bug, Rare Orange Crystal)
Mountain (Ice, Pebble, Fur, Rare Blue Crystal)
Ingredients spawn randomly over time in each room. The player clicks on ingredients to collect them. Collected ingredients appear in the Brewing Room, where the player can select up to two ingredients and combine them in a cauldron. If the combination matches a valid recipe, a potion is created and added to the player’s Journal. The goal of the game is to discover all potion combinations. The game ends when all recipes have been found.

The Model is responsible for all game data and logic. GameModel.java will store the following:
Current room (Forest, Cave, Mountain, Brewing Room)
Player inventory (ingredient counts)
Active ingredients currently spawned in each room 
Spawn timing and probabilities for each ingredient
Selected ingredients for brewing
All valid potion recipes
Discovered potions
Is journal open
Is brewing in progress
Has the player won

The Model contains no drawing code and does not handle user input directly.

The drawing code will be done through the View. GameView.java is responsible for rendering everything the player sees. This includes:

Room backgrounds (Forest, Cave, Mountain, Brewing Room)
Ingredient sprites placed in the world
Inventory display in the Brewing Room
Selected ingredients above the cauldron
Cauldron and brewing visuals
Potion list
Icons
Text

The View does not modify game state—it only reads from the Model.

What the Controller will handle (GameController.java)

The Controller manages all input and game flow like:
All mouse inputs
Pressing the brew button
Updating the Model based on player actions
Triggering room changes
Managing timing systems:
Ingredient spawning intervals
Brewing delays and animations
Running the main game loop


The project is complete when:
All 3 rooms (Forest, Cave, Mountain) are fully functional with unique ingredients
Ingredients spawn randomly over time with adjustable rates
Player can reliably collect ingredients via clicking
Inventory system works with stack counts
Brewing system allows selecting two ingredients, including duplicates
All potion combinations are implemented and working
A journal that keeps track of all discovered potions, and will show you the recipe and name of the discovered potion.
Rare crystals spawn rarely
Win condition triggers:
“You discovered all recipes!” message

At this point, the game is fully playable, all systems are connected through MVC, and new ingredients or potions can be added easily!