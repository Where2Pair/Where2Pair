package org.where2pair.grails

import org.where2pair.Address
import org.where2pair.Coordinates
import org.where2pair.DailyOpeningTimes;
import org.where2pair.DayOfWeek
import org.where2pair.Venue
import org.where2pair.VenueRepository
import org.where2pair.WeeklyOpeningTimes
import org.where2pair.WeeklyOpeningTimesBuilder
import org.where2pair.DailyOpeningTimes.SimpleTime

class GormVenueRepository implements VenueRepository {

	GormVenueDaoService gormVenueDaoService

	@Override
	List getAll() {
		gormVenueDaoService.getAll().collect { mapGormVenueToVenue(it) }
	}

	@Override
	Venue get(long id) {
		mapGormVenueToVenue(gormVenueDaoService.get(id))
	}

	private Venue mapGormVenueToVenue(GormVenue venue) {
		WeeklyOpeningTimesBuilder weeklyOpeningTimesBuilder = new WeeklyOpeningTimesBuilder()

		venue.openPeriods.each { GormOpenPeriod openPeriod ->
			weeklyOpeningTimesBuilder.addOpenPeriod(openPeriod.day,
					new SimpleTime(openPeriod.openHour, openPeriod.openMinute),
					new SimpleTime(openPeriod.closeHour, openPeriod.closeMinute))
		}

		WeeklyOpeningTimes weeklyOpeningTimes = weeklyOpeningTimesBuilder.build()
		new Venue(
				id: venue.id,
				name: venue.name,
				location: new Coordinates(lat: venue.latitude, lng: venue.longitude),
				address: new Address(
				addressLine1: venue.addressLine1,
				addressLine2: venue.addressLine2,
				addressLine3: venue.addressLine3,
				city: venue.city,
				postcode: venue.postcode,
				phoneNumber: venue.phoneNumber
				),
				weeklyOpeningTimes: weeklyOpeningTimes,
				features: venue.features.collect())
	}

	@Override
	long save(Venue venue) {
		GormVenue gormVenue = mapVenuePropertiesToGormVenue(venue, new GormVenue())
		GormVenue storedGormVenue = gormVenueDaoService.save(gormVenue)
		storedGormVenue.id
	}

	private GormVenue mapVenuePropertiesToGormVenue(Venue venue, GormVenue gormVenue) {
		Set openPeriods = []

		venue.weeklyOpeningTimes.each { DayOfWeek day, DailyOpeningTimes dailyOpeningTimes ->
			dailyOpeningTimes.openPeriods.each {
				openPeriods << new GormOpenPeriod(day: day,
				openHour: it.start.hour,
				openMinute: it.start.minute,
				closeHour: it.end.hour,
				closeMinute: it.end.minute)
			}
		}

		gormVenue.name = venue.name
		gormVenue.latitude = venue.location.lat
		gormVenue.longitude = venue.location.lng
		gormVenue.addressLine1 = venue.address.addressLine1
		gormVenue.addressLine2 = venue.address.addressLine2
		gormVenue.addressLine3 = venue.address.addressLine3
		gormVenue.city = venue.address.city
		gormVenue.postcode = venue.address.postcode
		gormVenue.phoneNumber = venue.address.phoneNumber
		gormVenue.openPeriods = openPeriods
		gormVenue.features = venue.features.collect()
		
		gormVenue
	}

	@Override
	public Venue findByNameAndCoordinates(String name, Coordinates coordinates) {
		GormVenue gormVenue = gormVenueDaoService.findByNameAndCoordinates(name, coordinates)
		mapGormVenueToVenue(gormVenue)
	}

	@Override
	public void update(Venue venue) {
		GormVenue existingGormVenue = gormVenueDaoService.get(venue.id)
		GormVenue updatedGormVenue = mapVenuePropertiesToGormVenue(venue, existingGormVenue)
		gormVenueDaoService.save(updatedGormVenue)
	}
}
