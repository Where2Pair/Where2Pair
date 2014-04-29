package org.where2pair.infra.venue.persistence

import org.where2pair.core.venue.common.VenueId
import org.where2pair.core.venue.common.Coordinates
import org.where2pair.core.venue.write.NewVenue
import org.where2pair.core.venue.write.NewVenueRepository


class AmazonS3NewVenueRepository implements NewVenueRepository {
    @Override
    VenueId save(NewVenue newVenue) {
        Map<String, ?> venueJson = newVenue.venueJson
        new VenueId(venueJson.name, new Coordinates(venueJson.location.latitude, venueJson.location.longitude), venueJson.address.addressLine1)
    }
}
