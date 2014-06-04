package org.where2pair.read.venue

interface VenueRepository {

    List<Venue> findAll()

    Venue get(VenueId id)

}

