# Easter Day

I have a track, an idea.

I must create a Concept Screen that actually uses some of the systems that are in place already in the actual game. After that we then add the stuff I have figured out.

We should have something called an anchor, which is the right shoulder of the character. From that point, not the center of the sprite or character, we can imagine a line aiming towards the mouse pointer. The anchor point will **not** move dynamically with the rotation of the mouse or joystick, because it will be anchored to a static position on the sprite depending on the direction the sprite is facing - we only have four directions of the sprite - for the time being.

Using this sight line, we can draw a hand and an arm that is somewhere on that line - giving us basic IK for the left arm and hand of the character. That will simply be a line from the left shoulder, the secondary anchor point, to a point on the "rifle" that the character is aiming. 

For a pistol type weapon, the secondary arm is unnecessary.

We can use the same technique to do baseball bat swings, throwing of molotovs etc.

So basically we want to make the arms dynamic. This might require us to scrap the sprites we currently use. Perhaps we can extract parts of the sprites, remove the arms from them etc, to construct these dynamically animated types of characters.

#gamedev 
