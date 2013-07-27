package org.where2pair

import org.joda.time.DateTime

import spock.lang.Specification;
import static spock.util.matcher.HamcrestMatchers.closeTo

class TimeProviderSpec extends Specification {

	TimeProvider timeProvider = new TimeProvider()
	
	def "should return current time"() {
		given:
		long actualTime = new DateTime().millis
		
		when:
		long currentTime = timeProvider.currentTime.millis
		
		then:
		currentTime closeTo(actualTime, 100)
	}
	
}
