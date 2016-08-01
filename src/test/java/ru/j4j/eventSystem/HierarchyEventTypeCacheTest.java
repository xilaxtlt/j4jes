package ru.j4j.eventSystem;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.j4j.eventSystem.event.Event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static ru.j4j.eventSystem.HierarchyEventTypeCache.flattenHierarchy;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
public class HierarchyEventTypeCacheTest {

    /* ************************************************************************************************************** */
    /* Tests */
    /* ************************************************************************************************************** */
    @Test(dataProvider = "Flatten Hierarchy Data Provider", singleThreaded = true, timeOut = 2500L)
    public void flattenHierarchyTest(Class<? extends Event> eventType, Class<? extends Event>[] contains) {
        Collection<Class<? extends Event>> types_A = flattenHierarchy(eventType);
        assertEquals(types_A.size(), contains.length);
        assertTrue(types_A.containsAll(asList(contains)));
    }

    @Test(dataProvider = "Flatten Hierarchy Data Provider", singleThreaded = true)
    public void flattenHierarchyTestWithClearCache(Class<? extends Event> eventType, Class<? extends Event>[] contains) {
        HierarchyEventTypeCache.cache.clear();
        flattenHierarchyTest(eventType, contains);
    }

    @Test(singleThreaded = true, timeOut = 2500L)
    public void cacheTest() {
        HierarchyEventTypeCache.cache.clear();

        flattenHierarchy(EventF.class);
        flattenHierarchy(EventF.class);

        List<Class<? extends Event>> types = new ArrayList<>(HierarchyEventTypeCache.cache.get(EventF.class));

        assertEquals(HierarchyEventTypeCache.cache.size(), 1);
        assertEquals(types.size(), 7);
        assertTrue(types.containsAll(asList(EventF.class, EventB.class, EventA.class, IntC.class, IntD.class, IntA.class, Event.class)));
    }

    /* ************************************************************************************************************** */
    /* Data Provider */
    /* ************************************************************************************************************** */
    @DataProvider(name = "Flatten Hierarchy Data Provider")
    public Object[][] flattenHierarchyDataProvider() {
        return new Object[][] {
            {EventA.class, new Class[]{EventA.class, Event.class}},
            {EventB.class, new Class[]{EventB.class, EventA.class, Event.class}},
            {EventC.class, new Class[]{EventC.class, IntC.class, IntD.class, IntA.class, Event.class}},
            {EventD.class, new Class[]{EventD.class, EventC.class, IntC.class, IntD.class, IntA.class, Event.class}},
            {EventF.class, new Class[]{EventF.class, EventB.class, EventA.class, IntC.class, IntD.class, IntA.class, Event.class}},
        };
    }

    /* ************************************************************************************************************** */
    /* Targets */
    /* ************************************************************************************************************** */
    public interface IntA extends Event {}
    public interface IntB {}
    public interface IntD extends IntA {}
    public interface IntC extends IntD {}

    public static class EventA implements Event {}
    public static class EventB extends EventA {}

    public static class EventC implements IntB, IntC {}
    public static class EventD extends EventC {}
    public static class EventF extends EventB implements IntC, IntB {}

}
