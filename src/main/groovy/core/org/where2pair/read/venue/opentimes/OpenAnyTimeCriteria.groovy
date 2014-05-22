package org.where2pair.read.venue.opentimes

import org.where2pair.read.venue.OpenTimesCriteria
import org.where2pair.read.venue.Venue

class OpenAnyTimeCriteria implements OpenTimesCriteria {
    @Override
    boolean satisfiedBy(Venue venue) {
        true
    }
}
