package org.where2pair.infra.venue.read

import org.joda.time.DateTime
import org.where2pair.core.venue.common.SimpleTime
import org.where2pair.core.venue.read.DayOfWeek

class TimeProvider {

    SimpleTime timeNow() {
        DateTime currentTime = getCurrentDateTime()
        new SimpleTime(currentTime.hourOfDay, currentTime.minuteOfHour)
    }

    DayOfWeek today() {
        DayOfWeek.getDayOfWeek(getCurrentDateTime())
    }

    private DateTime getCurrentDateTime() {
        new DateTime()
    }
}
