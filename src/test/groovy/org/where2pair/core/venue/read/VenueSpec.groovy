package org.where2pair.core.venue.read

import org.where2pair.core.venue.common.Coordinates
import spock.lang.Specification

import static org.where2pair.core.venue.read.DistanceUnit.KM
import static org.where2pair.core.venue.read.DistanceUnit.MILES
import static spock.util.matcher.HamcrestMatchers.closeTo
import static spock.util.matcher.HamcrestSupport.that

class VenueSpec extends Specification {

    def 'determines distance to supplied coordinates'() {
        given:
        Venue venue = new Venue(location: new Coordinates(lat: venueLatitude, lng: venueLongitude))
        Coordinates coordinates = new Coordinates(lat: targetLatitude, lng: targetLongitude)

        when:
        Distance distanceInKm = venue.distanceTo(coordinates, KM)
        Distance distanceInMiles = venue.distanceTo(coordinates, MILES)

        then:
        that distanceInKm.value, closeTo(expectedDistanceInKm, 0.01)
        distanceInKm.unit == KM
        that distanceInMiles.value, closeTo(expectedDistanceInKm * 0.62137, 0.01)
        distanceInMiles.unit == MILES

        where:
        venueLatitude | venueLongitude | targetLatitude | targetLongitude | expectedDistanceInKm
        0             | 0              | 0              | 0               | 0
        51.713416     | -1.406250      | 51.506552      | -0.11261        | 92.24
        51.530800     | -0.097933      | 51.520921      | -0.081625       | 1.57
        51.530800     | -0.097933      | -33.868135     | 151.210327      | 16990.86
    }

}
