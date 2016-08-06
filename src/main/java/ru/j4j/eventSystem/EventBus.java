package ru.j4j.eventSystem;

import ru.j4j.eventSystem.callback.Callback;
import ru.j4j.eventSystem.callback.CallbackBuilder;
import ru.j4j.eventSystem.callback.CallbackException;
import ru.j4j.eventSystem.event.Event;
import ru.j4j.eventSystem.event.EventHolder;
import ru.j4j.eventSystem.event.EventListener;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.*;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static ru.j4j.eventSystem.HierarchyEventTypeCache.flattenHierarchy;
import static ru.j4j.tools.exceptions.Exceptions.runThrowable;
import static ru.j4j.tools.exceptions.Exceptions.throwException;
import static ru.j4j.tools.reflection.ClassReflections.newInstance;
import static ru.j4j.tools.repeatable.Repeatable.repeat;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
public final class EventBus {
    private final ListenersBank listenersBank = new ListenersBank();
    private final Supplier<Executor> executorProvider;

    public EventBus() {
        this(ForkJoinPool::commonPool);
    }

    public EventBus(Executor executor) {
        this(() -> executor);
    }

    public EventBus(Supplier<Executor> executorProvider) {
        requireNonNull(executorProvider.get(), "Supplier of Executor must not return null");
        this.executorProvider = executorProvider;
    }

    public Object[] register  (Class...  classes) { return listenersBank.register(classes); }
    public void     register  (Object... objects) { listenersBank.register(objects); }
    public void     unregister(Class...  classes) { listenersBank.unregister(classes); }
    public void     unregister(Object... objects) { listenersBank.unregister(objects); }

    public void send(Event event) { send(event, (Callback<Event>)null, false); }
    public <T extends Event> void send(T event, CallbackBuilder<T> builder) { send(event, builder.getCallback()); }
    public <T extends Event> void send(T event, Callback<T> callback) { send(event, callback, false); }
    public <T extends Event> void send(T event, boolean async) { send(event, (Callback<T>) null, async); }
    public <T extends Event> void send(T event, CallbackBuilder<T> builder, boolean async) { send(event, builder.getCallback(), async); }

    public <T extends Event> void send(final T event, final Callback<T> callback, final boolean async) {

        if (event == null) return;

        final Supplier<Executor> executorProvider = this.executorProvider;
        final ListenersBank      listenersBank    = this.listenersBank;

        final Collection<Class<? extends Event>> flattenHierarchy = flattenHierarchy(event.getClass());

        for (Class<? extends Event> type: flattenHierarchy) {

            Collection<Listener> listeners = listenersBank.getRegisteredListeners(type);

            for (Listener listener: listeners) {

                final EventHolder<T> eventHolder = new EventHolder<>(event);
                final Runnable       task        = createTask(listener, eventHolder, callback);
                final Executor       executor    = async ? executorProvider.get() : null;

                if (executor != null)
                    executor.execute(task);
                else
                    task.run();

            }

        }

    }

    /* ************************************************************************************************************* */
    /* Private Section */
    /* ************************************************************************************************************* */

    private <T extends Event> Runnable createTask(final Listener listener,
                                                  final EventHolder<T> eventHolder,
                                                  final Callback<T> callback)
    {
        return () -> {
            final boolean send = callback.beforeSend(this, eventHolder);
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
                                listener.invoke(this, eventHolder.event);
                            } catch (IllegalAccessException e) {
                                throw throwException(e);
                            } catch (InvocationTargetException e) {
                                Throwable throwable = e.getTargetException();
                                if (throwable instanceof Exception) {
                                    handleException(listener, eventHolder, (Exception)throwable, callback);
                                } else {
                                    throw throwException(e);
                                }
                            }
                        }
                );

                callback.completed(this, eventHolder.event);
            }
        };
    }

    private <T extends Event> void handleException(final Listener listener,
                                                   final EventHolder<T> eventHolder,
                                                   final Exception exception,
                                                   final Callback<T> callback)
    {
        final Class<? extends CallbackException> clazz = listener.configuration.exceptionHandler();
        try {
            if (clazz != EventListener.DEFAULT_EXCEPTION_HANDLER.class) {
                @SuppressWarnings("unchecked")
                CallbackException<T> handler = (CallbackException<T>) newInstance(clazz);
                handler.exception(this, eventHolder, exception);
            } else {
                throw exception;
            }
        } catch (Exception e) {
            runThrowable(() -> callback.exception(this, eventHolder, exception));
        }
    }

}
