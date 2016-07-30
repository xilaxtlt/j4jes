package ru.j4j.tools.annotations;

import java.lang.annotation.*;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
public @interface NotNull {
}
