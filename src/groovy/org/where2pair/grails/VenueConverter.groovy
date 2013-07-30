package org.where2pair.grails

import org.where2pair.DailyOpeningTimes
import org.where2pair.DailyOpeningTimes.OpenPeriod
import org.where2pair.DayOfWeek
import org.where2pair.Venue

import static org.where2pair.DayOfWeek.MONDAY
import static org.where2pair.DayOfWeek.SUNDAY

class VenueConverter {

	List asVenueDTOs(List venues) { 
		if (!venues)
			return []
		
		venues.collect { Venue venue ->
			Map openHours = (MONDAY..SUNDAY).collectEntries { [dayToString(it), []] }

			venue.weeklyOpeningTimes.each { DayOfWeek day, DailyOpeningTimes dailyOpeningTimes ->
				dailyOpeningTimes.openPeriods.each { OpenPeriod openPeriod ->
					openHours[dayToString(day)] << [
						openHour: openPeriod.start.hour, 
						openMinute: openPeriod.start.minute,
						closeHour: openPeriod.end.hour,
						closeMinute: openPeriod.end.minute]
				}
			}
			
			new VenueDTO(
                name: venue.name,
				latitude: venue.location.lat,
				longitude: venue.location.lng,
				openHours: openHours
			)
		}
	}
	
	List asVenueWithDistanceDTOs(List venuesWithDistance) {
		if (!venuesWithDistance)
			return []
		
		List venues = venuesWithDistance.venue
		List distances = venuesWithDistance.distanceInKm
		List venueDTOs = asVenueDTOs(venues)
		[venueDTOs, distances].transpose().collect { VenueDTO venue, double distance -> 
			new VenueWithDistanceDTO(venue: venue, distanceInKm: distance) 
		}
	}
	
	private String dayToString(DayOfWeek day) {
		day.toString().toLowerCase()
	}
}
