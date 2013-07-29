package org.where2pair.grails

import static org.where2pair.DayOfWeek.MONDAY
import static org.where2pair.DayOfWeek.TUESDAY

import org.where2pair.Coordinates
import org.where2pair.DailyOpeningTimes
import org.where2pair.Venue
import org.where2pair.DailyOpeningTimes.OpenPeriod
import org.where2pair.DailyOpeningTimes.SimpleTime

import spock.lang.Specification

class GormVenueRepositorySpec extends Specification {

	GormVenueDaoService gormVenueDaoService = Mock()
	
	GormVenueRepository gormVenueRepository = new GormVenueRepository(
		gormVenueDaoService: gormVenueDaoService
	)
	
	def "should load objects through dao and map to Venue objects"() {
		given:
		GormVenue grailsVenue = new GormVenue(
			latitude: 1.0, 
			longitude: 0.1,
			openPeriods: [new GormOpenPeriod(day: MONDAY, openHour: 12, closeHour:18)])
		gormVenueDaoService.getAll() >> [grailsVenue]
		
		when:
		List venues = gormVenueRepository.getAll()
	
		then:
		venues.size() == 1
		with (venues[0]) { Venue venue ->
			venue.location == new Coordinates(lat: 1.0, lng: 0.1)
			venue.weeklyOpeningTimes[MONDAY] ==
				new DailyOpeningTimes(openPeriods: [new OpenPeriod(new SimpleTime(12, 0), new SimpleTime(18, 0))])
			!venue.weeklyOpeningTimes[TUESDAY].openPeriods
		}
	}
	
	def "should map objects and save through dao"() {
		given:
		VenueDTO venueDTO = new VenueDTO(
			latitude: 1.0,
			longitude: 0.1,
			openHours: [monday: [[openHour: 12, openMinute: 0, closeHour: 18, closeMinute: 30]],
						tuesday: [[openHour: 8, openMinute: 0, closeHour: 11, closeMinute: 0]]]
		)
		GormVenue gormVenue = new GormVenue(
			latitude: 1.0,
			longitude: 0.1,
			openPeriods: [new GormOpenPeriod(day: MONDAY, openHour: 12, openMinute: 0, closeHour: 18, closeMinute: 30),
				new GormOpenPeriod(day: TUESDAY, openHour: 8, openMinute: 0, closeHour: 11, closeMinute: 0)]
		)
		
		when:
		gormVenueRepository.save(venueDTO)
		
		then:
		1 * gormVenueDaoService.save(gormVenue)
	}
}
