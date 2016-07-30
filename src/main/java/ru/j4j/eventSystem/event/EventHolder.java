package ru.j4j.eventSystem.event;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
public final class EventHolder<T extends Event> {
    public T event;

    public EventHolder(T event) {
        this.event = event;
    }
}
