package org.where2pair.read.venue

interface VenueRepository {

    List<Venue> getAll()

    Venue get(VenueId id)

}
