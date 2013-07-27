package org.where2pair

import org.joda.time.DateTime
import spock.lang.Specification

class VenueFinderSpec extends Specification {

	static final CURRENT_TIME = new DateTime()
	static final USER_LOCATION = new Coordinates(lat: 0.2, lng: 0.1)
	VenueRepository venueRepository = Mock()
	TimeProvider timeProvider = Mock()
	VenueFinder venueFinder = new VenueFinder(venueRepository: venueRepository, timeProvider: timeProvider)
	
	def "should return at most 50 venues"() {
		given:
		venueRepository.getAll() >> 100.openVenues()
		
		when:
		List venues = venueFinder.findNearestTo(USER_LOCATION)
		
		then:
		venues.size() == 50
	}
	
	def "given less than 50 venues then return all venues"() {
		given:
		venueRepository.getAll() >> 49.openVenues()
		
		when:
		List venues = venueFinder.findNearestTo(USER_LOCATION)
		
		then:
		venues.size() == 49
	}
	
	def "given 0 venues then return 0 venues"() {
		given:
		venueRepository.getAll() >> 0.openVenues()
		
		when:
		List venues = venueFinder.findNearestTo(USER_LOCATION)
		
		then:
		venues.size() == 0
	}
	
	def "should only return open venues"() {
		given:
		venueRepository.getAll() >> 10.openVenues() + 5.closedVenues()
		
		when:
		List venues = venueFinder.findNearestTo(USER_LOCATION)
		
		then:
		venues.size() == 10
	}
	
	def "should return venues in ascending order by distance"() {
		given:
		List venueDistances = [ 10, 3, 5, 2 ]
		venueRepository.getAll() >> venueDistances.collect { openVenueWithDistance(it) }
		
		when:
		List venues = venueFinder.findNearestTo(USER_LOCATION)
		
		then:
		venues.collect { it.distanceInKmTo(USER_LOCATION) } == [2, 3, 5, 10]
	}
	
	def setup() {
		Integer.mixin(VenuesMixin)
		timeProvider.currentTime >> CURRENT_TIME
	}
	
	def cleanup() {
		Integer.metaClass = null
	}
	
	def openVenueWithDistance(it) {
		Venue venue = [isOpen: { dateTime -> dateTime == CURRENT_TIME },
			distanceInKmTo: { coordinates -> it }] as Venue
	}
	
	static class VenuesMixin {
		static List openVenues(int count) {
			Venue venue = [isOpen: { dateTime -> dateTime == VenueFinderSpec.CURRENT_TIME },
				distanceInKmTo: { coordinates -> 0 }] as Venue
			[venue] * count
		}
		
		static List closedVenues(int count) {
			Venue venue = [isOpen: { dateTime -> false }] as Venue
			[venue] * count
		}
	}
}
