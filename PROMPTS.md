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
Sucessfully added, the text is white and in the top right corner, the book icon is there, and the total count is there. When it is clicked on, the brew button and result potion are on top of the open journal, and the journal follows each room, which I don't want so I will be removing that next. 

# Prompt 11
Also there is a minor issue, where the brew button, and whatever potion the player last made that stays on screen when you move to the foreset. Only show that, as well as the journal on the brewing room
# Results
Fixed the issue

# Prompt 12
Can the brew button and potion also disapear when the journal is opened? (or I guess just make it go on top of everything)
# Results
Fixed the issue! Now I just want 

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

# Prompt 19
Replace the background of the Forest with the Forest.png
# Results
(Need to test)

# Prompt 20
Can we change the timing of ingredient collecting in the forest? I would like 1 ingredient to spawn randomly every 8-40 seconds or so. Have about a 1 in 5 chance  of two spawning instead of just 1. Also, remove the label above each ingredient, only show it if it is selected above the cauldron.
# Results
(Need to test)

Can you make the resulting potion icon this:
int[][] potion = {
{0,0,0,0,7,6,0,0,0,0},
{0,0,0,0,7,6,0,0,0,0},
{0,0,0,1,7,6,1,0,0,0},
{0,0,0,0,1,1,0,0,0,0},
{0,0,0,0,1,1,0,0,0,0},
{0,0,0,1,1,1,1,0,0,0},
{0,0,0,1,2,1,1,0,0,0},
{0,0,1,2,2,3,4,1,0,0},
{0,0,1,2,3,4,4,1,0,0},
{0,1,2,2,4,4,4,4,1,0},
{0,1,2,3,4,4,5,5,1,0},
{1,3,2,4,4,4,5,5,5,1},
{1,3,3,4,5,5,5,5,5,1},
{0,1,1,1,1,1,1,1,1,1}
};

Where 
The color pallete is this for the poison potion, 
    Color[] palette = {
    new Color(0, 0, 0, 0),        // 0
    new Color(179,179,179),       // 1
    new Color(128,128,128),       // 2
    new Color(181,230,29),        // 3
    new Color(56,127,62),         // 4
    new Color(26,94,42),          // 5
    new Color(142,94,74),         // 6
    new Color(176,139,122)        // 7
};

The greens should be changed to a different color that depend on the potion. 

# Prompt 21
When the potion is discovered, have the icon linger a little longer, with the name discovery just above it, and then have the icon spin and shrink into the journal icon, to show it's being added. Replace the little gray square in the journal for that potion with the icon when discovered. 
# Results
(Need to test)

# Prompt 22
I think it is time to up the potion game. Are you ready to make new rooms? I'm thinking each room has different ingredients, can we start by changing the ingredients of the forest, and adding a cave room? In the cave there will be the crystal, the Mushroom, and also a bug. In the Forest we will have Tree sap, Frog, and keep the Leaf.
# Results 
before running, it asked two questions (which I answered below, and then it ran)

# Prompt 22.5
To answer your questions first. For the Inventory layout, I plan on adding another room and add 6 more ingredients soon, so I would like to have two columns. I like the size of the ingredients currently, so if there isn't enough space we could use the top of the screen in the brewing if needed. For the second question, yes, I will add combinations later. Also, make the rate of spawning of each ingredient really easily modifyed, so I can change them for each ingredient individually. 
# Results
Yayyy, new room! The Cave and Forest, with new ingredients, and working spawn rates. The only thing I don't like is how i orignally built how the ingredients spawn, which I should change it few times until i like it. Also put placeholders for images, so that when I upload a new ones for each item, (I know it's a lot of work but I love art so it works out, bc I'll be doodling pixel art and feeling productive) it will be easy to use.

# Prompt 23
Awesome! Next room is a mountain.
It will have: Ice, Pebble, and Fur
# Results
Same as prompt above! it is a blank white room but it works. I think I also forgot to mention that all the things spawn in like this really specific area on the screen, in a rectangle towards the bottom, because I think in the original early versions, it chose to spawn them on the forest floor. I would like to change that to be random spawn within the bounds of the room later on.

# Prompt 24
Okay now I'm going to add three more items, a rare crystal for each room. they will each have a 1 in 32 chance of spawning. It can be the same image as the current crystal, but a different color. For the forest rare color, it is pink
for the cav rare crystal color, it is orange
And finally, the mountain's rare crystal is blue. 
# Results
It worked, but the rare cave crystal's colors are a little off, and I'd like to change the tint on them. And by a little off, I mean they are dull and ugly. 

# Prompt 25
Okay now the items are too close together in the ingredients on the brewing room, can you fix that? Just give them a little more space vertically, don't move them horizontally. also remove the title of the room, and lower where selected ingredents go, make sure they are centered still. 
# Results
Didn't give them more space, at all. But the title of the room was removed and did lower where selected ingredents go, and were a lot more centered. 

# Prompt 26
Can you seperate the ingredients more? I don't want the yellow boxes that show what is selected to overlap. Keep them all on the left side, and bring them a little bit higher, a little more to the top left part of the screen. 
# Results
I swear there is enough space for them without moving the cauldron, but it decided to move the cauldron over to the left a lot. I think it's actually fine, but the ingredient selected and the labels for the potions are not in the right spot over the cauldron now.  

# Prompt 27
Are you ready to up the potion game? I would like to now have 28 combinations of ingredients, to have 23 potions to the game. We might have to change how the potion journal looks.
I have a list of the combinations I would like to add.
# Results
(Need to test)

# Prompt 27.5 (Responding to Implementation Plan)
I think I would prefer arrow buttons for the pages of the journal! (If you could make the icons a little bigger that would be great too, because some will have more custom ones)

Here is the list!

Sap + Sap = Sticky Liquid
Sap + Ice = Sticky Liquid
Sap + Crystal = Tiny Vial
Sap + Leaf = Sticky Liquid
Sap + Fur = Muddled Mixture 
Pebble + Pebble = Heavy Potion
Pebble + Crystal = Heavy Potion
Bug + Frog = Night Vision Potion
Bug + Mushroom = Glow Potion
Bug + Crystal = Glow Potion
Fur + Fur = Fuzzy Potion
Bug + Bug = Bug Juice
Pebble + Fur = Friend Potion 
Crystal + Fur = Special Friend Potion
Ice + Rare Cave Crystal = SPICY WATER
Frog + Frog = Hopping Tonic
Leaf + Leaf = Leaf Juice
Bug + Sap = Spiderman’s Brew
Bug + Fur = Spiderman’s Brew
Fur + Frog = Allergic Reaction in a Bottle 
Leaf + Mushroom = Growing Potion
Pebble + Leaf = Ramune
Ice + Pebble = Rocks on the Rocks
Frog + Ice = Cold Blooded 
Mushroom + Crystal = Drug Dose
Mushroom + Frog = Speckled Skin Serum
Leaf + Frog = Glass Skin Serum (Translucency)  
Fur + Ice = Yellow Snow Cone Concoction
Ice + Ice = Water

