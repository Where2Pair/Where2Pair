package org.where2pair.read.venue

import groovy.transform.TupleConstructor
import org.where2pair.common.venue.JsonResponse
import org.where2pair.write.venue.NewVenueId
import org.where2pair.read.venue.mappingtojson.VenueToJsonMapper

import static JsonResponse.validJsonResponse
import static org.where2pair.common.venue.JsonResponse.resourceNotFound

@TupleConstructor
class ShowVenueController {
    VenueRepository venueRepository
    VenueToJsonMapper venueToJsonMapper

    JsonResponse show(String id) {
        def venueId = new VenueId(id)
        lookupVenue(venueId)
    }

    private JsonResponse lookupVenue(VenueId venueId) {
        def venue = venueRepository.get(venueId)

        if (venue) {
            return validJsonResponse(venueToJsonMapper.toJson(venue))
        } else {
            return venueNotFoundResponse(venueId.toString())
        }
    }

    private static JsonResponse venueNotFoundResponse(String id) {
        resourceNotFound("Venue with id $id could not be found")
    }
}
