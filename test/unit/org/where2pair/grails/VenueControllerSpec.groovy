package org.where2pair.grails

import grails.converters.JSON
import grails.test.mixin.*

import org.skyscreamer.jsonassert.JSONAssert;
import org.where2pair.Coordinates
import org.where2pair.Venue
import org.where2pair.VenueFinder

import spock.lang.Specification

@TestFor(VenueController)
class VenueControllerSpec extends Specification {

	VenueFinder venueFinder = Mock()
	GormVenueRepository gormVenueRepository = Mock()
	
	def "show should display search results for given coordinates"() {
		given:
		controller.params.'location1' = '1.0,0.1'
		venueFinder.findNearestTo(new Coordinates(lat: 1.0, lng: 0.1)) >> [new Venue()]
		controller.venueFinder = venueFinder
		
		when:
		controller.show()
		
		then:
		use (JSONMatcher) {
			response.text.equalToJsonOf([new Venue()])
		}
		response.status == 200
	}
	
	def "should save new venues"() {
		given:
		VenueDTO venueDTO = new VenueDTO(
			latitude: 1.0,
			longitude: 0.1,
			openHours: [monday: [[openHour: 12, openMinute: 0, closeHour: 18, closeMinute: 30]],
						tuesday: [[openHour: 8, openMinute: 0, closeHour: 11, closeMinute: 0]]]
		)
		request.method = 'POST'
		request.json = venueDTO
		controller.gormVenueRepository = gormVenueRepository
		
		when:
		controller.save()
		
		then:
		1 * gormVenueRepository.save(venueDTO)
		use (JSONMatcher) {
			response.text.equalToJsonOf(venueDTO)
		}
		response.status == 200
	}	
	
	def "test http actions"() {
		
	}
	
	def "should handle errors"() {
		
	}
	
	@Category(String)
	static class JSONMatcher {
		boolean equalToJsonOf(Object object) {
			JSONAssert.assertEquals((object as JSON).toString(), this, false)
			true
		}
	}
}