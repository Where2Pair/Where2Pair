package org.where2pair.venue.find

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.where2pair.venue.DailyOpeningTimes.SimpleTime
import org.where2pair.venue.DayOfWeek

@EqualsAndHashCode
@ToString
class OpenTimesCriteria {
    SimpleTime openFrom
    SimpleTime openUntil
    DayOfWeek dayOfWeek
}
