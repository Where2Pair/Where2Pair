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
        try {
            def venueId = VenueId.from(id)
            def venue = venueRepository.get(venueId)

            if (venue) {
                return validJsonResponse(venueToJsonMapper.toJson(venue))
            } else {
                return resourceNotFound("Venue with id $id could not be found")
            }
        } catch (MalformedVenueIdException e) {
            return resourceNotFound("Venue with id $id could not be found")
        }
    }
}
