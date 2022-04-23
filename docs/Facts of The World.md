# Facts of the World
Facts of the world is inspired by a talk at GDC where a developer working on Left Behind (the zombie game from Valve, can't remember the actual name), where he described how they made audio cues work. I will try to dig up a link to it and add it to this doc.
This is simply a stupid database of keys and values. The values can be of "any" type, by jumping through some hoops. 
This makes it possible for us to state *Facts of the World* - get it? 
A fact could be how many enemies have been killed, what level we are playing right now, if all the bosses have died, etc etc.
When a fact is updated, a message is sent, the "FactUpdated" message or whatever it is called. This in turn is subscribed to by the [[StoryManager]] - whenever a fact is updated, it will be notified and can in turn check the stories.

Stories are what connects us to that GDC talk. They used all these facts to trigger audio and dialogue, whereas I use these facts to trigger stories and effects of stories. 

This is done through Stories. This section could become its own note later. Anyhoo, a story is simply a collection of criteria. If the criteria are met, some consequence is applied.

Using anonymous methods, builders, Kotlin DSL and such, it is quite simple to build something that depends on any number of facts to then create any number of consequences. 

This enables us to build logic that depends on a disconnected sum of the game state without knowing anything about **how that state is created**. Facts are set by any number of systems, bits of code etc, and the story system reacts to these changes as they happen.

We could imagine changing the story system so that it checks all the stories periodically in the game loop but only does so if a flag is set to make it check without blocking the main loop.

An issue that I can see here is that the code might be slow since it accesses preferences like, all the time. 
