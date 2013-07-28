package org.where2pair.grails

import org.where2pair.Coordinates
import org.where2pair.DayOfWeek
import org.where2pair.Venue
import org.where2pair.WeeklyOpeningTimesBuilder
import org.where2pair.DailyOpeningTimes.SimpleTime

import spock.lang.Specification

import static org.where2pair.DayOfWeek.MONDAY
import static org.where2pair.DayOfWeek.TUESDAY

class VenueConverterSpec extends Specification {

	VenueConverter venueConverter = new VenueConverter()
	
	def "should convert Venue to VenueDTO"() {
		given:
		WeeklyOpeningTimesBuilder builder = new WeeklyOpeningTimesBuilder()
		builder.addOpenPeriod(MONDAY, new SimpleTime(12, 0), new SimpleTime(18, 30))
		builder.addOpenPeriod(TUESDAY, new SimpleTime(8, 0), new SimpleTime(11, 0))
		Venue venue = new Venue(
			location: new Coordinates(1.0, 0.1),
			weeklyOpeningTimes: builder.build()
		)
		VenueDTO expectedVenueDTO = new VenueDTO(
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
		
		when:
		List venueDTOs = venueConverter.asVenueDTOs([venue])
		
		then:
		venueDTOs == [expectedVenueDTO]
	}
	
}
