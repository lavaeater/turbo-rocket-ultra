# There's Levels to This
What's next?

We add more features to the map editor, one by one, until we have a simple way of making maps. Then we can add automation to it, which is just very very cool. 

We could also work on varying the sections, to make them not look exactly like each other.

One thing that could be cool is zoom in/out for the camera.

We have levels, we have stories, we have rules, we have facts.
To make this super extra hard-on fun, we would need a simple serializer functionality like the one we have for maps but for rules and such. It shouldn't be too hard, we just needs tons and tons of code for it.

And we also need to load maps (but I think it is basically done).

What we had in the other thingie was standardized consequences - We could work that into this as well, which would be needed to be able to write it 100% in a text editor.

But facts are not saved, not rules either, and I'm not entirely entirely sure about the "levels as pictures"-stuff either.

We should probably move ALL of this into a mapdefinition that can be serialized - and therefore loaded and unloaded, edited in a regular text editor. It **would** be cool if we could make the actual map into a simple textformat thing.
## Example map def
Space character is empty? 
Or if it is empty, replace it with a star or dot or something?
~~~
*s*************************
***************************
***************************
***************************
***************************
***************************
***************************
***************************
***************************
***************************
***************************
~~~


```
ecwee code boc
```

Yes
