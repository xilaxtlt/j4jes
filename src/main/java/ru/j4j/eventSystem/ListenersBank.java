package ru.j4j.eventSystem;

import ru.j4j.eventSystem.event.*;
import ru.j4j.tools.reflection.ClassReflections;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static ru.j4j.tools.annotations.AnnotationUtils.performAnnotationIfPresent;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
class ListenersBank {
    private final Map<Class<? extends Event>, Collection<Listener>> listenersMap = new ConcurrentHashMap<>();

    //TODO Register static methods
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
                                performAnnotationIfPresent(method, ru.j4j.eventSystem.event.EventListener.class, annotation -> {
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
        return listenersMap.computeIfAbsent(eventType, k -> new CopyOnWriteArraySet<>());
    }

}
