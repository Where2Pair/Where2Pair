package org.where2pair

import spock.lang.Specification

import static spock.util.matcher.HamcrestMatchers.closeTo

class VenueSpec extends Specification {

	def "determines distance to supplied coordinates"() {
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
	
	def "determines whether venue is open"() {
		given:
		OpenTimesCriteria openTimesCriteria = new OpenTimesCriteria()
		WeeklyOpeningTimes weeklyOpenTimes = Mock()
		weeklyOpenTimes.isOpen(openTimesCriteria) >> expectedOpenStatus
		Venue venue = new Venue(weeklyOpeningTimes: weeklyOpenTimes)
		
		when:
		boolean openStatus = venue.isOpen(openTimesCriteria)
		
		then:
		openStatus == expectedOpenStatus
		
		where:
		expectedOpenStatus << [true, false]
	}
	
	def "determines whether venue has features"() {
		given:
		Venue venue = new Venue(features: features.split(','))
		FeaturesCriteria featuresCriteria = new FeaturesCriteria(requestedFeatures: requestedFeatures.split(','))
		
		when:
		boolean hasFeatures = venue.hasFeatures(featuresCriteria)
		
		then:
		hasFeatures == expectedResult
		
		where:
		features 	| requestedFeatures | expectedResult
		'a,b,c'		| 'a,b'				| true
		'a,b,c'		| 'd'				| false
		''			| ''				| true
		'a,B,c'		| 'A,b,C'			| true
	}
}
