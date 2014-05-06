package org.where2pair.core.venue.read

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode
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
