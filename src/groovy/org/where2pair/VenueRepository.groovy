package org.where2pair

interface VenueRepository {

	List getAll()
	
	Venue get(long id)
	
	long save(Venue venue)
	
}
