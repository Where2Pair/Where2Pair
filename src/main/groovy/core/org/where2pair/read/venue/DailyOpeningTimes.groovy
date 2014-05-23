package org.where2pair.read.venue

import groovy.transform.Immutable
import groovy.transform.ToString
import org.where2pair.common.venue.SimpleTime

@Immutable
@ToString
class DailyOpeningTimes {
    private List<OpenPeriod> openPeriods = []

    boolean isOpenBetween(TimeWindow timeWindow) {
        openPeriods.find { it.isOpenBetween(timeWindow) }
    }

    boolean isOpenAt(SimpleTime time) {
        openPeriods.find { it.isOpenAt(time) }
    }

    boolean isOpen() {
        openPeriods.size() > 0
    }

    @Immutable
    static class OpenPeriod {
        private TimeWindow openTimeWindow

        boolean isOpenBetween(TimeWindow timeWindow) {
            openTimeWindow.supersetOf(timeWindow)
        }

        boolean isOpenAt(SimpleTime time) {
            openTimeWindow.contains(time)
        }

        SimpleTime getOpenFrom() {
            openTimeWindow.from
        }

        SimpleTime getOpenUntil() {
            openTimeWindow.until
        }
    }
}

