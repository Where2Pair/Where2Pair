package org.where2pair.read.venue.opentimes

import org.where2pair.read.venue.DayOfWeek
import groovy.transform.Immutable
import org.where2pair.read.venue.OpenTimesCriteria
import org.where2pair.read.venue.Venue

@Immutable
class OpenAnyTimeOnDayCriteria implements OpenTimesCriteria {

    DayOfWeek day

    @Override
    boolean satisfiedBy(Venue venue) {
        venue.weeklyOpeningTimes[day].isOpen()
    }
}

