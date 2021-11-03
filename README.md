# turbo-rocket-ultra

## What's up with the name, anyways?

The name of this repository is an homage to the first game I made with libgdx / kotlin and that game was an homage to the classic Amiga game Turbo Raketti (https://en.wikipedia.org/wiki/Turboraketti) that me and some friends played back in the 90s on our Amigas. It's a simple enough Thrust-clone with racing modes and fantastic dog fights. Endless replayability. It represents the holy grail, a fun game that should be *doable*, you know, for a person with no knowledge of how to code or do games. However, the game that I am currently working on in this repository is absolutely not a Turbo Raketti clone. It's just a name.

## Video Development Log

My ambition is to make coding sessions over on my youtube channel for the development of this game, to sort of learn-by-doing and also provide content for you people out there that are also trying out libgdx and Kotlin and want to make a game and don't know how.

## What is this game, anyways?

Inspirations off the bat for this game are:
* Lovers in a Dangerous Spacetime
* Helldivers
* South Park Tower Defense

To make the graphics and environment easy I am currently working with the concept of "zombie tower defense", which is obviously lame and very boring, but hey... it's easy to find art for.

### MVP

* Weapons 2 <- doing
* Interactable Components in-the-game
* Player graphics made from components / parts etc.
* Different weapons to shoot with
* Vehicles <- nice to have
* Build Towers <- Paused
* Objectives for maps <- done
* Far Off Map Rendering <- done
* Lights and particles <- done
* Auto-generated Snake-maps <- done
* Basic Sprite Editor <- done, kinda?
* IsoMetric Projection <- done!
* Towers <- Done
* Game Over Screen that does *anything* <- DONE!
* Start Screen that does *anything* <- done
* Scoring and Objectives <- done
* Game over man <-Doing
* Player death <- Done
* Fix enemy sensors / noticing <- advanced behaviors
* Fix enemy direction systems <- done?
* Controller support <- Done
* Twin Stick shooting <- done
* Multiplayer <- done
* Player damage / Enemy attacks <- Done
* Objectives <- Done!
* Enemies <- Done
* Blood Splatter <- Done
* MiniMap <- Done

## Weapons 2 <- doing

This, by nature, needs to consist of a **ton** of things, all very small, preferrably.

## Objectives for maps

So, apart from the gibberish below, what is an objective on a map? The easiest concept is an area that one needs to get to, or several. We could have a "ratio" of objectives, so that we output something like number of sections / 4 objectives per map, and these are simply spots you need to "hit" to move further in the game.

So, that is done, objectives, that is. 

So, what should we doing next? Traps? Bombs? Different guns? Ammo? Story?

What happened to the energy?

Lights, we need lights, one per section.

Same goes for enemies, obviously. 

So, next step is adding objectives and enemies on a per-map basis. We only render parts of the map, so it is not inconceivable... we could render the entirety of the map, the engine can handle it, of course, because that would make spawning of enemies easier etc... and otherwise we have to handle enemies just walking off into the sunset... Damn. I knew my cool idea was way too cool.

## Far Off Map Rendering

One of the points of doing the map as a series of inter-connected sections that the player moves in and out of was that I wouldn't have to take into consideration the planar geometry of the actual map. This means that we can, for instance, go north, west, south, east and not come back to the start of the map. This is kind of cool and I kind of like that. But it puts a real damper on the ability to have enemies and other things live their actual lives on the map. So, what do we do about that?

One idea was the "far off map rendering" strategy. This simply means that we take everything we do not currently render and render all of that somewhere out of the way. That would mean that enemies can meander about in that part of the map as they see fit. If they move into the sections that our intrepid player is in, we could just teleport them there. But that would also create the problem of keeping track of wether or not they could hear the players and stuff like that. Gaah. 

The lowest effort thing to do would be to simply implement a generator that doesn't overlap itself - or can handle overlapping. This is also something I would like to do anyways. In this case we could imagine a grid of n x n in size and we simply move about on that grid. If we overlap, we add connections. In this case, we can simply just walk about on the grid and if we have already been to a particular coordinate, we retrieve the existing thing there and connect to it.

## Lights and Particles

So, we're revisiting particles, maybe, but mostly we want to use box2d lights in the game.

## Levels and Maps

One way of doing this is obviously TILED. LibGDX supports tiled out of the box, which is nice. 

But is that what I want? Do I want some kind of background, grass or something? How do I create tilesets that work the way **I** want?
TODO: 
- [x] Check what tilesets / resources we have already
- [x] Generating a playable map from nothing?

What if we create a snaking trail, a labyrinth of sorts, that just represents the players required route from start to finish, then we add some objectives along the road, enemy spawn points and a goal. That is something we could work with and expand upon. That would be kind of cool actually. 

So, each section of the snaking map represents a length of "road". Lets start by assuming that this is maximally twice the size of the screen in size. We always start with a section of that specific size. We can then either move up, left or right. Every time we move to a new section, we have a set of possible directions we can move in. Lets go north, east, south, west instead. So, we start by being able to go in any of the four directions, but if we move for instance to the south on the first, then we can't go to the north on the next one. But we dont have to consider anything else than that, the map can curl around itself however it wants, because it becomes a grazy linked list and all we need to keep track of is exits and previous / next for overlapping display purposes (seamless transitions between parts).

### Data structure
I made it as a linked list, type of thing. What I want to do next is some weird kind of thing where we can have branching paths in this structure, that would probably require some kind of recursive code, it also would take slightly more effort than just changing three lines, so not just now.

### DevLog

The most important thing in game development is to not do what you have decided to do, but something else entirely - in this case particle effects. Thing is, it's not on the docket. This is an MVP, this is a proof of concept of a playable game, so we should focus on just doing towers.

## Building Towers
Towers is a rather large feature, I've come to realize. Break it down! So, in this case, this means focusing strictly on the building part of the Tower concept. 

How are towers supposed to work, really? Well, I propose that a player at any time can press a "build" button, which brings up a small interface to build a tower. The interface could be overlayed over that players HUD-spot, to not interfere with the gameplay, perhaps. We need a nice mechanism for that. There might be some limitation on how many, how fast and what type of towers a player can build.

* Build interface <- doing
* Tower sprites <- done
* Build actions <- done, kinda

### How 'bout some polish, eh?
I am against polish. Not *the* polish, they are some fine folks (having worked with a few), but the kind of polish that makes things shiny and new. Or rather, I know that polish takes a **ton** of time, the kind of time I don't have. But for this feature to *work*, it could use some polish.

### Different types of towers, eh?

So, to build towers, it would be cool to actually have different types of towers. I have made a very poor graphic of three different towers that could be used.

### Bring up Small interface to build towers
That shit will take all day, ALL DAY!

I've been fiddling with "simple" UI components and stuff, it would be really cool to be able to use something *very simple* to display a UI element. We'll see if it is actually possible. Scene2D isn't "bad" - but it's way too complicated and "complete" for what I need. So, game should still play, but the player should be immobilized when entering "build mode". Oh, and we need two types of towers so we can select what to build.

How to handle the "build mode" - well, the player could very well have a state machine or a state stack (as in game programming patterns), but do we *really* need to refactor "everything" for that purpose right now? Maybe - we'll see. For now, we will have a method somewhere that says if we are in "control mode" or "build mode".

Looking through the code, I find a remnant of the old vehicle code (removed since) that allows me to set a stationary flag. Fantastic. That will do for now, perhaps. But also perhaps, we need some kind of nifty mode.

## Done: Sprite Editor
I am not doing a sprite editor. Thing is, I use free resources for characters and animations and I just got a bit fed up with available anims and stuff, so I actually went out and bought some assets on itch.io - but then I have to set up the textures and stuff to actually use them.

So, when you use textures and stuff and you have some kind of actual operation up and running, you can just decide that all spritesheets should be structured in a specific way, like just the way it is structured - so that you can structure your animations and stuff properly.

But when buying sprites and stuff or using free assets, you do not have that freedom. So I actually built a small tool using C# and XAML to "tag" sprites in a spritesheet in a way that I can then use to load the assets.

However... that was made four years ago using C# and WPF - but I am not doing development using C# anymore and I basically hate WPF, like everyone else. But doing UI in LibGDX is absolutely awful - because it is using Scene2D which is a scenegraph and sort of doesn't simplify just doing UI presentation for you - I want to get results, not fiddle with Scene2D.

So, anyways, I have started doing my OWN ui Framework, because I just love to waste time. 

Here's what I want:
* select an image from disk <- not doing
* show that image <- done
* allow specifying sprite dimensions <- done
* draw lines over texture for sprites <- done
* iterate over lines and tag them with some kind of metadata <- not done
* save this data so it can be loaded later <- done

That's what we should do now.

### Step one: select image from disk
I will not implement a file select dialog. I will simply list all files in a pre-specified folder and then allow the user to select a file in that list.

I will skip this step because it is not important.

### Step two: display an image

So, this should be easy, right? ;-)

