package org.where2pair.read.venue

import groovy.transform.Immutable
import groovy.transform.TupleConstructor

@TupleConstructor
class VenueService {

    VenueRepository venueRepository

    List<VenueWithDistances> find(OpenTimesCriteria openTimesCriteria,
                                  FacilitiesCriteria facilitiesCriteria,
                                  LocationsCriteria locationsCriteria) {
        findAll(venues())
            .thatAreOpen(openTimesCriteria)
            .thatHaveRequestedFacilities(facilitiesCriteria)
            .sortedByDistance(locationsCriteria)
            .andRestrictTo50Results()
    }

    private List<Venue> venues() {
        venueRepository.getAll()
    }

    private static VenueFilter findAll(List<Venue> venues) {
        new VenueFilter(venues)
    }

    @Immutable
    private static class VenueFilter {
        List<Venue> venues

        VenueFilter thatAreOpen(OpenTimesCriteria openTimesCriteria) {
            new VenueFilter(venues.findAll { Venue venue ->
                openTimesCriteria.satisfiedBy(venue)
            })
        }

        VenueFilter thatHaveRequestedFacilities(FacilitiesCriteria facilitiesCriteria) {
            new VenueFilter(venues.findAll { Venue venue ->
                venue.hasFacilities(facilitiesCriteria)
            })
        }

        VenuesWithDistancesFilter sortedByDistance(LocationsCriteria locationsCriteria) {
            new VenuesWithDistancesFilter(venues.collect { Venue venue ->
                new VenueWithDistances(venue: venue, distances: locationsCriteria.distancesTo(venue))
            }.sort { it.averageDistance })
        }
    }

    @Immutable
    private static class VenuesWithDistancesFilter {
        List<VenueWithDistances> venues

        List<VenueWithDistances> andRestrictTo50Results() {
            venues.size() > 50 ? venues[0..49] : venues
        }
    }
}
