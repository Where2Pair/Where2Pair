package org.where2pair.core.venue.read

import groovy.transform.Immutable
import groovy.transform.ToString
import org.where2pair.core.venue.common.SimpleTime

@Immutable
@ToString
class OpenTimesCriteria {
    SimpleTime openFrom
    SimpleTime openUntil
    DayOfWeek dayOfWeek

    static OpenTimesCriteria openAnyTime() {
        null
    }

    TimeWindow getTimeRange() {
        new TimeWindow(openFrom, openUntil)
    }
}
