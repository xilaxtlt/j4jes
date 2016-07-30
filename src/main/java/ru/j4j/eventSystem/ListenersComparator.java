package ru.j4j.eventSystem;

import ru.j4j.eventSystem.event.EventListener;
import ru.j4j.eventSystem.event.EventListener.DEFAULT_CLASS;
import ru.j4j.eventSystem.exception.CyclicOrderingException;

import java.util.Comparator;

import static java.lang.String.format;

/**
 * @author Artemiy Shchekotov (xilaxtlt)
 */
class ListenersComparator implements Comparator<Listener> {

    @Override
    public int compare(Listener left, Listener right) {
        Class<?>      leftClass       = left.object.getClass();
        Class<?>      rightClass      = right.object.getClass();
        EventListener leftConfig      = left.configuration;
        EventListener rightConfig     = right.configuration;
        Class<?>      leftAfterClass  = leftConfig.afterListener();
        Class<?>      rightAfterClass = rightConfig.afterListener();

        boolean leftAfterRight = leftAfterClass != DEFAULT_CLASS.class
                                 && leftAfterClass.isAssignableFrom(rightClass);

        boolean rightAfterLeft = rightAfterClass != DEFAULT_CLASS.class
                                 && rightAfterClass.isAssignableFrom(leftClass);

        if (leftAfterRight && rightAfterLeft) {
            String leftName  = leftClass.getName();
            String rightName = rightClass.getName();
            throw new CyclicOrderingException(format("Mutual cyclic ordering. Listener: %s and %s", leftName, rightName));
        }

        if (leftAfterRight) {
            return 1;
        }

        if (rightAfterLeft) {
            return -1;
        }

        int leftOrder  = leftConfig.order();
        int rightOrder = rightConfig.order();

        return leftOrder - rightOrder;
    }

}
