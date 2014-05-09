package org.where2pair.infra.venue.read

import groovy.transform.TupleConstructor
import org.where2pair.core.venue.common.VenueId
import org.where2pair.core.venue.common.VenueId.MalformedVenueIdException
import org.where2pair.core.venue.read.VenueRepository
import org.where2pair.core.venue.read.mappingtojson.VenueToJsonMapper
import org.where2pair.infra.venue.web.JsonResponse

import static JsonResponse.validJsonResponse
import static org.where2pair.infra.venue.web.JsonResponse.resourceNotFound

@TupleConstructor
class ShowVenueController {
    VenueRepository venueRepository
    VenueToJsonMapper venueToJsonMapper

    JsonResponse show(String id) {
        def venueId
        try {
            venueId = VenueId.from(id)
        } catch (MalformedVenueIdException e) {
            return venueNotFoundResponse(id)
        }

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
