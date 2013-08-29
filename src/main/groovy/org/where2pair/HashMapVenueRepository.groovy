package org.where2pair

import java.util.List
import java.util.concurrent.ConcurrentHashMap;

class HashMapVenueRepository implements VenueRepository {

	ConcurrentHashMap venues = [:]
	
	@Override
	public List getAll() {
		venues.collect { id, venue ->
			venue.clone()
		}.asImmutable()
	}

	@Override
	public Venue get(long id) {
		find(id).clone()
	}

	@Override
	public long save(Venue venue) {
		venue.id = venues.size()
		venues[venue.id] = venue.clone()
		venue.id
	}

	@Override
	public Venue findByNameAndCoordinates(String name, Coordinates coordinates) {
		venues.find { id, Venue venue -> 
			venue.name == name && venue.location == coordinates 
		}.value
	}

	@Override
	public void update(Venue venue) {
		venues.replace(venue.id, venue.clone())
	}

	private Venue find(long id) {
		venues[id]
	}
}
