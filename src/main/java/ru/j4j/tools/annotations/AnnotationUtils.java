package ru.j4j.tools.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
public final class AnnotationUtils {

    public static <T extends Annotation> void performAnnotationIfPresent(Method source, Class<T> annotationType, Consumer<T> consumer) {
        T annotation = source.getAnnotation(annotationType);
        Optional.ofNullable(annotation).ifPresent(consumer);
    }

}
