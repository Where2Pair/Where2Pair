package org.where2pair.venue.find

import org.where2pair.venue.Coordinates
import org.where2pair.venue.Venue
import spock.lang.Specification

import static org.where2pair.venue.find.LocationsCriteriaBuilder.locationsCriteria

class LocationsCriteriaSpec extends Specification {

    static final Coordinates COORDS = new Coordinates(1.0, 0.1)
    Venue venue = Mock()

    def "expects between 1 and 1000 locations, and km or miles as distance unit"() {
        given:
        LocationsCriteria locationsCriteria = new LocationsCriteria(locations: locations, distanceUnit: distanceUnit)

        when:
        def errors = locationsCriteria.errors

        then:
        !errors

        where:
        locations              | distanceUnit
        [location1: COORDS]    | 'miles'
        [location1: COORDS]    | 'km'
        1000.locations(COORDS) | 'miles'
        1000.locations(COORDS) | 'km'
    }

    def "rejects more than 1000 supplied locations"() {
        given:
        LocationsCriteria locationsCriteria = new LocationsCriteria(locations: 1001.locations(COORDS), distanceUnit: 'miles')

        when:
        def (message, status) = locationsCriteria.errors

        then:
        message == "Only upto 1000 locations are supported at this time."
        status == 413
    }

    def "rejects 0 supplied locations"() {
        given:
        LocationsCriteria locationsCriteria = new LocationsCriteria(locations: [:], distanceUnit: 'miles')

        when:
        def (message, status) = locationsCriteria.errors

        then:
        message == "Missing locations from the request parameters. I expect a query in the form: nearest?location1=x1,y1&location2=x2,y2..."
        status == 400
    }

    def "rejects invalid distance unit"() {
        given:
        LocationsCriteria locationsCriteria = new LocationsCriteria(locations: [location1: COORDS], distanceUnit: 'furlongs')

        when:
        def (message, status) = locationsCriteria.errors

        then:
        message == "Distance unit 'FURLONGS' is invalid. Use either 'km' or 'miles' (omitting distanceUnit altogether defaults to 'km')."
        status == 400
    }

    def "returns distance in km from venue given km as distance unit"() {
        given:
        def coords = new Coordinates(0.1, 0.2)
        venue.distanceInKmTo(coords) >> 2.3
        def locationsCriteria = locationsCriteria().with([location1: coords]).withDistanceUnit('km')

        when:
        def distances = locationsCriteria.distancesTo(venue)

        then:
        distances == [location1: 2.3]
    }

    def "returns distance in miles from venue given miles as distance unit"() {
        given:
        def coords = new Coordinates(0.1, 0.2)
        venue.distanceInMilesTo(coords) >> 1.2
        def locationsCriteria = locationsCriteria().with(['location1': coords]).withDistanceUnit('miles')

        when:
        def distances = locationsCriteria.distancesTo(venue)

        then:
        distances == [location1: 1.2]
    }

    def "returns distances to all locations from venue"() {
        given:
        def coords1 = new Coordinates(0.1, 0.2)
        def coords2 = new Coordinates(0.2, 0.3)
        def coords3 = new Coordinates(0.3, 0.4)
        venue.distanceInKmTo(coords1) >> 2.3
        venue.distanceInKmTo(coords2) >> 5.9
        venue.distanceInKmTo(coords3) >> 3.4
        def locationsCriteria = locationsCriteria().with([location1: coords1, location2: coords2, location3: coords3]).withDistanceUnit('km')

        when:
        def distances = locationsCriteria.distancesTo(venue)

        then:
        distances == [location1: 2.3, location2: 5.9, location3: 3.4]
    }

    void setupSpec() {
        Integer.mixin(LocationsMixin)
    }

    void cleanupSpec() {
        Integer.metaClass = null
    }

    @Category(Integer)
    static class LocationsMixin {
        Map locations(Coordinates coords) {
            (1..this).collectEntries { ["location$it": coords] }
        }
    }
}
