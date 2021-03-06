package ru.j4j.eventSystem.callback;

import ru.j4j.eventSystem.event.Event;
import ru.j4j.eventSystem.EventBus;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
@FunctionalInterface
public interface CallbackCompleted<T extends Event> {

    void completed(EventBus eventBus, T event);

}