## Done: Towers!!

This is going to be FUN!

### Mechanics

Todo: 
* Building Towers  
* Tower Health
* Dismantling Towers
* Sound effects
* Rotation speed
* Tower Types and Variants
* Tower entities <- done
* Tower AI <- done

#### Towers

Towers are simply entities with a bucket of components. They might have behavior trees connected to them, for instance. Cool. Easy. There should be different types of towers, and I propose two basic types to begin with:
* Distractors / Thumpers - towers that get zombies or creatures to go towards them
* Gun Towers - towers that shoot at enemies at some distance from them

It's good to start with at least two different types of towers because that forces me to make a UI for building the two types, makes me have to have factories for the two types, AI and other systems for the two types. Very cool stuff. 

The behaviors of the towers should be controllable by using BTs, no issue there - and I mean the shooting towers, of course.

### Graphics
Just some kind of graphics related to towers would be nice.

## Notes on getting caught up in drama

What do I even mean by that? Well, I am of course talking about game-altering ideas about new mechanics. Here's the thing: I want to make the game isometric, I have always wanted that. But now is not the time, keep the eye on the prize and keep chugging along - isometric is several levels of confusion added upon confusion. We can deal with that in another game at a later stage, when we have "all" things figured out for this game.

## Done: Game Over, Man! - v0.1

