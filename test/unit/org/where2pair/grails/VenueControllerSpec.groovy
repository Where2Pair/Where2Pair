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

@TestFor(VenueController)
class VenueControllerSpec extends Specification {

	GormVenueRepository gormVenueRepository = Mock()
	VenueConverter venueConverter = new VenueConverter()

	def "should show all venues"() {
		given:
		request.method = 'GET'
		gormVenueRepository.getAll() >> 100.venues()
		List venueDTOs = toVenueDTO(100.venues())

		when:
		controller.show()

		then:
		response.text.equalToJsonOf(venueDTOs)
		response.status == 200
	}
	
	def "should save new venues"() {
		given:
		request.method = 'POST'
		VenueDTO venueDTO = new VenueDTO(
				latitude: 1.0,
				longitude: 0.1,
				openHours: [monday: [
						[openHour: 12, openMinute: 0, closeHour: 18, closeMinute: 30]
					],
					tuesday: [
						[openHour: 8, openMinute: 0, closeHour: 11, closeMinute: 0]
					]]
				)
		request.json = venueDTO

		when:
		controller.save()

		then:
		1 * gormVenueRepository.save(venueDTO)
		response.text.equalToJsonOf(venueDTO)
		response.status == 200
	}

	private def toVenueDTO(List venues) {
		venueConverter.asVenueDTOs(venues)
	}

	def setup() {
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
		List venues() {
			(0..this).collect {
				new Venue(location: new Coordinates(1.0, 0.5),
				weeklyOpeningTimes: new WeeklyOpeningTimesBuilder().build())
			}
		}
	}
}