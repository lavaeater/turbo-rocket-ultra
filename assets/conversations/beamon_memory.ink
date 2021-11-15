VAR met_before = false
VAR c_name = "Petter Knöös"
VAR guessed_right = false

VAR name_guess_0 = "Petter Knöös"
VAR name_guess_1 = "Frank Artschwager"
VAR name_guess_2 = "Ellika Skoogh"

-> first_meeting

=== first_meeting
Hej! { not met_before :
{~Trevligt att råkas | Vi har inte setts förut, tror jag? | Ah, en kollega, hur står det till? } 
- else: {~Det var länge sen! | Kul att se dig igen | Hur har du haft det? }
}

{ met_before: 
 {~Minns du mitt namn? | Kommer du ihåg vad jag heter? }
 - else: {~Vet du vad jag heter? | Kan du gissa mitt namn?}
}

* [Hejsan du heter {name_guess_0} va?] -> name_guess (name_guess_0)
* [Visst är det du som är {name_guess_1}?] -> name_guess (name_guess_1)
* [{name_guess_2} I presume?] -> name_guess (name_guess_2)

=== correct_name_guess
Ah, du visste mitt namn! -> END

=== wrong_guess
Nej, då minns du nog fel! -> first_meeting


=== name_guess(guessed_name)
{
    - guessed_name == c_name : -> correct_name_guess
    - else : -> wrong_guess
}

{ met_before } -> meet_again

=== meet_again
Hej! {~Det var länge sen | Vi har inte setts förut, tror jag? | Ah, en kollega, hur står det till? }
* [Hejsan du heter {name_guess_0} va?] -> name_guess (name_guess_0)
* [Visst är det du som är {name_guess_1}?] -> name_guess (name_guess_1)
* [{name_guess_2} I presume?] -> name_guess (name_guess_2)


->END