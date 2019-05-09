import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class ImmutableStackTest {

    private Stack<Integer> buildStack(Object[] inputParam) {
        Stack<Integer> stack = ImmutableStack.emptyStack();
        for (int i = 0; i < inputParam.length; i++) {
            if (inputParam[i] instanceof AddAction) {
                AddAction action = (AddAction) inputParam[i];
                stack = stack.add(action.value);
            } else if (inputParam[i] instanceof DeleteAction) {
                stack = stack.pop();
            }
        }
        return stack;
    }

    @ParameterizedTest
    @MethodSource
    void testStackShouldBeEmptyOrNot(Object[] inputParam, Boolean expectIsEmpty) {
        Stack<Integer> stack = buildStack(inputParam);
        Assertions.assertEquals(expectIsEmpty, stack.isEmpty());
    }


    private static Stream testStackShouldBeEmptyOrNot() {
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
    void testStackHeadAlwaysTheNewestNotDeletedValue(Object[] inputParam, Integer expectedHeadValue) {
        Stack<Integer> stack = buildStack(inputParam);
        Assertions.assertEquals(expectedHeadValue, stack.head());
    }

    private static Stream testStackHeadAlwaysTheNewestNotDeletedValue() {
        return Stream.of(
                Arguments.of(new Object[]{new AddAction(1)}, 1),
                Arguments.of(new Object[]{new AddAction(1), new AddAction(2)}, 2),
                Arguments.of(new Object[]{new AddAction(1), new AddAction(2), new AddAction(3)}, 3),
                Arguments.of(new Object[]{new AddAction(1), new AddAction(2), new DeleteAction()}, 1),
                Arguments.of(new Object[]{new AddAction(1), new AddAction(2), new DeleteAction(), new DeleteAction(), new AddAction(3)}, 3),
                Arguments.of(new Object[]{new AddAction(1), new DeleteAction(), new AddAction(2)}, 2)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testStackHeadErrorIfEmpty(Object[] inputParam, Class<? extends Exception> error) {
        Stack<Integer> stack = buildStack(inputParam);
        Assertions.assertThrows(error, stack::head);
    }

    @SuppressWarnings("RedundantArrayCreation")
    private static Stream testStackHeadErrorIfEmpty() {
        return Stream.of(
                Arguments.of(new Object[]{}, EmptyException.class),
                Arguments.of(new Object[]{new AddAction(1), new DeleteAction()}, EmptyException.class),
                Arguments.of(new Object[]{new AddAction(1), new AddAction(2), new DeleteAction(), new DeleteAction()}, EmptyException.class),
                Arguments.of(new Object[]{new AddAction(1), new DeleteAction(), new AddAction(2), new DeleteAction()}, EmptyException.class)
        );
    }


    @ParameterizedTest
    @MethodSource(value = "testStackHeadErrorIfEmpty")
    void testStackPopErrorIfEmpty(Object[] inputParam, Class<? extends Exception> error) {
        Stack<Integer> stack = buildStack(inputParam);
        Assertions.assertThrows(error, stack::pop);
    }

    @Test
    public void canPopFromSameObjectAgain() {
        @SuppressWarnings("unchecked")
        Stack<Integer> stack = ImmutableStack.emptyStack()
                .add(1)
                .add(2)
                .add(3);
        Stack<Integer> curr = stack;
        for (int i = 3; i >= 1; i--) {
            Assertions.assertEquals(i, curr.head());
            curr = curr.pop();
        }
        curr = stack;
        for (int i = 3; i >= 1; i--) {
            Assertions.assertEquals(i, curr.head());
            curr = curr.pop();
        }
    }

    @Test
    public void canAddFromSameObjectAgain() {
        @SuppressWarnings("unchecked")
        Stack<Integer> stack = ImmutableStack.emptyStack()
                .add(1);
        Stack<Integer> stack1 = stack.add(2).add(3);
        Stack<Integer> stack2 = stack.add(4).add(5);

        int[] stack1Expectation = new int[]{3, 2, 1};
        for (int i = 0; i < stack1Expectation.length; i++) {
            Assertions.assertEquals(stack1Expectation[i], stack1.head());
            stack1 = stack1.pop();
        }
        int[] stack2Expectation = new int[]{5, 4, 1};
        for (int i = 0; i < stack2Expectation.length; i++) {
            Assertions.assertEquals(stack2Expectation[i], stack2.head());
            stack2 = stack2.pop();
        }
    }

}
