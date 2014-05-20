package org.where2pair.main

import org.where2pair.read.venue.Venue
import org.where2pair.read.venue.VenueId
import org.where2pair.read.venue.VenueRepository
import org.where2pair.write.venue.NewVenueId

import java.util.concurrent.ConcurrentHashMap

class HashMapVenueCache implements VenueRepository {

    final ConcurrentHashMap<NewVenueId, Venue> venues = [:]

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

    void put(Venue venue) {
        venues[venue.id] = venue
    }
}
