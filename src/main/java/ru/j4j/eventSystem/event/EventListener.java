package ru.j4j.eventSystem.event;

import ru.j4j.eventSystem.EventBus;
import ru.j4j.eventSystem.callback.CallbackException;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(METHOD)
public @interface EventListener {
    /* ************************************************************************************************************* */
    /* Ordering */
    /* ************************************************************************************************************* */
    int   order()         default 0;
    Class afterListener() default DEFAULT_CLASS.class;


    /* ************************************************************************************************************* */
    /* Asynchronous */
    /* ************************************************************************************************************* */
    boolean asynchronous() default false;

    /* ************************************************************************************************************* */
    /* Repeat */
    /* ************************************************************************************************************* */
    int     repeatCount()            default 0;
    long    repeatTimeout()          default 0;
    int     repeatTimeoutFactor()    default 1;
    int     repeatTimeoutDivider()   default 1;
    long    repeatTimeoutIncrement() default 0;

    /* ************************************************************************************************************* */
    /* Exception */
    /* ************************************************************************************************************* */
    Class<? extends CallbackException> exceptionHandler() default DEFAULT_EXCEPTION_HANDLER.class;

    /* ************************************************************************************************************* */
    /* Default Classes */
    /* ************************************************************************************************************* */

    final class DEFAULT_CLASS {}

    final class DEFAULT_EXCEPTION_HANDLER implements CallbackException {
        @Override
        public void exception(EventBus eventBus, EventHolder event, Exception exception) throws Exception {
            throw exception;
        }
    }

}
