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
Have 2-5 random ingredients spawn in random places in the forest. When all are collected, have that reset after 1 minute. Remove the ingredient cuppoard room, so when ingredients are collected, show them to the brewing room screen, where they can be selected. If there are duplicates collected, stack them, and show how many with text underneath. Keep all visuals and drawing in GameView.java
# Results
Sucessfully implemented, the ingredients spawn in random locations, and when collected, they appear in the brewing room with a number indicating the count collected. They do visually stack, which makes the visuals a bit messy, but it works as intended. The clicking of the ingredients in the forest needs to be fixed, since it is unresponsive fairly frequently.

# Prompt 5
What could be the issue where the clicking of the ingredients sometimes unresponsive?
# Results 
Changed mouseClicked to mousePressed, which triggers the instant the mouse goes down, making it feel snappier and a little more forgiving. Also Added an isClickInStack helper method that artificially inflates the bounding box for inventory items up and to the right so that the entire stack is perfectly clickable in the inventory section. 

# Prompt 6
Can you remove the stacking feature, and just draw the one? If there is more than 1, just reflect that with the draw count
# Results
In the brewing room, when there is more than 1 ingredient, it will draw a stack of them. This time, that feature was removed so it looks a lot cleaner.

# Prompt 7
Replace the cauldron with the Cauldron.png 
# Results
Worked, but I kinda hated where it was placed and the size of it, so I had to ajust those (which sucks in this Antigravity. will you just let me do things myselfff ugh)
(did this for each of my little images)

# Prompt 8
Can you replace the Leaf, mushroom, and crystal with the respective files Leaf.png, Mushroom.png and Crystal.png?
# Results
Exactly what I wanted, sized perfectly. 

# Prompt 9
Add a discovered potions journal. When a potion is successfully brewed for the first time, add it to a discovered list in GameModel. Display discovered potions in GameView. 
Show progress like:
1 of 3 potions discovered.
# Results
Took it literally, even though there are only 2 potions to discover currently lol. White text in the corner, and added another potion combination that I didn't ask for, and I don't think it's been implemented yet. The journal just pulls up when you click it, and the text is cut off a little by the drawn spine, which I just deleted and now it looks fine.

# Prompt 10
Have a book (just draw it as a square in GameView.java for now) in the top right corner of the screen. When that is clicked on, show the journal contents. Above the book, have the total number of potions discovered out of the number there are to discover. (if possible, make it easy to add more potions and ingredients later on)
# Results
(Need to test)

# Prompt 11
Also there is a minor issue, where the brew button, and whatever potion the player last made that stays on screen when you move to the foreset. Only show that, as well as the journal on the brewing room
# Results


# Prompt 12
Can the brew button and potion also disapear when the journal is opened? (or I guess just make it go on top of everything)
# Results
(Need to test)

# Prompt 13 
Can you make it so you can select a second ingredient of the same kind? (of course only when they are available)
# Results
(Need to test)

# Prompt 14
Can you replace the journal Icon with the image Journal.png? As well as move the Brew button the the right of the cauldron?
# Results

# Prompt 15
The journal was stretched vertically, can you change it so the journal image keeps it's aspect ratio?
# Results   
Worked! Didn't stretch the icon. It was a little small though, so I resized it manually

# Prompt 16
Add a win condition. When all potions have been discovered, display: "You discovered all recipes!" Disable further brewing after victory.
# Results
(Need to test)

# Prompt 17
Can we upgrade the open journal a little bit? I would like to include the recipe for each of the discoverable potions in the journal, as well as a little icon that is what the potion looks like. (Those can just be gray rectangles for now). 
# Results
(Need to test)

# Prompt 18 
Now, when we discover the potion, the gray icon will become the potion icon I'll upload for it. Now, can we add a gravity effect? When the brew button is pressed, the selected ingredients drop into the cauldron, and 3 second pause before something (the potion's icon) comes out of it. 
# Results
(Need to test)