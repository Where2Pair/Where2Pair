package org.where2pair.common.venue

import groovy.transform.Immutable

import static java.lang.Double.parseDouble

@Immutable
class VenueId {
    String venueName
    Coordinates location
    String addressLine1

    static VenueId from(String venueId) {
        def components = venueId.split('\\|')

        if (components.length != 4) throw new MalformedVenueIdException()

        def name = components[0].replaceAll('_', ' ')
        def location = new Coordinates(parseDouble(components[1]), parseDouble(components[2]))
        def addressLine1 = components[3].replaceAll('_', ' ')
        new VenueId(name, location, addressLine1)
    }

    @Override
    String toString() {
        String nameComponent = venueName.replaceAll(' ', '_')
        String addressLine1Component = addressLine1.replaceAll(' ', '_')
        "${nameComponent}|${location.lat}|${location.lng}|${addressLine1Component}"
    }

    static class MalformedVenueIdException extends RuntimeException {

    }
}
