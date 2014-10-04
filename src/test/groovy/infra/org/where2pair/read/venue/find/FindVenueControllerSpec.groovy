package org.where2pair.read.venue.find

import static groovy.json.JsonOutput.toJson
import static org.where2pair.common.venue.Facility.POWER
import static org.where2pair.common.venue.Facility.WIFI
import static org.where2pair.common.venue.StatusCode.BAD_REQUEST
import static org.where2pair.common.venue.StatusCode.OK
import static org.where2pair.read.venue.DayOfWeek.TUESDAY
import static org.where2pair.read.venue.DistanceBuilder.fromCoordinates
import static org.where2pair.read.venue.DistanceUnit.KM
import static org.where2pair.read.venue.DistanceUnit.MILES
import static org.where2pair.read.venue.VenueWithDistancesBuilder.aVenue

import org.where2pair.common.venue.Facility
import org.where2pair.common.venue.SimpleTime
import org.where2pair.read.venue.Coordinates
import org.where2pair.read.venue.mappingtojson.VenuesWithDistancesToJsonMapper
import org.where2pair.read.venue.opentimes.OpenTimesCriteriaFactory
import spock.lang.Specification
import spock.lang.Unroll

class FindVenueControllerSpec extends Specification {

    def venueService = Mock(VenueFinderService)
    def openTimesCriteriaFactory = Mock(OpenTimesCriteriaFactory)
    def controller = new FindVenueController(venueService, openTimesCriteriaFactory)
    def mapper = new VenuesWithDistancesToJsonMapper()

    def 'finds venues that match criteria and returns as json'() {
        given:
        def openTimesParams = [openFrom: '13.30', openUntil: '14.30', openDay: 'Tuesday']
        def expectedOpenTimesCriteria = Mock(OpenTimesCriteria)
        openTimesCriteriaFactory.createOpenTimesCriteria(new SimpleTime(13, 30), new SimpleTime(14, 30), TUESDAY) >> expectedOpenTimesCriteria

        def facilitiesParams = [withFacilities: 'wifi,power']
        def expectedFacilitiesCriteria = new FacilitiesCriteria(requestedFacilities: [WIFI, POWER])

        def locationsParams = [location: ['1.0,0.1'], distanceUnit: 'km']
        def expectedLocationsCriteria = new LocationsCriteria(locations: [new Coordinates(1.0, 0.1)], distanceUnit: KM)

        def venuesFound = [aVenue().withDistance(fromCoordinates(1.0, 0.1).miles(10)).build()]
        venueService.find(expectedOpenTimesCriteria, expectedFacilitiesCriteria, expectedLocationsCriteria) >> venuesFound

        when:
        def jsonResponse = controller.findNearest(locationsParams + openTimesParams + facilitiesParams)

        then:
        jsonResponse.responseBody == toJson(mapper.toJsonStructure(venuesFound))
        jsonResponse.statusCode == OK
    }

    def 'supports multiple supplied locations'() {
        given:
        def params = [:]
        params.location = ['1.0,0.1', '2.0,0.2']
        params.distanceUnit = 'miles'
        def expectedLocationsCriteria = new LocationsCriteria([new Coordinates(1.0, 0.1), new Coordinates(2.0, 0.2)], MILES)

        when:
        controller.findNearest(params)

        then:
        1 * venueService.find(_, _, expectedLocationsCriteria)
    }

    def 'defaults to km distance unit when none supplied'() {
        given:
        def params = [location: ['1.0,0.1']]
        def expectedLocationsCriteria = new LocationsCriteria([new Coordinates(1.0, 0.1)], KM)

        when:
        controller.findNearest(params)

        then:
        1 * venueService.find(_, _, expectedLocationsCriteria)
    }

    @Unroll
    def 'rejects requests with invalid open times criteria'() {
        given:
        def params = minimumRequiredParams + invalidParams

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
        def params = minimumRequiredParams + invalidParams

        when:
        def response = controller.findNearest(params)

        then:
        response.responseBody == toJson([error: expectedErrorMessage])
        response.statusCode == BAD_REQUEST

        where:
        invalidParams                 | expectedErrorMessage
        [withFacilities: 'gymnasium'] | "Unrecognized facility requested. Facilities should be comma-separated values from the following list: ${Facility.values()}"
    }

    def 'rejects requests with invalid locations criteria'() {
        when:
        def response = controller.findNearest(invalidParams)

        then:
        response.responseBody == toJson([error: expectedErrorMessage])
        response.statusCode == BAD_REQUEST

        where:
        invalidParams                                     | expectedErrorMessage
        [no_location_specified: true]                     | 'Missing locations from request parameters. Query expected to be in the form: nearest?location1=x1,y1&location2=x2,y2...'
        [location: ['1.0,0.1'], distanceUnit: 'Furlongs'] | "Distance unit 'FURLONGS' is invalid. Use either 'KM' or 'MILES' (omitting distanceUnit altogether defaults to 'KM')."
    }

    Map<String, String> getMinimumRequiredParams() {
        [location: ['1.0,0.1']]
    }
}
