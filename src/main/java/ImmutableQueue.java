/**
 * Thinking process of creating immutable queue
 * 1. Copy using array(Working, Not implemented)
 * This is the first thing that comes to mind
 * every time we perform {@link ImmutableQueue#enQueue(Object)} we copy our entire array plus new object to new ImmutableQueue
 * every time we perform {@link ImmutableQueue#deQueue()} we copy our entire array except index 0 to new ImmutableQueue
 * Time complexity analysis :
 * {@link ImmutableQueue#enQueue(Object)} O(n) where n is number of element
 * {@link ImmutableQueue#deQueue()} O(n) where n is number of element
 * {@link ImmutableQueue#emptyQueue()} O(1)
 * {@link ImmutableQueue#head()} O(1)
 * <p>
 * 2. can we do better by using linked list concept?
 * lets suppose we have linked list such as below
 * 1 <- 2 <- 3 <- 4
 * notice that the pointer is pointing back to parent
 * when we want to delete 4 all we need to do is return (4).parent
 * and it will return the linked list of 1 <- 2 <- 3 and the time complexity will be just O(1)
 * but same thing will not work if we want to delete 1,
 * so we can actually do O(1) insert and O(1) delete for latest item (which is {@link ImmutableStack}, but it can't be done for {@link ImmutableQueue})
 * <p>
 *
 * is {@link ImmutableStack} actually useful for implementing {@link ImmutableQueue}?
 * We know queue can be implemented by using 2 stacks, lets see if this can perform better than our first solution
 * inserting into queue has no difference with inserting into stack, so we can implement {@link ImmutableQueue} with O(1) complexity same with {@link ImmutableStack}
 * deleting from queue is the problem we face before, how do we solve this?
 * we can pop data from our 1st stack and insert it to 2nd stack with O(n) operation
 * 1st stack : 1, 2, 3, 4
 * 2nd stack : 4, 3, 2, 1
 * notice that the 2nd stack will have reversed order compare to the first stack, then we can delete 1 from 2nd stack with O(1) operation
 * then what we need to do is create a new stack again by keep popping from 2nd stack, and store it back to 1st stack with O(n) operation
 * 2nd stack : 4, 3, 2
 * 1st stack : 2, 3, 4
 * Time complexity analysis
 * {@link ImmutableQueue#enQueue(Object)} O(1) where n is number of element
 * {@link ImmutableQueue#deQueue()} O(n) but exact calculation is O(2n) where n is number of element
 * {@link ImmutableQueue#emptyQueue()} O(1)
 * {@link ImmutableQueue#head()} O(1)
 *
 * @param <T> Generic type for data you want to store
 */
public class ImmutableQueue<T> implements Queue<T> {

    private Stack<T> stack;
    private T head;

    private ImmutableQueue(T head, Stack<T> stack) {
        this.stack = stack;
        this.head = head;
    }

    @Override
    public Queue<T> enQueue(T data) {
        return new ImmutableQueue<>(this.head, stack.add(data));
    }

    @Override
    public Queue<T> deQueue() {
        Stack<T> originalStack = stack;
        Stack<T> tempStack = ImmutableStack.emptyStack();
        while (!originalStack.isEmpty()) {
            tempStack = tempStack.add(originalStack.head());
            originalStack = originalStack.pop();
        }
        tempStack = tempStack.pop();
        if (tempStack.isEmpty()) {
            return ImmutableQueue.emptyQueue();
        }
        T head = tempStack.head();
        while (!tempStack.isEmpty()) {
            originalStack = originalStack.add(tempStack.head());
            tempStack = tempStack.pop();
        }
        return new ImmutableQueue<>(head, originalStack);
    }

    @Override
    public T head() {
        return head;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public static Queue emptyQueue() {
        return new EmptyQueue();
    }

    private static class EmptyQueue<T> implements Queue<T> {

        Stack<T> stack = ImmutableStack.emptyStack();

        @Override
        public Queue<T> enQueue(T data) {
            return new ImmutableQueue<>(data, stack.add(data));
        }

        @Override
        public Queue<T> deQueue() {
            throw new EmptyException();
        }

        @Override
        public T head() throws EmptyException {
            throw new EmptyException();
        }

        @Override
        public boolean isEmpty() {
            return true;
        }
    }

}
