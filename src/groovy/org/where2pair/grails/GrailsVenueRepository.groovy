package org.where2pair.grails

import org.where2pair.Coordinates
import org.where2pair.Venue
import org.where2pair.VenueRepository
import org.where2pair.WeeklyOpeningTimes
import org.where2pair.WeeklyOpeningTimesBuilder
import org.where2pair.DailyOpeningTimes.SimpleTime


class GrailsVenueRepository implements VenueRepository {

	GrailsVenueDaoService grailsVenueDaoService
	
	@Override
	List getAll() {
		grailsVenueDaoService.getAll().collect { createVenue(it) }
	}

	private Venue createVenue(GrailsVenue venue) {
		WeeklyOpeningTimesBuilder weeklyOpeningTimesBuilder = new WeeklyOpeningTimesBuilder()

		venue.openPeriods.each { GrailsOpenPeriod openPeriod ->
			weeklyOpeningTimesBuilder.addOpenPeriod(openPeriod.day,
					new SimpleTime(openPeriod.openHour, openPeriod.openMinute),
					new SimpleTime(openPeriod.closeHour, openPeriod.closeMinute))
		}

		WeeklyOpeningTimes weeklyOpeningTimes = weeklyOpeningTimesBuilder.build()

		new Venue(
				location: new Coordinates(lat: venue.latitude, lng: venue.longitude),
				weeklyOpeningTimes: weeklyOpeningTimes)
	}

}
