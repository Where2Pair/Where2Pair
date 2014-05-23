package org.where2pair.read.venue

import static OpenPeriodBuilder.on
import static VenueBuilder.aVenue
import static VenueDetailsBuilder.venueDetails
import static java.lang.Integer.parseInt
import static org.where2pair.common.venue.CoordinatesBuilder.coordinates
import static org.where2pair.common.venue.CoordinatesBuilder.someCoordinates
import static org.where2pair.common.venue.Facility.WIFI
import static org.where2pair.read.venue.DayOfWeek.FRIDAY
import static org.where2pair.read.venue.DayOfWeek.MONDAY
import static org.where2pair.read.venue.DayOfWeek.SATURDAY
import static org.where2pair.read.venue.DayOfWeek.SUNDAY
import static org.where2pair.read.venue.DayOfWeek.parseDayOfWeek

import org.where2pair.common.venue.Coordinates
import org.where2pair.common.venue.SimpleTime
import org.where2pair.read.venue.opentimes.OpenTimesCriteriaFactory
import spock.lang.Specification
import spock.lang.Unroll

import java.util.regex.Matcher

class VenueServiceSpec extends Specification {

    static final OPEN_ANY_TIME = openAnyTimeCriteria()
    static final ANY_FACILITIES = new FacilitiesCriteria(requestedFacilities: [])
    static final SINGLE_LOCATION = LocationsCriteria.distanceInMilesTo(someCoordinates())
    static final TODAY = SATURDAY
    static final TOMORROW = SUNDAY

    def venueRepository = Mock(VenueRepository)
    def venueService = new VenueService(venueRepository: venueRepository)

