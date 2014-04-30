package org.where2pair.infra.venue.write

import org.where2pair.core.venue.read.Venue


public interface VenueCache {

    void put(Venue venue)
}