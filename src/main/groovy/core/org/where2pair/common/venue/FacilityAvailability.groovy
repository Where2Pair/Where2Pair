package org.where2pair.common.venue

enum FacilityAvailability {
    AVAILABLE('Y', true),
    UNAVAILABLE('N', false),
    STATUS_UNKNOWN('UNKNOWN', false)

    final String label
    final boolean available

    private FacilityAvailability(String label, boolean available) {
        this.label = label
        this.available = available
    }

    static FacilityAvailability parseFacilityAvailability(String label) {
        FacilityAvailability availability = values().find { it.label == label.toUpperCase() }

        if (availability == null) throw new IllegalArgumentException("Unrecognized facility availability: $label")

        availability
    }

    static List<String> asStrings() {
        values().label
    }
}
