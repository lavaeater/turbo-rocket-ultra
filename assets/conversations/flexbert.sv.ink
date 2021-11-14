VAR met_before = false
VAR player_name = "Hulk Hogan"
VAR flexbert_knows_name = false
VAR reaction_score = 0
VAR step_of_story = 0
{ not met_before: 
    -> first_meeting 
- else: -> meet_again
}

=== first_meeting
... Hej?
Vem kan väl du vara, om jag får fråga?
* [Jag heter {player_name}] -> gives_name
* [Jag vill veta vem du är först] -> hides_name

=== gives_name
~reaction_score = reaction_score + 1
~flexbert_knows_name = true
Trevligt att råkas {player_name}
-> flexbert_presents

=== hides_name
~reaction_score = reaction_score - 1
Aha. 
Jag tror ju att vår värld först kan bli som 
den en gång var när vi alla beter oss som de
gjorde då - artigt.
Nåväl -> flexbert_presents
=== meet_again
Så vi möts igen 
{ step_of_story == 1:
Hittade du någonsin Greger Silvertand?
}
* [Ja]
* [Nej]
- Men, { reaction_score > 0: 
jag önskar dig all lycka på din färd.
- else:
 låt mig vara ifred bara.
 }
{ step_of_story == 2:
Hur går det?
}
->END

=== flexbert_presents
Jag heter Flexbert
{ not flexbert_knows_name:
    -> flexbert_asks_again
} -> next_step_in_story

=== flexbert_asks_again
Nu har jag givit dig det du ville ha.
Nu behöver jag veta ditt namn.
* [Jag är {player_name}] -> next_step_in_story
* [Jag vill inte ändå] -> hides_again

=== hides_again
Då är vår konversation slut.-> END

=== next_step_in_story
Att överleva i ödemarken är svårt. 
Du är otroligt ung för att ge dig på det själv.
* [Jag har sökt dig för att be dig om hjälp]
    Jag förstår
* [Jag är rädd nästan jämt]
    Rädsla är en del av livet i ödemarken. 
    Det är svårt, för oss alla, här ute.
    ** [Men överallt lurar farorna]
    ** [Jag är bara så ensam]
    ** [På nätterna hör jag alltid Ljudet]
    --    Faror är på riktigt
    men rädsla är bara i dina tankar
    Kom alltid i håg det - faror måste du akta dig för
    och hantera, men dina rädslor, de är du mästare över!

- Vad kan jag göra för dig?
* [Mina föräldrar är försvunna]
- Så otroligt vanligt i vår tid
Hur försvann de?

* [Slavare tog dem]
* [De och min syster togs av slavare]
* [De kom en dag och brände vår by och tog alla]

- Mm. 
Att hitta någon som blivit tagen av slavare idag är svårt.
Du måste söka dig till Bytarhålet. 
Där handlas det med slavar, cyklar, mat
och föremål.
Plocka med dig allt skrot du kan hitta på vägen
och leta sedan upp Stormack, ett ställe med nästan 
ätbar mat.
Fråga runt.
Den du söker heter Greger Silvertand. 
Han lever på att veta saker - 
vetande han sen kan säja dyrt.
Hälsa från mig så får du kanske ett bättre pris.
~ step_of_story = 1

* [Tack!]
* [Det ska jag.]
* [Önska mig lycka till.]
->END