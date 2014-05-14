package org.where2pair.read.venue.find

import groovy.transform.Immutable
import groovy.transform.ToString
import org.where2pair.common.venue.SimpleTime
import org.where2pair.read.venue.DayOfWeek
import org.where2pair.read.venue.TimeWindow

import static org.where2pair.read.venue.DayOfWeek.MONDAY

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
