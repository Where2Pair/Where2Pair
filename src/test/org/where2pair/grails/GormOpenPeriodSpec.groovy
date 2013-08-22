package org.where2pair.grails

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll;
import static org.where2pair.grails.ConstraintsValidator.validateConstraints

@TestFor(GormOpenPeriod)
class GormOpenPeriodSpec extends Specification {

	def setup() {
		mockForConstraintsTests(GormOpenPeriod)
	}
	
	@Unroll
	def "test open period constraints: checking #field for #error"() {
		when:
		def venue = new GormOpenPeriod(venue: new GormVenue(), "$field": val)
		
		then:
		validateConstraints(venue, field, error)
		
		where:
		field			| val		| error
		'day'			| null		| 'nullable'
		'openHour'		| -1		| 'range'
		'openHour'		| 36		| 'range'
		'openMinute'	| -1		| 'range'
		'openMinute'	| 60		| 'range'
		'closeHour'		| -1		| 'range'
		'closeHour'		| 36		| 'range'
		'closeMinute'	| -1		| 'range'
		'closeMinute'	| 60		| 'range'
	}
}