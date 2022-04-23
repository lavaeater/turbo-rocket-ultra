# New Facts
Make them from sealed classes.
Make them have multikeys built-in in some smart way.

Make criteria be smarter about their keys.

Here's a way:
Make the key of a fact into an array. This array can be serialized into a dot-string, like so: "Enemy.1.ReachedWayPoint"

The rules we want to be able to construct are of a query-type. So, in fact, we might want to ask.

"ReachedWayPoint" == true? in many different ways. 