package org.where2pair.infra.venue.write

import org.where2pair.core.venue.common.VenueId
import org.where2pair.core.venue.write.NewVenueService

public class NewVenueController {

    NewVenueService newVenueService

    String save(Map<String, ?> venueJson) {
        VenueId venueId = newVenueService.save(venueJson)
        venueId.toString()
    }

}