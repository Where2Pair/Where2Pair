package org.where2pair

import static org.where2pair.DayOfWeek.MONDAY
import static org.where2pair.DayOfWeek.TUESDAY
import static org.where2pair.DayOfWeek.WEDNESDAY
import static org.where2pair.DayOfWeek.THURSDAY

import java.util.Map;

import org.where2pair.DailyOpeningTimes.SimpleTime

class ObjectUtils {

	static Venue createVenue() {
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
	
	static Venue createDifferentVenue() {
		WeeklyOpeningTimesBuilder builder = new WeeklyOpeningTimesBuilder()
		builder.addOpenPeriod(WEDNESDAY, new SimpleTime(13, 30), new SimpleTime(19, 45))
		builder.addOpenPeriod(THURSDAY, new SimpleTime(18, 30), new SimpleTime(30, 0))
		new Venue(
			id: 66,
			name: 'different venue name',
			location: new Coordinates(10.0, -0.99),
			address: new Address(
				addressLine1: 'different addressLine1',
				addressLine2: 'different addressLine2',
				addressLine3: 'different addressLine3',
				city: 'different city',
				postcode: 'different postcode',
				phoneNumber: 'different 01234567890'
			),
			weeklyOpeningTimes: builder.build(),
			features: ['different wifi', 'different mobile payments']
		)
	}
	
	static Map createVenueJson() {
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
