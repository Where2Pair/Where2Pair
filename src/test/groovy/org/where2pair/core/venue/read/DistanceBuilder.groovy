package org.where2pair.core.venue.read

import groovy.transform.Immutable
import org.where2pair.core.venue.common.Coordinates

import static org.where2pair.core.venue.read.DistanceUnit.MILES


class DistanceBuilder {

    Coordinates coordinates

    static DistanceBuilder fromCoordinates(double latitude, double longitude) {
        new DistanceBuilder(new Coordinates(latitude, longitude))
    }

    private DistanceBuilder(Coordinates coordinates) {
        this.coordinates = coordinates
    }

    DistanceToCoordinates miles(int value) {
        new DistanceToCoordinates(new Distance(value, MILES), coordinates)
    }

    @Immutable
    static class DistanceToCoordinates {
        Distance distance
        Coordinates coordinates
    }
}
