package org.where2pair.main

import org.where2pair.common.venue.VenueId
import org.where2pair.read.venue.Venue
import org.where2pair.read.venue.VenueRepository

import java.util.concurrent.ConcurrentHashMap

class HashMapVenueCache implements VenueRepository {

    final ConcurrentHashMap<VenueId, Venue> venues = [:]

    @Override
    List<Venue> getAll() {
        venues.collect { id, venue ->
            venue
        }.asImmutable()
    }

    @Override
    Venue get(VenueId id) {
        venues[id]
    }

    @Override
    void put(Venue venue) {
        venues[venue.id] = venue
    }
}
