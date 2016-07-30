package ru.j4j.tools.reflection;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
public class ClassReflections {

    public static Object newInstance(Class clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
