package org.where2pair.venue

import static spock.util.matcher.HamcrestMatchers.closeTo
import static org.where2pair.venue.ObjectUtils.createVenue;

import org.where2pair.venue.DailyOpeningTimes.SimpleTime
import org.where2pair.venue.Coordinates;
import org.where2pair.venue.Venue;
import org.where2pair.venue.WeeklyOpeningTimes;
import org.where2pair.venue.find.FeaturesCriteria;
import org.where2pair.venue.find.OpenTimesCriteria;

import spock.lang.Specification

class VenueSpec extends Specification {

	def "determines distance to supplied coordinates"() {
		given:
		Venue venue = new Venue(location: new Coordinates(lat: venueLatitude, lng: venueLongitude))
		Coordinates coordinates = new Coordinates(lat: targetLatitude, lng: targetLongitude)
		
		when:
		double distanceInKm = venue.distanceInKmTo(coordinates)
		double distanceInMiles = venue.distanceInMilesTo(coordinates)
		
		then:
		distanceInKm closeTo(expectedDistanceInKm, 0.01)
		distanceInMiles closeTo(expectedDistanceInKm * 0.62137, 0.01)
		
		where:
		venueLatitude	| venueLongitude 	| targetLatitude	| targetLongitude	| expectedDistanceInKm
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
	
	def "is cloneable"() {
		given:
		Venue venue = createVenue()
		
		when:
		Venue newVenue = venue.clone()
		
		then:
		newVenue == venue
		!newVenue.is(venue)
	}
	
}
