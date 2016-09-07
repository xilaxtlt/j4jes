package ru.j4j.eventSystem;

import ru.j4j.eventSystem.callback.Callback;
import ru.j4j.eventSystem.event.Event;
import ru.j4j.eventSystem.event.EventHolder;

import static org.testng.Assert.assertNotNull;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
public class CallbackTestClass implements Callback<Event> {

    public volatile boolean completedWasInvoked;
    public volatile boolean completedWasInvokedInOtherThread;
    public volatile boolean exceptionWasInvoked;
    public volatile boolean exceptionWasInvokedInOtherThread;
    public volatile boolean beforeSendWasInvoked;
    public volatile boolean beforeSendWasInvokedInOtherThread;

    private final boolean beforeSendResult;
    private final long threadId = Thread.currentThread().getId();

    public CallbackTestClass() {
        this(true);
    }

    public CallbackTestClass(boolean beforeSendResult) {
        this.beforeSendResult = beforeSendResult;
    }

    @Override
    public void completed(EventBus eventBus, Event event) {
        assertNotNull(eventBus);
        assertNotNull(event);
        completedWasInvoked = true;
        completedWasInvokedInOtherThread = Thread.currentThread().getId() != threadId;
    }

    @Override
    public void exception(EventBus eventBus, EventHolder<Event> event, Exception exception) throws Exception {
        assertNotNull(eventBus);
        assertNotNull(event);
        assertNotNull(exception);
        exceptionWasInvoked = true;
        exceptionWasInvokedInOtherThread = Thread.currentThread().getId() != threadId;
    }

    @Override
    public boolean beforeSend(EventBus system, EventHolder<Event> eventHolder) {
        assertNotNull(system);
        assertNotNull(eventHolder);
        assertNotNull(eventHolder.getEvent());
        beforeSendWasInvoked = true;
        beforeSendWasInvokedInOtherThread = Thread.currentThread().getId() != threadId;
        return beforeSendResult;
    }
}
