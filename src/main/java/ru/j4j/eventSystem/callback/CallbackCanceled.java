package ru.j4j.eventSystem.callback;

import ru.j4j.eventSystem.event.Event;
import ru.j4j.eventSystem.EventBus;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
@FunctionalInterface
public interface CallbackCanceled<T extends Event> {

    void canceled(EventBus eventBus, T event, boolean isDone, Throwable exception);

}
