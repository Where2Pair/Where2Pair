package org.where2pair.core.venue.read

import org.where2pair.core.venue.read.LocationsCriteria

import static org.where2pair.core.venue.read.DistanceUnit.KM
import static org.where2pair.core.venue.read.DistanceUnit.MILES

import org.where2pair.core.venue.common.Coordinates
import org.where2pair.core.venue.read.Distance
import org.where2pair.core.venue.read.Venue

import spock.lang.Specification

class LocationsCriteriaSpec extends Specification {

    static final Coordinates COORDS = new Coordinates(1.0, 0.1)
    Venue venue = Mock()
	Distance distance = Mock()
	
    def "expects between 1 and 1000 locations, and km or miles as distance unit"() {
        given:
        LocationsCriteria locationsCriteria = new LocationsCriteria(locations: locations, distanceUnit: distanceUnit)

        when:
        def errors = locationsCriteria.errors

        then:
        !errors

        where:
        locations              	| distanceUnit
        [COORDS]    			| 'miles'
        [COORDS]    			| 'km'
        [COORDS] * 1000		 	| 'miles'
        [COORDS] * 1000		 	| 'km'
    }

    def "rejects more than 1000 supplied locations"() {
        given:
        LocationsCriteria locationsCriteria = new LocationsCriteria(locations: [COORDS] * 1001, distanceUnit: 'miles')

        when:
        def (message, status) = locationsCriteria.errors

        then:
        message == "Only upto 1000 locations are supported at this time."
        status == 413
    }

    def "rejects 0 supplied locations"() {
        given:
        LocationsCriteria locationsCriteria = new LocationsCriteria(locations: [], distanceUnit: 'miles')

        when:
        def (message, status) = locationsCriteria.errors

        then:
        message == "Missing locations from the request parameters. I expect a query in the form: nearest?location1=x1,y1&location2=x2,y2..."
        status == 400
    }

    def "rejects invalid distance unit"() {
        given:
        LocationsCriteria locationsCriteria = new LocationsCriteria(locations: [COORDS], distanceUnit: 'furlongs')

        when:
        def (message, status) = locationsCriteria.errors

        then:
        message == "Distance unit 'FURLONGS' is invalid. Use either 'km' or 'miles' (omitting distanceUnit altogether defaults to 'km')."
        status == 400
    }

    def "returns distance in km from venue given km as distance unit"() {
        given:
        def coords = new Coordinates(0.1, 0.2)
        venue.distanceTo(coords, KM) >> distance
        def locationsCriteria = new LocationsCriteria(locations: [coords], distanceUnit: 'km')

        when:
        def distances = locationsCriteria.distancesTo(venue)

        then:
        distances == [(coords): distance]
    }

    def "returns distance in miles from venue given miles as distance unit"() {
        given:
        def coords = new Coordinates(0.1, 0.2)
        venue.distanceTo(coords, MILES) >> distance
        def locationsCriteria = new LocationsCriteria(locations: [coords], distanceUnit: 'miles')

        when:
        def distances = locationsCriteria.distancesTo(venue)

        then:
        distances == [(coords): distance]
    }

    def "returns distances to all locations from venue"() {
        given:
        def coords1 = new Coordinates(0.1, 0.2)
        def coords2 = new Coordinates(0.2, 0.3)
        def coords3 = new Coordinates(0.3, 0.4)
		Distance distance2 = Mock()
		Distance distance3 = Mock()
        venue.distanceTo(coords1, KM) >> distance
        venue.distanceTo(coords2, KM) >> distance2
        venue.distanceTo(coords3, KM) >> distance3
        def locationsCriteria = new LocationsCriteria(locations: [coords1, coords2, coords3], distanceUnit: 'km')

        when:
        def distances = locationsCriteria.distancesTo(venue)

        then:
        distances == [(coords1): distance, (coords2): distance2, (coords3): distance3]
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
