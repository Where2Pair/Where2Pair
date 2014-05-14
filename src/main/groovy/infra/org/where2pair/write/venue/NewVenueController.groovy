package org.where2pair.write.venue

import org.where2pair.common.venue.JsonResponse
import org.where2pair.common.venue.VenueId

public class NewVenueController {

    NewVenueService newVenueService

    JsonResponse save(Map<String, ?> venueJson) {
        try {
            VenueId venueId = newVenueService.save(venueJson)
            return okResponse(venueId)
        } catch (InvalidVenueJsonException e) {
            return badRequestResponse(e)
        }
    }

    private JsonResponse okResponse(VenueId venueId) {
        JsonResponse.validJsonResponse([venueId: venueId.toString()])
    }

    private JsonResponse badRequestResponse(InvalidVenueJsonException e) {
        JsonResponse.badRequest(e.getMessage())
    }

}