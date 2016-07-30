package ru.j4j.tools.exceptions;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
public class Expulsion {

    private final Map<Integer, Throwable> deferredExceptions = new HashMap<>();

    public Throwable deferredThrow(Integer level, Throwable throwable) {
        return deferredExceptions.put(level, throwable);
    }

    public void throwIfExists(Integer level) throws Throwable {
        Throwable throwable = deferredExceptions.remove(level);
        if (throwable != null) {
            throw throwable;
        }
    }

    public void throwIfExistsAsRuntimeException(Integer level) {
        Throwable throwable = deferredExceptions.remove(level);
        if (throwable != null) {
            throw new RuntimeException(throwable.getMessage(), throwable);
        }
    }

}
