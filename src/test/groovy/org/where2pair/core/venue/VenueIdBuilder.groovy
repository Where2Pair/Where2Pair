package org.where2pair.core.venue

import org.where2pair.core.venue.common.VenueId
import org.where2pair.core.venue.common.Coordinates

import static org.where2pair.core.venue.CoordinatesBuilder.coordinates
import static org.where2pair.core.venue.CoordinatesBuilder.someCoordinates


class VenueIdBuilder {

    private String name = 'Starbucks'
    private CoordinatesBuilder coordinatesBuilder = coordinates().withLatitude(1.0).withLongitude(0.1)
    private String addressLine1 = '9 Appold Street'

    static VenueIdBuilder aVenueId() {
        new VenueIdBuilder()
    }

    VenueIdBuilder withName(String name) {
        this.name = name
        this
    }

    VenueIdBuilder withLatitude(double latitude) {
        coordinatesBuilder.withLatitude(latitude)
        this
    }

    VenueIdBuilder withLongitude(double longitude) {
        coordinatesBuilder.withLongitude(longitude)
        this
    }

    VenueIdBuilder withAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1
        this
    }

    VenueId build() {
        new VenueId(name, coordinatesBuilder.build(), addressLine1)
    }
}