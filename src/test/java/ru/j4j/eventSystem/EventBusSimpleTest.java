package ru.j4j.eventSystem;

import jdk.nashorn.internal.codegen.CompilerConstants;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.j4j.eventSystem.callback.Callback;
import ru.j4j.eventSystem.event.Event;
import ru.j4j.eventSystem.event.EventHolder;
import ru.j4j.eventSystem.event.EventListener;
import ru.j4j.eventSystem.event.MutableEvent;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
public class EventBusSimpleTest {

    private static abstract class AbstractEvent extends MutableEvent {
        final CountDownLatch latch = new CountDownLatch(1);
        volatile boolean runInOtherThread = false;
        private long threadId = Thread.currentThread().getId();
        public AbstractEvent() {
            super("EventA");
        }
        public void controlEvent() {
            runInOtherThread = threadId != Thread.currentThread().getId();
            latch.countDown();
        }
    }

    private static class EventA extends AbstractEvent {}

    private static class EventExceptionally extends EventA {
        public static final RuntimeException exception = new RuntimeException();
        @Override
        public void controlEvent() {
            super.controlEvent();
            throw exception;
        }
    }

    private static class ListenerA {
        @EventListener
        public void listenEventA(EventA eventA) {
            eventA.controlEvent();
        }
    }

    @Test(timeOut = 1000)
    public void sendEventTest() throws Exception {
        test(
                EventBus::new, EventA::new, CallbackTestClass::new,
                ((eventBus, eventA, callback) -> eventBus.send(eventA)),
                (eventBus, eventA, callback) -> assertFalse(eventA.runInOtherThread)
        );
        test(
                EventBus::new, EventA::new, CallbackTestClass::new,
                ((eventBus, eventA, callback) -> eventBus.send(eventA, false)),
                (eventBus, eventA, callback) -> assertFalse(eventA.runInOtherThread)
        );
    }

    @Test(timeOut = 1000)
    public void sendEventAsyncTest() throws Exception {
        test(
                EventBus::new, EventA::new, CallbackTestClass::new,
                ((eventBus, eventA, callback) -> eventBus.send(eventA, true)),
                (eventBus, eventA, callback) -> assertTrue(eventA.runInOtherThread)
        );
    }

    @Test(timeOut = 1000)
    @SuppressWarnings("Convert2MethodRef")
    public void sendEventCallbackTest() throws Exception {
        test( // Simple callback
                EventBus::new, EventA::new, CallbackTestClass::new,
                (eventBus, eventA, callback) -> eventBus.send(eventA, callback),
                (eventBus, eventA, callback) -> assertFalse(eventA.runInOtherThread),
                (eventBus, eventA, callback) -> assertTrue(callback.beforeSendWasInvoked),
                (eventBus, eventA, callback) -> assertTrue(callback.completedWasInvoked),
                (eventBus, eventA, callback) -> assertFalse(callback.exceptionWasInvoked),
                (eventBus, eventA, callback) -> assertFalse(callback.beforeSendWasInvokedInOtherThread),
                (eventBus, eventA, callback) -> assertFalse(callback.completedWasInvokedInOtherThread),
                (eventBus, eventA, callback) -> assertFalse(callback.exceptionWasInvokedInOtherThread)
        );
        test( // Callback with exception listener
                EventBus::new, EventExceptionally::new, CallbackTestClass::new,
                (eventBus, eventA, callback) -> eventBus.send(eventA, callback),
                (eventBus, eventA, callback) -> assertFalse(eventA.runInOtherThread),
                (eventBus, eventA, callback) -> assertTrue(callback.beforeSendWasInvoked),
                (eventBus, eventA, callback) -> assertFalse(callback.completedWasInvoked),
                (eventBus, eventA, callback) -> assertTrue(callback.exceptionWasInvoked),
                (eventBus, eventA, callback) -> assertFalse(callback.beforeSendWasInvokedInOtherThread),
                (eventBus, eventA, callback) -> assertFalse(callback.completedWasInvokedInOtherThread),
                (eventBus, eventA, callback) -> assertFalse(callback.exceptionWasInvokedInOtherThread)
        );
        test( // Callback with no send flag
                EventBus::new, EventA::new, () -> new CallbackTestClass(false),
                (eventBus, eventA, callback) -> eventBus.send(eventA, callback),
                (eventBus, eventA, callback) -> assertFalse(eventA.runInOtherThread),
                (eventBus, eventA, callback) -> assertTrue(callback.beforeSendWasInvoked),
                (eventBus, eventA, callback) -> assertFalse(callback.completedWasInvoked),
                (eventBus, eventA, callback) -> assertFalse(callback.exceptionWasInvoked),
                (eventBus, eventA, callback) -> assertFalse(callback.beforeSendWasInvokedInOtherThread),
                (eventBus, eventA, callback) -> assertFalse(callback.completedWasInvokedInOtherThread),
                (eventBus, eventA, callback) -> assertFalse(callback.exceptionWasInvokedInOtherThread)
        );

        /*
         * With false value for async flag
         */
        test( // Simple callback
                EventBus::new, EventA::new, CallbackTestClass::new,
                (eventBus, eventA, callback) -> eventBus.send(eventA, callback, false),
                (eventBus, eventA, callback) -> assertFalse(eventA.runInOtherThread),
                (eventBus, eventA, callback) -> assertTrue(callback.beforeSendWasInvoked),
                (eventBus, eventA, callback) -> assertTrue(callback.completedWasInvoked),
                (eventBus, eventA, callback) -> assertFalse(callback.exceptionWasInvoked),
                (eventBus, eventA, callback) -> assertFalse(callback.beforeSendWasInvokedInOtherThread),
                (eventBus, eventA, callback) -> assertFalse(callback.completedWasInvokedInOtherThread),
                (eventBus, eventA, callback) -> assertFalse(callback.exceptionWasInvokedInOtherThread)
        );
        test( // Callback with exception listener
                EventBus::new, EventExceptionally::new, CallbackTestClass::new,
                (eventBus, eventA, callback) -> eventBus.send(eventA, callback, false),
                (eventBus, eventA, callback) -> assertFalse(eventA.runInOtherThread),
                (eventBus, eventA, callback) -> assertTrue(callback.beforeSendWasInvoked),
                (eventBus, eventA, callback) -> assertFalse(callback.completedWasInvoked),
                (eventBus, eventA, callback) -> assertTrue(callback.exceptionWasInvoked),
                (eventBus, eventA, callback) -> assertFalse(callback.beforeSendWasInvokedInOtherThread),
                (eventBus, eventA, callback) -> assertFalse(callback.completedWasInvokedInOtherThread),
                (eventBus, eventA, callback) -> assertFalse(callback.exceptionWasInvokedInOtherThread)
        );
        test( // Callback with no send flag
                EventBus::new, EventA::new, () -> new CallbackTestClass(false),
                (eventBus, eventA, callback) -> eventBus.send(eventA, callback, false),
                (eventBus, eventA, callback) -> assertFalse(eventA.runInOtherThread),
                (eventBus, eventA, callback) -> assertTrue(callback.beforeSendWasInvoked),
                (eventBus, eventA, callback) -> assertFalse(callback.completedWasInvoked),
                (eventBus, eventA, callback) -> assertFalse(callback.exceptionWasInvoked),
                (eventBus, eventA, callback) -> assertFalse(callback.beforeSendWasInvokedInOtherThread),
                (eventBus, eventA, callback) -> assertFalse(callback.completedWasInvokedInOtherThread),
                (eventBus, eventA, callback) -> assertFalse(callback.exceptionWasInvokedInOtherThread)
        );
    }

