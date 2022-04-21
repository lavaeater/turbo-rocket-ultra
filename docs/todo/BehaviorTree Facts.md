# Behavior Tree Facts
I want to add a mechanism where the different components and behavior tree things set different facts in the FactsOfTheWorld-mechanism. This would mean that we might set or unset counters for enemies entering rooms, detecting enemies, changing behavior trees and stuff like that.
This can then in turn make it possible to have a sound system that reacts to different facts being set - thus triggering sounds, events whatever, using Stories.

This needs a case. What is the case, really?

Can different enemies have their own little worlds of facts?

What we want to do is set facts with dots, or using some kind of hierarchy. That would mean, basically, a fudging graph. But graphs are chaotic and disordered, a tree is logical and structured. Always keep it simple unless you need to.

