package org.where2pair.core.venue

class VenueFinder {

    VenueRepository venueRepository

    List findNearestTo(OpenTimesCriteria openTimesCriteria, FacilitiesCriteria facilitiesCriteria, LocationsCriteria locationsCriteria) {
        List openVenues = venueRepository.getAll().findAll { Venue venue ->
            venue.isOpen(openTimesCriteria)
        }

        List venuesWithFacilities = openVenues.findAll { Venue venue ->
            venue.hasFacilities(facilitiesCriteria)
        }

        List sortedVenues = sortVenuesByDistance(venuesWithFacilities, locationsCriteria)

        restrictTo50Results(sortedVenues)
    }

    private List sortVenuesByDistance(List openVenues, LocationsCriteria locationsCriteria) {
        List venuesWithDistances = openVenues.collect { Venue venue ->
            new VenueWithDistances(venue: venue, distances: locationsCriteria.distancesTo(venue))
        }

        venuesWithDistances.sort { it.averageDistance }
    }

    private List restrictTo50Results(List venues) {
        venues.size() > 50 ? venues[0..49] : venues
    }

}
