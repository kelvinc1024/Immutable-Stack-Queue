import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class ImmutableQueueTest {

    private Queue<Integer> buildQueue(Object[] inputParam) {
        Queue<Integer> queue = ImmutableQueue.emptyQueue();
        for (int i = 0; i < inputParam.length; i++) {
            if (inputParam[i] instanceof AddAction) {
                AddAction action = (AddAction) inputParam[i];
                queue = queue.enQueue(action.value);
            } else if (inputParam[i] instanceof DeleteAction) {
                queue = queue.deQueue();
            }
        }
        return queue;
    }

    @ParameterizedTest
    @MethodSource
    void testQueueShouldBeEmptyOrNot(Object[] inputParam, Boolean expectIsEmpty) {
        Queue<Integer> queue = buildQueue(inputParam);
        Assertions.assertEquals(expectIsEmpty, queue.isEmpty());
    }


    private static Stream testQueueShouldBeEmptyOrNot() {
        return Stream.of(
                Arguments.of(new Object[]{}, Boolean.TRUE),
                Arguments.of(new Object[]{new AddAction(1), new AddAction(2), new AddAction(3)}, Boolean.FALSE),
                Arguments.of(new Object[]{new AddAction(1), new AddAction(2), new DeleteAction(), new DeleteAction()}, Boolean.TRUE),
                Arguments.of(new Object[]{new AddAction(1), new DeleteAction(), new AddAction(2), new DeleteAction()}, Boolean.TRUE),
                Arguments.of(new Object[]{new AddAction(1), new DeleteAction(), new AddAction(2)}, Boolean.FALSE)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testQueueHeadAlwaysTheOldestNotDeletedValue(Object[] inputParam, Integer expectedHeadValue) {
        Queue<Integer> queue = buildQueue(inputParam);
        Assertions.assertEquals(expectedHeadValue, queue.head());
    }

    private static Stream testQueueHeadAlwaysTheOldestNotDeletedValue() {
        return Stream.of(
                Arguments.of(new Object[]{new AddAction(1)}, 1),
                Arguments.of(new Object[]{new AddAction(1), new AddAction(2)}, 1),
                Arguments.of(new Object[]{new AddAction(1), new AddAction(2), new AddAction(3)}, 1),
                Arguments.of(new Object[]{new AddAction(1), new AddAction(2), new DeleteAction()}, 2),
                Arguments.of(new Object[]{new AddAction(1), new AddAction(2), new DeleteAction(), new DeleteAction(), new AddAction(3)}, 3),
                Arguments.of(new Object[]{new AddAction(1), new DeleteAction(), new AddAction(2)}, 2)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testQueueHeadErrorIfEmpty(Object[] inputParam, Class<? extends Exception> error) {
        Queue<Integer> queue = buildQueue(inputParam);
        Assertions.assertThrows(error, queue::head);
    }

    @SuppressWarnings("RedundantArrayCreation")
    private static Stream testQueueHeadErrorIfEmpty() {
        return Stream.of(
                Arguments.of(new Object[]{}, EmptyException.class),
                Arguments.of(new Object[]{new AddAction(1), new DeleteAction()}, EmptyException.class),
                Arguments.of(new Object[]{new AddAction(1), new AddAction(2), new DeleteAction(), new DeleteAction()}, EmptyException.class),
                Arguments.of(new Object[]{new AddAction(1), new DeleteAction(), new AddAction(2), new DeleteAction()}, EmptyException.class)
        );
    }


    @ParameterizedTest
    @MethodSource(value = "testQueueHeadErrorIfEmpty")
    void testStackDequeueErrorIfEmpty(Object[] inputParam, Class<? extends Exception> error) {
        Queue<Integer> queue = buildQueue(inputParam);
        Assertions.assertThrows(error, queue::deQueue);
    }

    @Test
    public void canDequeueFromSameObjectAgain() {
        @SuppressWarnings("unchecked")
        Queue<Integer> queue = ImmutableQueue.emptyQueue()
                .enQueue(1)
                .enQueue(2)
                .enQueue(3);
        Queue<Integer> curr = queue;
        for (int i = 1; i <= 3; i++) {
            Assertions.assertEquals(i, curr.head());
            curr = curr.deQueue();
        }
        curr = queue;
        for (int i = 1; i <= 3; i++) {
            Assertions.assertEquals(i, curr.head());
            curr = curr.deQueue();
        }
    }

    @Test
    public void canEnqueueFromSameObjectAgain() {
        @SuppressWarnings("unchecked")
        Queue<Integer> queue = ImmutableQueue.emptyQueue()
                .enQueue(1);
        Queue<Integer> queue1 = queue.enQueue(2).enQueue(3);
        Queue<Integer> queue2 = queue.enQueue(4).enQueue(5);

        int[] queue1Expectation = new int[]{1, 2, 3};
        for (int i = 0; i < queue1Expectation.length; i++) {
            Assertions.assertEquals(queue1Expectation[i], queue1.head());
            queue1 = queue1.deQueue();
        }
        int[] queue2Expectation = new int[]{1, 4, 5};
        for (int i = 0; i < queue2Expectation.length; i++) {
            Assertions.assertEquals(queue2Expectation[i], queue2.head());
            queue2 = queue2.deQueue();
        }
    }
}
