package org.where2pair

import org.joda.time.DateTime
import spock.lang.Specification

class VenueFinderSpec extends Specification {

	static final CURRENT_TIME = new DateTime()
	static final USER_LOCATION = new Coordinates(lat: 0.2, lng: 0.1)
	VenueRepository venueRepository = Mock()
	TimeProvider timeProvider = Mock()
	DistanceCalculator distanceCalculator = Mock()
	VenueFinder venueFinder = new VenueFinder(venueRepository: venueRepository, 
		timeProvider: timeProvider, distanceCalculator: distanceCalculator)
	
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
	
	def "should return 50 closest venues, ordered ascending by distance"() {
		given:
		List nearbyVenues = 50.openVenues()
		venueRepository.getAll() >> 50.openVenues() + nearbyVenues
		distanceCalculator.distanceInKmTo(_ as Venue, USER_LOCATION) >>> (99..0)
		
		when:
		List venues = venueFinder.findNearestTo(USER_LOCATION)
		
		then:
		venues.venue == nearbyVenues.reverse()
	}
	
	def "should consider all coordinates when determining the distance"() {
		given:
		List venues = 1.openVenues()
		venueRepository.getAll() >> venues
		Coordinates coords1 = new Coordinates(0.1, 0.2)
		Coordinates coords2 = new Coordinates(0.2, 0.3)
		Coordinates coords3 = new Coordinates(0.3, 0.4)
		
		when:
		venueFinder.findNearestTo(coords1, coords2, coords3)
		
		then:
		1 * distanceCalculator.distanceInKmTo(venues[0], coords1, coords2, coords3)
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
	
	@Category(Integer)
	static class VenuesMixin {
		List openVenues() {
			venuesWithTemplate {
				[isOpen: { dateTime -> dateTime == VenueFinderSpec.CURRENT_TIME },
					distanceInKmTo: { coordinates -> 0 }] as Venue
			}
		}
		
		List closedVenues() {
			venuesWithTemplate {
				[isOpen: { dateTime -> false }] as Venue
			}
		}
		
		Closure venuesWithTemplate = { Closure c ->
			if (this == 0) return []
			(0..(this - 1)).collect { c() }
		}
	}
}
