package org.where2pair.infra.venue.web

import org.where2pair.core.venue.Coordinates
import org.where2pair.core.venue.LocationsCriteria
import org.where2pair.infra.venue.web.LocationsCriteriaParser
import spock.lang.Specification

class LocationsCriteriaParserSpec extends Specification {

    Map params = [:]
    LocationsCriteriaParser locationsCriteriaParser = new LocationsCriteriaParser()

    def "creates locations criteria based on location and distance unit"() {
        given:
        params.'location' = ['1.0,0.1']
        params.'distanceUnit' = 'miles'
        def expectedLocationsCriteria = new LocationsCriteria(locations: [new Coordinates(1.0, 0.1)], distanceUnit: 'miles')

        when:
        def locationsCriteria = locationsCriteriaParser.parse(params)

        then:
        locationsCriteria == expectedLocationsCriteria
    }

    def "supports multiple supplied locations"() {
        given:
        params."location" = ['1.0,0.1', '2.0,0.2']
        params.'distanceUnit' = 'miles'
        def expectedLocationsCriteria = new LocationsCriteria(locations: [new Coordinates(1.0, 0.1), new Coordinates(2.0, 0.2)], distanceUnit: 'miles')

        when:
        def locationsCriteria = locationsCriteriaParser.parse(params)

        then:
        locationsCriteria == expectedLocationsCriteria
    }

    def "defaults to km distance unit when none supplied"() {
        given:
        params.'location' = ['1.0,0.1']
        def expectedLocationsCriteria = new LocationsCriteria(locations: [new Coordinates(1.0, 0.1)], distanceUnit: 'km')

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
