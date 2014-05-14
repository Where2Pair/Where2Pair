package org.where2pair.read.venue


public enum Facility {

    WIFI,
    POWER,
    MOBILE_PAYMENTS

    static Facility parseFacility(String facility) {
        facility.toUpperCase() as Facility
    }

}