### Mechanics

When all players have died their final death, the game should move to the game over screen. This could be a scene2d stage with some info on how the game went. In this we should also add - kill counts! Yay! And objective counts, per player, perhaps?

1. Make game transition to game over screen when all lives are spent
2. Count number of kills per player.

## Done: Interrupted Tasks v0.1

### Notes
So, what I wanted to achieve as a proof-of-concept was the idea of zombies hearing stuff, getting distracted and the like. So if you run around shooting zombies, they might be attracted to the noise. Hypothetically this could be a play strategy, like putting out "thumpers" that make noise and attract zombies, etc. We'll see. This is done by simply adding a component to the zombie from some other part of the game (a noticing system, or contact manager), which interrupts what the zombie is doing right now and sends it away to investigate the noise for some time. It took way too much effort because I misunderstood decorators and guards completely. Decorator wraps tasks and adds special behavior to them, Guards are tasks that evaluate to success or fail - and depending on that the actual task is executed or not.

### Mechanics

How do I implement guards and interruption of actions being performed? As it is now, these types of tasks I have implemented simply return success or failure OR running - but it would be cool to add some kind of guard / interruption of these tasks using some kind of "simple" mechanic - which of course could be done in several ways.

How about messages? A message from the ContactManager to the behaviorsystem (yes probably)? Or the contact manager adds a component and that component is marked as a "failcurrent task"-component? An interrupt-component, if you will? So, that could be super simple, to make some kind of "guard" system happen. An interrupt occurs... more on this later.

Implement my own Interrupt-decorator. Basically "Interrupted if has" - same as I already did, of course. What is the change? No change... damn. Add Interrupt-component to all entitycomponent-tasks, could work, all these systems could look for some particular component and say "fail" if it shows up, we will try that, won't we?

Decorator aren't guards. What .. the .. hell.

Why didn't it work before, then? Make it work this time...

So, an important PSA about Guards - I for some reason got it into my head that guards = decorators, but that is absolutely not the case. Instead it is more like, well, guards could be any task. It could be an entire subtree of the behavior tree, or an imported tree, or whatever. 

