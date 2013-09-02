package org.where2pair.venue.find

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.where2pair.venue.Venue

@EqualsAndHashCode
@ToString
class VenueWithDistances {
    Venue venue
    Map distances

    double getAverageDistance() {
        distances.values().sum() / distances.size()
    }
}
