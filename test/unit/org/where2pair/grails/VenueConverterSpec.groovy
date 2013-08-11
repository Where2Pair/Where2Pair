package org.where2pair.grails

import static org.where2pair.DayOfWeek.MONDAY
import static org.where2pair.DayOfWeek.TUESDAY
import grails.test.mixin.*

import org.where2pair.Address
import org.where2pair.Coordinates
import org.where2pair.Venue
import org.where2pair.VenueWithDistance
import org.where2pair.WeeklyOpeningTimesBuilder
import org.where2pair.DailyOpeningTimes.SimpleTime

import spock.lang.Specification

class VenueConverterSpec extends Specification {

	VenueConverter venueConverter = new VenueConverter()
    final static long VENUE_ID = 11

    def "should convert Venue to VenueDto"() {
		given:
		Venue venue = createVenue()
		VenueDto expectedVenueDto = createCorrespondingVenueDto()
		
		when:
		VenueDto venueDto = venueConverter.asVenueDto(venue)
		
		then:
		venueDto == expectedVenueDto
	}
	
	def "should convert Venues to VenueDtos"() {
		given:
		Venue venue = createVenue()
		VenueDto expectedVenueDTO = createCorrespondingVenueDto()
		
		when:
		List venueDtos = venueConverter.asVenueDtos([venue])
		
		then:
		venueDtos == [expectedVenueDTO]
	}
	
	def "should convert VenueWithDistance to VenueWithDistanceDto"() {
		given:
		Venue venue = createVenue()
		VenueDto venueDto = createCorrespondingVenueDto()
		VenueWithDistance venueWithDistance = new VenueWithDistance(venue: venue, distanceInKm: 10.5)
		VenueWithDistanceDto expectedVenueWithDistanceDTO = new VenueWithDistanceDto(venue: venueDto, distanceInKm: 10.5)
		
		when:
		List venueWithDistanceDTOs = venueConverter.asVenueWithDistanceDtos([venueWithDistance])
		
		then:
		venueWithDistanceDTOs == [expectedVenueWithDistanceDTO]
	}
	
	private Venue createVenue() {
		WeeklyOpeningTimesBuilder builder = new WeeklyOpeningTimesBuilder()
		builder.addOpenPeriod(MONDAY, new SimpleTime(12, 0), new SimpleTime(18, 30))
		builder.addOpenPeriod(TUESDAY, new SimpleTime(8, 0), new SimpleTime(11, 0))
		new Venue(
            id: VENUE_ID,
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
	
	private VenueDto createCorrespondingVenueDto() {
		new VenueDto(
            id: VENUE_ID,
			latitude: 1.0,
			longitude: 0.1,
			addressLine1: 'addressLine1',
			addressLine2: 'addressLine2',
			addressLine3: 'addressLine3',
			city: 'city',
			postcode: 'postcode',
			phoneNumber: '01234567890',
			openHours: [monday: [[openHour: 12, openMinute: 0, closeHour: 18, closeMinute: 30]],
						tuesday: [[openHour: 8, openMinute: 0, closeHour: 11, closeMinute: 0]],
						wednesday: [],
						thursday: [],
						friday: [],
						saturday: [],
						sunday: []] as LinkedHashMap,
			features: ['wifi', 'mobile payments'] as HashSet
		)
	}
}
