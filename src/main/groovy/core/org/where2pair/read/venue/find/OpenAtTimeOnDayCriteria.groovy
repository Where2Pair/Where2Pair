package org.where2pair.read.venue.find

import groovy.transform.Immutable
import org.where2pair.common.venue.SimpleTime
import org.where2pair.read.venue.DayOfWeek
import org.where2pair.read.venue.Venue

@Immutable
class OpenAtTimeOnDayCriteria implements OpenTimesCriteria {

    SimpleTime time
    DayOfWeek day

    @Override
    boolean satisfiedBy(Venue venue) {
        venue.weeklyOpeningTimes[day].isOpenAt(time)
    }
}
