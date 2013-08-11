package org.where2pair.grails

import org.where2pair.Venue

import static org.where2pair.DayOfWeek.MONDAY
import static org.where2pair.grails.GormVenueBuilderForIntegration.aGormVenue

import grails.plugin.spock.IntegrationSpec

class GormVenueDaoServiceSpec extends IntegrationSpec {

    GormVenueDaoService gormVenueDaoService = new GormVenueDaoService()

    def "retrieves venue by id"() {
        given:
        GormVenue venue = aGormVenue()
        GormVenue savedVenue = gormVenueDaoService.save(venue)

        when:
        GormVenue retrievedVenue = gormVenueDaoService.get(savedVenue.id)

        then:
        retrievedVenue.name == venue.name
    }
	
	def "fetches all GormVenues from db"() {
		given:
		List venues = 100.venues()
		venues.each { gormVenueDaoService.save(it) }
		
		when:
		List fetchedVenues = gormVenueDaoService.getAll()		
	
		then:
		fetchedVenues == venues	
	}
	
	def "saves associated open periods"() {
		given:
		GormOpenPeriod openPeriod = new GormOpenPeriod(day: MONDAY, openHour: 12, closeHour: 24)
		GormVenue venue = aGormVenue()
		venue.openPeriods = [openPeriod]
		gormVenueDaoService.save(venue)
	
		when:
		GormVenue fetchedVenue = gormVenueDaoService.getAll()[0]
		
		then:
		GormOpenPeriod.list().size() == 1
		fetchedVenue.openPeriods == [openPeriod] as Set
	}
	
	def "test validation errors"() {
		
	}
	
	def setupSpec() {
		Integer.mixin(VenuesMixin)
	}
	
	def cleanupSpec() {
		Integer.metaClass = null
	}
	
	@Category(Integer)
	static class VenuesMixin {
		List venues() {
			(0..this).collect { aGormVenue() }
		}
	}
}
