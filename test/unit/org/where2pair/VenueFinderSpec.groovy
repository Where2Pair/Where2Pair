package org.where2pair

import org.joda.time.DateTime
import spock.lang.Specification

class VenueFinderSpec extends Specification {

	static final OPEN_TIMES_CRITERIA = new OpenTimesCriteria()
	static final FEATURES_CRITERIA = new FeaturesCriteria()
	static final USER_LOCATION = new Coordinates(lat: 0.2, lng: 0.1)
	VenueRepository venueRepository = Mock()
	DistanceCalculator distanceCalculator = Mock()
	VenueFinder venueFinder = new VenueFinder(venueRepository: venueRepository, 
		distanceCalculator: distanceCalculator)
	
	def "should return at most 50 venues"() {
		given:
		venueRepository.getAll() >> 100.openVenues()
		
		when:
		List venues = venueFinder.findNearestTo(OPEN_TIMES_CRITERIA, FEATURES_CRITERIA, USER_LOCATION)
		
		then:
		venues.size() == 50
	}
	
	def "given less than 50 venues then return all venues"() {
		given:
		venueRepository.getAll() >> 49.openVenues()
		
		when:
		List venues = venueFinder.findNearestTo(OPEN_TIMES_CRITERIA, FEATURES_CRITERIA, USER_LOCATION)
		
		then:
		venues.size() == 49
	}
	
	def "given 0 venues then return 0 venues"() {
		given:
		venueRepository.getAll() >> 0.openVenues()
		
		when:
		List venues = venueFinder.findNearestTo(OPEN_TIMES_CRITERIA, FEATURES_CRITERIA, USER_LOCATION)
		
		then:
		venues.size() == 0
	}
	
	def "only returns open venues"() {
		given:
		venueRepository.getAll() >> 10.openVenues() + 5.closedVenues()
		
		when:
		List venues = venueFinder.findNearestTo(OPEN_TIMES_CRITERIA, FEATURES_CRITERIA, USER_LOCATION)
		
		then:
		venues.size() == 10
	}
	
	def "only returns venues that meet features criteria"() {
		given:
		venueRepository.getAll() >> 15.venuesWithFeatures() + 5.venuesWithoutFeatures()
		
		when:
		List venues = venueFinder.findNearestTo(OPEN_TIMES_CRITERIA, FEATURES_CRITERIA, USER_LOCATION)
		
		then:
		venues.size() == 15
	}
	
	def "returns 50 closest venues, ordered ascending by distance"() {
		given:
		List nearbyVenues = 50.openVenues()
		venueRepository.getAll() >> 50.openVenues() + nearbyVenues
		distanceCalculator.distanceInKmTo(_ as Venue, USER_LOCATION) >>> (99..0)
		
		when:
		List venues = venueFinder.findNearestTo(OPEN_TIMES_CRITERIA, FEATURES_CRITERIA, USER_LOCATION)
		
		then:
		venues.venue == nearbyVenues.reverse()
	}
	
	def "considers all coordinates when determining the distance"() {
		given:
		List venues = 1.openVenues()
		venueRepository.getAll() >> venues
		Coordinates coords1 = new Coordinates(0.1, 0.2)
		Coordinates coords2 = new Coordinates(0.2, 0.3)
		Coordinates coords3 = new Coordinates(0.3, 0.4)
		
		when:
		venueFinder.findNearestTo(OPEN_TIMES_CRITERIA, FEATURES_CRITERIA, coords1, coords2, coords3)
		
		then:
		1 * distanceCalculator.distanceInKmTo(venues[0], coords1, coords2, coords3)
	}
	
	def setup() {
		Integer.mixin(VenuesMixin)
	}
	
	def cleanup() {
		Integer.metaClass = null
	}
	
	def openVenueWithDistance(it) {
		Venue venue = [isOpen: { openTimesCriteria -> openTimesCriteria == OPEN_TIMES_CRITERIA },
			distanceInKmTo: { coordinates -> it }] as Venue
	}
	
	@Category(Integer)
	static class VenuesMixin {
		List openVenues() {
			venuesWithTemplate {
				[isOpen: { openTimesCriteria -> openTimesCriteria == VenueFinderSpec.OPEN_TIMES_CRITERIA },
					hasFeatures: { featuresCriteria -> true },
					distanceInKmTo: { coordinates -> 0 }] as Venue
			}
		}
		
		List closedVenues() {
			venuesWithTemplate {
				[isOpen: { openTimesCriteria -> false },
					hasFeatures: { featuresCriteria -> true }] as Venue
			}
		}
		
		List venuesWithFeatures() {
			venuesWithTemplate {
				[isOpen: { openTimesCriteria -> true },
					hasFeatures: { featuresCriteria -> featuresCriteria == VenueFinderSpec.FEATURES_CRITERIA },
					distanceInKmTo: { coordinates -> 0 }] as Venue
			}
		}
		
		List venuesWithoutFeatures() {
			venuesWithTemplate {
				[isOpen: { openTimesCriteria -> true },
					hasFeatures: { featuresCriteria -> false}] as Venue
			}
		}
		
		def venuesWithTemplate(Closure c) {
			if (this == 0) return []
			(0..(this - 1)).collect { c() }
		}
	}
}
