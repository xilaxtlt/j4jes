package ru.j4j.eventSystem;

import org.testng.Assert;
import org.testng.annotations.Test;
import ru.j4j.eventSystem.event.Event;

import java.util.Arrays;
import java.util.Collection;

import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static ru.j4j.eventSystem.HierarchyEventTypeCache.flattenHierarchy;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
public class HierarchyEventTypeCacheTest {

    @Test
    public void test() {
        //FIXME Проблема бесконечного цикла
        Collection<Class<? extends Event>> types_A = flattenHierarchy(EventA.class);
        assertTrue(types_A.containsAll(asList(EventA.class, Event.class)));
    }

    public static class EventA implements Event {}

}
