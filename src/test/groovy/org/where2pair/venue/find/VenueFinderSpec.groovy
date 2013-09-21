package org.where2pair.venue.find

import org.where2pair.venue.Venue
import org.where2pair.venue.VenueRepository
import spock.lang.Specification
import spock.lang.Unroll

class VenueFinderSpec extends Specification {

    VenueRepository venueRepository = Mock()
    VenueFinder venueFinder = new VenueFinder(venueRepository: venueRepository)

    @Unroll("#rationale")
    def "returns correct count of venues"() {
        given:
        venueRepository.getAll() >> numberOf.openVenues()

        when:
        List venues = venueFinder.findNearestTo(OPEN_TIMES_CRITERIA, FEATURES_CRITERIA, USER_LOCATION)

        then:
        venues.size() == expectedVenueCount

        where:
        rationale                                          | numberOf | expectedVenueCount
        "should return at most 50 venues"                  | 100      | 50
        "given less than 50 venues then return all venues" | 49       | 49
        "given 0 venues then return 0 venues"              | 0        | 0
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
        nearbyVenues = assignUniqueIds(nearbyVenues)
        venueRepository.getAll() >> 50.openVenues() + nearbyVenues
        LocationsCriteria locationsCriteria = Mock()
        locationsCriteria.distancesTo(_ as Venue) >>> (99..0).collect { ["location$it": it] }

        when:
        List venues = venueFinder.findNearestTo(OPEN_TIMES_CRITERIA, FEATURES_CRITERIA, locationsCriteria)

        then:
        venues.venue == nearbyVenues.reverse()
    }

    def assignUniqueIds(List venues) {
        int idIndex = 0
        venues.collect { it.id = ++idIndex; it }
    }

    def setup() {
        Integer.mixin(VenuesMixin)
    }

    def cleanup() {
        Integer.metaClass = null
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
                        hasFeatures: { featuresCriteria -> false }] as Venue
            }
        }

        def venuesWithTemplate(Closure c) {
            if (this == 0) return []
            (0..(this - 1)).collect { c() }
        }
    }

    static final OPEN_TIMES_CRITERIA = new OpenTimesCriteria()
    static final FEATURES_CRITERIA = new FacilitiesCriteria()
    LocationsCriteria USER_LOCATION = Mock(LocationsCriteria) {
        distancesTo(_) >> [location1: 1.0]
    }
}
