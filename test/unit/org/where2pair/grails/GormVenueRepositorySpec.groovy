package org.where2pair.grails

import static org.where2pair.DayOfWeek.MONDAY
import static org.where2pair.DayOfWeek.TUESDAY
import static org.where2pair.grails.GormVenueBuilder.aGormVenue

import org.where2pair.Address
import org.where2pair.Coordinates
import org.where2pair.DailyOpeningTimes
import org.where2pair.Venue
import org.where2pair.WeeklyOpeningTimesBuilder
import org.where2pair.DailyOpeningTimes.OpenPeriod
import org.where2pair.DailyOpeningTimes.SimpleTime

import spock.lang.Specification

class GormVenueRepositorySpec extends Specification {

	GormVenueDaoService gormVenueDaoService = Mock()

	GormVenueRepository gormVenueRepository = new GormVenueRepository(
		gormVenueDaoService: gormVenueDaoService
	)
	
	def "loads all GormVenues through dao and map to Venues"() {
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

	def "loads a GormVenue through dao and map to Venue"() {
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

	def "maps Venues and saves through dao"() {
		given:
		WeeklyOpeningTimesBuilder builder = new WeeklyOpeningTimesBuilder()
		builder.addOpenPeriod(MONDAY, new SimpleTime(12, 0), new SimpleTime(18, 30))
		builder.addOpenPeriod(TUESDAY, new SimpleTime(8, 0), new SimpleTime(11, 0))
		Venue venue = new Venue(
			name: 'my venue',
			location: new Coordinates(1.0, 0.1),
			address: new Address(
				addressLine1: 'addressLine1',
				addressLine2: 'addressLine2',
				addressLine3: 'addressLine3',
				city: 'city',
				postcode: 'postcode',
				phoneNumber: '01234567890'
			),
			weeklyOpeningTimes: builder.build(),
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
		long savedId = gormVenueRepository.save(venue)

		then:
		savedId == 10
	}
	
	def "finds a Venue by name and coordinates from dao"() {
		given:
		long id = 1L
		GormVenue gormVenue = aGormVenue()
		gormVenue.id = id
		gormVenueDaoService.findByNameAndCoordinates('name', new Coordinates(1.0, 0.1)) >> gormVenue

		when:
		Venue venue = gormVenueRepository.findByNameAndCoordinates('name', new Coordinates(1.0, 0.1))

		then:
		venue.id == id
		venue.name == gormVenue.name
	}
	
	def "updates and existing Venue"() {
		given:
		WeeklyOpeningTimesBuilder builder = new WeeklyOpeningTimesBuilder()
		builder.addOpenPeriod(MONDAY, new SimpleTime(12, 0), new SimpleTime(18, 30))
		Venue venue = new Venue(
			id: 99,
			name: 'my venue',
			location: new Coordinates(1.0, 0.1),
			address: new Address(
				addressLine1: 'addressLine1',
				addressLine2: 'addressLine2',
				addressLine3: 'addressLine3',
				city: 'city',
				postcode: 'postcode',
				phoneNumber: '01234567890'
			),
			weeklyOpeningTimes: builder.build(),
			features: ['wifi', 'mobile payments']
		)
		GormVenue existingGormVenue = new GormVenue()
		gormVenueDaoService.get(99) >> existingGormVenue
		
		when:
		gormVenueRepository.update(venue)
		
		then:
		1 * gormVenueDaoService.save({
			it == existingGormVenue
			it.name = 'my venue'
			it.latitude = 1.0
			it.longitude = 0.1
			it.addressLine1 = 'addressLine1'
			it.addressLine2 = 'addressLine2'
			it.addressLine3 = 'addressLine3'
			it.city = 'city'
			it.postcode = 'postcode'
			it.phoneNumber = 'phoneNumber'
			it.openPeriods = [new GormOpenPeriod(day: MONDAY, openHour: 12, openMinute: 0, closeHour: 18, closeMinute: 30)]
			it.features = ['wifi', 'mobile payments']
		})
	}
}
