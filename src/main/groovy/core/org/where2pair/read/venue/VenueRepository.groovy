package org.where2pair.read.venue

import org.where2pair.common.venue.VenueId


interface VenueRepository {

    List<Venue> getAll()

    Venue get(VenueId id)

}
