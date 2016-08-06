package ru.j4j.eventSystem.event;

import ru.j4j.eventSystem.callback.Callback;
import ru.j4j.tools.annotations.NotNull;
import ru.j4j.tools.annotations.Nullable;

import java.io.Serializable;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
public interface Event extends Serializable {

    @NotNull
    default String getEventName() { return this.getClass().getName(); }

    default boolean asynchronous() { return false; }

    default int  repeatCount()                { return 0; }
    default long repeatTimeout()              { return 0; }
    default int  repeatTimeoutFactor()        { return 1; }
    default int  repeatTimeoutDivider()       { return 1; }
    default int  repeatTimeoutIncremental()   { return 0; }

    @Nullable
    default Callback<? extends Event> getCallback()   { return null; }
    default boolean                   asyncCallback() { return false; }

}
