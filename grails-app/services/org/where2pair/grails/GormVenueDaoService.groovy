package org.where2pair.grails

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
}
