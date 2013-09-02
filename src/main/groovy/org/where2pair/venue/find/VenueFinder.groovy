package org.where2pair.venue.find

import org.where2pair.venue.Venue
import org.where2pair.venue.VenueRepository

class VenueFinder {

    VenueRepository venueRepository

    List findNearestTo(OpenTimesCriteria openTimesCriteria, FeaturesCriteria featuresCriteria, LocationsCriteria locationsCriteria) {
        List openVenues = venueRepository.getAll().findAll { Venue venue ->
            venue.isOpen(openTimesCriteria)
        }

        List venuesWithFeatures = openVenues.findAll { Venue venue ->
            venue.hasFeatures(featuresCriteria)
        }

        List sortedVenues = sortVenuesByDistance(venuesWithFeatures, locationsCriteria)

        restrictTo50Results(sortedVenues)
    }

    private List sortVenuesByDistance(List openVenues, LocationsCriteria locationsCriteria) {
        List venuesWithDistance = openVenues.collect { Venue venue ->
            new VenueWithDistances(venue: venue, distances: locationsCriteria.distancesTo(venue))
        }

        venuesWithDistance.sort { VenueWithDistances venue ->
            venue.averageDistance
        }
    }

    private List restrictTo50Results(List venues) {
        venues.size() > 50 ? venues[0..49] : venues
    }

}
