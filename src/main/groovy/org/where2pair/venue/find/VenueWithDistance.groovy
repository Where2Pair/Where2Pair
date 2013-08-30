package org.where2pair.venue.find

import org.where2pair.venue.Venue;

import groovy.transform.EqualsAndHashCode;
import groovy.transform.Immutable
import groovy.transform.ToString;


@EqualsAndHashCode
@ToString
class VenueWithDistance {
	Venue venue
	double distanceInKm
}
