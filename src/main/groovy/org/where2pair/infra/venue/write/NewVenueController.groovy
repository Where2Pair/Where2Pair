package org.where2pair.infra.venue.write

import org.where2pair.core.venue.common.VenueId
import org.where2pair.core.venue.write.InvalidVenueJsonException
import org.where2pair.core.venue.write.NewVenueService
import org.where2pair.infra.venue.web.ServerResponse
import org.where2pair.infra.venue.web.StatusCode

import static org.where2pair.infra.venue.web.StatusCode.BAD_REQUEST
import static org.where2pair.infra.venue.web.StatusCode.OK

public class NewVenueController {

    NewVenueService newVenueService

    ServerResponse save(Map<String, ?> venueJson) {
        try {
            VenueId venueId = newVenueService.save(venueJson)
            return okResponse(venueId)
        } catch (InvalidVenueJsonException e) {
            return badRequestResponse(e)
        }
    }

    private ServerResponse okResponse(VenueId venueId) {
        new ServerResponse(
                statusCode: OK,
                responseBody: venueId.toString())
    }

    private ServerResponse badRequestResponse(InvalidVenueJsonException e) {
        new ServerResponse(
                statusCode: BAD_REQUEST,
                responseBody: e.message)
    }

}