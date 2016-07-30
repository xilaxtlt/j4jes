package ru.j4j.eventSystem.callback;

import ru.j4j.eventSystem.event.Event;
import ru.j4j.eventSystem.event.EventHolder;
import ru.j4j.eventSystem.EventBus;
import ru.j4j.tools.annotations.Nullable;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
public final class CallbackBuilder<T extends Event> {

    private final CallbackImpl<T> callback = new CallbackImpl<>();

    private CallbackBuilder() {
    }

    public static <T extends Event> CallbackBuilder<T> buildCallback() {
        return new CallbackBuilder<>();
    }

    public static <T extends Event> CallbackBuilder<T> buildCallback(Class<T> eventType) {
        return new CallbackBuilder<>();
    }

    public CallbackBuilder<T> ifSend(@Nullable CallbackBeforeSend<T> callbackBeforeSend) {
        callback.callbackBeforeSend = callbackBeforeSend;
        return this;
    }

    public CallbackBuilder<T> ifSend(@Nullable CallbackBeforeSendVoid<T> callbackBeforeSend) {
        callback.callbackBeforeSend = callbackBeforeSend == null
                ? null
                : callbackBeforeSend::beforeSendWithReturn;
        return this;
    }

    public CallbackBuilder<T> ifComplete(@Nullable CallbackCompleted<T> callbackCompleted) {
        callback.callbackCompleted = callbackCompleted;
        return this;
    }

    public CallbackBuilder<T> ifCancel(@Nullable CallbackCanceled<T> callbackCanceled) {
        callback.callbackCanceled = callbackCanceled;
        return this;
    }

    public CallbackBuilder<T> ifException(@Nullable CallbackException<T> callbackException) {
        callback.callbackException = callbackException;
        return this;
    }

    public Callback<T> getCallback() { return callback; }

    public static class CallbackImpl<T extends Event> implements Callback<T> {
        private CallbackBeforeSend<T> callbackBeforeSend;
        private CallbackCompleted<T>  callbackCompleted;
        private CallbackCanceled<T>   callbackCanceled;
        private CallbackException<T>  callbackException;

        private CallbackImpl() {
            // nothing to do
        }

        @Override
        public boolean beforeSend(EventBus system, EventHolder<T> eventHolder) {
            return callbackBeforeSend == null || callbackBeforeSend.beforeSend(system, eventHolder);
        }

        @Override
        public void completed(EventBus eventBus, T event) {
            if (callbackCompleted != null) {
                callbackCompleted.completed(eventBus, event);
            }
        }

        @Override
        public void canceled(EventBus eventBus, T event, boolean isDone, Throwable exception) {
            if (callbackCanceled != null) {
                callbackCanceled.canceled(eventBus, event, isDone, exception);
            }
        }

        @Override
        public void exception(EventBus eventBus, EventHolder<T> event, Exception exception) throws Exception {
            if (callbackCanceled != null) {
                callbackException.exception(eventBus, event, exception);
            }
        }
    }

}
