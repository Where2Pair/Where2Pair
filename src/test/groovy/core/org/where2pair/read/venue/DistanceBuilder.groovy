package org.where2pair.read.venue

import static org.where2pair.read.venue.DistanceUnit.MILES

import groovy.transform.Immutable

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

