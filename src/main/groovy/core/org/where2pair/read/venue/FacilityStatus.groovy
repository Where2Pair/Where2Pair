package org.where2pair.read.venue

import groovy.transform.Immutable
import org.where2pair.common.venue.Facility

import static org.where2pair.read.venue.FacilityStatus.Status.STATUS_UNKNOWN

@Immutable
class FacilityStatus {

    Facility facility
    Status status

    static FacilityStatus facilityStatusUnknown(Facility facility) {
        new FacilityStatus(facility, STATUS_UNKNOWN)
    }

    boolean isAvailable() {
        status.isAvailable()
    }

    enum Status {
        AVAILABLE('Y', true),
        UNAVAILABLE('N', false),
        STATUS_UNKNOWN('UNKNOWN', false)

        final String label
        final boolean available

        private Status(String label, boolean available) {
            this.label = label
            this.available = available
        }

        static Status parseStatus(String label) {
            Status status = values().find { it.label == label.toUpperCase() }

            if (status == null) throw new IllegalArgumentException("Unrecognized status: $label")

            status
        }
    }

}