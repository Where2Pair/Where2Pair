package org.where2pair

import static org.where2pair.DayOfWeek.MONDAY
import static org.where2pair.DayOfWeek.SUNDAY

import org.where2pair.Address
import org.where2pair.Coordinates
import org.where2pair.DailyOpeningTimes
import org.where2pair.DayOfWeek
import org.where2pair.Venue
import org.where2pair.WeeklyOpeningTimes;
import org.where2pair.WeeklyOpeningTimesBuilder
import org.where2pair.DailyOpeningTimes.OpenPeriod
import org.where2pair.DailyOpeningTimes.SimpleTime

class VenueJsonMarshaller {

	OpenHoursJsonMarshaller openHoursJsonMarshaller
	
    Map asVenueJson(Venue venue) {
		[
            id: venue.id,
			name: venue.name ?: '',
			latitude: venue.location.lat,
			longitude: venue.location.lng,
			address: [
				addressLine1: venue.address.addressLine1 ?: '',
				addressLine2: venue.address.addressLine2 ?: '',
				addressLine3: venue.address.addressLine3 ?: '',
				city: venue.address.city ?: '',
				postcode: venue.address.postcode ?: '',
				phoneNumber: venue.address.phoneNumber ?: ''
			],
			openHours: openHoursJsonMarshaller.asOpenHoursJson(venue.weeklyOpeningTimes),
			features: venue.features.collect()
		]

    }

	List asVenuesJson(List venues) {
		if (!venues)
			return []
		
		venues.collect { Venue venue ->
			asVenueJson(venue)
		}
	}
	
	List asVenuesWithDistanceJson(List venuesWithDistance) {
		if (!venuesWithDistance)
			return []
		
		List venues = venuesWithDistance.venue
		List distances = venuesWithDistance.distanceInKm
		List venueDtos = asVenuesJson(venues)
		[venueDtos, distances].transpose().collect { Map venueJson, double distance -> 
			[distanceInKm: distance, venue: venueJson]
		}
	}
	
	Venue asVenue(Map json) {
		new Venue(id: json.id ?: 0,
			name: json.name,
			location: new Coordinates(json.latitude, json.longitude),
			address: new Address(json.address ?: [:]),
			weeklyOpeningTimes: openHoursJsonMarshaller.asWeeklyOpeningTimes(json.openHours),
			features: json.features)
	}
}
