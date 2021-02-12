# turbo-rocket-ultra

## Collecting thoughts on controls of more than one entity
So I am working on making the character entity have the ability to enter a vehicle. This could be really cool for a multiplayer game where one player could steer the vehicle and the other players manning guns etc. Inspirations for this concept is of course Lovers in a Dangerous SpaceTime and HellDivers (but in helldivers the mechanic isn't very good, the tanks are a bit useless, at least the ones I have access to).
So, the problem of taking breaks and not taking notes of what you're doing is that when you return to the code, the code doesn't make sense - because it is only half way done. And here we are.
So... in this first iteration, there is no need for me to be able to control the player entity that is now shooting guns from a tower or something. What I need to figure out is how to control the car.
Now, there is nothing stopping me from controlling BOTH the car and the player character, in principal. There however seem to be code or concepts missing from the code altogheter. Like, the car can drive, but it only drives because we are affecting the player body with forces, no forces are actually applied to the car body itself. So before I make that happen, I figured I had to come up with an idea of how all of this should work. 
If we were using an XBOX360 controller, this would be easy. One stick controls the player, the other the car, etc. Basically, instead of controlling player movement with one stick, this would control vehicle movement. 

How do we implement this for our car - player thing while not having a controller, right now? 

## En anglais

So, the text below is in swedish, sorry 'bout that. I will switch to english from now on.

What is this then?

It is my repo for what I have, in an entry in my art journal, called "A small space combat game". I have made it public (license coming, I am just lazy, but you can use this if you want to, just fork it) so that anyone looking for a template / inspiration / how-to on libgdx and box2d and Kotlin could find it and perhaps find some use for the code.

I normally do all my "games" with Ashley for entity management etc, but I decided against this for this time. An idea I had this time was that I wanted to work from a very bare-bones approach. I like the ECS approach, I just wanted to not deal with it this time - or add it later, through refactoring.

What I also decided was that I shouldn't overstretch. All my games are always super-ambitious, and super-not-ever-done. So, this time, I am going to **stay** on a particular feature / function until it is done and not deal with stuff that aren't relevant right now, at all. So, in the beginning, there will be no procedural map generation or sprite / texture loading or anything like that. I started with the ship, the thrust and control and then some shooting, making sure each and everyone of those things are 100% done to the current requirements before moving on to the next thing I want to have.

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
eyJoaXN0b3J5IjpbLTEwNzk4OTk0Myw2OTMyMjI4NzZdfQ==
-->