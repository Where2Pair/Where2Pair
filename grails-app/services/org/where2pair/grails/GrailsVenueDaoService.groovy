package org.where2pair.grails

import java.util.List;

import org.where2pair.VenueRepository;

class GrailsVenueDaoService {

	@Override
	List getAll() {
		GrailsVenue.list()
	}

}
