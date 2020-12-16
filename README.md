# turbo-rocket-ultra

## En anglais

So, the text below is in swedish, sorry 'bout that. I will switch to english from now on.

What is this then?

It is my repo for what I have, in an entry in my art journal, called "A small space combat game". I have made it public (license coming, I am just lazy, but you can use this if you want to, just fork it) so that anyone looking for a template / inspiration / how-to on libgdx and box2d and Kotlin could find it and perhaps find some use for the code.

I normally do all my "games" with Ashley for entity management etc, but I decided against this for this time. An idea I had this time was that I wanted to work from a very bare-bones approach. I like the ECS approach, I just wanted to not deal with it this time - or add it later, through refactoring.

What I also decided was that I shouldn't overstretch. All my games are always super-ambitious, and super-not-ever-done. So, this time, I am going to **stay** on a particular feature / function until it is done and not deal with stuff that aren't relevant right now, at all. So, in the beginning, there will be no procedural map generation or sprite / texture loading or anything like that. I started with the ship, the thrust and control and then some shooting, making sure each and everyone of those things are 100% done to the current requirements before moving on to the next thing I want to have.

## Update

This is my devlog.

I am going to try to make some kind of AI-training in all of this. The AI will have inputs, rewards etc. I will try to make some deepq stuff or something.



## Roadmap

The road map is incomplete and exists offline in an art journal. The inspiration for the game is of course the classic games Thrust, Gravity Force and Turbo Raketti - games where you control a ship with thrust and rotation and can shoot some kind of projectile. In the current implementation, there is gravity and some randomly distributed obstacles - but no map to speak of. My dream game would be a co-op team shooter, or something.

Rather, my dream game is a procedurally generated mega-rpg-world without boredom but with actual live AI people... nah.

This game will also seek inspiration from such great games as Lovers in a Dangerous Spacetime and South Park Tower Defense. So, I want to have co-op, perhaps with two or more players controlling one ship.

Anyways, the roadmap is this:

* Ship with Steering and Propulsion <- DONE!
* Shooting <- DONE!
* I am not certain

If I want the multiplayer aspect to work, I should try controller support. But perhaps I want the racing component? Or is it the AI-controlled enemies that I want? Different types of weapons? What should be next?

Next should probably be the feature that requires the least work to make it a "game". So, so far we can fly, we can shoot, but we cannot die, we cannot win. 

So the next feature will be ship collisions.

* Collisions with ship <- DONE!

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
