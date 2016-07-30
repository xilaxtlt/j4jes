package ru.j4j.eventSystem;

import ru.j4j.eventSystem.event.Event;
import ru.j4j.eventSystem.event.EventListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
final class Listener {
    public final EventListener configuration;
    public final Object        object;
    public final Method        method;

    private final int argumentCount;
    private final int eventBusArgumentIndex;
    private final int eventArgumentIndex;

    Listener(EventListener configuration, Object object, Method method) {
        this.configuration = configuration;
        this.object        = object;
        this.method        = method;
        this.argumentCount = method.getParameterCount();

        method.setAccessible(true);

        Class[] argumentTypes = method.getParameterTypes();
        this.eventBusArgumentIndex = findArgumentIndex(EventBus.class, argumentTypes);
        this.eventArgumentIndex    = findArgumentIndex(Event.class, argumentTypes);
    }

    void invoke(EventBus eventBus, Event event) throws InvocationTargetException, IllegalAccessException {
        Object[] args = new Object[argumentCount];

        if (eventBusArgumentIndex > -1) {
            args[eventBusArgumentIndex] = eventBus;
        }

        if (eventArgumentIndex > -1) {
            args[eventArgumentIndex] = event;
        }

        method.invoke(object, args);
    }

    private int findArgumentIndex(Class<?> argumentType, Class<?>[] argumentTypes) {
        for (int i = 0; i < argumentTypes.length; i++) {
            if (argumentType.isAssignableFrom(argumentTypes[i])) {
                return i;
            }
        }
        return -1;
    }

}
