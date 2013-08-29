package org.where2pair

import static org.where2pair.DayOfWeek.MONDAY
import static org.where2pair.DayOfWeek.TUESDAY
import static org.where2pair.ObjectUtils.createVenue
import static org.where2pair.ObjectUtils.createVenueJson


import org.where2pair.Address
import org.where2pair.Coordinates
import org.where2pair.Venue
import org.where2pair.VenueWithDistance
import org.where2pair.WeeklyOpeningTimes
import org.where2pair.WeeklyOpeningTimesBuilder
import org.where2pair.DailyOpeningTimes.SimpleTime

import spock.lang.Specification

class VenueJsonMarshallerSpec extends Specification {

	Venue venue = createVenue()
	Map venueJson = createVenueJson()
	OpenHoursJsonMarshaller openHoursJsonMarshaller = Mock() {
		asOpenHoursJson(venue.weeklyOpeningTimes) >> venueJson.openHours
		asWeeklyOpeningTimes(venueJson.openHours) >> venue.weeklyOpeningTimes
	}
	VenueJsonMarshaller venueJsonMarshaller = new VenueJsonMarshaller(
		openHoursJsonMarshaller: openHoursJsonMarshaller )

    def "converts Venue to json"() {
		when:
		Map result = venueJsonMarshaller.asVenueJson(venue)
		
		then:
		result == venueJson
	}
	
	def "converts Venues to json"() {
		when:
		List result = venueJsonMarshaller.asVenuesJson([venue])
		
		then:
		result == [venueJson]
	}
	
	def "converts VenueWithDistance to json"() {
		given:
		VenueWithDistance venueWithDistance = new VenueWithDistance(venue: venue, distanceInKm: 10.5)
		Map expectedVenueWithDistanceJson = [
			distanceInKm: 10.5,
			venue: venueJson]
		
		when:
		List venuesWithDistanceJson = venueJsonMarshaller.asVenuesWithDistanceJson([venueWithDistance])
		
		then:
		venuesWithDistanceJson == [expectedVenueWithDistanceJson]
	}
	
	def "renders null string values as empty strings"() {
		given:
		Venue venue = new Venue(
			address: new Address(),
			weeklyOpeningTimes: new WeeklyOpeningTimes(),
			location: new Coordinates(0, 0))
		
		when:
		Map venueJson = venueJsonMarshaller.asVenueJson(venue)
		
		then:
		venueJson.name == ""
		venueJson.address.addressLine1 == ""
		venueJson.address.addressLine2 == ""
		venueJson.address.addressLine3 == ""
		venueJson.address.city == ""
		venueJson.address.postcode == ""
		venueJson.address.phoneNumber == ""
	}
	
	def "converts json to Venue"() {
		when:
		Venue result = venueJsonMarshaller.asVenue(venueJson)
	
		then:
		result == venue
	}
	
	def "converts json to Venue when address is null"() {
		given:
		venueJson.address = null
		venue.address = new Address()
		
		when:
		Venue result = venueJsonMarshaller.asVenue(venueJson)
		
		then:
		result == venue
	}
	
	def "converts json to Venue when id is null"() {
		given:
		venueJson.id = null
		venue.id = 0
		
		when:
		Venue result = venueJsonMarshaller.asVenue(venueJson)
		
		then:
		result == venue
	}
	
}
