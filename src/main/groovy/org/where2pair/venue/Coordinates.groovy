package org.where2pair.venue

import groovy.transform.Immutable

@Immutable
class Coordinates {

    double lat
    double lng

    double distanceInKmTo(Coordinates coordinates) {
        double earthRadius = 6371
        double dLat = Math.toRadians(coordinates.lat - lat)
        double dLng = Math.toRadians(coordinates.lng - lng)
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(coordinates.lat)) * Math.cos(Math.toRadians(lat)) *
                Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        double dist = earthRadius * c
        dist.doubleValue()
    }

    double distanceInMilesTo(Coordinates coordinates) {
        distanceInKmTo(coordinates) * 0.62137
    }
}
