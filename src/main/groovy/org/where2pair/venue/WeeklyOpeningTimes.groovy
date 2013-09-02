package org.where2pair.venue

import groovy.transform.AutoClone
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.where2pair.venue.find.OpenTimesCriteria

@EqualsAndHashCode
@ToString
@AutoClone
class WeeklyOpeningTimes {

    Map weeklyOpeningTimes

    boolean isOpen(OpenTimesCriteria openTimesCriteria) {
        weeklyOpeningTimes[openTimesCriteria.dayOfWeek]
                .isOpen(openTimesCriteria.openFrom, openTimesCriteria.openUntil)
    }

    def getAt(key) {
        weeklyOpeningTimes[key]
    }

    @Override
    void each(Closure c) {
        weeklyOpeningTimes.each(c)
    }

}
