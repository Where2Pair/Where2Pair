package org.where2pair.core.venue

import org.where2pair.core.venue.common.VenueId
import org.where2pair.core.venue.common.Coordinates
import org.where2pair.core.venue.read.Distance
import org.where2pair.core.venue.read.FacilitiesCriteria
import org.where2pair.core.venue.read.LocationsCriteria
import org.where2pair.core.venue.read.OpenTimesCriteria
import org.where2pair.core.venue.read.Venue
import org.where2pair.core.venue.read.VenueRepository
import org.where2pair.core.venue.read.VenueService
import spock.lang.Specification
import spock.lang.Unroll

import static org.where2pair.core.venue.read.DistanceUnit.KM
import static org.where2pair.core.venue.VenueIdBuilder.aVenueId

class VenueServiceSpec extends Specification {

    VenueRepository venueRepository = Mock()
    VenueService venueService = new VenueService(venueRepository: venueRepository)

    @Unroll("#rationale")
    def "returns correct number of venues"() {
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
        locationsCriteria.distancesTo(_ as Venue) >>> (99..0).collect { [(new Coordinates(1.0,0.1)): new Distance(value:it, unit:KM)] }

        when:
        List venues = venueService.findNearestTo(OPEN_TIMES_CRITERIA, FACILITIES_CRITERIA, locationsCriteria)

        then:
        venues.venue == nearbyVenues.reverse()
    }

    def "saves new Venues"() {
        given:
        Venue venue = new Venue(name: 'name', location: new Coordinates(1.0, 0.1))
        VenueId venueId = aVenueId().build()
        venueRepository.save(venue) >> venueId

        when:
        VenueId result = venueService.save(venue)

        then:
        result == venueId
    }

    def assignUniqueIds(List<Venue> venues) {
        int idIndex = 0
        venues.collect { it.id = aVenueId().withName("${++idIndex}").build(); it }
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
