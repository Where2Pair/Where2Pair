package org.where2pair.core.venue.read

import groovy.transform.AutoClone
import groovy.transform.Immutable
import groovy.transform.ToString

@Immutable
@ToString
@AutoClone
class Address {
    String addressLine1
    String addressLine2
    String addressLine3
    String city
    String postcode
    String phoneNumber
}
