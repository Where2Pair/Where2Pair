package org.where2pair.core.venue.write

class NewVenueSavedEventPublisher {

    final NewVenueSavedEventListener newVenueSavedEventListener

    public NewVenueSavedEventPublisher(NewVenueSavedEventListener newVenueSavedEventListener) {
        this.newVenueSavedEventListener = newVenueSavedEventListener
    }

    void publish(NewVenueSavedEvent newVenueSavedEvent) {
        newVenueSavedEventListener.notifyNewVenueSaved(newVenueSavedEvent)
    }
}
