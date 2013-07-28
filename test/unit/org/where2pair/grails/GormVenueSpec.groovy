
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
	
	@Unroll
	def "test venue coordinate constraints: checking #field for #error"() {
		when:
		def venue = new GormVenue("$field": val)
		
		then:
		validateConstraints(venue, field, error)
		
		where:
		field		| val		| error
		'latitude'	| 41.26		| 'max'
		'latitude'	| -120.9763	| 'min'
		'longitude'	| 115.85	| 'max'
		'longitude'	| -31.97	| 'min'
	}
}