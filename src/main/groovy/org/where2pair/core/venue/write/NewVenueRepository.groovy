package org.where2pair.core.venue.write

import org.where2pair.core.venue.common.VenueId

public interface NewVenueRepository {

    VenueId save(NewVenue newVenue)

}