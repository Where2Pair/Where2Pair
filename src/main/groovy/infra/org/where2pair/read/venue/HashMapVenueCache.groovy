package org.where2pair.read.venue

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class HashMapVenueCache implements VenueRepository {

    final ConcurrentMap<VenueId, Venue> venues = [:] as ConcurrentHashMap<VenueId, Venue>

    @Override
    List<Venue> findAll() {
        venues.collect { id, venue ->
            venue
        }.asImmutable()
    }

    @Override
    Venue get(VenueId id) {
        venues[id]
    }

    void put(Venue venue) {
        venues[venue.id] = venue
    }
}

