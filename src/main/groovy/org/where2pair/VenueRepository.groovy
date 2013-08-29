package org.where2pair

interface VenueRepository {

	List getAll()
	
	Venue get(long id)
	
	long save(Venue venue)
	
	Venue findByNameAndCoordinates(String name, Coordinates coordinates)
	
	void update(Venue venue)
}
