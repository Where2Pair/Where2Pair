package org.where2pair.core.venue.read

class VenueService {

    VenueRepository venueRepository

    List<VenueWithDistances> findNearestTo(OpenTimesCriteria openTimesCriteria,
                                           FacilitiesCriteria facilitiesCriteria,
                                           LocationsCriteria locationsCriteria) {
        List<Venue> openVenues = findOpenVenues(openTimesCriteria)
        List<Venue> venuesWithFacilities = filterVenuesByRequestedFacilities(openVenues, facilitiesCriteria)
        List<VenueWithDistances> sortedVenues = sortVenuesByDistance(venuesWithFacilities, locationsCriteria)

        restrictTo50Results(sortedVenues)
    }

    private List<Venue> findOpenVenues(OpenTimesCriteria openTimesCriteria) {
        venueRepository.getAll().findAll { Venue venue ->
            venue.isOpen(openTimesCriteria)
        }
    }

    private List<Venue> filterVenuesByRequestedFacilities(List<Venue> venues, FacilitiesCriteria facilitiesCriteria) {
        venues.findAll { Venue venue ->
            venue.hasFacilities(facilitiesCriteria)
        }
    }

    private List<VenueWithDistances> sortVenuesByDistance(List<Venue> openVenues, LocationsCriteria locationsCriteria) {
        List<VenueWithDistances> venuesWithDistances = openVenues.collect { Venue venue ->
            new VenueWithDistances(venue: venue, distances: locationsCriteria.distancesTo(venue))
        }

        venuesWithDistances.sort { it.averageDistance }
    }

    private List restrictTo50Results(List venues) {
        venues.size() > 50 ? venues[0..49] : venues
    }

}
