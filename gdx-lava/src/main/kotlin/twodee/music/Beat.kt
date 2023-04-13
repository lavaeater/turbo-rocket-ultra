package twodee.music

import twodee.messaging.IMessage

data class Beat(val timeBars: Float, val sixteenth: Int, val hitTime: Float): IMessage
