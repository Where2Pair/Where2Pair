package org.where2pair.grails

import static org.joda.time.DateTimeConstants.MONDAY
import static org.joda.time.DateTimeConstants.TUESDAY

import org.where2pair.Coordinates
import org.where2pair.DailyOpeningTimes
import org.where2pair.Venue
import org.where2pair.DailyOpeningTimes.OpenPeriod
import org.where2pair.DailyOpeningTimes.SimpleTime

import spock.lang.Specification

class GrailsVenueRepositorySpec extends Specification {

	GrailsVenueDaoService grailsVenueDaoService = Mock()
	
	GrailsVenueRepository grailsVenueRepository = new GrailsVenueRepository(
		grailsVenueDaoService: grailsVenueDaoService
	)
	
	def "should load objects through dao and map to Venue objects"() {
		given:
		GrailsVenue grailsVenue = new GrailsVenue(
			latitude: 1.0, 
			longitude: 0.1,
			openPeriods: [new GrailsOpenPeriod(day: 1, openHour: 12, closeHour:18)])
		grailsVenueDaoService.getAll() >> [grailsVenue]
		
		when:
		List venues = grailsVenueRepository.getAll()
	
		then:
		venues.size() == 1
		with (venues[0]) { Venue venue ->
			venue.location == new Coordinates(lat: 1.0, lng: 0.1)
			venue.weeklyOpeningTimes.weeklyOpeningTimes[MONDAY] ==
				new DailyOpeningTimes(openPeriods: [new OpenPeriod(new SimpleTime(12, 0), new SimpleTime(18, 0))])
			!venue.weeklyOpeningTimes.weeklyOpeningTimes[TUESDAY].openPeriods
		}
	}
}
