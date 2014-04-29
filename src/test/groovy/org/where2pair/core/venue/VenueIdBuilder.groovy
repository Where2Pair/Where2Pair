package org.where2pair.core.venue

import org.where2pair.core.venue.common.VenueId
import org.where2pair.core.venue.common.Coordinates

import static org.where2pair.core.venue.CoordinatesBuilder.someCoordinates


class VenueIdBuilder {

    private String name = 'Starbucks'
    private Coordinates location = someCoordinates()

    static VenueIdBuilder aVenueId() {
        new VenueIdBuilder()
    }

    VenueIdBuilder withName(String name) {
        this.name = name
        this
    }

    VenueId build() {
        new VenueId(name, location)
    }
}
