package org.where2pair.write.venue

import groovy.transform.Immutable

import java.security.MessageDigest

@Immutable
class NewVenueId {
    String name
    double latitude
    double longitude
    String addressLine1

    @Override
    String toString() {
        MessageDigest digest = MessageDigest.getInstance('MD5')
        digest.update(stringForHashing().bytes)
        new BigInteger(1, digest.digest()).toString(16).padLeft(32, '0')
    }

    private String stringForHashing() {
        "${name}|${latitude}|${longitude}|${addressLine1}"
    }
}

