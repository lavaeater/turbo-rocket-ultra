## Text Crawl
This could be combined with the transition concept. Here's what I want to do:
- [x] Show a dialog or crawl with paused game before level starts (what to do etc)
- [x] A working pause mode
- [x] Pause game when level is done and show end-of-level text

More thoughts: there is more to this than meets the eye. It comes down to a few very important points, namely: how do we do these things? How do we control the flow of the game, the exchange of information in the game and where is this information stored. Right now, it is something of a "hot mess", but that is to be expected. This particular feature shines a light on a deficiency of the game and changes need to be made.

For instance, previously we worked under the assumption that pausing would be its own screen, that's not the case right now, I think I like the way it is now much better. We just need to slow down and think about it for a minute or two, away from the code. 

It's like there are all these different ways of doing things. Like, I can actually control the game using my *Story* concept, which allows me to create rules and apply consequences. I have done some alterations to that because, well, it only cared about one story before and that might be wrong, I don't know...
