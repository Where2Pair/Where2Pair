package org.where2pair.venue

import groovy.transform.AutoClone
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.where2pair.venue.find.FeaturesCriteria
import org.where2pair.venue.find.OpenTimesCriteria

@EqualsAndHashCode
@ToString
@AutoClone
class Venue {
    long id
    String name
    Coordinates location
    Address address
    WeeklyOpeningTimes weeklyOpeningTimes
    Set features

    boolean isOpen(OpenTimesCriteria openTimesCriteria) {
        weeklyOpeningTimes.isOpen(openTimesCriteria)
    }

    boolean hasFeatures(FeaturesCriteria featuresCriteria) {
        Set upperCaseAvailableFeatures = features.collect { it.toUpperCase() }
        Set upperCaseRequestedFeatures = featuresCriteria.requestedFeatures.collect { it.toUpperCase() }
        upperCaseRequestedFeatures.every { it in upperCaseAvailableFeatures }
    }

    double distanceInKmTo(Coordinates coordinates) {
        location.distanceInKmTo(coordinates)
    }

    double distanceInMilesTo(Coordinates coordinates) {
        location.distanceInMilesTo(coordinates)
    }
}
