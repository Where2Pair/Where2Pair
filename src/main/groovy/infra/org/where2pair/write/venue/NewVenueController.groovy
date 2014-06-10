package org.where2pair.write.venue

import groovy.transform.TupleConstructor
import org.where2pair.common.venue.JsonResponse

@TupleConstructor
class NewVenueController {

    NewVenueService newVenueService

    JsonResponse save(String rawVenueJson) {
        try {
            def venueId = newVenueService.save(new RawVenueJson(rawVenueJson))
            return okResponse(venueId)
        } catch (InvalidVenueJsonException e) {
            return badRequestResponse(e)
        }
    }

    private JsonResponse okResponse(NewVenueId venueId) {
        JsonResponse.validJsonResponse([venueId: venueId.toString()])
    }

    private JsonResponse badRequestResponse(InvalidVenueJsonException e) {
        JsonResponse.badRequest(e.message)
    }

}
