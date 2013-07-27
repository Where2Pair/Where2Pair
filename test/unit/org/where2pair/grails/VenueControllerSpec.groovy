
package org.where2pair.grails

import grails.converters.JSON
import grails.test.mixin.*

import org.where2pair.Coordinates
import org.where2pair.Venue
import org.where2pair.VenueFinder

import spock.lang.Specification

@TestFor(VenueController)
class VenueControllerSpec extends Specification {

	VenueFinder venueFinder = Mock()
	
	def "show should display search results for given coordinates"() {
		given:
		controller.params.'location1' = '1.0,0.1'
		venueFinder.findNearestTo(new Coordinates(lat: 1.0, lng: 0.1)) >> [new Venue()]
		controller.venueFinder = venueFinder
		
		when:
		controller.show()
		
		then:
		response.text == ([new Venue()] as JSON).toString()
	}
	
}