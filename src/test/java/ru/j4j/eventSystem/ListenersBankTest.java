package ru.j4j.eventSystem;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.j4j.eventSystem.event.Event;
import ru.j4j.eventSystem.event.EventListener;
import ru.j4j.eventSystem.exception.MethodModifierException;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
@SuppressWarnings("unused")
public class ListenersBankTest {
    static final String BANK_NAME_BY_CLASS  = "Register by class";
    static final String BANK_NAME_BY_OBJECT = "Register by object";

    /* ************************************************************************************************************* */
    /* Tests */
    /* ************************************************************************************************************* */
    @Test(dataProvider = "Listener A only by class and object")
    public void registerTest(String bankName, ListenersBank bank) {
        List<Listener> listenersEventA = new ArrayList<>(bank.getRegisteredListeners(Event_A.class));
        List<Listener> listenersEventB = new ArrayList<>(bank.getRegisteredListeners(Event_B.class));
        List<Listener> listenersEventC = new ArrayList<>(bank.getRegisteredListeners(Event_C.class));

        assertEquals(listenersEventA.size(), 2);
        assertEquals(listenersEventB.size(), 1);
        assertEquals(listenersEventC.size(), 0);

        assertEquals(listenersEventA.get(0).method.getName(), "listenA_2");
        assertEquals(listenersEventA.get(1).method.getName(), "listenA_1");
        assertEquals(listenersEventB.get(0).method.getName(), "listenB");
    }

    @Test(dataProvider = "ListenerWithStaticMethod by class and object",
            expectedExceptions = MethodModifierException.class)
    public void registerStaticMethodTest(Object target) {
        if (target instanceof Class) new ListenersBank().register((Class)target);
        else new ListenersBank().register(target);
    }

    @Test(dataProvider = "Listener A only by class and object")
    public void orderByOrderTest(String bankName, ListenersBank bank) {
        List<Listener> listeners = new ArrayList<>(bank.getRegisteredListeners(Event_A.class));

        assertEquals(listeners.size(), 2);

        assertEquals(listeners.get(0).method.getName(), "listenA_2");
        assertEquals(listeners.get(1).method.getName(), "listenA_1");
    }

    @Test(dataProvider = "Listener A and B by class and object")
    public void orderByClassOrdering(String bankName, ListenersBank bank) {
        List<Listener> listeners = new ArrayList<>(bank.getRegisteredListeners(Event_B.class));

        assertEquals(listeners.size(), 2);

        assertEquals(listeners.get(0).method.getDeclaringClass().getSimpleName(), "Listener_B");
        assertEquals(listeners.get(1).method.getDeclaringClass().getSimpleName(), "Listener_A");
    }

    @Test(dataProvider = "Listener A and B by class and object")
    public void unregisterByClassTest(String bankName, ListenersBank bank) {
        bank.unregister(Listener_A.class);
        List<Listener> listeners_A = new ArrayList<>(bank.getRegisteredListeners(Event_A.class));
        List<Listener> listeners_B = new ArrayList<>(bank.getRegisteredListeners(Event_B.class));

        assertEquals(listeners_A.size(), 0);
        assertEquals(listeners_B.size(), 1);

        assertEquals(listeners_B.get(0).method.getDeclaringClass().getSimpleName(), "Listener_B");
    }

    @Test
    public void unregisterByObjectTest() {
        ListenersBank bank        = new ListenersBank();
        Listener_A    listener_a  = new Listener_A();
        Listener_B    listener_b  = new Listener_B();
        Listener_B    listener_b2 = new Listener_B();

        bank.register(listener_a, listener_b);
        bank.unregister(listener_a, listener_b2);

        List<Listener> listeners_A = new ArrayList<>(bank.getRegisteredListeners(Event_A.class));
        List<Listener> listeners_B = new ArrayList<>(bank.getRegisteredListeners(Event_B.class));

        assertEquals(listeners_A.size(), 0);
        assertEquals(listeners_B.size(), 1);

        assertEquals(listeners_B.get(0).method.getDeclaringClass().getSimpleName(), "Listener_B");
    }

    /* ************************************************************************************************************* */
    /* Data Providers */
    /* ************************************************************************************************************* */
    @DataProvider(name = "Listener A only by class and object", parallel = true)
    public Object[][] listenerAOnlyByClassAndObject() {
        ListenersBank bank_1 = new ListenersBank();
        ListenersBank bank_2 = new ListenersBank();
        bank_1.register(Listener_A.class);
        bank_2.register(new Listener_A());
        return new Object[][] {
            {BANK_NAME_BY_CLASS,  bank_1},
            {BANK_NAME_BY_OBJECT, bank_2}
        };
    }

    @DataProvider(name = "Listener A and B by class and object", parallel = true)
    public Object[][] listenerAAndBByClassAndObject() {
        ListenersBank bank_1 = new ListenersBank();
        ListenersBank bank_2 = new ListenersBank();
        bank_1.register(Listener_A.class, Listener_B.class);
        bank_2.register(new Listener_A(), new Listener_B());
        return new Object[][] {
                {BANK_NAME_BY_CLASS,  bank_1},
                {BANK_NAME_BY_OBJECT, bank_2}
        };
    }

    @DataProvider(name = "ListenerWithStaticMethod by class and object", parallel = true)
    public Object[][] listenerWithStaticMethodByClassAndObject() {
        return new Object[][] {
                {ListenerWithStaticMethod.class},
                {new ListenerWithStaticMethod()}
        };
    }

    /* ************************************************************************************************************* */
    /* Targets */
    /* ************************************************************************************************************* */
    static class Listener_A {
        @EventListener(order = 2)
        public void listenA_1(EventBus eventBus, Event_A event_a) {}
        @EventListener(order = 1)
        public void listenA_2(Event_A event_a, EventBus eventBus) {}
        @EventListener(afterListener = Listener_B.class)
        public void listenB(Event_B event_b) {}
        public void listenC(Event_C event_c) {}
    }

    static class Listener_B {
        @EventListener()
        public void listenB(Event_B event_b) {}
    }

    static class ListenerWithStaticMethod {
        @EventListener
        public static void listen() {}
    }

    static class Event_A implements Event {}
    static class Event_B implements Event {}
    static class Event_C implements Event {}

}
