package org.where2pair.infra.venue.persistence

import org.where2pair.core.venue.Coordinates
import org.where2pair.core.venue.Venue
import org.where2pair.core.venue.VenueRepository

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class HashMapVenueRepository implements VenueRepository {

    ConcurrentHashMap venues = [:]
    AtomicInteger idGenerator = new AtomicInteger()

    @Override
    public List getAll() {
        venues.collect { id, venue ->
            venue.clone()
        }.asImmutable()
    }

    @Override
    public Venue get(String id) {
        find(id)?.clone()
    }

    @Override
    public String save(Venue venue) {
        venue.id = String.valueOf(idGenerator.incrementAndGet())
        venues[venue.id] = venue.clone()
        venue.id
    }

    @Override
    public Venue findByNameAndCoordinates(String name, Coordinates coordinates) {
        venues.find { id, Venue venue ->
            venue.name == name && venue.location == coordinates
        }?.value
    }

    @Override
    public void update(Venue venue) {
        venues.replace(venue.id, venue.clone())
    }

    private Venue find(String id) {
        venues[id]
    }
}
