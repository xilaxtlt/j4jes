package ru.j4j.tools.exceptions;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
public final class Exceptions {

    public static RuntimeException throwException(Throwable throwable) {
        if (throwable instanceof RuntimeException) {
            throw (RuntimeException)throwable;
        } else if (throwable instanceof Error) {
            throw (Error)throwable;
        } else {
            throw new RuntimeException(throwable.getMessage(), throwable);
        }
    }

    public static void runThrowable(ThrowableRunnable runnable) {
        try {
            runnable.run();
        } catch (Throwable t) {
            throw throwException(t);
        }
    }

    @FunctionalInterface
    public interface ThrowableRunnable {
        void run() throws Throwable;
    }

}
