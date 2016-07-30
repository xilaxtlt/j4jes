package ru.j4j.eventSystem.exception;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
public class CyclicOrderingException extends IllegalStateException {

    public CyclicOrderingException() {
        super();
    }

    public CyclicOrderingException(String s) {
        super(s);
    }

    public CyclicOrderingException(String message, Throwable cause) {
        super(message, cause);
    }

    public CyclicOrderingException(Throwable cause) {
        super(cause);
    }

}
