package org.where2pair.grails

import java.util.List;

import org.where2pair.VenueRepository;

class GormVenueDaoService {

	GormVenue get(long id) {
		
	}
	
	List getAll() {
		GormVenue.list()
	}
	
	GormVenue save(GormVenue venue) {
		venue.save()
	}	
}
