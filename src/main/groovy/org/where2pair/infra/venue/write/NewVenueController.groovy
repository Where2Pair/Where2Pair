package org.where2pair.infra.venue.write

import org.where2pair.core.venue.common.VenueId
import org.where2pair.core.venue.write.InvalidVenueJsonException
import org.where2pair.core.venue.write.NewVenueService
import org.where2pair.infra.venue.web.JsonResponse

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