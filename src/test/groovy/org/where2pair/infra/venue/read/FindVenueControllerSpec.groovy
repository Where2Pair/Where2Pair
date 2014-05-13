package org.where2pair.infra.venue.read

import org.where2pair.core.venue.common.Coordinates
import org.where2pair.core.venue.common.Facility
import org.where2pair.core.venue.common.SimpleTime
import org.where2pair.core.venue.read.FacilitiesCriteria
import org.where2pair.core.venue.read.LocationsCriteria
import org.where2pair.core.venue.read.OpenTimesCriteria
import org.where2pair.core.venue.read.VenueService
import spock.lang.Specification
import spock.lang.Unroll

import static groovy.json.JsonOutput.toJson
import static org.where2pair.core.venue.common.Facility.POWER
import static org.where2pair.core.venue.common.Facility.WIFI
import static org.where2pair.core.venue.read.DayOfWeek.*
import static org.where2pair.core.venue.read.DistanceUnit.KM
import static org.where2pair.core.venue.read.VenueBuilder.aVenue
import static org.where2pair.infra.venue.web.StatusCode.BAD_REQUEST
import static org.where2pair.infra.venue.web.StatusCode.OK

class FindVenueControllerSpec extends Specification {

    static final TIME_NOW = new SimpleTime(1, 2)
    static final TODAY = FRIDAY
    def venueService = Mock(VenueService)
    def timeProvider = Mock(TimeProvider)
    def locationsCriteriaParser = new LocationsCriteriaParser()
    def controller = new FindVenueController(venueService, locationsCriteriaParser, timeProvider)

    def 'finds venues that match criteria and returns as json'() {
        given:
        def openTimesParams = [openFrom: '13.30', openUntil: '14.30', openDay: 'Tuesday']
        def facilitiesParams = [withFacilities: 'wifi,power']
        def locationsParams = [location: '1.0,0.1', distanceUnit: 'km']
        def expectedOpenTimesCriteria = new OpenTimesCriteria(openFrom: new SimpleTime(13, 30), openUntil: new SimpleTime(14, 30), dayOfWeek: TUESDAY)
        def expectedFacilitiesCriteria = new FacilitiesCriteria(requestedFacilities: [WIFI, POWER])
        def expectedLocationsCriteria = new LocationsCriteria(locations: [new Coordinates(1.0, 0.1)], distanceUnit: KM)
        def venuesFound = [aVenue().build()]
        venueService.find(expectedOpenTimesCriteria, expectedFacilitiesCriteria, expectedLocationsCriteria) >> venuesFound

        when:
        def jsonResponse = controller.findNearest(locationsParams + openTimesParams + facilitiesParams)

        then:
        jsonResponse.responseBody == toJson(venuesFound)
        jsonResponse.statusCode == OK
    }

    @Unroll
    def 'given openFrom: #openFromParam openUntil: #openUntilParam openDay: #openDayParam finds venues open during correct time range'() {
        given:
        def params = getMinimumRequiredParams()
        if (openFromParam != 'missing') params.openFrom = openFromParam
        if (openUntilParam != 'missing') params.openUntil = openUntilParam
        if (openDayParam != 'missing') params.openDay = openDayParam

        when:
        controller.findNearest(params)

        then:
        1 * venueService.find({ OpenTimesCriteria criteria ->
            criteria.openFrom == expectedOpenFrom
            criteria.openUntil == expectedOpenUntil
            criteria.dayOfWeek == expectedOpenDay
        }, _, _)

        where:
        openFromParam | openUntilParam | openDayParam | expectedOpenFrom       | expectedOpenUntil      | expectedOpenDay
        'missing'     | 'missing'      | 'missing'    | TIME_NOW               | TIME_NOW               | TODAY
        '13.30'       | 'missing'      | 'missing'    | new SimpleTime(13, 30) | new SimpleTime(13, 30) | TODAY
        'missing'     | '13.30'        | 'missing'    | TIME_NOW               | new SimpleTime(13, 30) | TODAY
        '13.30'       | '18.45'        | 'missing'    | new SimpleTime(13, 30) | new SimpleTime(18, 45) | TODAY
        '13.30'       | 'missing'      | 'monday'     | new SimpleTime(13, 30) | new SimpleTime(13, 30) | MONDAY
        'missing'     | '18.45'        | 'wednesday'  | TIME_NOW               | new SimpleTime(18, 45) | WEDNESDAY
        '13.30'       | '18.45'        | 'thursday'   | new SimpleTime(13, 30) | new SimpleTime(18, 45) | THURSDAY
        'missing'     | 'missing'      | 'sunday'     | new SimpleTime(0, 0)   | new SimpleTime(35, 59) | SUNDAY
    }

    @Unroll
    def 'rejects requests with invalid open times criteria'() {
        given:
        def params = getMinimumRequiredParams() + invalidParams

        when:
        def response = controller.findNearest(params)

        then:
        response.responseBody == toJson([error: expectedErrorMessage])
        response.statusCode == BAD_REQUEST

        where:
        invalidParams          | expectedErrorMessage
        [openFrom: '1200']     | "'openFrom' not supplied in the correct format. Expected to be in the form: openFrom:<hour>.<minute>"
        [openFrom: 'abc.xyz']  | "'openFrom' not supplied in the correct format. Expected to be in the form: openFrom:<hour>.<minute>"
        [openUntil: '1200']    | "'openUntil' not supplied in the correct format. Expected to be in the form: openUntil:<hour>.<minute>"
        [openUntil: 'abc.xyz'] | "'openUntil' not supplied in the correct format. Expected to be in the form: openUntil:<hour>.<minute>"
        [openDay: 'abcday']    | "'openDay' not recognized. Expected to be a day from Monday-Sunday"
    }

    def 'rejects requests with invalid facilities'() {
        given:
        def params = getMinimumRequiredParams() + invalidParams

        when:
        def response = controller.findNearest(params)

        then:
        response.responseBody == toJson([error: expectedErrorMessage])
        response.statusCode == BAD_REQUEST

        where:
        invalidParams            | expectedErrorMessage
        [withFacilities: 'gymnasium'] | "Unrecognized facility requested. Facilities should be comma-separated values from the following list: ${Facility.values()}"
    }

    def 'rejects requests with invalid locations criteria'() {
        when:
        def response = controller.findNearest(invalidParams)

        then:
        response.responseBody == toJson([error: expectedErrorMessage])
        response.statusCode == BAD_REQUEST

        where:
        invalidParams                                   | expectedErrorMessage
        [no_location_specified: true]                   | 'Missing locations from request parameters. Query expected to be in the form: nearest?location1=x1,y1&location2=x2,y2...'
        [location: '1.0,0.1', distanceUnit: 'Furlongs'] | "Distance unit 'FURLONGS' is invalid. Use either 'KM' or 'MILES' (omitting distanceUnit altogether defaults to 'KM')."
    }

    Map<String, String> getMinimumRequiredParams() {
        [location: '1.0,0.1']
    }

    def setup() {
        timeProvider.timeNow() >> TIME_NOW
        timeProvider.today() >> TODAY
    }

}