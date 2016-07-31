package ru.j4j.tools.reflection;

import java.lang.reflect.Constructor;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
public class ClassReflections {

    public static Object newInstance(Class<?> clazz) {
        try {
            Constructor<?> constructor = clazz.getEnclosingConstructor();
            if (constructor == null) {
                constructor = clazz.getDeclaredConstructor();
            }
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
