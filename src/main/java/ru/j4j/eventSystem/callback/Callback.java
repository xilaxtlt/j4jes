package ru.j4j.eventSystem.callback;

import ru.j4j.eventSystem.event.Event;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
public interface Callback<T extends Event>
        extends CallbackCompleted<T>,
                CallbackBeforeSend<T>,
                CallbackException<T> {
}
