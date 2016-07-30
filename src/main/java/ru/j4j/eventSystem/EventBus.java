package ru.j4j.eventSystem;

import ru.j4j.eventSystem.callback.Callback;
import ru.j4j.eventSystem.callback.CallbackBuilder;
import ru.j4j.eventSystem.event.Event;
import ru.j4j.eventSystem.event.EventHolder;

import java.util.concurrent.*;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static ru.j4j.eventSystem.HierarchyEventTypeCache.flattenHierarchy;

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
        flattenHierarchy(event.getClass()).forEach(type ->
                listenersBank.getRegisteredListeners(type).forEach(listener ->
                        new Courier<>(
                                this,
                                new EventHolder<>(event),
                                listener,
                                callback
                        ).deliverEvent(async ? executorProvider : null)
                )
        );
    }

}
