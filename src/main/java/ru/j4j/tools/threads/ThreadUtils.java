package ru.j4j.tools.threads;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
public class ThreadUtils {

    public static void sleep(long timeout) {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
