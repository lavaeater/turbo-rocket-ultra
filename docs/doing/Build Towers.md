# Build Towers
Well, we basically have towers, we basically can build them, this is about making that feature happen and having at least two or three types of towers with nice sprites that we can build and that do different things.
## New thoughts on coupled entities
It's not entities that are coupled. It is components that describe state. So how does that work? Can we have a component that contains the state of some other entity, to be able to modify said state later? 


### What to do, the plan
Intents. When the player presses "build", he intends to build something. So we had a component to that entity called IntendsToBuildComponent. 
When an entity has that component, a system will handle that... no, we will have generic IntentComponent, called 
`IntendsTo<Build>,

that way we can have a sealed class...

That in turn makes the intentsystem create the necessary entities and components to display build stuff on the screen. Test this track.

The idea of having *one* system that handles intents might have serious limitations, but I'll go with it for now. It's like, this could be "simple intents" or something to that effect, something that helps me set stuff up for more advanced things or something. 


First, we do inventory of what we actually have.

## What we have

To do this, we need to run the game.


### Things to do
#### Render pipeline
I have previously done stuff that sort of mean that the game renders things in several places. This is not ideal. The ideal scenario is rendering EVERYTHING in the single rendering system. This is because we want to use frame buffers for lights and FX, and that means that stuff have to be rendered in one loop to be caught in the frame buffer stuff, I assume. 

This simply means that we have to "rethink" some rendering choices. Take the build system. An easy way to handle the build stuff is to simply watch for the build flag. If the build flag is true, we add an entity that has a transform component and a sprite attached to it. If the flag reverts to false, we simply remove this entity. 

This will mean that this particular thing will be rendered in the regular render loop.
The same actuall goes for guns, weapons and such... maybe. We'll see about that. But it could mean that we have entities with like a transform and rotation and stuff that is in fact separate from the actual control of the player entity and that would mean that we could have additional systems that *only* manage weapons positions and stuff like that.

But we will start with simply adding the build component. It has to be identifiable.

Ah, simple. We ADD a component to the player entity, called build mode, that has a reference to the entity in question. 

So, what have we done so far? 

Well, we have added a new entity and that entity will simply hold a position and a sprite. It should work like a charm!

Or, we'll at least see.

