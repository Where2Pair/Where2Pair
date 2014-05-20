package org.where2pair.read.venue

import groovy.transform.TupleConstructor
import org.where2pair.read.venue.find.FacilitiesCriteria
import org.where2pair.read.venue.find.LocationsCriteria
import org.where2pair.read.venue.find.OpenTimesCriteria

@TupleConstructor
class VenueService {

    VenueRepository venueRepository

    List<VenueWithDistances> find(OpenTimesCriteria openTimesCriteria,
                                           FacilitiesCriteria facilitiesCriteria,
                                           LocationsCriteria locationsCriteria) {
        List<Venue> openVenues = findOpenVenues(openTimesCriteria)
        List<Venue> venuesWithFacilities = filterVenuesByRequestedFacilities(openVenues, facilitiesCriteria)
        List<VenueWithDistances> sortedVenues = sortVenuesByDistance(venuesWithFacilities, locationsCriteria)

        restrictTo50Results(sortedVenues)
    }

    private List<Venue> findOpenVenues(OpenTimesCriteria openTimesCriteria) {
        venueRepository.getAll().findAll { Venue venue ->
            openTimesCriteria.satisfiedBy(venue)
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