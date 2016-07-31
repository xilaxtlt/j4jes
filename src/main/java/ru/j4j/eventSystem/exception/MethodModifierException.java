package ru.j4j.eventSystem.exception;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
public class MethodModifierException extends RuntimeException {

    public MethodModifierException() {
        super();
    }

    public MethodModifierException(String message) {
        super(message);
    }

    public MethodModifierException(String message, Throwable cause) {
        super(message, cause);
    }

    public MethodModifierException(Throwable cause) {
        super(cause);
    }

}
