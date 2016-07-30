package ru.j4j.tools.concurrency;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static ru.j4j.tools.exceptions.Exceptions.throwException;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
public class LazyHolder<T> {
    private final AtomicInteger flag = new AtomicInteger();
    private final Supplier<T> supplier;
    private Throwable throwable;
    private T object;

    public LazyHolder(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        if (object == null && flag.compareAndSet(0, 1)) {
            try {
                object = supplier.get();
                flag.set(2);
            } catch (RuntimeException | Error e) {
                throwable = e;
                flag.set(3);
                throw e;
            }
        }

        while (flag.get() == 1) {
            Thread.yield();
            if (flag.get() == 3) {
                throw throwException(throwable);
            }
        }

        return object;
    }

}
