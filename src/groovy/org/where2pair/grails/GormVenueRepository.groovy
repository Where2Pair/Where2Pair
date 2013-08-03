package org.where2pair.grails

import org.where2pair.Coordinates
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
                name: venue.name,
				location: new Coordinates(lat: venue.latitude, lng: venue.longitude),
				weeklyOpeningTimes: weeklyOpeningTimes)
	}

	long save(VenueDto venueDto) {
		GormVenue gormVenue = mapVenueDTOToGormVenue(venueDto)
		GormVenue storedGormVenue = gormVenueDaoService.save(gormVenue)
		storedGormVenue.id
	}	
	
	private GormVenue mapVenueDTOToGormVenue(VenueDto venueDto) {
		Set openPeriods = []
		
		venueDto.openHours.each { String day, List openPeriodsList ->
			openPeriodsList.each {
				openPeriods << new GormOpenPeriod(day: DayOfWeek.parseString(day),
					 openHour: it.openHour,
					 openMinute: it.openMinute,
					 closeHour: it.closeHour,
					 closeMinute: it.closeMinute)
			}
		}
		
		new GormVenue(
            name: venueDto.name,
			latitude: venueDto.latitude,
			longitude: venueDto.longitude,
			openPeriods: openPeriods
		)
	}
}
