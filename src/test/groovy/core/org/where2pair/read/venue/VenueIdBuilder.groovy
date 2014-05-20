package org.where2pair.read.venue


class VenueIdBuilder {

    static VenueId aRandomVenueId() {
        new VenueId(UUID.randomUUID().toString())
    }
}
