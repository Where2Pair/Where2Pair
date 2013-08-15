
package org.where2pair.grails

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll;
import static org.where2pair.grails.ConstraintsValidator.validateConstraints

@TestFor(GormVenue)
class GormVenueSpec extends Specification {

	def setup() {
		mockForConstraintsTests(GormVenue)
	}
	
	def "test saving venue with minimal properties set"() {
		given:
		def venue = new GormVenue(name: 'name',
			latitude: 1.0,
			longitude: -1.0,
			openPeriods: [new GormOpenPeriod()])
		
		when:
		boolean validated = venue.validate()
		
		then:
		println venue.errors
		validated
	}
	
	@Unroll
	def "test venue constraints: checking #field for #error"() {
		when:
		def venue = new GormVenue("$field": val)
		
		then:
		validateConstraints(venue, field, error)
		
		where:
		field			| val		| error
		'name'			| null		| 'nullable'
		'name'			| ''		| 'blank'
		'latitude'		| 90.1		| 'max'
		'latitude'		| -90.1		| 'min'
		'longitude'		| 180.1		| 'max'
		'longitude'		| -180.1	| 'min'
		'openPeriods'	| []		| 'minSize'
	}
}