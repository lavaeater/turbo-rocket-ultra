## Tuesday the 9th of august
So, here we are then. I have put so much in a different obsidian vault, but now I truly questioning the wisdom of it. Perhaps we should keep Obsidian for what it is good at, which is writing text into markdown files, whereas trello is good for managing boards and cards, so perhaps test a trello integration for the kanban board instead?


## Monday the 21st of february
So, I haven't written anything right here since before christmas, ey? What on earth are we doing on this project, really? Check the logs!

I cleaned out all of the branches to make space in my head. Branches should only exist while working on a feature, that is a necessary cleanup thing we got to get working. Anyways, what do I actually want to do right now with this game? It could be fun to just focus on one tiny tiny detail to understand more and figure out more on how to do certain things. This goes for the handling of grab-points for weapons, the UI etc.

When it comes to more fully expanded game features, I believe that [[4. Arkiv/5. Inte Riktigt Projekt/gamedev/turbo-rocket-ultra/Build Towers]] would be friggin' awesome as a next step for us - we have some code working for it, we just need, as with the tiny features mentioned above, to polish it and make it *feel* complete and finished.

## Tuesday the 21st of december
So, I am hesitating when it comes to the oblique projection work. I think it has to be something done in the background, so I will simply decide on a thing to do, for instance half all heights on box2d static bodies in the game (all characters have passable top halves, so they are good so far) and thereby shrinking the maps. But I also have to change all tiles etc, I think I could be well off sitting for 24 hours straight with this and perhaps then I would like to rework some other stuff. I don't know what to do. I will explore the todo-list and see what jumps out, and add things that I come up with.

Write a short text of what each does and so on.

I have now prioritized this list according to how much I think it adds to the games polish and / or value, then how fun it would be to actually implement (this is supposed to be fun) and then taking into account the effort I think would be involved. I will now simply do them from top to bottom until I get to the line below, then I will re-evaluate everything.

It also appears that the mouse position thing was so low hanging that I had already fixed it.
#misc
- [ ] [[4. Arkiv/5. Inte Riktigt Projekt/gamedev/turbo-rocket-ultra/Build Towers]]
- [ ] Fix some warnings
- [x] Text Crawl
- [x] Add explosion particle effect to grenade collision
- [x] Fix mouse position by polling position instead of event-driven system
- [x] Flip weapon sprites when facing west
- [x] Soundscape II - the Moaning
- [x] Objectives II, the sequel
--------
- [ ] Transitions schmanzitions
- [ ] Text Crawl II - with templates for stats and stuff
- [ ] Enemy AI II, with avoiding walls
- [ ] Objectives III - now with players all over the map
- [ ] Split Screen
- [ ] Bat swing
- [ ] HUD II, Header Upper Displayer <- rediscover MVVM pattern, two-way binding
- [ ] More Enemy Sprites (generate them)
- [ ] Lightmaps for sprites
- [ ] Player graphics made from components / parts etc.
- [ ] Vehicles would be cool
- [ ] Gibs and Body Parts II - blood trails and audio
- [ ] Zombies throwing projectiles
- [ ] Some other type of enemy
- [ ] Zombies with guns <- this would be genuinely cool, actually
- [ ] Nicer Setup UI, using Scene2d perhaps
- [ ] Puzzle obstacles / machines
- [ ] Oblique Projection

## Build Towers
Well, we basically have towers, we basically can build them, this is about making that feature happen and having at least two or three types of towers with nice sprites that we can build and that do different things.

First, we do inventory of what we actually have.

## Text Crawl
This could be combined with the transition concept. Here's what I want to do:
- [x] Show a dialog or crawl with paused game before level starts (what to do etc)
- [x] A working pause mode
- [x] Pause game when level is done and show end-of-level text

More thoughts: there is more to this than meets the eye. It comes down to a few very important points, namely: how do we do these things? How do we control the flow of the game, the exchange of information in the game and where is this information stored. Right now, it is something of a "hot mess", but that is to be expected. This particular feature shines a light on a deficiency of the game and changes need to be made.

For instance, previously we worked under the assumption that pausing would be its own screen, that's not the case right now, I think I like the way it is now much better. We just need to slow down and think about it for a minute or two, away from the code. 

It's like there are all these different ways of doing things. Like, I can actually control the game using my *Story* concept, which allows me to create rules and apply consequences. I have done some alterations to that because, well, it only cared about one story before and that might be wrong, I don't know...

## Transitions
It uses nested framebuffers and a separate SpriteBatch (instead of generic batch) so I couldn't make myself bother. Will try something else instead. Maybe I will simply clone his project and update the code and rewrite it in Kotlin.

