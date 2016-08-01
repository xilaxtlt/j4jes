package ru.j4j.eventSystem;

import ru.j4j.eventSystem.event.Event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableCollection;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
final class HierarchyEventTypeCache {
    static final Map<Class<? extends Event>, Collection<Class<? extends Event>>> cache = new ConcurrentHashMap<>();

    static Collection<Class<? extends Event>> flattenHierarchy(Class<? extends Event> eventType) {
        return cache.computeIfAbsent(eventType, HierarchyEventTypeCache::__flattenHierarchy);
    }

    private static Collection<Class<? extends Event>> __flattenHierarchy(Class<? extends Event> eventType) {
        final Class<Event>                       eventClass = Event.class;
        final Collection<Class<? extends Event>> result     = new HashSet<>();

        result.add(eventType);
        result.addAll(collectEventInterfaces(eventType));

        Class superType = eventType;
        while ((superType = superType.getSuperclass()) != null
                && eventClass.isAssignableFrom(superType))
        {
            @SuppressWarnings("unchecked")
            Class<? extends Event> type = (Class<? extends Event>)superType;
            result.add(type);
            result.addAll(collectEventInterfaces(type));
        }

        return unmodifiableCollection(result);
    }

    @SuppressWarnings("unchecked")
    private static Collection<Class<? extends Event>> collectEventInterfaces(final Class<? extends Event> type) {
        final Class<Event>                       eventClass = Event.class;
        final Collection<Class<? extends Event>> result     = new ArrayList<>();
        final Consumer[]                         closure    = new Consumer[1];

        closure[0] = object -> {
            if (!eventClass.isAssignableFrom((Class)object)) {
                return;
            }
            Class<? extends Event> target     = (Class<? extends Event>) object;
            Class<?>[]             interfaces = target.getInterfaces();
            if (type != object) {
                result.add(target);
            }
            asList(interfaces).forEach(closure[0]);
        };

        closure[0].accept(type);

        return result;
    }

}
