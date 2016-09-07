package ru.j4j.eventSystem.event;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
public final class EventHolder<T extends Event> {
    private final boolean asyncMode;
    private final AtomicInteger atomic = new AtomicInteger();

    private T event;

    public EventHolder(boolean asyncMode) {
        this.asyncMode = asyncMode;
    }

    public T getEvent() {
        return asyncMode && atomic.get() > 0 ? event : event;
    }

    public void setEvent(T event) {
        this.event = event;
        if (asyncMode) {
            atomic.incrementAndGet();
        }
    }

}
