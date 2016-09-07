package ru.j4j.eventSystem.callback;

import org.testng.annotations.Test;
import ru.j4j.eventSystem.EventBus;
import ru.j4j.eventSystem.event.Event;
import ru.j4j.eventSystem.event.EventHolder;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static ru.j4j.eventSystem.callback.CallbackBuilder.buildCallback;

/**
 * @author Artemiy_Shchekotov
 * @since 8/2/2016.
 */
@SuppressWarnings({"unchecked", "ThrowableInstanceNeverThrown"})
public class CallbackBuilderTest {

    static EventBus           eventBus    = new EventBus();
    static Event              event       = new Event() {};
    static EventHolder<Event> eventHolder = new EventHolder<>(event);
    static Exception          exception   = new Exception();

    @Test
    public void ifSendVoidTest() {
        class CallbackImpl implements CallbackBeforeSendVoid {
            public void beforeSend(EventBus system, EventHolder eventHolder) {}
        }
        CallbackBeforeSendVoid callbackBeforeSendVoid = spy(new CallbackImpl());
        buildCallback().ifSend(callbackBeforeSendVoid).getCallback().beforeSend(eventBus, eventHolder);
        verify(callbackBeforeSendVoid, times(1)).beforeSend(eq(eventBus), eq(eventHolder));
        verify(callbackBeforeSendVoid, times(1)).beforeSendWithReturn(eq(eventBus), eq(eventHolder));
    }

    @Test
    public void ifSendTest() {
        CallbackBeforeSend callbackBeforeSend = mock(CallbackBeforeSend.class);
        buildCallback().ifSend(callbackBeforeSend).getCallback().beforeSend(eventBus, eventHolder);
        verify(callbackBeforeSend, times(1)).beforeSend(eq(eventBus), eq(eventHolder));
    }

    @Test
    public void ifExceptionTest() throws Exception {
        CallbackException callbackException = mock(CallbackException.class);
        buildCallback().ifException(callbackException).getCallback().exception(eventBus, eventHolder, exception);
        verify(callbackException, times(1)).exception(eq(eventBus), eq(eventHolder), eq(exception));
    }

    @Test
    public void ifCompleteTest() {
        CallbackCompleted callbackCompleted = mock(CallbackCompleted.class);
        buildCallback().ifComplete(callbackCompleted).getCallback().completed(eventBus, event);
        verify(callbackCompleted, times(1)).completed(eq(eventBus), eq(event));
    }
}
