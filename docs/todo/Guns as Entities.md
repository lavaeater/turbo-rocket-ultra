## Guns as Entities
Instead of having a bunch of *extra sprites*, which is just code for "extra work" to be honest, we could really reduce everything to be **"one entity, one sprite"**. This would mean that the gun is being rendered as a separate sprite, not an extra sprite, and that we set its properties (rotations etc), completely separately from the actual player sprite. 