package org.where2pair.venue.find

import org.where2pair.venue.Coordinates;
import org.where2pair.venue.Venue;
import org.where2pair.venue.find.DistanceCalculator;

import spock.lang.Specification
import static spock.util.matcher.HamcrestMatchers.closeTo

class DistanceCalculatorSpec extends Specification {

	DistanceCalculator distanceCalculator = new DistanceCalculator()
	
	def "given a single set of coordinates then return distance to those coordinates from venue"() {
		given:
		Coordinates coords = new Coordinates(0.1, 0.2)
		Venue venue = Mock()
		venue.distanceInKmTo(coords) >> 2.3
		
		when:
		double distance = distanceCalculator.distanceInKmTo(venue, coords)
		
		then:
		distance == 2.3
	}
	
	def "given multiple coordinates then return average distance to all coordinates from venue"() {
		given:
		Coordinates coords1 = new Coordinates(0.1, 0.2)
		Coordinates coords2 = new Coordinates(0.2, 0.3)
		Coordinates coords3 = new Coordinates(0.3, 0.4)
		Venue venue = Mock()
		venue.distanceInKmTo(coords1) >> 2.3
		venue.distanceInKmTo(coords2) >> 5.9
		venue.distanceInKmTo(coords3) >> 3.4
		
		when:
		double distance = distanceCalculator.distanceInKmTo(venue, coords1, coords2, coords3)
		
		then:
		distance closeTo((2.3 + 5.9 + 3.4)/3, 0.01)
	}
}
