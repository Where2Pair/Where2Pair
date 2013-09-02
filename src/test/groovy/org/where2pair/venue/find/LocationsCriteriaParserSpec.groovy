package org.where2pair.venue.find

import spock.lang.Specification

import static org.where2pair.venue.find.LocationsCriteriaBuilder.locationsCriteria

class LocationsCriteriaParserSpec extends Specification {

    Map params = [:]
    LocationsCriteriaParser locationsCriteriaParser = new LocationsCriteriaParser()

    def "creates locations criteria based on location and distance unit"() {
        given:
        params.'location1' = '1.0,0.1'
        params.'distanceUnit' = 'miles'
        def expectedLocationsCriteria = locationsCriteria().with([location1: [1.0, 0.1]]).withDistanceUnit('miles')

        when:
        def locationsCriteria = locationsCriteriaParser.parse(params)

        then:
        locationsCriteria == expectedLocationsCriteria
    }

    def "supports multiple supplied locations"() {
        given:
        params."location1" = '1.0,0.1'
        params."location2" = '2.0,0.2'
        params.'distanceUnit' = 'miles'
        def expectedLocationsCriteria = locationsCriteria().with([location1: [1.0, 0.1], location2: [2.0, 0.2]]).withDistanceUnit('miles')

        when:
        def locationsCriteria = locationsCriteriaParser.parse(params)

        then:
        locationsCriteria == expectedLocationsCriteria
    }

    def "defaults to km distance unit when none supplied"() {
        given:
        params.'location1' = '1.0,0.1'
        def expectedLocationsCriteria = locationsCriteria().with([location1: [1.0, 0.1]]).withDistanceUnit('km')

        when:
        def locationsCriteria = locationsCriteriaParser.parse(params)

        then:
        locationsCriteria == expectedLocationsCriteria
    }

    def "given no locations returns criteria with zero locations"() {
        when:
        def locationsCriteria = locationsCriteriaParser.parse(params)

        then:
        locationsCriteria.locations.size() == 0
    }

}
