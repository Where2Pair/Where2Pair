package org.where2pair.infra.venue.write

import org.where2pair.core.venue.write.NewVenueSavedEvent
import org.where2pair.core.venue.write.NewVenueSavedEventSubscriber

class AmazonS3NewVenueRepository implements NewVenueSavedEventSubscriber {

    @Override
    void notifyNewVenueSaved(NewVenueSavedEvent newVenueSavedEvent) {

    }
}
