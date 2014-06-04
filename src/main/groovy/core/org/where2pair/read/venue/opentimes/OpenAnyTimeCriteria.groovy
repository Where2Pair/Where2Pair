package org.where2pair.read.venue.opentimes

import org.where2pair.read.venue.find.OpenTimesCriteria
import org.where2pair.read.venue.Venue

class OpenAnyTimeCriteria implements OpenTimesCriteria {
    @Override
    boolean satisfiedBy(Venue venue) {
        true
    }
}

