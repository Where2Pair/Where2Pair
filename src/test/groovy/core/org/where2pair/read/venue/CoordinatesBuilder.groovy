package org.where2pair.read.venue

class CoordinatesBuilder {

    private double latitude = 1.0
    private double longitude = 0.1

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

