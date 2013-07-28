package org.where2pair.grails

import org.where2pair.DailyOpeningTimes
import org.where2pair.DayOfWeek
import org.where2pair.Venue
import org.where2pair.DailyOpeningTimes.OpenPeriod
import static org.where2pair.DayOfWeek.FRIDAY
import static org.where2pair.DayOfWeek.MONDAY
import static org.where2pair.DayOfWeek.SATURDAY
import static org.where2pair.DayOfWeek.SUNDAY
import static org.where2pair.DayOfWeek.THURSDAY
import static org.where2pair.DayOfWeek.TUESDAY
import static org.where2pair.DayOfWeek.WEDNESDAY

class VenueConverter {

	List asVenueDTOs(List venues) { 
		venues.collect { Venue venue ->
			Map openHours = (MONDAY..SUNDAY).collectEntries { [dayToString(it), []] }

			venue.weeklyOpeningTimes.weeklyOpeningTimes.each { DayOfWeek day, DailyOpeningTimes dailyOpeningTimes ->
				dailyOpeningTimes.openPeriods.each { OpenPeriod openPeriod ->
					openHours[dayToString(day)] << [
						openHour: openPeriod.start.hour, 
						openMinute: openPeriod.start.minute,
						closeHour: openPeriod.end.hour,
						closeMinute: openPeriod.end.minute]
				}
			}
			
			new VenueDTO(
				latitude: venue.location.lat,
				longitude: venue.location.lng,
				openHours: openHours
			)
		}
	}
	
	private String dayToString(DayOfWeek day) {
		day.toString().toLowerCase()
	}
}
