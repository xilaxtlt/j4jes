package ru.j4j.eventSystem;

import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import ru.j4j.eventSystem.event.Event;
import ru.j4j.eventSystem.event.EventListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
public class ListenerTest {

    @Spy
    static ListenerA listenerA;

    static Method listenMethod;
    static Method listenEventBusMethod;
    static Method listenEventAMethod;
    static Method listenEventBustEventAMethod;
    static Method listenEventAEventBusMethod;

    static EventListener listenConfig;
    static EventListener listenEventBusConfig;
    static EventListener listenEventAConfig;
    static EventListener listenEventBustEventAConfig;
    static EventListener listenEventAEventBusConfig;

    static Listener listenListener;
    static Listener listenEventBusListener;
    static Listener listenEventAListener;
    static Listener listenEventBustEventAListener;
    static Listener listenEventAEventBusListener;

    @BeforeClass
    public void before() throws NoSuchMethodException {
        MockitoAnnotations.initMocks(this);

        listenMethod                = listenerA.getClass().getDeclaredMethod("listen");
        listenEventBusMethod        = listenerA.getClass().getDeclaredMethod("listen", EventBus.class);
        listenEventAMethod          = listenerA.getClass().getDeclaredMethod("listen", EventA.class);
        listenEventBustEventAMethod = listenerA.getClass().getDeclaredMethod("listen", EventBus.class, EventA.class);
        listenEventAEventBusMethod  = listenerA.getClass().getDeclaredMethod("listen", EventA.class, EventBus.class);

        listenConfig                = listenMethod.getAnnotation(EventListener.class);
        listenEventBusConfig        = listenEventBusMethod.getAnnotation(EventListener.class);
        listenEventAConfig          = listenEventAMethod.getAnnotation(EventListener.class);
        listenEventBustEventAConfig = listenEventBustEventAMethod.getAnnotation(EventListener.class);
        listenEventAEventBusConfig  = listenEventAEventBusMethod.getAnnotation(EventListener.class);

        listenListener                = new Listener(listenConfig, listenerA, listenMethod);
        listenEventBusListener        = new Listener(listenEventBusConfig, listenerA, listenEventBusMethod);
        listenEventAListener          = new Listener(listenEventAConfig, listenerA, listenEventAMethod);
        listenEventBustEventAListener = new Listener(listenEventBustEventAConfig, listenerA, listenEventBustEventAMethod);
        listenEventAEventBusListener  = new Listener(listenEventAEventBusConfig, listenerA, listenEventAEventBusMethod);
    }

    @Test
    public void constructorTest() {
        assertEquals(listenListener.configuration, listenConfig);
        assertEquals(listenEventBusListener.configuration, listenEventBusConfig);
        assertEquals(listenEventAListener.configuration, listenEventAConfig);
        assertEquals(listenEventBustEventAListener.configuration, listenEventBustEventAConfig);
        assertEquals(listenEventAEventBusListener.configuration, listenEventAEventBusConfig);

        assertEquals(listenListener.object, listenerA);
        assertEquals(listenEventBusListener.object, listenerA);
        assertEquals(listenEventAListener.object, listenerA);
        assertEquals(listenEventBustEventAListener.object, listenerA);
        assertEquals(listenEventAEventBusListener.object, listenerA);

        assertEquals(listenListener.method, listenMethod);
        assertEquals(listenEventBusListener.method, listenEventBusMethod);
        assertEquals(listenEventAListener.method, listenEventAMethod);
        assertEquals(listenEventBustEventAListener.method, listenEventBustEventAMethod);
        assertEquals(listenEventAEventBusListener.method, listenEventAEventBusMethod);
    }

    @Test
    public void invokeTest() throws InvocationTargetException, IllegalAccessException {
        EventBus eventBus_1 = new EventBus();
        EventBus eventBus_2 = new EventBus();
        EventBus eventBus_3 = new EventBus();
        EventBus eventBus_4 = new EventBus();
        EventBus eventBus_5 = new EventBus();

        EventA eventA_1 = new EventA();
        EventA eventA_2 = new EventA();
        EventA eventA_3 = new EventA();
        EventA eventA_4 = new EventA();
        EventA eventA_5 = new EventA();

        listenListener.invoke(eventBus_1, eventA_1);
        listenEventBusListener.invoke(eventBus_2, eventA_2);
        listenEventAListener.invoke(eventBus_3, eventA_3);
        listenEventBustEventAListener.invoke(eventBus_4, eventA_4);
        listenEventAEventBusListener.invoke(eventBus_5, eventA_5);

        verify(listenerA, times(1)).listen();
        verify(listenerA, times(1)).listen(eq(eventBus_2));
        verify(listenerA, times(1)).listen(eq(eventA_3));
        verify(listenerA, times(1)).listen(eq(eventBus_4), eq(eventA_4));
        verify(listenerA, times(1)).listen(eq(eventA_5), eq(eventBus_5));
    }

    public static class EventA implements Event {}

    public static class ListenerA {
        @EventListener
        public void listen() {}

        @EventListener
        public void listen(EventBus eventBus) {}

        @EventListener
        public void listen(EventA eventA) {}

        @EventListener
        public void listen(EventBus eventBus, EventA eventA) {}

        @EventListener
        public void listen(EventA eventA, EventBus eventBus) {}
    }

}
