package org.where2pair.core.venue

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.where2pair.core.venue.DailyOpeningTimes.SimpleTime
import org.where2pair.core.venue.DayOfWeek

@EqualsAndHashCode
@ToString
class OpenTimesCriteria {
    SimpleTime openFrom
    SimpleTime openUntil
    DayOfWeek dayOfWeek
}
