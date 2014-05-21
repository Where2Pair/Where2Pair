package org.where2pair.read.venue

import org.joda.time.DateTime
import org.where2pair.common.venue.SimpleTime

class TimeProvider {

    SimpleTime timeNow() {
        DateTime currentTime = getCurrentDateTime()
        new SimpleTime(currentTime.hourOfDay, currentTime.minuteOfHour)
    }

    DayOfWeek today() {
        DayOfWeek.getDayOfWeek(getCurrentDateTime())
    }

    private static DateTime getCurrentDateTime() {
        new DateTime()
    }
}
