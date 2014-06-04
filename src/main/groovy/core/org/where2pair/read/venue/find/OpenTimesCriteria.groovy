package org.where2pair.read.venue.find

import org.where2pair.read.venue.Venue

interface OpenTimesCriteria {

    boolean satisfiedBy(Venue venue)

}

