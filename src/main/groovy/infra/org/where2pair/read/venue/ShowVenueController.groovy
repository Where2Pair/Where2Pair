package org.where2pair.read.venue

import static JsonResponse.validJsonResponse
import static org.where2pair.common.venue.JsonResponse.resourceNotFound

import groovy.transform.TupleConstructor
import org.where2pair.common.venue.JsonResponse
import org.where2pair.read.venue.mappingtojson.VenueToJsonMapper

@TupleConstructor
class ShowVenueController {
    VenueRepository venueRepository
    VenueToJsonMapper venueToJsonMapper

    JsonResponse show(String id) {
        def venueId = new VenueId(id)
        lookupVenue(venueId)
    }

    private JsonResponse lookupVenue(VenueId venueId) {
        def venueFound = venueRepository.get(venueId)

        if (venueFound) {
            return validJsonResponse(venueToJsonMapper.toJsonStructure(venueFound))
        }

        return venueNotFoundResponse(venueId.toString())
    }

    private static JsonResponse venueNotFoundResponse(String id) {
        resourceNotFound("Venue with id $id could not be found")
    }
}

