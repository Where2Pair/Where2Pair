package org.where2pair.read.venue

import org.where2pair.common.venue.JsonResponse
import org.where2pair.read.venue.opentimes.OpenTimesCriteriaFactory
import org.where2pair.read.venue.mappingtojson.VenuesWithDistancesToJsonMapper

import static org.where2pair.common.venue.JsonResponse.badRequest
import static org.where2pair.common.venue.JsonResponse.validJsonResponse

class FindVenueController {

    final VenueService venueService
    final OpenTimesCriteriaParser openTimesCriteriaParser
    final FacilitiesCriteriaParser facilitiesCriteriaParser
    final LocationsCriteriaParser locationsCriteriaParser
    final VenuesWithDistancesToJsonMapper venuesWithDistancesToJsonMapper

    FindVenueController(VenueService venueService, OpenTimesCriteriaFactory openTimesCriteriaFactory) {
        this.venueService = venueService
        this.openTimesCriteriaParser = new OpenTimesCriteriaParser(openTimesCriteriaFactory)
        this.facilitiesCriteriaParser = new FacilitiesCriteriaParser()
        this.locationsCriteriaParser = new LocationsCriteriaParser()
        this.venuesWithDistancesToJsonMapper = new VenuesWithDistancesToJsonMapper()
    }

    JsonResponse findNearest(Map<String, ?> params) {
        try {
            def openTimesCriteria = openTimesCriteriaParser.parse(params)
            def facilitiesCriteria = facilitiesCriteriaParser.parse(params)
            def locationsCriteria = locationsCriteriaParser.parse(params)

            def venues = venueService.find(openTimesCriteria, facilitiesCriteria, locationsCriteria)

            return venuesAsJson(venues)
        } catch (QueryParseException e) {
            return badRequest(e.message)
        }
    }

    private JsonResponse venuesAsJson(List<VenueWithDistances> venues) {
        def venuesJson = venuesWithDistancesToJsonMapper.toJsonStructure(venues)
        return validJsonResponse(venuesJson)
    }
}
