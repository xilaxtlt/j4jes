package ru.j4j.eventSystem.callback;

import ru.j4j.eventSystem.event.Event;
import ru.j4j.eventSystem.event.EventHolder;
import ru.j4j.eventSystem.EventBus;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
@FunctionalInterface
public interface CallbackException<T extends Event> {

    void exception(EventBus eventBus, EventHolder<T> event, Exception exception) throws Exception;

}
