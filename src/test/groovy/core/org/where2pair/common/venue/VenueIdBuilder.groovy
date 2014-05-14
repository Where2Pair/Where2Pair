package org.where2pair.common.venue

import static java.util.UUID.randomUUID
import static CoordinatesBuilder.coordinates

class VenueIdBuilder {

    private String name = 'Starbucks'
    private CoordinatesBuilder coordinatesBuilder = coordinates().withLatitude(1.0).withLongitude(0.1)
    private String addressLine1 = '9 Appold Street'

    static VenueIdBuilder aVenueId() {
        new VenueIdBuilder()
    }

    static VenueId aRandomVenueId() {
        aVenueId().withName(randomUUID() as String)
                .withLongitude(new Random().nextDouble())
                .withLatitude(new Random().nextDouble())
                .withAddressLine1(randomUUID() as String)
                .build()
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
