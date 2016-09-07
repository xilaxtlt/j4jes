package ru.j4j.eventSystem.event;

import ru.j4j.eventSystem.callback.Callback;
import ru.j4j.tools.annotations.NotNull;
import ru.j4j.tools.annotations.Nullable;

import java.io.Serializable;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
public interface Event extends Serializable {

    boolean                   DEFAULT_ASYNCHRONOUS_VALUE         = false;
    int                       DEFAULT_REPEAT_COUNT_VALUE         = 0;
    long                      DEFAULT_TIMEOUT_VALUE              = 0;
    int                       DEFAULT_REPEAT_TIMEOUT_FACTOR      = 1;
    int                       DEFAULT_REPEAT_TIMEOUT_DIVIDER     = 1;
    int                       DEFAULT_REPEAT_TIMEOUT_INCREMENTAL = 0;
    Callback<? extends Event> DEFAULT_CALLBACK_VALUE             = null;
    boolean                   DEFAULT_ASYNC_CALLBACK_VALUE       = false;

    @NotNull
    default String eventName() { return this.getClass().getName(); }

    default boolean asynchronous() { return DEFAULT_ASYNCHRONOUS_VALUE; }

    default int  repeatCount()                { return DEFAULT_REPEAT_COUNT_VALUE; }
    default long repeatTimeout()              { return DEFAULT_TIMEOUT_VALUE; }
    default int  repeatTimeoutFactor()        { return DEFAULT_REPEAT_TIMEOUT_FACTOR; }
    default int  repeatTimeoutDivider()       { return DEFAULT_REPEAT_TIMEOUT_DIVIDER; }
    default int  repeatTimeoutIncremental()   { return DEFAULT_REPEAT_TIMEOUT_INCREMENTAL; }

    @Nullable
    default Callback<? extends Event> callback()      { return DEFAULT_CALLBACK_VALUE; }
    default boolean                   asyncCallback() { return DEFAULT_ASYNC_CALLBACK_VALUE; }

}
