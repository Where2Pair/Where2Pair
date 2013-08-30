package org.where2pair.venue.find

import static org.where2pair.venue.DayOfWeek.FRIDAY
import static org.where2pair.venue.DayOfWeek.MONDAY
import static org.where2pair.venue.DayOfWeek.SUNDAY
import static org.where2pair.venue.DayOfWeek.THURSDAY
import static org.where2pair.venue.DayOfWeek.WEDNESDAY

import org.where2pair.venue.Address
import org.where2pair.venue.Coordinates
import org.where2pair.venue.ErrorResponse
import org.where2pair.venue.Venue
import org.where2pair.venue.VenueJsonMarshaller
import org.where2pair.venue.WeeklyOpeningTimesBuilder
import org.where2pair.venue.DailyOpeningTimes.SimpleTime

import spock.lang.Specification
import spock.lang.Unroll

class FindVenueControllerSpec extends Specification {

	static final TIME_NOW = new SimpleTime(1, 2)
	static final TODAY = FRIDAY
	FindVenueController controller = new FindVenueController()
	VenueFinder venueFinder = Mock()
	TimeProvider timeProvider = Mock()
	VenueJsonMarshaller venueJsonMarshaller = Mock() {
		asVenuesWithDistanceJson(_) >> [[:]]
	}
	Map params = [:]
	
	def "should display search results for given coordinates"() {
		given:
		params.'location1' = '1.0,0.1'

		when:
		controller.findNearest(params)

		then:
		1 *	venueFinder.findNearestTo(_,_,new Coordinates(1.0,0.1))
	}

	def "should support multiple supplied locations"() {
		given:
		(1..1000).each { params."location$it" = '1.0,0.1'}
		List expectedCoordArgs = [new Coordinates(1.0,0.1)] * 1000

		when:
		controller.findNearest(params)

		then:
		1 * venueFinder.findNearestTo(_,_,expectedCoordArgs)
	}

	def "should reject more than 1000 supplied locations"() {
		given:
		(1..1001).each { params."location$it" = '1.0,0.1'}

		when:
		ErrorResponse response = controller.findNearest(params)


		then:
		response.message == "Only upto 1000 locations are supported at this time."
		response.status == 413
	}

	def "should reject 0 supplied locations"() {
		when:
		ErrorResponse response = controller.findNearest([:])

		then:
		response.message == "Missing locations from the request parameters. I expect a query in the form: nearest?location1=x1,y1&location2=x2,y2..."
		response.status == 413
	}

	@Unroll
	def "given openFrom: #openFromParam openUntil: #openUntilParam openDay: #openDayParam should find venues open during correct time range"() {
		given:
		params.'location1' = '1.0,0.1'
		if (openFromParam != 'missing') params.'openFrom' = openFromParam
		if (openUntilParam != 'missing') params.'openUntil' = openUntilParam
		if (openDayParam != 'missing') params.'openDay' = openDayParam
		
		when:
		controller.findNearest(params)
		
		then:
		1 * venueFinder.findNearestTo({ OpenTimesCriteria criteria ->
			criteria.openFrom == expectedOpenFrom
			criteria.openUntil == expectedOpenUntil
			criteria.dayOfWeek == expectedOpenDay
		}, _, _)
		
		where:
		openFromParam 	| openUntilParam 	| openDayParam  | expectedOpenFrom 			| expectedOpenUntil 		| expectedOpenDay
		'missing'		| 'missing'			| 'missing'		| TIME_NOW					| TIME_NOW					| TODAY
		'13.30'			| 'missing'			| 'missing'		| new SimpleTime(13, 30)	| new SimpleTime(13, 30)	| TODAY
		'missing'		| '13.30'			| 'missing'		| TIME_NOW					| new SimpleTime(13, 30)	| TODAY
		'13.30'			| '18.45'			| 'missing'		| new SimpleTime(13, 30)	| new SimpleTime(18, 45)	| TODAY
		'13.30'			| 'missing'			| 'monday'		| new SimpleTime(13, 30)	| new SimpleTime(13, 30)	| MONDAY
		'missing'		| '18.45'			| 'wednesday'	| TIME_NOW					| new SimpleTime(18, 45)	| WEDNESDAY
		'13.30'			| '18.45'			| 'thursday'	| new SimpleTime(13, 30)	| new SimpleTime(18, 45)	| THURSDAY
		'missing'		| 'missing'			| 'sunday'		| new SimpleTime(0, 0)		| new SimpleTime(35, 59)	| SUNDAY
	}

	def "parses features from request and uses them to find venues"() {
		given:
		params.'location1' = '1.0,0.1'
		params.'withFeatures' = 'wifi,baby_changing'
		
		when:
		controller.findNearest(params)
		
		then:
		1 * venueFinder.findNearestTo(_, { FeaturesCriteria criteria ->
			criteria.requestedFeatures == ['wifi', 'baby_changing'] as HashSet
		}, _)
	}
		
	def setup() {
		timeProvider.timeNow() >> TIME_NOW
		timeProvider.today() >> TODAY
		controller.venueFinder = venueFinder
		controller.venueJsonMarshaller = venueJsonMarshaller
		controller.timeProvider = timeProvider
		Integer.mixin(VenuesMixin)
	}

	def cleanup() {
		Integer.metaClass = null
	}

	@Category(Integer)
	static class VenuesMixin {
		List venuesWithDistance() {
			(0..this).collect {
				new VenueWithDistance(venue: new Venue(
					location: new Coordinates(1.0, 0.5),
					weeklyOpeningTimes: new WeeklyOpeningTimesBuilder().build(), 
					address: new Address()),
				distanceInKm: 10.5)
			}
		}
	}
}