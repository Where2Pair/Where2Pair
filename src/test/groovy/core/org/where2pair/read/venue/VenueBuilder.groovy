package org.where2pair.read.venue

import static org.where2pair.read.venue.VenueDetailsBuilder.venueDetails
import static org.where2pair.read.venue.VenueIdBuilder.aRandomVenueId

class VenueBuilder {
    private VenueId venueId = aRandomVenueId()
    @Delegate private VenueDetailsBuilder venueDetailsBuilder = venueDetails()

    private VenueBuilder() {
    }

    static VenueBuilder aVenue() {
        new VenueBuilder()
    }

    VenueBuilder withId(VenueId venueId) {
        this.venueId = venueId
        this
    }

    VenueBuilder with(VenueDetailsBuilder venueDetailsBuilder) {
        this.venueDetailsBuilder = venueDetailsBuilder
        this
    }

    Venue build() {
        VenueDetails venueDetails = venueDetailsBuilder.build()
        Venue.newInstance(venueId, venueDetails)
    }

    Map<String, ?> toJson() {
        Map<String, ?> json = venueDetailsBuilder.toJson()
        json.id = venueId.toString()
        json
    }
}

