package ru.j4j.eventSystem;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.j4j.eventSystem.event.EventListener;
import ru.j4j.eventSystem.exception.CyclicOrderingException;

import java.lang.reflect.Method;

import static org.testng.Assert.assertTrue;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
public class ListenersComparatorTest {

    static ListenersComparator comparator = new ListenersComparator();

    static class A  { @EventListener(order = 1) public void m() {} }
    static class A2 { @EventListener(order = 1) public void m() {} }
    static class B  { @EventListener(order = 2) public void m() {} }
    static class C  { @EventListener(order = 3, afterListener = D.class) public void m() {} }
    static class D  { @EventListener(order = 4) public void m() {} }
    static class F  { @EventListener(order = 5, afterListener = G.class) public void m() {} }
    static class G  { @EventListener(order = 6, afterListener = F.class) public void m() {} }

    static Listener listener_A;
    static Listener listener_A2;
    static Listener listener_B;
    static Listener listener_C;
    static Listener listener_D;
    static Listener listener_F;
    static Listener listener_G;

    @BeforeClass
    public static void before() throws NoSuchMethodException {
        Object object_A  = new A();
        Object object_A2 = new A2();
        Object object_B  = new B();
        Object object_C  = new C();
        Object object_D  = new D();
        Object object_F  = new F();
        Object object_G  = new G();

        Method method_A  = object_A .getClass().getMethod("m");
        Method method_A2 = object_A2.getClass().getMethod("m");
        Method method_B  = object_B .getClass().getMethod("m");
        Method method_C  = object_C .getClass().getMethod("m");
        Method method_D  = object_D .getClass().getMethod("m");
        Method method_F  = object_F .getClass().getMethod("m");
        Method method_G  = object_G .getClass().getMethod("m");

        EventListener config_A  = method_A .getAnnotation(EventListener.class);
        EventListener config_A2 = method_A2.getAnnotation(EventListener.class);
        EventListener config_B  = method_B .getAnnotation(EventListener.class);
        EventListener config_C  = method_C .getAnnotation(EventListener.class);
        EventListener config_D  = method_D .getAnnotation(EventListener.class);
        EventListener config_F  = method_F .getAnnotation(EventListener.class);
        EventListener config_G  = method_G .getAnnotation(EventListener.class);

        listener_A  = new Listener(config_A , object_A , method_A );
        listener_A2 = new Listener(config_A2, object_A2, method_A2);
        listener_B  = new Listener(config_B , object_B , method_B );
        listener_C  = new Listener(config_C , object_C , method_C );
        listener_D  = new Listener(config_D , object_D , method_D );
        listener_F  = new Listener(config_F , object_F , method_F );
        listener_G  = new Listener(config_G , object_G , method_G );
    }

    @Test
    public void orderingTest() {
        assertTrue(comparator.compare(listener_A, listener_A2) == 0);
        assertTrue(comparator.compare(listener_A, listener_B) < 0);
        assertTrue(comparator.compare(listener_B, listener_A) > 0);
    }

    @Test
    public void ofterListenerTest() {
        assertTrue(comparator.compare(listener_C, listener_D) > 0);
        assertTrue(comparator.compare(listener_D, listener_C) < 0);
        assertTrue(comparator.compare(listener_C, listener_B) > 0);
        assertTrue(comparator.compare(listener_B, listener_C) < 0);
    }

    @Test(expectedExceptions = CyclicOrderingException.class)
    public void MutualCyclicOrderingTest() {
        comparator.compare(listener_F, listener_G);
    }

}
