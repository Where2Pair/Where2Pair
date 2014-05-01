package org.where2pair.core.venue.read

import groovy.transform.Immutable
import groovy.transform.ToString
import org.where2pair.core.venue.common.SimpleTime

import static org.where2pair.core.venue.read.DayOfWeek.MONDAY

@Immutable
@ToString
class OpenTimesCriteria {
    SimpleTime openFrom
    SimpleTime openUntil
    DayOfWeek dayOfWeek

    static OpenTimesCriteria openAnyTime() {
        new OpenTimesCriteria(new SimpleTime(23, 59), new SimpleTime(0, 0), MONDAY)
    }

    TimeWindow getTimeRange() {
        new TimeWindow(openFrom, openUntil)
    }
}
