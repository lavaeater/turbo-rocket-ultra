## Guns as Entities
Instead of having a bunch of *extra sprites*, which is just code for "extra work" to be honest, we could really reduce everything to be **"one entity, one sprite"**. This would mean that the gun is being rendered as a separate sprite, not an extra sprite, and that we set its properties (rotations etc), completely separately from the actual player sprite. 

Either that or create some kind of local scenegraph for each and every entity that has sprites.

Sort of like extra sprites but with more intelligence. 


But doing a scene-graph goes against the strength of ECS's, so instead I think the idea of linked entities, where related entities get their properties set in systems that correlate them somehow. This is how we did it with the build system, I think it could work well with guns as well.

Test the entity-track first.