So, if I understand correctly, the dynamic guard selector evaluates the childrens guards and it picks the FIRST child whose guard evaluates to true. So our investigate-task should only be run when the enemy has noticed something, and the noticing function is there again. 

So I will try to set up the tree to have like "HasComponentTask" as guard - but the task can't then be inverted, or can it? Yes it can. The inverter inverts the result of the child task, which means we have to take that into account when doing this logic. It will work.



## Done: Multiplayer v0.1

### Mechanics

This feature will add about 20 sub-features. To have multiplayer, we must now have a way of starting the game, pausing the game, adding players, removing players, etc. And of course the most important one of them all: selecting the player character. So I will add:
* A FSM for the Game state <- this is where these are actually **very** useful
* A game setup screen <- not done
* A pause screen <- "done"
* A game over screen <- "done"
* Ways to move between these etc. <- done

After that, we can actually use the multiplayer functionality, shouldn't be too difficult.

## Done: Twin Stick v0.1

Needs tweaking of the aiming, it is by far not perfected. We need to move to a polling system for the axes, to enable fiddling with the aim, but using lerp was a boon.


## Done: Controller Support v0.1
### Mechanics

Controllers will be used in the classic Twin-Stick-Shooter manner. The only thing needed is basically just implementing it. 

## Done: Player Damage / Enemy Attacks v0.1

### Mechanics
Just like when the player shoots, enemy attacks will have a cooldown. When they are within a certain range, they will simply attack the player and with some probability they will succeed. Blood must splatter, health must be reduced, the player must be careful

## Done: Objectives v0.1

### Mechanics
I want to have many different mechanics in the game, but the base mechanic will, for now, be different points to visit on the map. These will be indicated on the minimap (nice) and to clear the level, the player has to go to the objective. Every level will increase the number of objectives and the number of enemies. Yay!

Object pooling will be necessary soon.

## Done: Blood Splatter v0.1
I got a crazy idea on how to do blood splatter. It's so cool I can't even focus on creating enemy attacks...

The idea: when an enemy is hit, some box2d bodies "as particles", will be created and flung out from the enemy in like the direction of the hit with some randomization thrown in.

But what are other, more efficient and not as demanding ways of doing blood splatter? I could imagine just actually creating textures as we go, using pixmaps, and drawing them rotated. That could work as well, and I will try that - because this thing with the method now is that it creates crazy amounts of entities and they linger for quite some time 

## Done: Enemies v0.1

Enemies are SO important for a game, right? 

So, what are we going to do here, for our enemies?

### 1. Mechanics of the Enemies

To start off, I think my enemies should be simple zombies. Perhaps you could evade the zombies by sneaking about on some maps. Some zombies might be a bit smarter, some might have better hearing and so on, so they might have different behaviors like that. But the basic zombie wants to eat brains and to do that it has to get close. So, there should be a bunch of them. They should amble about rather aimlessly - unless they notice the player. When they notice the player, they should start trying to get to the player.
A cool thing would be that if they notice other zombies being excited by something, they start following that zombie - thus making it likely that they notice the player and try to attack.
When in range for an attack, they will attack. 
When ambling about, they simply walk in some direction for some time, after which they will pick a different direction to walk in. 
How will they find the player? I just figured it out, actually. They have *passive* and *active* sensors. The passive sensors are just a circle around them that notices if the player enters it. The active sensors on the other hand, they simply activate when stopping to check for a direction to walk in. It is the direction they will walk in described as a triangle, basically, or polygon if you will.
I implemented the active sensors simply by raycasting in a semicircle, so the enemy stops and scans an area. If the enemy finds the player, the chase is on, otherwise, it will amble on. I also added a feature where the enemy will follow another enemy if they discover each other-unless it is already following the player. This means enemies will cluster together in packs after a while of running around aimlessly.

### 2. Graphics of the enemies
So, I already had the graphics in hand, just needed to add setting the animstate and direction of the sprites, which was easy enough. 

### 3. Sounds of enemies
I won't be doing sounds for the enemies right now.

