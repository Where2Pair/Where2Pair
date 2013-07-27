package org.where2pair.grails

import grails.test.mixin.TestFor
import spock.lang.Specification


class GrailsVenueDaoServiceSpec extends Specification {

	GrailsVenueDaoService grailsVenueDaoService = new GrailsVenueDaoService()
	
	def "should fetch all GrailsVenues from db"() {
		given:
		List venues = 100.venues()
		venues.each { it.save(failOnError: true) }
		
		when:
		List fetchedVenues = grailsVenueDaoService.getAll()		
	
		then:
		fetchedVenues == venues	
	}
	
	def setupSpec() {
		Integer.mixin(VenuesMixin)
	}
	
	def cleanupSpec() {
		Integer.metaClass = null
	}
	
	static class VenuesMixin {
		static List venues(int count) {
			100.collect { new GrailsVenue() }
		}
	}
}
