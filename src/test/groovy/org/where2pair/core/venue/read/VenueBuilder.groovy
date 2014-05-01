package org.where2pair.core.venue.read

import org.where2pair.core.venue.common.Coordinates
import org.where2pair.core.venue.common.VenueId


class VenueBuilder {
    private VenueId venueId = new VenueId('venue-name', new Coordinates(1.0, 0.1), 'address-line-1')
    @Delegate private VenueDetailsBuilder venueDetailsBuilder = new VenueDetailsBuilder()

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
