package org.where2pair.venue.find

import static org.where2pair.venue.DayOfWeek.FRIDAY
import static org.where2pair.venue.DayOfWeek.MONDAY
import static org.where2pair.venue.DayOfWeek.SUNDAY
import static org.where2pair.venue.DayOfWeek.THURSDAY
import static org.where2pair.venue.DayOfWeek.WEDNESDAY
import static org.where2pair.venue.find.LocationsCriteriaBuilder.locationsCriteria

import org.where2pair.venue.Address
import org.where2pair.venue.Coordinates
import org.where2pair.venue.Venue
import org.where2pair.venue.VenueJsonMarshaller
import org.where2pair.venue.WeeklyOpeningTimesBuilder
import org.where2pair.venue.DailyOpeningTimes.SimpleTime

import spock.lang.Specification
import spock.lang.Unroll

class FindVenueControllerSpec extends Specification {

	static final TIME_NOW = new SimpleTime(1, 2)
	static final TODAY = FRIDAY
	Map params = [:]
	FindVenueController controller = new FindVenueController()
	VenueFinder venueFinder = Mock()
	TimeProvider timeProvider = Mock()
	LocationsCriteria validLocationsCriteria = Mock()
	LocationsCriteriaParser locationsCriteriaParser = Mock()
	VenueJsonMarshaller venueJsonMarshaller = Mock()
	
	def "finds venues based on valid locations criteria"() {
		when:
		controller.findNearest(params)

		then:
		1 *	venueFinder.findNearestTo(_,_,validLocationsCriteria)
	}
	
	def "rejects requests with invalid locations criteria"() {
		given:
		Map invalidParams = ['no-location-specified': true]
		LocationsCriteria invalidLocationsCriteria = Mock()
		invalidLocationsCriteria.errors >> ["errorMessage", 404]
		locationsCriteriaParser.parse(invalidParams) >> invalidLocationsCriteria
		
		when:
		ErrorResponse response = controller.findNearest(invalidParams)

		then:
		response.message == "errorMessage"
		response.status == 404
	}
	
	def "parses features from request and uses them to find venues"() {
		given:
		params.'withFeatures' = 'wifi,baby_changing'
		
		when:
		controller.findNearest(params)
		
		then:
		1 * venueFinder.findNearestTo(_, { FeaturesCriteria criteria ->
			criteria.requestedFeatures == ['wifi', 'baby_changing'] as HashSet
		}, _)
	}
	
	@Unroll
	def "given openFrom: #openFromParam openUntil: #openUntilParam openDay: #openDayParam finds venues open during correct time range"() {
		given:
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
		
	def setup() {
		timeProvider.timeNow() >> TIME_NOW
		timeProvider.today() >> TODAY
		locationsCriteriaParser.parse(params) >> validLocationsCriteria
		venueJsonMarshaller.asVenuesWithDistanceJson(_) >> [[:]]
		controller.venueFinder = venueFinder
		controller.venueJsonMarshaller = venueJsonMarshaller
		controller.timeProvider = timeProvider
		controller.locationsCriteriaParser = locationsCriteriaParser
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
				distance: 10.5)
			}
		}
	}
}