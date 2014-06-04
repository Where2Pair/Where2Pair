package org.where2pair.read.venue.opentimes

import groovy.transform.Immutable
import org.where2pair.common.venue.SimpleTime
import org.where2pair.read.venue.DayOfWeek
import org.where2pair.read.venue.find.OpenTimesCriteria
import org.where2pair.read.venue.TimeWindow
import org.where2pair.read.venue.Venue

@Immutable
class OpenBetweenTimesOnDayCriteria implements OpenTimesCriteria {

    SimpleTime openFrom
    SimpleTime openUntil
    DayOfWeek openDay

    @Override
    boolean satisfiedBy(Venue venue) {
        venue.weeklyOpeningTimes[openDay].isOpenBetween(new TimeWindow(openFrom, openUntil))
    }
}

