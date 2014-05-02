package org.where2pair.core.venue.read

import groovy.transform.Immutable
import groovy.transform.ToString
import org.where2pair.core.venue.common.SimpleTime

@Immutable
@ToString
class DailyOpeningTimes {
    List<OpenPeriod> openPeriods = []

    boolean isOpen(TimeWindow timeWindow) {
        openPeriods.find { it.isOpen(timeWindow) }
    }

    @Immutable
    static class OpenPeriod {
        TimeWindow openTimeWindow

        boolean isOpen(TimeWindow timeWindow) {
            openTimeWindow.supersetOf(timeWindow)
        }

        SimpleTime getOpenFrom() {
            openTimeWindow.from
        }

        SimpleTime getOpenUntil() {
            openTimeWindow.until
        }
    }
}
