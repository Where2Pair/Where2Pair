package org.where2pair.grails

import org.where2pair.Coordinates

class GormVenueDaoService {

	GormVenue get(long id) {
        GormVenue.get(id)
	}
	
	List getAll() {
		GormVenue.list()
	}
	
	GormVenue save(GormVenue venue) {
		venue.save()
	}	
	
	GormVenue findByNameAndCoordinates(String name, Coordinates coordinates) {
		
	}
}
