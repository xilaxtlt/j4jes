package ru.j4j.eventSystem;

import ru.j4j.eventSystem.event.*;
import ru.j4j.eventSystem.event.EventListener;
import ru.j4j.eventSystem.exception.MethodModifierException;
import ru.j4j.tools.reflection.ClassReflections;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static ru.j4j.tools.annotations.AnnotationUtils.performAnnotationIfPresent;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
class ListenersBank {
    private static final ListenersComparator listenersComparator = new ListenersComparator();
    private final Map<Class<? extends Event>, Collection<Listener>> listenersMap = new ConcurrentHashMap<>();

    Object[] register(Class... classes) {
        if (classes == null) return null;
        Object[] objects = stream(classes)
                .map(ClassReflections::newInstance)
                .toArray();
        register(objects);
        return objects;
    }

    void register(Object... objects) {
        if (objects == null) return;
        asList(objects)
                .forEach(object -> asList(object.getClass().getMethods())
                        .forEach(method ->
                                performAnnotationIfPresent(method, EventListener.class, annotation -> {
                                    assertNoStaticMethod(method);
                                    @SuppressWarnings("unchecked")
                                    Class<Event> eventType = stream(method.getParameterTypes())
                                            .filter(Event.class::isAssignableFrom)
                                            .findFirst()
                                            .map(clazz -> (Class<Event>)clazz)
                                            .orElse(Event.class);
                                    Collection<Listener> listeners = getRegisteredListeners(eventType);
                                    listeners.add(new Listener(annotation, object, method));
                                })
                        )
                );
    }

    void unregister(Class... classes) {
        listenersMap.values()
                .forEach(list -> list
                        .forEach(listener -> {
                            Class clazz = listener.object.getClass();
                            stream(classes)
                                    .filter(c -> clazz == c)
                                    .findAny()
                                    .ifPresent(c -> list.remove(listener));
                        })
                );
    }

    void unregister(Object... objects) {
        listenersMap.values()
                .forEach(list -> list
                        .forEach(listener -> stream(objects)
                                .filter(o -> listener.object == o)
                                .findAny()
                                .ifPresent(o -> list.remove(listener)))
                );
    }

    Collection<Listener> getRegisteredListeners(Class<? extends Event> eventType) {
        return listenersMap.computeIfAbsent(eventType, k -> new ConcurrentSkipListSet<>(listenersComparator));
    }

    private static void assertNoStaticMethod(Method method) {
        if (Modifier.isStatic(method.getModifiers())) {
            String clazzName  = method.getDeclaringClass().getName();
            String methodName = method.getName();
            throw new MethodModifierException(
                    format("Method %s of class %s must not have static modifier", methodName, clazzName));
        }
    }

}
