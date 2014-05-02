package org.where2pair.core.venue.read

import groovy.transform.Immutable
import groovy.transform.ToString

@Immutable
@ToString
class WeeklyOpeningTimes {

    Map<DayOfWeek, DailyOpeningTimes> weeklyOpeningTimes

    boolean isOpen(OpenTimesCriteria openTimesCriteria) {
        weeklyOpeningTimes[openTimesCriteria.dayOfWeek].isOpen(openTimesCriteria.timeRange)
    }

    def getAt(key) {
        weeklyOpeningTimes[key]
    }

    @Override
    void each(Closure c) {
        weeklyOpeningTimes.each(c)
    }

}
