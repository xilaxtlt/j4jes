package ru.j4j.eventSystem.event;

import ru.j4j.eventSystem.callback.Callback;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
public abstract class MutableEvent implements Event {

    protected final String eventName;

    protected boolean                   asynchronous;
    protected int                       repeatCount;
    protected long                      repeatTimeout;
    protected int                       repeatTimeoutFactor;
    protected int                       repeatTimeoutDivider;
    protected int                       repeatTimeoutIncremental;
    protected Callback<? extends Event> callback;
    protected boolean                   asyncCallback;

    public MutableEvent(String eventName) {
        this.asynchronous             = DEFAULT_ASYNCHRONOUS_VALUE;
        this.repeatCount              = DEFAULT_REPEAT_COUNT_VALUE;
        this.repeatTimeout            = DEFAULT_TIMEOUT_VALUE;
        this.repeatTimeoutFactor      = DEFAULT_REPEAT_TIMEOUT_FACTOR;
        this.repeatTimeoutDivider     = DEFAULT_REPEAT_TIMEOUT_DIVIDER;
        this.repeatTimeoutIncremental = DEFAULT_REPEAT_TIMEOUT_INCREMENTAL;
        this.callback                 = DEFAULT_CALLBACK_VALUE;
        this.asyncCallback            = DEFAULT_ASYNC_CALLBACK_VALUE;
        this.eventName                = eventName;
    }

    @Override
    public final String eventName() {
        return eventName;
    }

    @Override
    public boolean asynchronous() {
        return asynchronous;
    }

    public void setAsynchronous(boolean asynchronous) {
        this.asynchronous = asynchronous;
    }

    @Override
    public int repeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    @Override
    public long repeatTimeout() {
        return repeatTimeout;
    }

    public void setRepeatTimeout(long repeatTimeout) {
        this.repeatTimeout = repeatTimeout;
    }

    @Override
    public int repeatTimeoutFactor() {
        return repeatTimeoutFactor;
    }

    public void setRepeatTimeoutFactor(int repeatTimeoutFactor) {
        this.repeatTimeoutFactor = repeatTimeoutFactor;
    }

    @Override
    public int repeatTimeoutDivider() {
        return repeatTimeoutDivider;
    }

    public void setRepeatTimeoutDivider(int repeatTimeoutDivider) {
        this.repeatTimeoutDivider = repeatTimeoutDivider;
    }

    @Override
    public int repeatTimeoutIncremental() {
        return repeatTimeoutIncremental;
    }

    public void setRepeatTimeoutIncremental(int repeatTimeoutIncremental) {
        this.repeatTimeoutIncremental = repeatTimeoutIncremental;
    }

    @Override
    public Callback<? extends Event> callback() {
        return callback;
    }

    public void setCallback(Callback<? extends Event> callback) {
        this.callback = callback;
    }

    @Override
    public boolean asyncCallback() {
        return asyncCallback;
    }

    public void setAsyncCallback(boolean asyncCallback) {
        this.asyncCallback = asyncCallback;
    }
}
