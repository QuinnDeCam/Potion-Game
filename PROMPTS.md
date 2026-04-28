# Prompt 1
I'm building a Java Swing game called Potion Finder using MVC. Create three files: GameModel.java, GameView.java, and GameController.java. GameController should have the main method and open a JFrame window. GameView should extend JPanel and display a simple placeholder title screen that says "Potion Finder." GameModel should contain only placeholder comments for game data for now. Do not add gameplay yet. Just get the window to open correctly and the MVC structure wired together.
# Results
Created all three, files for set up. When complied and run, a 800x600 window, with the words "Potion Finder."

# Prompt 2
Add a simple room system with 3 rooms: 
- Forest
- Ingredient Cupboard 
- Brewing Room. 
Store the current room in GameModel. In GameView, display the current room name and draw a different simple visual for each room using basic shapes or text. Add buttons in the GUI to switch between rooms. Do not add ingredient logic yet.
# Results
Wouldn't run, there was an issue where the method signatures for drawIngredientCupboard and drawBrewingRoom were missing the Graphics g parameter type. Told AI to fix it, and sucessfully set up the game. Created three rooms with the desired idea, with simple shapes to distinguish things. 

# Prompt 3
Now add ingredient collection. In the Forest, allow collecting: Mushroom, Leaf, and Crystal. Use to collect ingredients, have it so the player must click on it. Store ingredient counts in GameModel. Display the player's inventory in GameView, where they show up in ingredient room after collecting.
# Results

# Prompt 4
Replace the simple triangle forest in GameView with my image file forest.png. Show me how to load the image and draw it in paintComponent when the current room is Forest.