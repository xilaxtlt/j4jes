package ru.j4j.eventSystem;

import ru.j4j.eventSystem.callback.Callback;
import ru.j4j.eventSystem.callback.CallbackException;
import ru.j4j.eventSystem.event.Event;
import ru.j4j.eventSystem.event.EventHolder;
import ru.j4j.eventSystem.event.EventListener;
import ru.j4j.tools.annotations.Disposable;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static ru.j4j.tools.exceptions.Exceptions.runThrowable;
import static ru.j4j.tools.exceptions.Exceptions.throwException;
import static ru.j4j.tools.reflection.ClassReflections.newInstance;
import static ru.j4j.tools.repeatable.Repeatable.repeat;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
@Disposable
class Courier<T extends Event> {
    private final EventBus eventBus;
    private final EventHolder<T> eventHolder;
    private final String         eventName;
    private final Listener       listener;
    private final Callback<T> callback;

    private final AtomicBoolean deliveryLocker = new AtomicBoolean(false);

    /* ************************************************************************************************************* */
    /* Constructors Section */
    /* ************************************************************************************************************* */

    Courier(EventBus eventBus,
            EventHolder<T> eventHolder,
            Listener       listener,
            Callback<T>    callback)
    {
        this.eventBus = eventBus;
        this.eventHolder = eventHolder;
        this.eventName   = requireNonNull(eventHolder.event.getEventName(), "Event must have name.");
        this.listener    = listener;
        this.callback    = callback;
    }

    /* ************************************************************************************************************* */
    /* Package Section */
    /* ************************************************************************************************************* */

    void deliverEvent(final Supplier<Executor> executorProvider) {
        if (!deliveryLocker.compareAndSet(false, true)) {
            throw new IllegalStateException("This Courier has already been run. Event name: " + eventName);
        }

        final Executor executor = createExecutor(executorProvider);
        final Runnable task     = createTask();

        //TODO Ordinal (with sync and async dependencies)
        executor.execute(task);
    }

    /* ************************************************************************************************************* */
    /* Private Section */
    /* ************************************************************************************************************* */

    private Executor createExecutor(final Supplier<Executor> executorProvider)
    {
        final Executor executor = executorProvider != null ? executorProvider.get() : null;
        if (executor != null) {
            return task -> {
                final CompletableFuture<Void> future = CompletableFuture.runAsync(task, executor);
                future.whenComplete((result, throwable) -> {
                    if (future.isCancelled()) {
                        callback.canceled(eventBus, eventHolder.event, future.isDone(), throwable);
                    }
                });
            };
        } else {
            return Runnable::run;
        }
    }

    private Runnable createTask() {
        return () -> {
            final boolean send = callback.beforeSend(eventBus, eventHolder);
            if (send) {
                EventListener config = listener.configuration;
                repeat(
                        config.repeatCount(),
                        config.repeatTimeout(),
                        config.repeatTimeoutFactor(),
                        config.repeatTimeoutDivider(),
                        config.repeatTimeoutIncrement(),
                        () -> {
                            try {
                                listener.invoke(eventBus, eventHolder.event);
                            } catch (IllegalAccessException e) {
                                throw throwException(e);
                            } catch (InvocationTargetException e) {
                                Throwable throwable = e.getTargetException();
                                if (throwable instanceof Exception) {
                                    handleException((Exception)throwable);
                                } else {
                                    throw throwException(e);
                                }
                            }
                        }
                );

                callback.completed(eventBus, eventHolder.event);
            }
        };
    }

    private void handleException(final Exception exception)
    {
        final Class<? extends CallbackException> clazz = listener.configuration.exceptionHandler();
        try {
            if (clazz != EventListener.DEFAULT_EXCEPTION_HANDLER.class) {
                @SuppressWarnings("unchecked")
                CallbackException<T> handler = (CallbackException<T>) newInstance(clazz);
                handler.exception(eventBus, eventHolder, exception);
            } else {
                throw exception;
            }
        } catch (Exception e) {
            runThrowable(() -> callback.exception(eventBus, eventHolder, exception));
        }

    }

}
