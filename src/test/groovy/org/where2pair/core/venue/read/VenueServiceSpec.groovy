package org.where2pair.core.venue.read

import org.where2pair.core.venue.common.Coordinates
import org.where2pair.core.venue.common.CoordinatesBuilder
import org.where2pair.core.venue.common.SimpleTime
import spock.lang.Specification
import spock.lang.Unroll

import static CoordinatesBuilder.coordinates
import static CoordinatesBuilder.someCoordinates
import static org.where2pair.core.venue.read.DayOfWeek.FRIDAY
import static org.where2pair.core.venue.read.DayOfWeek.MONDAY
import static org.where2pair.core.venue.read.Facility.WIFI
import static org.where2pair.core.venue.read.OpenPeriodBuilder.on
import static org.where2pair.core.venue.read.VenueBuilder.aVenue

class VenueServiceSpec extends Specification {

    static final OPEN_ANY_TIME = OpenTimesCriteria.openAnyTime()
    static final ANY_FACILITIES = FacilitiesCriteria.anyFacilities()
    static final SINGLE_LOCATION = LocationsCriteria.distanceInMilesTo(someCoordinates())

    def venueRepository = Mock(VenueRepository)
    def venueService = new VenueService(venueRepository: venueRepository)

    @Unroll
    def 'finds at most 50 venues'() {
        given:
        venueRepository.getAll() >> [aVenue().build()] * numberOfVenues

        when:
        List<VenueWithDistances> venues = venueService.find(OPEN_ANY_TIME, ANY_FACILITIES, SINGLE_LOCATION)

        then:
        venues.size() == expectedVenueCount

        where:
        numberOfVenues | expectedVenueCount
        100            | 50
        49             | 49
        0              | 0
    }

    def 'finds only open venues'() {
        given:
        def openTimesCriteria = new OpenTimesCriteria(dayOfWeek: MONDAY, openFrom: new SimpleTime(12, 15), openUntil: new SimpleTime(12, 45))
        def openVenue = aVenue().withOpenPeriod(on(MONDAY).from(12).until(13)).build()
        def closedVenue = aVenue().withOpenPeriod(on(FRIDAY).from(18).until(19)).build()
        venueRepository.getAll() >> five(openVenue) + ten(closedVenue) + five(openVenue)

        when:
        List<VenueWithDistances> venues = venueService.find(openTimesCriteria, ANY_FACILITIES, SINGLE_LOCATION)

        then:
        venues.size() == 10
    }

    def 'finds only venues with requested facilities'() {
        given:
        def facilitiesCriteria = new FacilitiesCriteria([WIFI])
        def venueWithWifi = aVenue().withFacilities(WIFI).build()
        def venueWithoutWifi = aVenue().withNoFacilities().build()
        venueRepository.getAll() >> ten(venueWithWifi) + ten(venueWithoutWifi) + five(venueWithWifi)

        when:
        List<VenueWithDistances> venues = venueService.find(OPEN_ANY_TIME, facilitiesCriteria, SINGLE_LOCATION)

        then:
        venues.size() == 15
    }

    def 'finds venues ordered ascending by distance'() {
        given:
        def expectedVenueOrdering = create100VenuesOrderedAscendingByDistanceFrom(SINGLE_LOCATION)
        def jumbledVenues = expectedVenueOrdering.sort { new Random().nextInt() }
        venueRepository.getAll() >> jumbledVenues

        when:
        List<VenueWithDistances> venues = venueService.find(OPEN_ANY_TIME, ANY_FACILITIES, SINGLE_LOCATION)

        then:
        venues.venue == expectedVenueOrdering
    }

    List<Venue> five(Venue venue) {
        [venue] * 5
    }

    List<Venue> ten(Venue venue) {
        [venue] * 10
    }

    List<Venue> create100VenuesOrderedAscendingByDistanceFrom(LocationsCriteria locationsCriteria) {
        Coordinates location = locationsCriteria.locations[0]
        List<Venue> venues = []
        (0..99).each {
            venues << aVenue().withLocation(coordinates().withLatitude(location.lat + it).withLongitude(location.lng)).build()
        }
        venues
    }
}
