package ru.j4j.eventSystem.callback;

import ru.j4j.eventSystem.event.Event;
import ru.j4j.eventSystem.event.EventHolder;
import ru.j4j.eventSystem.EventBus;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
@FunctionalInterface
public interface CallbackBeforeSendVoid<T extends Event> {

    void beforeSend(EventBus system, EventHolder<T> eventHolder);

    default boolean beforeSendWithReturn(EventBus system, EventHolder<T> eventHolder) {
        beforeSend(system, eventHolder);
        return true;
    }

}
