package org.where2pair.core.venue.read

import org.where2pair.core.venue.common.VenueId


interface VenueRepository {

    List<Venue> getAll()

    Venue get(VenueId id)

}
