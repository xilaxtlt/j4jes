package ru.j4j.eventSystem;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
public class EventSystemTest {

    public @interface InterfaceMethodListener {
    }

    public interface SomeInterfaceA {
        void someMethodA();
        void someMethodB(String parameter1, String parameter2);
        String someMethodC();
        String someMethodD(String parameter1, String parameter2);
    }

    public static class SomeClassA implements SomeInterfaceA {
        @Override
        @InterfaceMethodListener
        public void someMethodA() {

        }

        @Override
        public void someMethodB(String parameter1, String parameter2) {

        }

        @Override
        public String someMethodC() {
            return null;
        }

        @Override
        public String someMethodD(String parameter1, String parameter2) {
            return null;
        }
    }

}
