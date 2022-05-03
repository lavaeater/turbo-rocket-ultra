# Serializing Rules, etc
The code is a bit custom when it comes to all of this, we might work on this later when we figure out a good concept for how to handle it.

The concept is simple - when setting a piece of code as the consequence, we could instead set that code, through the builder, into some sort of globally static object containing a list of functions with keys. These keys could then be what is actually stored inside the rule / criterion object, making them serializable. Since we use lazy properties for all external dependencies, that would solve that problem. 