package org.where2pair.grails

import org.where2pair.Coordinates
import spock.lang.Specification

class GrailsVenueRepositorySpec extends Specification {

	GrailsVenueDaoService grailsVenueDaoService = Mock()
	
	GrailsVenueRepository grailsVenueRepository = new GrailsVenueRepository(
		grailsVenueDaoService: grailsVenueDaoService
	)
	
	def "should load objects through dao and map to Venue objects"() {
		given:
		GrailsVenue grailsVenue = new GrailsVenue(latitude: 1.0, longitude: 0.1)
		grailsVenueDaoService.getAll() >> [grailsVenue]
		
		when:
		List venues = grailsVenueRepository.getAll()
	
		then:
		venues.size() == 1
		venues[0].location == new Coordinates(lat: 1.0, lng: 0.1)
	}

}
