package org.where2pair.core.venue.read

import groovy.transform.AutoClone
import groovy.transform.EqualsAndHashCode
import groovy.transform.Immutable
import groovy.transform.ToString
import org.where2pair.core.venue.common.SimpleTime

@EqualsAndHashCode
@ToString
@AutoClone
class DailyOpeningTimes {
    List<OpenPeriod> openPeriods = []

    boolean isOpen(TimeWindow timeWindow) {
        openPeriods.find { it.isOpen(timeWindow) }
    }

    @Immutable
    static class OpenPeriod {
        TimeWindow openTimeWindow

        boolean isOpen(TimeWindow timeWindow) {
            openTimeWindow.intersects(timeWindow)
        }

        SimpleTime getOpenFrom() {
            openTimeWindow.from
        }

        SimpleTime getOpenUntil() {
            openTimeWindow.until
        }
    }
}
