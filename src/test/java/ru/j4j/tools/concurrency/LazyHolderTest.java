package ru.j4j.tools.concurrency;

import org.testng.Assert;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Stream.generate;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
public class LazyHolderTest extends Assert {

    private static final List<Container> data = new ArrayList<>();

    static {
        generate(Container::new)
                .limit(100000)
                .forEach(container -> {
                    container.objectCreator = new ObjectCreator();
                    container.lazyHolder    = new LazyHolder<>(container.objectCreator::createObject);
                    data.add(container);
                });
    }

    @Test(invocationCount = 1000, threadPoolSize = 1000)
    public void createObjectTest() throws InterruptedException {
        data.forEach(container -> {
            container.object = container.lazyHolder.get();
            assertNotNull(container.object);
            assertEquals(container.objectCreator.counter.get(), 1);
        });
    }

    private static class Container {
        LazyHolder<Object> lazyHolder;
        ObjectCreator objectCreator;
        Object object;
    }

    private static class ObjectCreator {
        final AtomicInteger counter = new AtomicInteger();

        public Object createObject() {
            counter.incrementAndGet();
            return new Object();
        }
    }

}