### 4. Polish
No polish right now. If we want more advanced behaviors in the future, behavior trees are the way to go. These are included in the libGDX core and are a bit tricky to use but once you get the hang of it, it's really cool.

## Done: Shooting System v0.1
So, I have added some sprites to the game and am now working on a shooting system. I will publish a video on how I implemented that and with some trouble shooting related to framerates, vector maths, raycasting in box2d, etc, very soon. So, the first thing was actually shooting, then adding some kind of rate of fire (for different types of weapons). Then doing damage (done) and destroying enemies when they are dead - also done.

So for every feature in a game I think (I am not a pro, don't listen to me) you could make a small default TODO-list for it that needs to be done for every feature to consider it done:
1. Mechanics
2. Graphics
3. Audio
4. Polish

What do I mean with these?

### Mechanics
The mechanics are obviously how the feature works. In this case, the shooting system, I needed to figure out lots of things regarding it that aren't part of presentation, but just how I would want the shooting to actually work. So, what happens when the player pulls the trigger? How do I handle rate of fire? How do I handle the actual shot flying away?
When I started the game I actually used box2d bodies flying around - which I don't think is very efficient and not what I was looking for. The problem is that you have to manage their speed and stuff in a somewhat complicated way (to sort of simulate muzzle velocties of handguns etc). Now, for my game, bullets and projectiles can be considered basically instantaneous, it's going to be close combat. So I went with Box2D raycasting instead (http://www.iforce2d.net/b2dtut/raycasting and https://github.com/libktx/ktx/tree/master/box2d).
That isnt't that complicated once you get the hang of how to handle it - but then I had to think about rate of fire. In the real world, what happens is, if you have an automatic or semi-automatic gun, the force from gasses in the barrel or just recoil forces from the explosion forces the mechanism of the gun to cycle, pulling a new cartridge from a magazine, loading it and then firing again. But if the gun is ready to shoot, you pull the trigger and the shot goes away instantly. The way I was doing it was backwards, there was a delay, corresponding to the delay that would come **after** a shot, before a shot was actually fired. So I had to rework the rate of fire to work as a cooldown mechanism, as many magic or special abilities work in many games, but in this case then just for a gun (but this can then of course be used for any type of game mechanic later). Then I had to draw debug lines and solve some fantastic aiming issues that were occuring because I am not fluent in vector maths. Heres a link to how to do a line **through** two points, not just between them, correctly: https://www.debugcn.com/en/article/63417562.html
So, all of these things are part of the **mechanics** of the shooting system - before I have those down, there isn't much of a point doing anything else related to the feature. If shooting misses the target, if damage isn't being taken, etc, there is nothing to add graphics to, really. 

### Graphics
Well, this is simple enough: the graphical presentation of the feature. For the shooting system, this could be drawing a gun, a muzzle flash and some blood splatter. I could also add like an ammo display or something to that effect in the UI. 

### Audio
As the graphics, just the audio presentation for this feature.

### Polish
This isn't necessary, always. This is a hobby for me, but if you are doing a game for commercial purposes, your features need polish. This is the stage where you let someone test it and give feedback and then you iterate back on the mechanics of the feature over and over again until it is just nailed.

## So where am I with the shooting system regarding this?

### Mechanics
There are so many mechanics-things I would like to implemement - but perhaps they are part of other features? Like shotgun mechanics (basically doing a spreading using several calls to raycasting etc), ammo constraints, reloads, etc. But they might be part of a "gun feature" in the future. For now I wanted to be able to:
* aim
* shoot
* hit enemies
* not shoot through walls
* deal damage
* remove enemies when dead
* not count sensors as hits 

And one could then perhaps say that this is feature complete since all of these things are actually implemented - except I need to change the aim vector thingie so it works with a controller - basically transform the aimVector into unit vector, i.e. *normalizing* it. That's perhaps only five minutes of coding. From watching my video on this, you probably realize it wasn't five minutes of coding, it was more than ten minutes of a grown man failing high school maths. Or are vectors more high level than that? I can only hope. 

Why normalize the aim vector? Because input from controllers give you values from -1.0 to 1.0 in x and y, which can then be used to set the value for that aimvector easily. Totally worth it, got some nice video from it.

### Graphics
Not done at all. What I want:
* Muzzle flash
* Blood splatter
* Death anim (really part of some other feature, might skip this

I'm probably not going to do any of these, actually. I'll move on to the next stage of gameplay in the game, now that we can shoot some!

### Audio
Nothing has been done with audio at all. 
* Sound for shots <- Done now, complete with shell casings dropping on the ground
* Sound for hits on enemies
* Sound for hits on obstacles

So, I will start work on these last things now.

## Stuff that are already "done"
* Character and Enemy Sprites <- done
* Introduce Ashley ECS <- done
* More variables to be able to control aspects of game, such as linear drag etc.  <- "done"
* Better controls, as in mouse aim perhaps? <- done
* Ability to enter vehicles and drive around / shoot from. <- was done, is not relevant *now*


## All notes below are old notes - they might be way off

## Collecting thoughts on controls of more than one entity

### Current Control System
So, I separated control logic into two different systems, VehicleControlSystem and PlayerControlSystem. They both act on entities that have the VehicleControlComponent and PlayerControlComponent, respectively. These map input data from an InputMapper into different values on the components. When the player enters a vehicle, the PlayerControlComponent simply gets a flag set that indicates that he no longer will be walking around.

Also, an important note: when creating Box2D bodies and fixtures, the fixtures are positioned relative to the body's center, not the world. That took me... more time than it should to figure out.

So I am working on making the character entity have the ability to enter a vehicle. This could be really cool for a multiplayer game where one player could steer the vehicle and the other players manning guns etc. Inspirations for this concept is of course Lovers in a Dangerous SpaceTime and HellDivers (but in helldivers the mechanic isn't very good, the tanks are a bit useless, at least the ones I have access to).
So, the problem of taking breaks and not taking notes of what you're doing is that when you return to the code, the code doesn't make sense - because it is only half way done. And here we are.
So... in this first iteration, there is no need for me to be able to control the player entity that is now shooting guns from a tower or something. What I need to figure out is how to control the car.
Now, there is nothing stopping me from controlling BOTH the car and the player character, in principal. There however seem to be code or concepts missing from the code altogheter. Like, the car can drive, but it only drives because we are affecting the player body with forces, no forces are actually applied to the car body itself. So before I make that happen, I figured I had to come up with an idea of how all of this should work. 
If we were using an XBOX360 controller, this would be easy. One stick controls the player, the other the car, etc. Basically, instead of controlling player movement with one stick, this would control vehicle movement. 

How do we implement this for our car - player thing while not having a controller, right now? 
We have to separate aiming and movement. For this, for now, we will use mouse controlling aim and keyboard controlling movement. Then we can "simply" switch so that the movement controls controll the vehicle instead of the player.

But even more "for now", I would like to focus on the controlling of the vehicle, so I might simply remove the player control system when jumping into the vehicle.

1. When player presses J, remove player control system - this will instead be a component thing later
2. Make sure vehicle control system is running. Might need some debugging. 

## En anglais

So, the text below is in swedish, sorry 'bout that. I will switch to english from now on.

What is this then?

It is my repo for what I have, in an entry in my art journal, called "A small space combat game". I have made it public (license coming, I am just lazy, but you can use this if you want to, just fork it) so that anyone looking for a template / inspiration / how-to on libgdx and box2d and Kotlin could find it and perhaps find some use for the code.

I normally do all my "games" with Ashley for entity management etc, but I decided against this for this time. An idea I had this time was that I wanted to work from a very bare-bones approach. I like the ECS approach, I just wanted to not deal with it this time - or add it later, through refactoring.

What I also decided was that I shouldn't overstretch. All my games are always super-ambitious, and super-not-ever-done. So, this time, I am going to **stay** on a particular feature / function until it is done and not deal with stuff that aren't relevant right now, at all. So, in the beginning, there will be no procedural map generation or sprite / texture loading or anything like that. I started with the ship, the thrust and control and then some shooting, making sure each and everyone of those things are 100% done to the current requirements before moving on to the next thing I want to have.

## Current Goal

Make a driveable car.


## Friday, 1st of January 2021

A new year, a new dawn. Nauseous.

So, instead of focusing on the joining of car and player (which I have already succeeded with), I will now experiment with car steering. Seeing as this is nothing but an experiment in general where the focus is mechanics etc, I can do this. The end goal is the same as stated below, and I will now introduce a "current goal"-heading above that will be updated with the notes I make per day. In the future I might break this document up in parts or yadadayada, who cares.

So, current goal is "make a car". This will be a challenge for sure, but we want skidding and chaotic steering, so I will focus on that for a minute. 


## Latest, Tuesday 29th of December

### Goal

Make it possible to enter some kind of vehicle and man either the wheel or a gun in the vehicle. The end goal is of course to be able to play several players in the same vehicle and shoot stuff and such. Cool, huh?

So, how do we do it? Well, using box2d, we can probably link bodies to each other or something - or we might have to destroy the player body in favor of a new body that is located on the vehicle in some kind of jointed way.

So, first step is to create some kind of vehicle. Easy



## Even later,  Tuesday 29th of December

Never quit while behind. No, really, an important thing is this: take a break. I played some Inside with the kids and then I managed to solve the problem when returning. I implemented some display of debug info using the old Scene2d UI from other games. What had happened was that I rationalized away a separate vector for the **direction** of new shots. It worked for a while because we were going around 0,0, but as we move away, the vector used to calculate speed and direction became more and more corrupt and weird.

So, mouse aim now... aims right and works. What's next? Always keep a log.

Anyway, lets plan, work and analyze what we are doing: The next item on the todo-list was the ability to enter vehicles and shoot from them. That would be so friggin' cool it's not even real...

So, how do we do it? Well, we can start by simply making a dumb vehicle that is AI-controlled. It can for sure use Behavior trees or sensors or something, but it needs to drive by itself...

## Tuesday 29th of December

Always quit when not ahead. So, I have managed to implement some kind of mouse aim. There is just two problems:
1. It aims wrong
2. It stops working

So, the aim seems to be a few degrees off - why? Well, it could be any number of things: I use body coordinates a bunch, perhaps I should always use transform coordinates? Or what? Like, the box2d world just works as it should and should kinda be mapped to some other kind of coordinate system. Thing is, I only use the debug renderer which is not necessarily the correct thing to do.

There might also be an issue with the aspect ratio of the viewport. If it does not match the screen, we might have a problem. That is most likely what's wrong...

OK, whatever,  but the aiming suddenly breaking down is NOT cool. That is a gamebreaker right now. 

## Update

This is my devlog.

I am going to try to make some kind of AI-training in all of this. The AI will have inputs, rewards etc. I will try to make some deepq stuff or something.



## Roadmap

### Up next

### TODO:
* Introduce Ashley <- done
* More variables to be able to control aspects of game, such as linear drag etc.  <- "done"
* Better controls, as in mouse aim perhaps? <- done
* Ability to enter vehicles and drive around / shoot from. 

# Later dude
* Draw a sprite for the character
* Textures and sprites?

## Shooting from a platform




## Walking is different from flying 

A flying ship obviously behaves differently from a walking character. <- did some things on this


One idea is that we could try to implement some kind of AI, something that can steer the ship. But Neural Networks are very difficult to implement.

So, we have made this with triangular space-ships, but triangular spaceships are boring. What I want is many different ways of transportation etc - like walking around, top down, and shooting stuff, and then later, riding together on some kind of transportation where one player mans the guns and the other mans the steering, that kinda thing. So we could really use something other than the triangle and also we should probably add, you know, some graphics. But real simple graphics. 

So the road map would now be "I would like to be able to go around and shoot people in this game".

So the todo for this is... we need ashley, right now, people. There is no doubt in my mind. We also need to print out debug info for the entitites. But the point of using Ashley would be to make the game more "refactorable". So let's record a session of me doing that then, perhaps?

### Stuff that have been done

* Ship with Steering and Propulsion <- DONE!
* Shooting <- DONE!
* Collisions with ship <- DONE!

The road map is incomplete and exists offline in an art journal. The inspiration for the game is of course the classic games Thrust, Gravity Force and Turbo Raketti - games where you control a ship with thrust and rotation and can shoot some kind of projectile. In the current implementation, there is gravity and some randomly distributed obstacles - but no map to speak of. My dream game would be a co-op team shooter, or something.

Rather, my dream game is a procedurally generated mega-rpg-world without boredom but with actual live AI people... nah.

This game will also seek inspiration from such great games as Lovers in a Dangerous Spacetime and South Park Tower Defense. So, I want to have co-op, perhaps with two or more players controlling one ship.

Anyways, the roadmap is this:


The issue is that constructing an AI is a cool thing. But making a game is something else - it needs to be you know, done and stuff like that. 


* I am not certain

If I want the multiplayer aspect to work, I should try controller support. But perhaps I want the racing component? Or is it the AI-controlled enemies that I want? Different types of weapons? What should be next?

Next should probably be the feature that requires the least work to make it a "game". So, so far we can fly, we can shoot, but we cannot die, we cannot win. 

So the next feature will be ship collisions.


## Alla framsteg KAN ju noteras här?

Hur ska jag egentligen jobba med någonting alls, egentligen? Man måste anteckna så mycket att man kan hoppa tillbaka in i projektet när som helst i någon framtid. Vart man är, vart man är på väg. Anteckningarna ska väcka ens minnen, ens känslor, och ha information så att man kan tekniskt förstå vad som behöver och kan göras.

Så, det här är TurboRaketti Ultra. Eller?

Jag konstaterade efter att vi spelat Lovers in a Dangerous Space Time att det jag mest av allt vill göra är att utveckla ett gameplay. Jag vill göra narrativa spel också, men jag vill också kunna göra actionspel - med kooperativ multiplayer. Det är the name of the game.

Så, jag tänker inte anteckna någonting om en stor roadmap för hela spelet, utan bara börja med det som behövs och bara anteckna det som behövs.

Så, det jag tänker göra härnäst är... 

# WIP - skepp med styrning och framdrivning

## Skjutning

Jag har råkat påbörja och avsluta den grundläggande skjutningen. Poängen här var litegrann att få till riktningen och hastigheten 100% korrekt. Det gör man genom att stanna vid ett givet koncept och verkligen satsa på att få det rätt, innan man går vidare till nästa del. 

## Styrning

Styrning implementeras med en ShipControl-klass som ska agera mellanhand mellan input-system (tangentbord, handkontroll) och kroppen i världen. Vad vi behöver göra härnäst är att ta emot input från tangentbordet, sen handkontroller, om möjligt i linux, förstås.

Styrning är samma sak som att ta input från något vad som helst och göra om det till kommandon eller liknande som t.ex. gasar eller annat på vårat skepp. Vi vill stödja handkontroller såsom Xbox360-kontrollers (fungerar det på linux? Eller, what what? Fan också.), så vi behöver något slags *abstraktion* för kontrollen. 

Vår kontroll ska vara thrust-rotation-baserad. Så spelaren använder någon kontroll för att "gasa" och en annan kontroll för att rotera skeppet med- eller motsols. 

Det här gör vi på det gamla vanliga sättet. Vi bygger en box2d-värld, vi klistrar på texturer på de objekten, allt blir bra. Men hur fungerar det då...

Oj oj oj,  vad roligt. Vi kan göra en box2d-kropp med leder, per tutorials etc. Superkul ju.
<!--stackedit_data:
eyJoaXN0b3J5IjpbMTM2NjQ1MTI2LDE3MzA5MDI4NzIsLTE0OD
EwMjg2MjYsMTk0MjU4NzAwMSwtNzI4NTU3OTk0LC0yNTg5ODY5
NzksLTEzNjkwOTAwMjEsMTQyNjY0OTg4NSwtMzc3MTg5MzQzLC
0xODM3MjI3MDk4LC0xNDM1ODYzNzIxLDI3OTk0NTU3NSwtMzg0
OTU4NjYwLDExMTkxNDg5OTMsLTE5MDY3NTEzMzMsLTg1MTQ4Mz
U5MCwxNzAzNDYzNDkyLDE0ODA4Nzg4MzEsMTMzMzU3MDc3OSwt
Mzk1NDUzODIzXX0=
-->