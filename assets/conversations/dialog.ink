VAR CurrentNpcName = "Kalle"

-> start

=== start ===
Hallå där, främling, jag heter {CurrentNpcName}

*   [Hallå där, själv] -> nice_continue
*   [Jag pratar helst inte med drivare] -> rude_continue
*   [Aaah, var kom du ifrån?] -> startled_continue

=== startled_continue ===
Oj, förlåt om jag överraskade dig.
På väg någon särskild stans? -> going_somewhere

=== nice_continue
På väg någon särskild stans? -> going_somewhere


=== rude_continue ===
Du måste ha träffat fel sorts drivare tidigare.
Det kan jag förstås inte göra nåt åt. 
Är du på väg någon särskild stans? -> going_somewhere


=== going_somewhere ===
*   [Arrowhead, för handel] -> trade_talk
*   [Jag vill helst inte säga] -> nice_privacy
*   [Det har du inte med att göra] -> rude_privacy

=== trade_talk ===
Arrowhead, säger du?
Där handlas det mest med slavar, så vitt jag vet.
Vad skaffar väl en trevlig person som du där?
* [Mina föräldrar blev tagna av plundrare] -> trade_info
* [Jag vill helst inte säga] -> nice_privacy
* [Det har du inte med att göra] -> rude_privacy

=== nice_privacy ===
Aha.
Det kan vara klokt att hålla en del saker för sig själv.
Han en säker fortsatt resa -> END 

=== rude_privacy ===
Åhå. Ja, du får göra som du vill här i världen. -> END

=== trade_info ===
En sorglig historia. Många familjer splittras av plundrargängen.
När du kommer till Arrowhead, 
sök upp Wolk Zonfinnaren, 
han vet allt som är värt att veta i Arrowhead.
Kanske vet han något om dina föräldrar? 
Lycka till!
* [Tack] -> END