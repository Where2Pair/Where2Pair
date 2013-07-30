package org.where2pair.grails

import grails.converters.JSON
import grails.test.mixin.*

import org.skyscreamer.jsonassert.JSONAssert
import org.where2pair.Coordinates
import org.where2pair.Venue
import org.where2pair.VenueFinder
import org.where2pair.VenueWithDistance
import org.where2pair.WeeklyOpeningTimesBuilder

import spock.lang.Specification

@TestFor(VenueFinderController)
class VenueFinderControllerSpec extends Specification {

	VenueFinder venueFinder = Mock()
	GormVenueRepository gormVenueRepository = Mock()
	VenueConverter venueConverter = new VenueConverter()

	def "should display search results for given coordinates"() {
		given:
		request.method = 'GET'
		controller.params.'location1' = '1.0,0.1'
		venueFinder.findNearestTo(new Coordinates(1.0,0.1)) >> 10.venuesWithDistance()
		List venueDTOs = toVenueWithDistanceDTO(10.venuesWithDistance())

		when:
		controller.findNearest()

		then:
		response.text.equalToJsonOf(venueDTOs)
		response.status == 200
	}

	def "should support multiple supplied locations"() {
		given:
		request.method = 'GET'
		(1..1000).each { controller.params."location$it" = '1.0,0.1'}
		List expectedArgs = [new Coordinates(1.0,0.1)] * 1000

		when:
		controller.findNearest()

		then:
		1 * venueFinder.findNearestTo(expectedArgs)
	}

	def "should reject more than 1000 supplied locations"() {
		given:
		request.method = 'GET'
		(1..1001).each { controller.params."location$it" = '1.0,0.1'}

		when:
		controller.findNearest()

		then:
		response.text == "Only upto 1000 locations are supported at this time."
		response.status == 413
	}

	def "should reject 0 supplied locations"() {
		given:
		request.method = 'GET'

		when:
		controller.findNearest()

		then:
		response.text == "Missing locations from the request parameters. I expect a query in the form: findNearest?location1=x1,y1&location2=x2,y2..."
		response.status == 413
	}

	private def toVenueWithDistanceDTO(List venues) {
		venueConverter.asVenueWithDistanceDTOs(venues)
	}

	def setup() {
		controller.venueFinder = venueFinder
		controller.gormVenueRepository = gormVenueRepository
		controller.venueConverter = venueConverter
		String.mixin(JSONMatcher)
		Integer.mixin(VenuesMixin)
	}

	def cleanup() {
		String.metaClass = null
		Integer.metaClass = null
	}

	@Category(String)
	static class JSONMatcher {
		boolean equalToJsonOf(Object object) {
			JSONAssert.assertEquals(new JSON(object).toString(), this, false)
			true
		}
	}

	@Category(Integer)
	static class VenuesMixin {
		List venuesWithDistance() {
			(0..this).collect {
				new VenueWithDistance(venue: new Venue(location: new Coordinates(1.0, 0.5),
				weeklyOpeningTimes: new WeeklyOpeningTimesBuilder().build()),
				distanceInKm: 10.5)
			}
		}
	}
}