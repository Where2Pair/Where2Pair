package org.where2pair.read.venue

import org.where2pair.common.venue.Coordinates
import org.where2pair.common.venue.CoordinatesBuilder
import org.where2pair.common.venue.SimpleTime
import org.where2pair.read.venue.find.FacilitiesCriteria
import org.where2pair.read.venue.find.LocationsCriteria
import org.where2pair.read.venue.find.OpenTimesCriteria
import spock.lang.Specification
import spock.lang.Unroll

import static OpenPeriodBuilder.on
import static VenueBuilder.aVenue
import static VenueDetailsBuilder.venueDetails
import static org.where2pair.common.venue.CoordinatesBuilder.coordinates
import static org.where2pair.common.venue.CoordinatesBuilder.someCoordinates
import static org.where2pair.read.venue.DayOfWeek.FRIDAY
import static org.where2pair.read.venue.DayOfWeek.MONDAY
import static org.where2pair.read.venue.Facility.WIFI
import static org.where2pair.read.venue.VenueBuilder.aVenue

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
        def openVenue = aVenue().with(venueDetails().withOpenPeriod(on(MONDAY).from(12).until(13))).build()
        def closedVenue = aVenue().with(venueDetails().withOpenPeriod(on(FRIDAY).from(18).until(19))).build()
        venueRepository.getAll() >> five(openVenue) + ten(closedVenue) + five(openVenue)

        when:
        List<VenueWithDistances> venues = venueService.find(openTimesCriteria, ANY_FACILITIES, SINGLE_LOCATION)

        then:
        venues.size() == 10
    }

    def 'finds only venues with requested facilities'() {
        given:
        def facilitiesCriteria = new FacilitiesCriteria([WIFI] as HashSet)
        def venueWithWifi = aVenue().with(venueDetails().withFacilities(WIFI)).build()
        def venueWithoutWifi = aVenue().with(venueDetails().withNoFacilities()).build()
        venueRepository.getAll() >> ten(venueWithWifi) + ten(venueWithoutWifi) + five(venueWithWifi)

        when:
        List<VenueWithDistances> venues = venueService.find(OPEN_ANY_TIME, facilitiesCriteria, SINGLE_LOCATION)

        then:
        venues.size() == 15
    }

    def 'finds venues ordered ascending by distance'() {
        given:
        def expectedVenueOrdering = create100VenuesOrderedAscendingByDistanceFrom(SINGLE_LOCATION)
        def jumbledVenues = expectedVenueOrdering.clone().sort { new Random().nextInt() }
        venueRepository.getAll() >> jumbledVenues

        when:
        List<VenueWithDistances> venues = venueService.find(OPEN_ANY_TIME, ANY_FACILITIES, SINGLE_LOCATION)

        then:
        venues.venue == expectedVenueOrdering[0..49]
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
            venues << aVenue().with(venueDetails().withLocation(coordinates().withLatitude(location.lat + it).withLongitude(location.lng))).build()
        }
        venues
    }
}
