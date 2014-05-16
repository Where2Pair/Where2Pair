package org.where2pair.read.venue.find

import org.where2pair.read.venue.Venue

class OpenAnyTimeCriteria implements OpenTimesCriteria {
    @Override
    boolean satisfiedBy(Venue venue) {
        true
    }
}
