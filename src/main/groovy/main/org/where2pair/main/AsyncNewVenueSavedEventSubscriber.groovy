package org.where2pair.main

import static java.util.concurrent.Executors.newCachedThreadPool

import groovyx.gpars.GParsExecutorsPool
import org.where2pair.write.venue.NewVenueSavedEvent
import org.where2pair.write.venue.NewVenueSavedEventSubscriber

import java.util.concurrent.ExecutorService

class AsyncNewVenueSavedEventSubscriber implements NewVenueSavedEventSubscriber {

    private static final ExecutorService EXECUTOR_SERVICE = newCachedThreadPool()
    private final NewVenueSavedEventSubscriber subscriber

    static NewVenueSavedEventSubscriber asAsyncSubscriber(NewVenueSavedEventSubscriber newVenueSavedEventSubscriber) {
        new AsyncNewVenueSavedEventSubscriber(newVenueSavedEventSubscriber)
    }

    private AsyncNewVenueSavedEventSubscriber(NewVenueSavedEventSubscriber subscriber) {
        this.subscriber = subscriber
    }

    @Override
    void notifyNewVenueSaved(NewVenueSavedEvent newVenueSavedEvent) {
        GParsExecutorsPool.withExistingPool(EXECUTOR_SERVICE) {
            Closure notify = { subscriber.notifyNewVenueSaved(newVenueSavedEvent) }
            notify.callAsync()
        }
    }
}

