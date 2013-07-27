package org.where2pair.grails

import org.where2pair.Coordinates
import org.where2pair.Venue
import org.where2pair.VenueRepository


class GrailsVenueRepository implements VenueRepository {

	GrailsVenueDaoService grailsVenueDaoService
	
	@Override
	List getAll() {
		grailsVenueDaoService.getAll().collect {
			new Venue(location: new Coordinates(lat: it.latitude, lng: it.longitude))
		}
	}

}
