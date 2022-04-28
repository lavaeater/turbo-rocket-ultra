# New Fact System
Whatwhat, why?

I want to rewrite it to make the system easier to understand, easier to use, easier to expand.

Facts should be basically what they are, but criteria should be more versatile. 

First off, make facts into sealed classes, it is the bomb. They never have type erasure, for instance.
## New Facts
Make them from sealed classes.
Make them have multikeys built-in in some smart way.

Make criteria be smarter about their keys.

Here's a way:
Make the key of a fact into an array. This array can be serialized into a dot-string, like so: "Enemy.1.ReachedWayPoint"

The rules we want to be able to construct are of a query-type. So, in fact, we might want to ask.

"ReachedWayPoint" == true? in many different ways. 