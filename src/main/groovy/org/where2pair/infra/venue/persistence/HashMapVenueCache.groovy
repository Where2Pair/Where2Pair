package org.where2pair.infra.venue.persistence

import org.where2pair.core.venue.common.VenueId
import org.where2pair.core.venue.read.Venue
import org.where2pair.core.venue.read.VenueRepository

import java.util.concurrent.ConcurrentHashMap

class HashMapVenueCache implements VenueRepository {

    final ConcurrentHashMap venues = [:]

    @Override
    public List getAll() {
        venues.collect { id, venue ->
            venue.clone()
        }.asImmutable()
    }

    @Override
    public Venue get(VenueId id) {
        venues[id]?.clone()
    }

    void put(Venue venue) {
        venues[venue.id] = venue
    }
}
