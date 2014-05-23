package org.where2pair.common.venue

enum Facility {

    WIFI,
    POWER,
    MOBILE_PAYMENTS

    static Facility parseFacility(String facility) {
        facility.replaceAll(' ', '_').toUpperCase() as Facility
    }

    static List<String> asStrings() {
        values()*.toString()
    }

    @Override
    String toString() {
        super.toString().toLowerCase().replaceAll('_', ' ')
    }
}
