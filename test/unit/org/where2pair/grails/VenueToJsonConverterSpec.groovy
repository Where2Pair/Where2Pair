package org.where2pair.grails

import static org.where2pair.DayOfWeek.MONDAY
import static org.where2pair.DayOfWeek.TUESDAY

import org.where2pair.Address
import org.where2pair.Coordinates
import org.where2pair.Venue
import org.where2pair.VenueWithDistance
import org.where2pair.WeeklyOpeningTimesBuilder
import org.where2pair.DailyOpeningTimes.SimpleTime

import spock.lang.Specification

class VenueToJsonConverterSpec extends Specification {

	VenueToJsonConverter venueConverter = new VenueToJsonConverter()

    def "should convert Venue to map"() {
		given:
		Venue venue = createVenue()
		Map expectedVenueMap = createCorrespondingVenueMap()
		
		when:
		Map venueMap = venueConverter.asVenueJson(venue)
		
		then:
		venueMap == expectedVenueMap
	}
	
	def "should convert Venues to map"() {
		given:
		Venue venue = createVenue()
		Map expectedVenueMap = createCorrespondingVenueMap()
		
		when:
		List venueMap = venueConverter.asVenuesJson([venue])
		
		then:
		venueMap == [expectedVenueMap]
	}
	
	def "should convert VenueWithDistance to map"() {
		given:
		Venue venue = createVenue()
		Map expectedVenueWithDistanceMap = createCorrespondingVenueMap()
		VenueWithDistance venueWithDistance = new VenueWithDistance(venue: venue, distanceInKm: 10.5)
		expectedVenueWithDistanceMap.distanceInKm = 10.5
		
		when:
		List venuesWithDistanceMap = venueConverter.asVenuesWithDistanceJson([venueWithDistance])
		
		then:
		venuesWithDistanceMap == [expectedVenueWithDistanceMap]
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
