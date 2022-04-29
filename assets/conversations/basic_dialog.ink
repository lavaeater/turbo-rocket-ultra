VAR met_before = false
VAR first_encounter = true
VAR c_name = "Ralph Macchio"
VAR guessed_right = false
VAR name_guess_0 = "Ralph Macchio"
VAR name_guess_1 = "Elijah Woods"
VAR name_guess_2 = "Ivan The Terrible"
-> first_meeting
=== first_meeting
Hello! 
{ not met_before :
{~Nice to meet you.|We haven't met before, I believe?|A friendly face, a pleasant surprise! }
- else:{~I recognize you! |We met before, I think? |We meet again, I believe? }
}
{~There's something about this place...|I just have such a hard time remembering things...}

{ first_encounter :
Everyone here has forgotten their name. 
{~Me too.|It's quite alarming.}
}
{ met_before:
 {~I still can't remember my name - do you? |You wouldn't happen know my name, would you? }
 - else: {~Do you know my name? |Would you wager a guess on my name?}
} -> guess_some_names

=== guess_some_names
* [Sure! Your name is {name_guess_0}, isn't it?] -> name_guess (name_guess_0)
* [Surely you must be {name_guess_1}?] -> name_guess (name_guess_1)
* [{name_guess_2} I presume?] -> name_guess (name_guess_2)
* [I haven't got the slightest idea, I'm afraid...] -> no_guess

=== correct_name_guess
~guessed_right = true
{~Why yes, yes, that's it|Aah, how could you guess right so quickly?|What a relief, that's it!}!
-> END

=== wrong_guess
{~No, no, that's not it, I'm sure.|Rings a bell, but not quite the right ring to it.|A nice name indeed, but not mine, I fear.}
Care to guess again?
-> guess_some_names
=== name_guess(guessed_name)
{
    - guessed_name == c_name : -> correct_name_guess
    - else : -> wrong_guess
}
=== no_guess
{~That's alright, it'll be OK. |Not to worry, it'll come back to me. |Go on, wander on, I'll make it out of here.}
->END