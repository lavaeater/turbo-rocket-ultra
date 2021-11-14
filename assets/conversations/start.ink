-> start

=== start ===
Ingen minns hur världen blev som den är nu.
Varje kväll brukade vår pappa berätta de sagor hans pappa berättat för honom. 
Mamma, jag och syster skrattade alltid när han dansade katastrofdansen framför elden.
Vi letade prylar från tiden före katastrofdansen som kunde hjälpa oss eller för att byta mot mat.
Utanför vår bosättning lurade faror vart man än gick. 
Monster bor i vildmarken och äter allt de stöter på.
Ute på de öde slätterna drar plundrare fram på jakt efter fria människor att ta som slavar.
En dag tog kom plundrarna till vår bosättning. Motstånd var meningslöst. 
De tog allt vi ägde och vi blev slavar.
Mamma och pappa togs ifrån oss.
Jag och min syster växte upp bland plundrarna. 
Vi skickades in på de farligaste platserna för att leta prylar.
En dag hittade vi en robot från den gamla tiden när vi var på pryljakt. 
Min syster tryckte på en knapp på roboten.
-> robot_start

=== robot_start ===
Bzz... uppstart. Kalibrerar. Behöver röstinput för kalibrering. Säg "Lyd mig"
*   [Lyd mig?] -> robot
*   [Va, vadå?] -> robot_intro

=== robot_intro ===
    För att jag ska kunna lyda kommando behöver jag kalibreras till en röst. Vill ni att jag ska lyda er?
*   [Ja?]
    Säg då "Lyd mig" så är kaliberingen klar -> robot_intro
*   [Lyd mig] -> robot
-> robot
*   [Vad menar du med lyda?] -> robot_purpose

=== robot ===
Kalibrering klar.
Ljudet av steg hördes från korridoren utanför rummet. Arga röster från våra plågoandar hördes.
-> scavenger_dies

=== robot_purpose ===
En robot måste lyda en människa. Jag är en nannybot 3000, designad för en orolig tid där föräldrar arbetar mycket och behöver en kapabel barnvakt snedstreck läromästare till sin avkomma. Jag kan leka 400 pedagogiska lekar som samtidigt lär ut ledarskapsfärdigheter för framtidens ledare.
*   [Lyd mig] -> robot
*   [Framtidens ledare?]
Ett barn är ett oskrivet blad. Med de rätta förebilderna och en uppfostran baserad på lek, träning och ansvar kan framtiden göras bättre och bättre. Omvärlden är samtidigt en farlig plats. Nannybot 3000 har alla nödvändiga accessoarer för att skydda barnen från faror.
-> scavenger_arrives 

=== scavenger_arrives ===
Ljudet av steg hördes från korridoren utanför rummet. Arga röster från våra plågoandar hördes.
*   [Lyd mig] -> robot
*   [Kan du rädda oss från plundrarna?]
    Kalibreringen är ännu inte klar -> robot_intro
    



-> scavenger_dies

=== scavenger_dies === 
Plundrarna kom runt hörnet. Samtidigt for roboten upp och innan vi hann säga något hade den oskadliggjort dem.
-> story_begins

=== story_begins ===
Hot oskadliggjort. Datum- och tidkalibrering behövs. Enligt den interna klockan är datumet... bzz.. intern klocka trasig.
Datum och tid kan inte avgöras automatiskt. Kalibrering mot stjärnhimmel kan göras en molnfri natt.
Vi måste få er hem för mellanmål och en okulär besikning säger mig att det är dags att boka tvätt-tid. Ge mig er adress så bokar jag en Über åt oss hem. 

Vi förstod inte mycket av robotens prat. Vi förstod att den var från tiden innan katastrofen. Vi stal förråd så vi kunde klara oss en stund och gjorde ett läger utom synhåll för plundrarnas läger. 

-> END