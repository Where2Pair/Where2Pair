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
		GormVenue gormVenue = mapVenueToGormVenue(venue)
		GormVenue storedGormVenue = gormVenueDaoService.save(gormVenue)
		storedGormVenue.id
	}	
	
	private GormVenue mapVenueToGormVenue(Venue venue) {
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
		
		new GormVenue(
            name: venue.name,
			latitude: venue.location.lat,
			longitude: venue.location.lng,
			addressLine1: venue.address.addressLine1,
			addressLine2: venue.address.addressLine2,
			addressLine3: venue.address.addressLine3,
			city: venue.address.city,
			postcode: venue.address.postcode,
			phoneNumber: venue.address.phoneNumber,
			openPeriods: openPeriods,
			features: venue.features.collect()
		)
	}
}