    @Unroll
    def 'finds at most 50 venues'() {
        given:
        venueRepository.all >> [aVenue().build()] * numberOfVenues

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

    @Unroll
    def 'finds only open venues'() {
        given:
        def openTimesCriteria = parseOpenTimesCriteria(searchCriteria)
        def venueWithOpenPeriod = parseVenue(openFrom, openUntil, openDay)
        venueRepository.all >> [venueWithOpenPeriod]

        when:
        List<VenueWithDistances> openVenues = venueService.find(openTimesCriteria, ANY_FACILITIES, SINGLE_LOCATION)

        then:
        (openVenues.size() == 1) == expectedOpen

        where:
        searchCriteria       | openFrom | openUntil | openDay  | expectedOpen
        'anytime'            | '12:00'  | '13:00'   | MONDAY   | true
        'anytime Monday'     | '12:00'  | '13:00'   | MONDAY   | true
        'anytime Tuesday'    | '12:00'  | '13:00'   | MONDAY   | false
        '12:00-13:00 Monday' | '12:00'  | '13:00'   | MONDAY   | true
        '12:00-13:00 Monday' | '12:00'  | '13:00'   | FRIDAY   | false
        '12:00-14:00 Monday' | '12:00'  | '13:00'   | MONDAY   | false
        '12:00-13:00'        | '12:00'  | '13:00'   | TODAY    | true
        '12:00-13:00'        | '12:00'  | '13:00'   | TOMORROW | false
        '12:00-? Monday'     | '12:00'  | '13:00'   | MONDAY   | true
        '14:00-? Monday'     | '12:00'  | '13:00'   | MONDAY   | false
        '?-13:00 Monday'     | '12:00'  | '13:00'   | MONDAY   | true
        '?-14:00 Monday'     | '12:00'  | '13:00'   | MONDAY   | false
        '12:00-?'            | '12:00'  | '13:00'   | TODAY    | true
        '12:00-?'            | '12:00'  | '13:00'   | TOMORROW | false
        '?-13:00'            | '12:00'  | '13:00'   | TODAY    | true
        '?-13:00'            | '12:00'  | '13:00'   | TOMORROW | false
    }

    def 'finds only venues with requested facilities'() {
        given:
        def facilitiesCriteria = new FacilitiesCriteria([WIFI] as HashSet)
        def venueWithWifi = aVenue().with(venueDetails().withFacilities(WIFI)).build()
        def venueWithoutWifi = aVenue().with(venueDetails().withNoFacilities()).build()
        venueRepository.all >> ten(venueWithWifi) + ten(venueWithoutWifi) + five(venueWithWifi)

        when:
        List<VenueWithDistances> venues = venueService.find(OPEN_ANY_TIME, facilitiesCriteria, SINGLE_LOCATION)

        then:
        venues.size() == 15
    }

    def 'finds venues ordered ascending by distance'() {
        given:
        def expectedVenueOrdering = create100VenuesOrderedAscendingByDistanceFrom(SINGLE_LOCATION)
        def jumbledVenues = expectedVenueOrdering.clone().sort { new Random().nextInt() }
        venueRepository.all >> jumbledVenues

        when:
        List<VenueWithDistances> venues = venueService.find(OPEN_ANY_TIME, ANY_FACILITIES, SINGLE_LOCATION)

        then:
        venues.venue == expectedVenueOrdering[0..49]
    }

    private static OpenTimesCriteria parseOpenTimesCriteria(String searchCriteria) {
        SimpleTime openFrom = null
        SimpleTime openUntil = null
        DayOfWeek openDay = null

        Matcher timesMatcher = searchCriteria =~ /^([[\d^\W]&&\S]+)[\s.]*/
        Matcher dayMatcher = searchCriteria =~ /.*\s(\w+)$/

        if (dayMatcher) {
            def day = dayMatcher[0][1]
            openDay = parseDayOfWeek(day)
        }
        if (timesMatcher) {
            def times = timesMatcher[0][1]
            (openFrom, openUntil) = parseTimesFrom(times)
        }

        newOpenTimesCriteriaFactory().createOpenTimesCriteria(openFrom, openUntil, openDay)
    }

    private static List<SimpleTime> parseTimesFrom(String searchCriteria) {
        String[] times = searchCriteria.split('-')
        times.collect {
            if (it != '?') {
                def numbers = it.split(':').collect { parseInt(it) }
                return new SimpleTime(numbers[0], numbers[1])
            }
        }
    }

    private static Venue parseVenue(String openFrom, String openUntil, DayOfWeek openDay) {
        def (SimpleTime openFromTime, SimpleTime openUntilTime) = parseSimpleTimes(openFrom, openUntil)
        aVenue().with(venueDetails().withOpenPeriod(on(openDay).from(openFromTime).until(openUntilTime))).build()
    }

    private static List<SimpleTime> parseSimpleTimes(startTime, endTime) {
        def (int startHour, int startMinute) = parse(startTime)
        def (int endHour, int endMinute) = parse(endTime)
        [new SimpleTime(startHour, startMinute), new SimpleTime(endHour, endMinute)]
    }

    private static List<Integer> parse(time) {
        time.split(':').collect { parseInt(it) }
    }

    private static List<Venue> five(Venue venue) {
        [venue] * 5
    }

    private static List<Venue> ten(Venue venue) {
        [venue] * 10
    }

    private static List<Venue> create100VenuesOrderedAscendingByDistanceFrom(LocationsCriteria locationsCriteria) {
        Coordinates location = locationsCriteria.locations[0]
        List<Venue> venues = []
        (0..99).each {
            venues << aVenue().with(venueDetails().withLocation(
                    coordinates().withLatitude(location.lat + it).withLongitude(location.lng))).build()
        }
        venues
    }

    private static OpenTimesCriteria openAnyTimeCriteria() {
        newOpenTimesCriteriaFactory().createOpenTimesCriteria(null, null, null)
    }

    private static OpenTimesCriteriaFactory newOpenTimesCriteriaFactory() {
        def timeProvider = [today: { TODAY }] as TimeProvider
        new OpenTimesCriteriaFactory(timeProvider)
    }
}

