package org.where2pair.venue.find

import org.where2pair.venue.Coordinates
import org.where2pair.venue.Venue;

import spock.lang.Shared
import spock.lang.Specification
import static org.where2pair.venue.find.LocationsCriteriaBuilder.locationsCriteria
import static spock.util.matcher.HamcrestMatchers.closeTo

class LocationsCriteriaSpec extends Specification {

	static final Coordinates COORDS = new Coordinates(1.0,0.1)
	Venue venue = Mock()
	
	def "expects between 1 and 1000 locations, and km or miles as distance unit"() {
		given:
		LocationsCriteria locationsCriteria = new LocationsCriteria(locations: locations, distanceUnit: distanceUnit)
		
		when:
		def errors = locationsCriteria.errors
		
		then:
		!errors
		
		where:
		locations 		| distanceUnit
		[COORDS]  		| 'miles'
		[COORDS]  		| 'km'
		[COORDS] * 1000 | 'miles'
		[COORDS] * 1000 | 'km'
	}
		
	def "rejects more than 1000 supplied locations"() {
		given:
		LocationsCriteria locationsCriteria = new LocationsCriteria(locations: [COORDS] * 1001, distanceUnit: 'miles')

		when:
		def (message, status) = locationsCriteria.errors

		then:
		message == "Only upto 1000 locations are supported at this time."
		status == 413
	}

	def "rejects 0 supplied locations"() {
		given:
		LocationsCriteria locationsCriteria = new LocationsCriteria(locations: [], distanceUnit: 'miles')
		
		when:
		def (message, status) = locationsCriteria.errors

		then:
		message == "Missing locations from the request parameters. I expect a query in the form: nearest?location1=x1,y1&location2=x2,y2..."
		status == 400
	}
	
	def "rejects invalid distance unit"() {
		given:
		LocationsCriteria locationsCriteria = new LocationsCriteria(locations: [COORDS], distanceUnit: 'furlongs')
		
		when:
		def (message, status) = locationsCriteria.errors
		
		then:
		message == "Distance unit 'FURLONGS' is invalid. Use either 'km' or 'miles' (omitting distanceUnit altogether defaults to 'km')."
		status == 400
	}
	
	def "given locations criteria in km then returns distance in km from venue"() {
		given:
		def coords = new Coordinates(0.1, 0.2)
		venue.distanceInKmTo(coords) >> 2.3
		def locationsCriteria = locationsCriteria().withLocation(coords).withDistanceUnit('km')
		
		when:
		double distance = locationsCriteria.distanceTo(venue)
		
		then:
		distance == 2.3
	}
	
	def "given locations criteria in miles then returns distance in miles from venue"() {
		given:
		def coords = new Coordinates(0.1, 0.2)
		venue.distanceInMilesTo(coords) >> 1.2
		def locationsCriteria = locationsCriteria().withLocation(coords).withDistanceUnit('miles')
		
		when:
		double distance = locationsCriteria.distanceTo(venue)
		
		then:
		distance == 1.2
	}
	
	def "given multiple coordinates then return average distance to all coordinates from venue"() {
		given:
		def coords1 = new Coordinates(0.1, 0.2)
		def coords2 = new Coordinates(0.2, 0.3)
		def coords3 = new Coordinates(0.3, 0.4)
		venue.distanceInKmTo(coords1) >> 2.3
		venue.distanceInKmTo(coords2) >> 5.9
		venue.distanceInKmTo(coords3) >> 3.4
		def locationsCriteria = locationsCriteria().withLocation(coords1).withLocation(coords2).withLocation(coords3).withDistanceUnit('km')
		
		when:
		double distance = locationsCriteria.distanceTo(venue)
		
		then:
		distance closeTo((2.3 + 5.9 + 3.4)/3, 0.01)
	}
}
