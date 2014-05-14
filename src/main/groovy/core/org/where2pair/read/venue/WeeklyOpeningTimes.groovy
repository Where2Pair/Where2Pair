package org.where2pair.read.venue

import groovy.transform.Immutable
import groovy.transform.ToString
import org.where2pair.read.venue.find.OpenTimesCriteria

@Immutable
@ToString
class WeeklyOpeningTimes {

    private Map<DayOfWeek, DailyOpeningTimes> weeklyOpeningTimes

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
