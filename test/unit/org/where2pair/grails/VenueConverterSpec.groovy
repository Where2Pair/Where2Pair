package org.where2pair.grails

import static org.where2pair.DayOfWeek.MONDAY
import static org.where2pair.DayOfWeek.TUESDAY
import org.where2pair.Coordinates
import org.where2pair.Venue
import org.where2pair.VenueWithDistance
import org.where2pair.WeeklyOpeningTimesBuilder
import org.where2pair.DailyOpeningTimes.SimpleTime

import spock.lang.Specification

class VenueConverterSpec extends Specification {

	VenueConverter venueConverter = new VenueConverter()
	
	def "should convert Venue to VenueDto"() {
		given:
		Venue venue = createVenue()
		VenueDTO expectedVenueDto = createCorrespondingVenueDto()
		
		when:
		VenueDTO venueDto = venueConverter.asVenueDto(venue)
		
		then:
		venueDto == expectedVenueDto
	}
	
	def "should convert Venues to VenueDtos"() {
		given:
		Venue venue = createVenue()
		VenueDTO expectedVenueDTO = createCorrespondingVenueDto()
		
		when:
		List venueDTOs = venueConverter.asVenueDtos([venue])
		
		then:
		venueDTOs == [expectedVenueDTO]
	}
	
	def "should convert VenueWithDistance to VenueWithDistanceDto"() {
		given:
		Venue venue = createVenue()
		VenueDTO venueDto = createCorrespondingVenueDto()
		VenueWithDistance venueWithDistance = new VenueWithDistance(venue: venue, distanceInKm: 10.5)
		VenueWithDistanceDTO expectedVenueWithDistanceDTO = new VenueWithDistanceDTO(venue: venueDto, distanceInKm: 10.5)
		
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
			location: new Coordinates(1.0, 0.1),
			weeklyOpeningTimes: builder.build()
		)
	}
	
	private VenueDTO createCorrespondingVenueDto() {
		new VenueDTO(
			latitude: 1.0,
			longitude: 0.1,
			openHours: [monday: [[openHour: 12, openMinute: 0, closeHour: 18, closeMinute: 30]],
						tuesday: [[openHour: 8, openMinute: 0, closeHour: 11, closeMinute: 0]],
						wednesday: [],
						thursday: [],
						friday: [],
						saturday: [],
						sunday: []] as LinkedHashMap
		)
	}
}
