package org.where2pair

import org.joda.time.DateTime

import spock.lang.Specification

import static spock.util.matcher.HamcrestMatchers.closeTo

class VenueSpec extends Specification {

	def "should determine distance to supplied coordinates"() {
		given:
		Venue venue = new Venue(location: new Coordinates(lat: venueLatitude, lng: venueLongitude))
		Coordinates coordinates = new Coordinates(lat: targetLatitude, lng: targetLongitude)
		
		when:
		double distance = venue.distanceInKmTo(coordinates)
		
		then:
		distance closeTo(expectedDistance, 0.01)
		
		where:
		venueLatitude	| venueLongitude 	| targetLatitude	| targetLongitude	| expectedDistance
		0				|	0				| 0					| 0					| 0	
		51.713416		|	-1.406250		| 51.506552			| -0.11261			| 92.24
		51.530800		|	-0.097933		| 51.520921			| -0.081625			| 1.57	
		51.530800		|	-0.097933		| -33.868135		| 151.210327		| 16990.86	
	}
	
	def "should determine whether venue is open"() {
		given:
		DateTime dateTime = new DateTime()
		WeeklyOpeningTimes weeklyOpenTimes = Mock()
		weeklyOpenTimes.isOpen(dateTime) >> expectedOpenStatus
		Venue venue = new Venue(weeklyOpeningTimes: weeklyOpenTimes)
		
		when:
		boolean openStatus = venue.isOpen(dateTime)
		
		then:
		openStatus == expectedOpenStatus
		
		where:
		expectedOpenStatus << [true, false]
	}
	
}
