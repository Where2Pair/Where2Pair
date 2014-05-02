package org.where2pair.core.venue.read

import static org.where2pair.core.venue.read.FacilityStatus.AVAILABLE
import static org.where2pair.core.venue.read.FacilityStatus.UNAVAILABLE


public enum Facility {

    WIFI,
    MOBILE_PAYMENTS

    static Map<Facility, FacilityStatus> statusesFor(Facility... availableFacilities) {
        Facility.values().collectEntries {
            [it, (it in availableFacilities) ? AVAILABLE : UNAVAILABLE]
        }
    }

    static Map<Facility, FacilityStatus> statusesFor(Collection<Facility> availableFacilities) {
        Facility.values().collectEntries {
            [it, (it in availableFacilities) ? AVAILABLE : UNAVAILABLE]
        }
    }
}