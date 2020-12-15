# turbo-rocket-ultra

## Alla framsteg KAN ju noteras här?

Hur ska jag egentligen jobba med någonting alls, egentligen? Man måste anteckna så mycket att man kan hoppa tillbaka in i projektet när som helst i någon framtid. Vart man är, vart man är på väg. Anteckningarna ska väcka ens minnen, ens känslor, och ha information så att man kan tekniskt förstå vad som behöver och kan göras.

Så, det här är TurboRaketti Ultra. Eller?

Jag konstaterade efter att vi spelat Lovers in a Dangerous Space Time att det jag mest av allt vill göra är att utveckla ett gameplay. Jag vill göra narrativa spel också, men jag vill också kunna göra actionspel - med kooperativ multiplayer. Det är the name of the game.

Så, jag tänker inte anteckna någonting om en stor roadmap för hela spelet, utan bara börja med det som behövs och bara anteckna det som behövs.

Så, det jag tänker göra härnäst är... 

# WIP - skepp med styrning och framdrivning

## Styrning

Styrning implementeras med en ShipControl-klass som ska agera mellanhand mellan input-system (tangentbord, handkontroll) och kroppen i världen. Vad vi behöver göra härnäst är att ta emot input från tangentbordet, sen handkontroller, om möjligt i linux, förstås.

Styrning är samma sak som att ta input från något vad som helst och göra om det till kommandon eller liknande som t.ex. gasar eller annat på vårat skepp. Vi vill stödja handkontroller såsom Xbox360-kontrollers (fungerar det på linux? Eller, what what? Fan också.), så vi behöver något slags *abstraktion* för kontrollen. 

Vår kontroll ska vara thrust-rotation-baserad. Så spelaren använder någon kontroll för att "gasa" och en annan kontroll för att rotera skeppet med- eller motsols. 

Det här gör vi på det gamla vanliga sättet. Vi bygger en box2d-värld, vi klistrar på texturer på de objekten, allt blir bra. Men hur fungerar det då...

Oj oj oj,  vad roligt. Vi kan göra en box2d-kropp med leder, per tutorials etc. Superkul ju.
