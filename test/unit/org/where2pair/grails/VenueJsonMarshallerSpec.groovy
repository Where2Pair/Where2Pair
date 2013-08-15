package org.where2pair.grails

import static org.where2pair.DayOfWeek.MONDAY
import static org.where2pair.DayOfWeek.TUESDAY
import grails.test.mixin.*

import org.where2pair.Address
import org.where2pair.Coordinates
import org.where2pair.Venue
import org.where2pair.VenueWithDistance
import org.where2pair.WeeklyOpeningTimes
import org.where2pair.WeeklyOpeningTimesBuilder
import org.where2pair.DailyOpeningTimes.SimpleTime

import spock.lang.Specification

class VenueJsonMarshallerSpec extends Specification {

	VenueJsonMarshaller venueJsonMarshaller = new VenueJsonMarshaller()

    def "converts Venue to map"() {
		given:
		Venue venue = createVenue()
		Map expectedVenueMap = createCorrespondingVenueMap()
		
		when:
		Map venueMap = venueJsonMarshaller.asVenueJson(venue)
		
		then:
		venueMap == expectedVenueMap
	}
	
	def "converts Venues to map"() {
		given:
		Venue venue = createVenue()
		Map expectedVenueMap = createCorrespondingVenueMap()
		
		when:
		List venueMap = venueJsonMarshaller.asVenuesJson([venue])
		
		then:
		venueMap == [expectedVenueMap]
	}
	
	def "converts VenueWithDistance to map"() {
		given:
		Venue venue = createVenue()
		VenueWithDistance venueWithDistance = new VenueWithDistance(venue: venue, distanceInKm: 10.5)
		Map expectedVenueWithDistanceMap = [
			distanceInKm: 10.5,
			venue: createCorrespondingVenueMap()]
		
		when:
		List venuesWithDistanceMap = venueJsonMarshaller.asVenuesWithDistanceJson([venueWithDistance])
		
		then:
		venuesWithDistanceMap == [expectedVenueWithDistanceMap]
	}
	
	def "renders null string values as empty strings"() {
		given:
		Venue venue = new Venue(
			address: new Address(),
			weeklyOpeningTimes: new WeeklyOpeningTimes(),
			location: new Coordinates(0, 0))
		
		when:
		Map venueMap = venueJsonMarshaller.asVenueJson(venue)
		
		then:
		venueMap.name == ""
		venueMap.address.addressLine1 == ""
		venueMap.address.addressLine2 == ""
		venueMap.address.addressLine3 == ""
		venueMap.address.city == ""
		venueMap.address.postcode == ""
		venueMap.address.phoneNumber == ""
	}
	
	def "converts map to Venue"() {
		given:
		Map venueJson = createCorrespondingVenueMap()
		Venue expectedVenue = createVenue()
		
		when:
		Venue venue = venueJsonMarshaller.asVenue(venueJson)
	
		then:
		venue == expectedVenue
	}
	
	def "converts map to Venue when address is null"() {
		given:
		Map venueJson = createCorrespondingVenueMap()
		Venue expectedVenue = createVenue()
		venueJson.address = null
		expectedVenue.address = new Address()
		
		when:
		Venue venue = venueJsonMarshaller.asVenue(venueJson)
		
		then:
		venue == expectedVenue
	}
	
	def "converts map to Venue when id is null"() {
		given:
		Map venueJson = createCorrespondingVenueMap()
		Venue expectedVenue = createVenue()
		venueJson.id = null
		expectedVenue.id = 0
		
		when:
		Venue venue = venueJsonMarshaller.asVenue(venueJson)
		
		then:
		venue == expectedVenue
	}
	
	private Venue createVenue() {
		WeeklyOpeningTimesBuilder builder = new WeeklyOpeningTimesBuilder()
		builder.addOpenPeriod(MONDAY, new SimpleTime(12, 0), new SimpleTime(18, 30))
		builder.addOpenPeriod(TUESDAY, new SimpleTime(8, 0), new SimpleTime(11, 0))
		new Venue(
            id: 99,
			name: 'venue name',
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
	}
	
	private Map createCorrespondingVenueMap() {
		[
			id: 99,
			name: 'venue name',
			latitude: 1.0,
			longitude: 0.1,
			address: [
				addressLine1: 'addressLine1',
				addressLine2: 'addressLine2',
				addressLine3: 'addressLine3',
				city: 'city',
				postcode: 'postcode',
				phoneNumber: '01234567890'
			],
			openHours: [monday: [[openHour: 12, openMinute: 0, closeHour: 18, closeMinute: 30]],
						tuesday: [[openHour: 8, openMinute: 0, closeHour: 11, closeMinute: 0]],
						wednesday: [],
						thursday: [],
						friday: [],
						saturday: [],
						sunday: []] as LinkedHashMap,
			features: ['wifi', 'mobile payments']
		]
	}
}