    @Test(timeOut = 1000)
    public void sendEventCallbackAsyncTest() throws Exception {
        test( // Simple callback
                EventBus::new, EventA::new, CallbackTestClass::new,
                (eventBus, eventA, callback) -> eventBus.send(eventA, callback, true),
                (eventBus, eventA, callback) -> assertTrue(eventA.runInOtherThread),
                (eventBus, eventA, callback) -> assertTrue(callback.beforeSendWasInvoked),
                (eventBus, eventA, callback) -> assertTrue(callback.completedWasInvoked),
                (eventBus, eventA, callback) -> assertFalse(callback.exceptionWasInvoked),
                (eventBus, eventA, callback) -> assertTrue(callback.beforeSendWasInvokedInOtherThread),
                (eventBus, eventA, callback) -> assertTrue(callback.completedWasInvokedInOtherThread),
                (eventBus, eventA, callback) -> assertFalse(callback.exceptionWasInvokedInOtherThread)
        );
        test( // Callback with exception listener
                EventBus::new, EventExceptionally::new, CallbackTestClass::new,
                (eventBus, eventA, callback) -> eventBus.send(eventA, callback, true),
                (eventBus, eventA, callback) -> assertTrue(eventA.runInOtherThread),
                (eventBus, eventA, callback) -> assertTrue(callback.beforeSendWasInvoked),
                (eventBus, eventA, callback) -> assertFalse(callback.completedWasInvoked),
                (eventBus, eventA, callback) -> assertTrue(callback.exceptionWasInvoked),
                (eventBus, eventA, callback) -> assertTrue(callback.beforeSendWasInvokedInOtherThread),
                (eventBus, eventA, callback) -> assertFalse(callback.completedWasInvokedInOtherThread),
                (eventBus, eventA, callback) -> assertTrue(callback.exceptionWasInvokedInOtherThread)
        );
        test( // Callback with no send flag
                EventBus::new, EventA::new, () -> new CallbackTestClass(false),
                (eventBus, eventA, callback) -> eventBus.send(eventA, callback, true),
                (eventBus, eventA, callback) -> assertTrue(eventA.runInOtherThread),
                (eventBus, eventA, callback) -> assertTrue(callback.beforeSendWasInvoked),
                (eventBus, eventA, callback) -> assertFalse(callback.completedWasInvoked),
                (eventBus, eventA, callback) -> assertFalse(callback.exceptionWasInvoked),
                (eventBus, eventA, callback) -> assertTrue(callback.beforeSendWasInvokedInOtherThread),
                (eventBus, eventA, callback) -> assertFalse(callback.completedWasInvokedInOtherThread),
                (eventBus, eventA, callback) -> assertFalse(callback.exceptionWasInvokedInOtherThread)
        );
    }

    public void sendEventBuilderTest() {
        //TODO
    }

    public void sendEventBuilderAsyncTest() {
        //TODO
    }

    @SafeVarargs
    private static <C extends Callback<Event>> void test(Supplier<EventBus> eventBusSupplier,
                                                         Supplier<EventA> eventASupplier,
                                                         Supplier<C> callbackSupplier,
                                                         TestConsumer<C> testConsumer,
                                                         TestConsumer<C>... assertions)
            throws Exception
    {
        EventBus eventBus = eventBusSupplier.get();
        eventBus.register(ListenerA.class);
        EventA eventA = eventASupplier.get();
        C callback = callbackSupplier.get();
        testConsumer.consume(eventBus, eventA, callback);
        eventA.latch.await();
        for (TestConsumer<C> consumer: assertions) {
            consumer.consume(eventBus, eventA, callback);
        }
    }

    @FunctionalInterface
    private interface TestConsumer<C extends Callback<Event>> {
        void consume(EventBus eventBus, EventA eventA, C callback) throws Exception;
    }

}
