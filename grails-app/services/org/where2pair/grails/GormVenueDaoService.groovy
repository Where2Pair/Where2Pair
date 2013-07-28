package org.where2pair.grails

import java.util.List;

import org.where2pair.VenueRepository;

class GormVenueDaoService {

	List getAll() {
		GormVenue.list()
	}

	void save(GormVenue venue) {
		venue.save()
	}	
}
