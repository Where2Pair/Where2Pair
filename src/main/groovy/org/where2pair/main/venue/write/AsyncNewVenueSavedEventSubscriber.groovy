package org.where2pair.main.venue.write

import groovyx.gpars.GParsExecutorsPool
import org.where2pair.core.venue.write.NewVenueSavedEvent
import org.where2pair.core.venue.write.NewVenueSavedEventSubscriber

import java.util.concurrent.ExecutorService

import static java.util.concurrent.Executors.newCachedThreadPool


class AsyncNewVenueSavedEventSubscriber implements NewVenueSavedEventSubscriber {

    private static final ExecutorService executorService = newCachedThreadPool()
    private final NewVenueSavedEventSubscriber subscriber

    static NewVenueSavedEventSubscriber asAsyncSubscriber(NewVenueSavedEventSubscriber newVenueSavedEventSubscriber) {
        new AsyncNewVenueSavedEventSubscriber(newVenueSavedEventSubscriber)
    }

    private AsyncNewVenueSavedEventSubscriber(NewVenueSavedEventSubscriber subscriber) {
        this.subscriber == subscriber
    }

    @Override
    void notifyNewVenueSaved(NewVenueSavedEvent newVenueSavedEvent) {
        GParsExecutorsPool.withExistingPool(executorService) {
            Closure notify = { subscriber.notifyNewVenueSaved(newVenueSavedEvent) }
            notify.callAsync()
        }
    }
}
