package org.where2pair.core.venue.read

import org.where2pair.core.venue.common.Facility
import groovy.transform.Immutable

@Immutable
class FacilityStatus {

    Facility facility
    Status status

    static FacilityStatus facilityAvailable(Facility facility) {
        new FacilityStatus(facility, Status.AVAILABLE)
    }

    static FacilityStatus facilityUnavailable(Facility facility) {
        new FacilityStatus(facility, Status.UNAVAILABLE)
    }

    static enum Status {
        AVAILABLE('Y'),
        UNAVAILABLE('N'),
        STATUS_UNKNOWN('UNKNOWN')

        private String label

        private Status(String label) {
            this.label = label
        }

        @Override
        public String toString() {
            label
        }
    }

}