package org.where2pair.core.venue

import org.where2pair.core.venue.common.Coordinates


class CoordinatesBuilder {

    private double latitude
    private double longitude

    static Coordinates someCoordinates() {
        new CoordinatesBuilder().build()
    }

    def build() {
        new Coordinates(latitude, longitude)
    }
}
