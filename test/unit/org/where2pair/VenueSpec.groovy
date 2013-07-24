package org.where2pair

import org.joda.time.DateTime

import spock.lang.Specification

class VenueSpec extends Specification {

	def "should determine distance to supplied coordinates"() {
		given:
		Venue venue = new Venue(location: new Coordinates(lat: venueLatitude, lng: venueLongitude))
		Coordinates coordinates = new Coordinates(lat: targetLatitude, lng: targetLongitude)
		
		when:
		double distance = venue.distanceInKmTo(coordinates)
		
		then:
		distance == expectedDistance
		
		where:
		venueLatitude | venueLongitude 	| targetLatitude	| targetLongitude	| expectedDistance
		0			  |	0				| 0					| 0					| 0	
		0			  |	0				| 0					| 0					| 0	
		0			  |	0				| 0					| 0					| 0	
		0			  |	0				| 0					| 0					| 0	
	}
	
	def "should determine whether venue is open"() {
		given:
		DateTime dateTime = new DateTime()
		WeeklyOpenTimes weeklyOpenTimes = Mock()
		Venue venue = new Venue(weeklyOpenTimes: weeklyOpenTimes)
		weeklyOpenTimes.isOpen(dateTime) >> expectedOpenStatus
		
		when:
		boolean open = venue.isOpen(dateTime)
		
		then:
		open == expectedOpenStatus
		
		where:
		expectedOpenStatus << [true, false]
	}
	
}
