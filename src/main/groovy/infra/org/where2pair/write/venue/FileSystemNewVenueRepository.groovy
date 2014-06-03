package org.where2pair.write.venue

import groovy.transform.TupleConstructor

@TupleConstructor
class FileSystemNewVenueRepository implements NewVenueSavedEventSubscriber {

    final File rootFilePath
    final CurrentTimeProvider timeProvider

    @Override
    void notifyNewVenueSaved(NewVenueSavedEvent newVenueSavedEvent) {

    }

    List<NewVenueSavedEvent> getAll() {
        []
    }
}

