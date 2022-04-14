## Thrown Item Trajectory
Whaat? Not for now, at least. I have Box2D working nicely and I'm very happy with performance etc. What I will do now is try to instead work on the 2D / 3D projection of the game - partly because I think that the look and feel of a game is completely central to playability. So, I have thrown items, well, they should fall to the ground. implementing that tiny, tiny feature would make the game look insanely good. Or at least better

So, what do we need to do?
- [x] Introduce height into transform
- [x] Feed transform back into the y-coordinate of bodies by using... forces? Impulses? Something like that.
- [x] Profit