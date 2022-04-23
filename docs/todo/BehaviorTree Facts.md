# Behavior Tree Facts
I want to add a mechanism where the different components and behavior tree things set different facts in the FactsOfTheWorld-mechanism. This would mean that we might set or unset counters for enemies entering rooms, detecting enemies, changing behavior trees and stuff like that.
This can then in turn make it possible to have a sound system that reacts to different facts being set - thus triggering sounds, events whatever, using Stories.

This needs a case. What is the case, really?

Can different enemies have their own little worlds of facts?

What we want to do is set facts with dots, or using some kind of hierarchy. That would mean, basically, a fudging graph. But graphs are chaotic and disordered, a tree is logical and structured. Always keep it simple unless you need to.

Dots and schmots. These can be viewed as completely separate, set however we wish. So start by adding the ability to set facts inside the behavior tree implementation. Why? We'll see.
What is it that I want to do with this idea? There is something there, I just cannot figure it out, completely.

The state of a character is handled  by the components - so the facts of the world are just that, facts of the world. Has an enemy met the player, chased the player, hit the player? Store in components, perhaps? 
Or in facts of the world, but why? Because then those facts become accessible as data for the story system, which can be used to play sounds, show stuff (toasts), whatever. Anything is possible.

- [x] Create Unique Id for every enemy
- [x] Make factsOfTheWorld have a callBack for updates
- [x] In this callback, send message FactUpdated
- [x] Make StoryManager listen to this message
- [x] On Fact Updates, check all stories
- [x] Remove checkStories from render loop