### Objectives II - Quirkier
Examples: Kill n enemies. Hold an area for n minutes. Come up with something really fun, oooh, I got it, every player has to be in a separate place at the same time!
- [x] Kill n enemies
- [x] Hold area for n seconds 
- [ ] All players in different place at the same time

#### Kill N Enemies
This could be saved in some kind of setting for the map or something (check the story stuff). However, we also want to be able to add rules making spawning go faster and faster for this, would be real cool.

#### Hold an area for n seconds
So, a timer starts counting down, or a progress bar starts going up or whatever, like in Helldivers, and at the same time the spawners go haywire and spawn lots of enemies, perhaps in waves? So, like they spawn one per update for 10 updates, or stuff like that. Some cool settings for each

#### Different places
Slightly harder, but not that hard. Every player has to go to some specific place on the map, could be a cool time to implement split-screen so that the player that is the farthest from the center of the group is split off, temporarily, to enable better controls. This would be so fucking cool I piss my pants. 

- [x] Enemy Kill Counter
- [x] Faster and Faster Spawning
- [x] Area Hold Timer (when is area not holding? Distance)
- [x] Area Hold Timer in Map Def
- [ ] Enemy wave spawning (perhaps ten at a time etc for spawn component)
- [ ] Dynamic split screen <- coolest feature ever

### Soundscape II - the Moaning
To make audio effects working nicely, we need a way of controlling how many are playing at any one time, their duration, and queueing. I have managed to implement some of these things, but we need a few more.
- [x] Debug View for Audio Channels

#### Debug View For Audio Channels
This should be simple - just add some properties to channel, like name, name of sound being played, if it is played. Should be easy enough.


So, some ambient sound effects, some zombie sound effects, explosions, screaming, burning, etc.
Make a list of sound effects that we absolutely need and then tick them off as you find them
- [ ] Shuffling feet
- [ ] Screaming Boss
- [ ] Zombie being spawned
- [ ] Objective Reached
- [x] Magazine empty - is now one-liner
- [x] Cool one-liners said by players
- [x] Burning flames
- [x] Gasoline explosion
- [x] Grenade impact
- [x] Limbs being torn
- [x] Screaming, panicky Zombies
- [x] Moans

### Hud II
Make the HUD pretty and useful and legible. Work on more simple databinding stuff and Scene2D extensions etc.

### More enemy sprites
Go back to the character editor and make sure we have access to female bodies as well (only male now for some reason), and also enable generating a bunch of different sprites or variations using it.

### Lightmaps for sprites
To really make use of the box2d light stuff we could use some lightmaps for the sprites. This would take some learning.

### Enemy AI II
Make the enemy handle walls and obstacles better. It shouldn't be that hard. In fact, we could make a goddamned A* graph of the entire map space that is passable terrain and that would in fact solve the problem. Or at least make a graph of points that makes sure the enemy does not walk into walls, it could be done. Hey, every section could have a "get valid points"-method. Also, make enemies more aggressive towards players, now they seem to ignore them quite a bit, perhaps sensors are turned off or something.

### Player Graphics
This is what categorizes as a FUN task, it should be FUN! But it also requires lots of work, mainly in making art happen. Making heads, bodies, hair, stuff like that, and enabling generating characters and sprite sheets from that. Lots of work, but there could be great payoffs in the end.

### Vehicles
This is like the holy grail of features. This is what all this started with, the entire game. So, players enter vehicles, control different parts of the vehicle etc. This could be combined with the concept "moving level" where the players are on a platform that is moving through some kind of river / level somehow.

### Gibs and Body Parts II - blood trails and audio
What it sounds like. I want smeared blood and a cool sound effect to go with it.

### Flip weapon sprites when facing east
So, what it sounds like. Should be the lowest hanging fruit of the bunch.

### Explosion Effect for Grenades
Take the effect we have added to assets and add it to the game. Would make a world of difference.

### Zombies throwing projectiles
Have zombies / enemies throwing projectiles to add to the hectice nature of the game.

### Some other type of enemy
Running fast dogs, slime crawling about swallowing players, tentacles, why not tentacles? Exploding enemies? Poisonous enemies?

### Zombies with guns
Or some other enemy, obviously, but enemies that can shoot back.

### Bat swing
The bat swing is all about execution. It is an animation of a box2d object and that seems like a hassle. But it CAN obviously be done. Figure it out, champ!

### Nicer Setup UI, using Scene2d
So, just make a better setup screen, add icons, texts etc that explains what should happen etc.

### Fix mouse position using polling
Well, this might be even lower hanging than the previous fruit mentioned in that context. 

### Puzzle obstacles
Perhaps stuff where someone has to stand on a button to open a door, whatever. Also add some crushing machines and stuff. 

### Oblique Projection
This might not be as important as I want it to be.