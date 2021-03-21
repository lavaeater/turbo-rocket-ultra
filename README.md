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
* Multiplayer
* Controller support
* Twin Stick shooting
* Vehicles
* Towers
* Enemies
* Different weapons to shoot with

## Doing: Shooting System
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

### P




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
<!--stackedit_data:
eyJoaXN0b3J5IjpbMjExMjE4MzQ0NiwxOTQwMjY0NzcsLTEwNz
E0MDk1ODQsNzQxMDA3NTMxLDY5MzIyMjg3Nl19
-->