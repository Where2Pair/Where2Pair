package org.where2pair.read.venue

import groovy.transform.Immutable
import groovy.transform.ToString

@Immutable
@ToString
class WeeklyOpeningTimes {
    private Map<DayOfWeek, DailyOpeningTimes> weeklyOpeningTimes

    def getAt(key) {
        weeklyOpeningTimes[key]
    }

    void each(Closure c) {
        weeklyOpeningTimes.each(c)
    }

    void any(Closure c) {
        weeklyOpeningTimes.values().any(c)
    }
}

