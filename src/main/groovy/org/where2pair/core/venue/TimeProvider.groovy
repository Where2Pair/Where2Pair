package org.where2pair.core.venue

import org.joda.time.DateTime
import org.where2pair.core.venue.DailyOpeningTimes.SimpleTime
import org.where2pair.core.venue.DayOfWeek;

class TimeProvider {

    DateTime getCurrentDateTime() {
        new DateTime()
    }

    SimpleTime timeNow() {
        DateTime currentTime = getCurrentDateTime()
        new SimpleTime(currentTime.hourOfDay, currentTime.minuteOfHour)
    }

    DayOfWeek today() {
        DayOfWeek.getDayOfWeek(getCurrentDateTime())
    }
}
