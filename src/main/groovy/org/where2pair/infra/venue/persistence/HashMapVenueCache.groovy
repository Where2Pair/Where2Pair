package org.where2pair.infra.venue.persistence

import org.where2pair.core.venue.common.VenueId
import org.where2pair.core.venue.read.Venue
import org.where2pair.core.venue.read.VenueRepository

import java.util.concurrent.ConcurrentHashMap

class HashMapVenueCache implements VenueRepository {

    final ConcurrentHashMap<VenueId, Venue> venues = [:]

    @Override
    List<Venue> getAll() {
        venues.collect { id, venue ->
            venue.clone()
        }.asImmutable()
    }

    @Override
    Venue get(VenueId id) {
        venues[id]?.clone()
    }

    @Override
    void put(Venue venue) {
        venues[venue.id] = venue
    }
}
