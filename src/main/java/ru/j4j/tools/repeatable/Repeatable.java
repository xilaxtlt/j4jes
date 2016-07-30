package ru.j4j.tools.repeatable;

import static ru.j4j.tools.threads.ThreadUtils.sleep;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
public class Repeatable {

    public static void repeat(int count, long timeout, int factor, int divider, long increment, Runnable task) {
        if (count < 1) {
            task.run();
            return;
        }

        if (divider == 0) divider = 1;

        while (true) try {
            task.run();                                                                  // run standard
            break;
        } catch (RuntimeException e) {
            count--;
            if (count < 0) {
                throw e;                                                                 // complete repeating
            }
            sleep(timeout);                                                              // wait timeout
            timeout = timeout * factor / divider + increment;                            // change timeout
        }
    }

}
