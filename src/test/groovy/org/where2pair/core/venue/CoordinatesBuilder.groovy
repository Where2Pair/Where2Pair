package org.where2pair.core.venue

import org.where2pair.core.venue.common.Coordinates


class CoordinatesBuilder {

    private double latitude
    private double longitude

    static Coordinates someCoordinates() {
        new CoordinatesBuilder().build()
    }

    static CoordinatesBuilder coordinates() {
        new CoordinatesBuilder()
    }

    CoordinatesBuilder withLatitude(double latitude) {
        this.latitude = latitude
        this
    }

    CoordinatesBuilder withLongitude(double longitude) {
        this.longitude = longitude
        this
    }

    Coordinates build() {
        new Coordinates(latitude, longitude)
    }
}
