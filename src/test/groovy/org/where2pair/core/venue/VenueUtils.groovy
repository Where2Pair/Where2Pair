package org.where2pair.core.venue

import org.where2pair.core.venue.read.Address
import org.where2pair.core.venue.common.Coordinates

import static org.where2pair.core.venue.StringUtils.randomString

class VenueUtils {

    private static final RANDOM = new Random()

    static String randomName() {
        randomString()
    }

    static Address randomAddress() {
        new Address(randomString(), randomString(), randomString(), randomString(), randomString(), randomString())
    }

    static Coordinates randomCoordinates() {
        new Coordinates(RANDOM.nextDouble())
    }
}
