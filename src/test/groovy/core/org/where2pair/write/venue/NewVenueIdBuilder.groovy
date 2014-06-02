package org.where2pair.write.venue

import static java.util.UUID.randomUUID

class NewVenueIdBuilder {

    private String name = 'Starbucks'
    private double latitude = 1.0
    private double longitude = 0.1
    private String addressLine1 = '9 Appold Street'

    static NewVenueIdBuilder aVenueId() {
        new NewVenueIdBuilder()
    }

    static NewVenueId aRandomVenueId() {
        aVenueId().withName(randomUUID() as String)
                .withLongitude(randomDouble())
                .withLatitude(randomDouble())
                .withAddressLine1(randomUUID() as String)
                .build()
    }

    private static double randomDouble() {
        new Random().nextDouble()
    }

    NewVenueIdBuilder withName(String name) {
        this.name = name
        this
    }

    NewVenueIdBuilder withLatitude(double latitude) {
        this.latitude = latitude
        this
    }

    NewVenueIdBuilder withLongitude(double longitude) {
        this.longitude = longitude
        this
    }

    NewVenueIdBuilder withAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1
        this
    }

    NewVenueId build() {
        new NewVenueId(name, latitude, longitude, addressLine1)
    }
}

