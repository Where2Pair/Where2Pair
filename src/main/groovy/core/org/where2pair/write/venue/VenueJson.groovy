package org.where2pair.write.venue

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

@EqualsAndHashCode
@TupleConstructor
class VenueJson {
    Map<String, ?> jsonMap

    def propertyMissing(String property) {
        jsonMap[property]
    }
}
