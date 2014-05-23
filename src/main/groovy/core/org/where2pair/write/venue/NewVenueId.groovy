package org.where2pair.write.venue

import groovy.transform.Immutable
import org.where2pair.common.venue.Coordinates

import java.security.MessageDigest

@Immutable
class NewVenueId {
    String venueName
    Coordinates location
    String addressLine1

    String encode() {
        MessageDigest digest = MessageDigest.getInstance('MD5')
        digest.update(toString().bytes)
        new BigInteger(1, digest.digest()).toString(16).padLeft(32, '0')
    }

    @Override
    String toString() {
        String nameComponent = venueName.replaceAll(' ', '_')
        String addressLine1Component = addressLine1.replaceAll(' ', '_')
        "${nameComponent}|${location.lat}|${location.lng}|${addressLine1Component}"
    }
}

