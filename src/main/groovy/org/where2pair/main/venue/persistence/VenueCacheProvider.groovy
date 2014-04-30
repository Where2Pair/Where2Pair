package org.where2pair.main.venue.persistence

import com.google.inject.Provider
import org.where2pair.infra.venue.persistence.HashMapVenueCache
import org.where2pair.infra.venue.write.VenueCache


class VenueCacheProvider implements Provider<VenueCache> {
    @Override
    VenueCache get() {
        return new HashMapVenueCache()
    }
}
