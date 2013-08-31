package org.where2pair.venue.find

import static org.where2pair.venue.find.LocationsCriteriaBuilder.locationsCriteria
import spock.lang.Specification;

class LocationsCriteriaParserSpec extends Specification {

	Map params = [:]
	LocationsCriteriaParser locationsCriteriaParser = new LocationsCriteriaParser()
	
	def "creates locations criteria based on location and distance unit"() {
		given:
		params.'location1' = '1.0,0.1'
		params.'distanceUnit' = 'miles'
		def expectedLocationsCriteria = locationsCriteria().withLocation(1.0,0.1).withDistanceUnit('miles')
		
		when:
		def locationsCriteria = locationsCriteriaParser.parse(params)

		then:
		locationsCriteria == expectedLocationsCriteria
	}

	def "supports multiple supplied locations"() {
		given:
		(1..1000).each { params."location$it" = '1.0,0.1'}
		params.'distanceUnit' = 'miles'
		def expectedLocationsCriteria = locationsCriteria().withLocations([[1.0,0.1]] * 1000).withDistanceUnit('miles')

		when:
		def locationsCriteria = locationsCriteriaParser.parse(params)

		then:
		locationsCriteria == expectedLocationsCriteria
	}

	def "defaults to km distance unit when none supplied"() {
		given:
		params.'location1' = '1.0,0.1'
		def expectedLocationsCriteria = locationsCriteria().withLocation(1.0,0.1).withDistanceUnit('km')
		
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
