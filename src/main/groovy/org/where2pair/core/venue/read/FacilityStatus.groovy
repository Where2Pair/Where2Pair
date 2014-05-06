package org.where2pair.core.venue.read

import org.where2pair.core.venue.common.Facility
import groovy.transform.Immutable

import static org.where2pair.core.venue.read.FacilityStatus.Status.AVAILABLE
import static org.where2pair.core.venue.read.FacilityStatus.Status.UNAVAILABLE

@Immutable
class FacilityStatus {

    Facility facility
    Status status

    static FacilityStatus facilityAvailable(Facility facility) {
        new FacilityStatus(facility, AVAILABLE)
    }

    static FacilityStatus facilityUnavailable(Facility facility) {
        new FacilityStatus(facility, UNAVAILABLE)
    }

    boolean isAvailable() {
        this.status == AVAILABLE
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