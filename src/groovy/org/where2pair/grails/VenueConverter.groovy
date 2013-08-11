package org.where2pair.grails

import org.where2pair.DailyOpeningTimes
import org.where2pair.DailyOpeningTimes.OpenPeriod
import org.where2pair.DayOfWeek
import org.where2pair.Venue

import static org.where2pair.DayOfWeek.MONDAY
import static org.where2pair.DayOfWeek.SUNDAY

class VenueConverter {

    VenueDto asVenueDto(Venue venue) {
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
		
		new VenueDto(
            id: venue.id,
			name: venue.name,
			latitude: venue.location.lat,
			longitude: venue.location.lng,
			addressLine1: venue.address.addressLine1,
			addressLine2: venue.address.addressLine2,
			addressLine3: venue.address.addressLine3,
			city: venue.address.city,
			postcode: venue.address.postcode,
			phoneNumber: venue.address.phoneNumber,
			openHours: openHours,
			features: venue.features.collect()
		)

    }

	List asVenueDtos(List venues) {
		if (!venues)
			return []
		
		venues.collect { Venue venue ->
			asVenueDto(venue)
		}
	}
	
	List asVenueWithDistanceDtos(List venuesWithDistance) {
		if (!venuesWithDistance)
			return []
		
		List venues = venuesWithDistance.venue
		List distances = venuesWithDistance.distanceInKm
		List venueDtos = asVenueDtos(venues)
		[venueDtos, distances].transpose().collect { VenueDto venue, double distance -> 
			new VenueWithDistanceDto(venue: venue, distanceInKm: distance) 
		}
	}
	
	private String dayToString(DayOfWeek day) {
		day.toString().toLowerCase()
	}
}
