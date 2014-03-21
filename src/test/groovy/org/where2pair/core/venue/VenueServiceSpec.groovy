package org.where2pair.core.venue

import static org.where2pair.core.venue.DistanceUnit.KM

import spock.lang.Specification
import spock.lang.Unroll

class VenueServiceSpec extends Specification {

    VenueRepository venueRepository = Mock()
    VenueService venueService = new VenueService(venueRepository: venueRepository)

    @Unroll("#rationale")
    def "returns correct count of venues"() {
        given:
        venueRepository.getAll() >> numberOf.openVenues()

        when:
        List venues = venueService.findNearestTo(OPEN_TIMES_CRITERIA, FACILITIES_CRITERIA, USER_LOCATION)

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
        List venues = venueService.findNearestTo(OPEN_TIMES_CRITERIA, FACILITIES_CRITERIA, USER_LOCATION)

        then:
        venues.size() == 10
    }

    def "only returns venues that meet facilities criteria"() {
        given:
        venueRepository.getAll() >> 15.venuesWithFacilities() + 5.venuesWithoutFacilities()

        when:
        List venues = venueService.findNearestTo(OPEN_TIMES_CRITERIA, FACILITIES_CRITERIA, USER_LOCATION)

        then:
        venues.size() == 15
    }

    def "returns 50 closest venues, ordered ascending by distance"() {
        given:
        List nearbyVenues = 50.openVenues()
        nearbyVenues = assignUniqueIds(nearbyVenues)
        venueRepository.getAll() >> 50.openVenues() + nearbyVenues
        LocationsCriteria locationsCriteria = Mock()
        locationsCriteria.distancesTo(_ as Venue) >>> (99..0).collect { [(new Coordinates(1.0,it)): new Distance(value:it, unit:KM)] }

        when:
        List venues = venueService.findNearestTo(OPEN_TIMES_CRITERIA, FACILITIES_CRITERIA, locationsCriteria)

        then:
        venues.venue == nearbyVenues.reverse()
    }

    def "when no matching Venue already exists, then saves new Venue"() {
        given:
        Venue venue = new Venue(name: 'name', location: new Coordinates(1.0, 0.1))
        venueRepository.findByNameAndCoordinates('name', new Coordinates(1.0, 0.1)) >> null
        venueRepository.save(venue) >> 99

        when:
        long result = venueService.save(venue)

        then:
        result == 99
    }

    def "when matching Venue is found, then updates existing Venue"() {
        given:
        Venue venue = new Venue(id: 0, name: 'name', location: new Coordinates(1.0, 0.1))
        Venue matchingVenue = new Venue(id: 99)
        venueRepository.findByNameAndCoordinates('name', new Coordinates(1.0, 0.1)) >> matchingVenue

        when:
        long result = venueService.save(venue)

        then:
        result == 99
        1 * venueRepository.update({ it == venue && it.id == 99 })
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
                [isOpen: { openTimesCriteria -> openTimesCriteria == VenueServiceSpec.OPEN_TIMES_CRITERIA },
                        hasFacilities: { facilitiesCriteria -> true },
                        distanceTo: { coordinates, distanceUnit -> new Distance(value: 0, unit: KM) }] as Venue
            }
        }

        List closedVenues() {
            venuesWithTemplate {
                [isOpen: { openTimesCriteria -> false },
                        hasFacilities: { facilities -> true }] as Venue
            }
        }

        List venuesWithFacilities() {
            venuesWithTemplate {
                [isOpen: { openTimesCriteria -> true },
                        hasFacilities: { facilitiesCriteria -> facilitiesCriteria == VenueServiceSpec.FACILITIES_CRITERIA },
                        distanceTo: { coordinates, distanceUnit -> new Distance(value: 0, unit: KM) }] as Venue
            }
        }

        List venuesWithoutFacilities() {
            venuesWithTemplate {
                [isOpen: { openTimesCriteria -> true },
                        hasFacilities: { facilitiesCriteria -> false }] as Venue
            }
        }

        def venuesWithTemplate(Closure c) {
            if (this == 0) return []
            (0..(this - 1)).collect { c() }
        }
    }

    static final OPEN_TIMES_CRITERIA = new OpenTimesCriteria()
    static final FACILITIES_CRITERIA = new FacilitiesCriteria()
    LocationsCriteria USER_LOCATION = Mock(LocationsCriteria) {
        distancesTo(_) >> [(new Coordinates(1.0,0.1)): new Distance(value: 1.0, unit: KM)]
    }
}
