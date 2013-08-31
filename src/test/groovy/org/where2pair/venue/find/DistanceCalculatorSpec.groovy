package org.where2pair.venue.find

import org.where2pair.venue.Coordinates
import org.where2pair.venue.Venue
import org.where2pair.venue.find.DistanceCalculator
import spock.lang.Specification
import static spock.util.matcher.HamcrestMatchers.closeTo
import static org.where2pair.venue.find.LocationsCriteriaBuilder.locationsCriteria
import static org.where2pair.venue.find.DistanceUnit.METRIC
import static org.where2pair.venue.find.DistanceUnit.IMPERIAL

class DistanceCalculatorSpec extends Specification {

	DistanceCalculator distanceCalculator = new DistanceCalculator()
	Venue venue = Mock()
	
	def "given locations criteria in metric then returns distance in km from venue"() {
		given:
		def coords = new Coordinates(0.1, 0.2)
		venue.distanceInKmTo(coords) >> 2.3
		def locationsCriteria = locationsCriteria().withLocation(coords).withDistanceUnit(METRIC)
		
		when:
		double distance = distanceCalculator.distanceBetween(venue, locationsCriteria)
		
		then:
		distance == 2.3
	}
	
	def "given locations criteria in imperial then returns distance in miles from venue"() {
		given:
		def coords = new Coordinates(0.1, 0.2)
		venue.distanceInMilesTo(coords) >> 1.2
		def locationsCriteria = locationsCriteria().withLocation(coords).withDistanceUnit(IMPERIAL)
		
		when:
		double distance = distanceCalculator.distanceBetween(venue, locationsCriteria)
		
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
		def locationsCriteria = locationsCriteria().withLocation(coords1).withLocation(coords2).withLocation(coords3).withDistanceUnit(METRIC)
		
		when:
		double distance = distanceCalculator.distanceBetween(venue, locationsCriteria)
		
		then:
		distance closeTo((2.3 + 5.9 + 3.4)/3, 0.01)
	}
}
