I think I see now where I went wrong.

I did some things right, namely enabling the tasks to be Component-based, which was actually kinda cool. 

However, some things have become more complicated due to this, at least it feels this way. 

So, good things are: making the decorators that check for components. This means we can easily just add components to the entity and thus make it do other things. The bad thing is that "amble" isn't a singular task that the agent can be doing, it is, in reality, an entire behavior and should be modeled that way.

This is not a bad thing, this is a good thing. The way I set up the BTs gives us some ability to move forward easily.

But what I want to do now is move code back inside the nodes. We want the nodes to contain the code they're supposed to have, and not use them for Behavior as we have done previously.