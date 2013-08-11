package org.where2pair.grails

import static org.where2pair.DayOfWeek.MONDAY
import static org.where2pair.DayOfWeek.TUESDAY
import static org.where2pair.grails.GormVenueBuilder.aGormVenue

import org.where2pair.Address
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
	
	def "should load all GormVenues through dao and map to Venues"() {
		given:
		GormVenue gormVenue = new GormVenue(
				latitude: 1.0,
				longitude: 0.1,
				addressLine1: 'addressLine1',
				addressLine2: 'addressLine2',
				addressLine3: 'addressLine3',
				city: 'city',
				postcode: 'postcode',
				phoneNumber: '01234567890',
				openPeriods: [
	              new GormOpenPeriod(day: MONDAY, openHour: 12, closeHour:18)
	            ],
				features: ['wifi', 'mobile payments'])
		gormVenue.id = 11L
		gormVenueDaoService.getAll() >> [gormVenue]
		
		when:
		List venues = gormVenueRepository.getAll()
		
		then:
		venues.size() == 1
		venues[0].location == new Coordinates(lat: 1.0, lng: 0.1)
		venues[0].address == new Address(
			addressLine1: 'addressLine1',
			addressLine2: 'addressLine2',
			addressLine3: 'addressLine3',
			city: 'city',
			postcode: 'postcode',
			phoneNumber: '01234567890'
		)
		venues[0].weeklyOpeningTimes[MONDAY] ==
			new DailyOpeningTimes(openPeriods: [
				new OpenPeriod(new SimpleTime(12, 0), new SimpleTime(18, 0))
            ])
		!venues[0].weeklyOpeningTimes[TUESDAY].openPeriods
		venues[0].features == ['wifi', 'mobile payments'] as HashSet
	}

	def "should load a GormVenue through dao and map to Venue"() {
		given:
		long id = 1L
		GormVenue gormVenue = aGormVenue()
        gormVenue.id = id
		gormVenueDaoService.get(id) >> gormVenue

		when:
		Venue venue = gormVenueRepository.get(id)

		then:
        venue.id == id
		venue.name == gormVenue.name
	}

	def "should map objects and save through dao"() {
		given:
		VenueDto venueDto = new VenueDto(
				name: 'my venue',
				latitude: 1.0,
				longitude: 0.1,
				addressLine1: 'addressLine1',
				addressLine2: 'addressLine2',
				addressLine3: 'addressLine3',
				city: 'city',
				postcode: 'postcode',
				phoneNumber: '01234567890',
				openHours: [monday: [[openHour: 12, openMinute: 0, closeHour: 18, closeMinute: 30]],
					tuesday: [[openHour: 8, openMinute: 0, closeHour: 11, closeMinute: 0]]],
				features: ['wifi', 'mobile payments']
		)
		GormVenue gormVenue = new GormVenue(
				name: 'my venue',
				latitude: 1.0,
				longitude: 0.1,
				addressLine1: 'addressLine1',
				addressLine2: 'addressLine2',
				addressLine3: 'addressLine3',
				city: 'city',
				postcode: 'postcode',
				phoneNumber: '01234567890',
				openPeriods: [
	              new GormOpenPeriod(day: MONDAY, openHour: 12, closeHour:18, closeMinute: 30),
				  new GormOpenPeriod(day: TUESDAY, openHour: 8, closeHour:11)
	            ],
				features: ['wifi', 'mobile payments'])
		GormVenue storedGormVenue = gormVenue
		storedGormVenue.id = 10
		gormVenueDaoService.save(gormVenue) >> storedGormVenue

		when:
		long savedId = gormVenueRepository.save(venueDto)

		then:
		savedId == 10
	}
}
