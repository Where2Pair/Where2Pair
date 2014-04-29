package org.where2pair.core.venue.read

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.where2pair.core.venue.common.SimpleTime

@EqualsAndHashCode
@ToString
class OpenTimesCriteria {
    SimpleTime openFrom
    SimpleTime openUntil
    DayOfWeek dayOfWeek

    TimeWindow getTimeRange() {
        new TimeWindow(openFrom, openUntil)
    }
}
