package org.where2pair

import groovy.transform.AutoClone;
import groovy.transform.EqualsAndHashCode;
import groovy.transform.ToString;

@EqualsAndHashCode
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
