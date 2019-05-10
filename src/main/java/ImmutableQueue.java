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
 * 2. Implement using 2 stack first iteration (implementation in commit hash : 8880d2ad36e528b4ae317a4025b5f6d590e1272c)
 * can we do better by using linked list concept?
 * lets suppose we have linked list such as below
 * 1 <- 2 <- 3 <- 4
 * notice that the pointer is pointing back to parent
 * when we want to delete 4 all we need to do is return (4).parent
 * and it will return the linked list of 1 <- 2 <- 3 and the time complexity will be just O(1)
 * but same thing will not work if we want to delete 1,
 * so we can actually do O(1) insert and O(1) delete for latest item (which is {@link ImmutableStack}, but it can't be done for {@link ImmutableQueue})
 * <p>
 * <p>
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
 * <p>
 * 3. Implement using 2 stack with amortized case (implementation in current commit hash)
 * We still can perform better than our 2nd solution, the main key point here is we don't need to reverse the stack every time to do a pop
 * How the algorithm work :
 * 1. When first deQueue : We can reverse the 1st stack and keep it on the 2nd stack for deQueue, and then clear the 1st stack
 * 2. When enQueue : we still insert as usual to the 1st stack
 * 3. When deQueue again : when we want to perform delete operation we keep doing pop in the 2nd stack if it is not empty, else we go to step 1
 * <p>
 * input scenario
 * perform queue 1, 2, 3, 4
 * 1st stack : 1, 2, 3, 4
 * 2nd stack : empty
 * perform dequeue
 * 1st stack : empty
 * 2nd stack : 4, 3, 2, 1 -> pop 1 -> 4, 3, 2
 * perform queue
 * 1st stack : 5, 6
 * 2nd stack : 4, 3, 2
 * perform 3 times dequeue
 * 1st stack : 5, 6
 * 2nd stack : 4, 3, 2 -> pop 2 -> 4, 3 -> pop 3 -> 4 -> pop 4 -> empty
 * perform dequeue
 * 1st stack : empty
 * 2nd stack : 6, 5 -> pop 5 -> 6
 * <p>
 * Time complexity analysis
 * {@link ImmutableQueue#enQueue(Object)} O(1) where n is number of element
 * {@link ImmutableQueue#deQueue()} O(1) but because it is amortized, with worst case of O(n) when 2nd stack is empty where n is size of 1st stack
 * {@link ImmutableQueue#emptyQueue()} O(1)
 * {@link ImmutableQueue#head()} O(1)
 *
 * @param <T> Generic type for data you want to store
 */
public class ImmutableQueue<T> implements Queue<T> {

    private Stack<T> stack; // 1st stack on 3rd explanation
    private Stack<T> reversedStack; // 2nd stack on 3rd explanation

    private ImmutableQueue(Stack<T> stack, Stack<T> reversedStack) {
        this.stack = stack;
        this.reversedStack = reversedStack;
    }

    @Override
    public Queue<T> enQueue(T data) {
        return new ImmutableQueue<>(stack.add(data), reversedStack);
    }

    private Stack<T> getReversedStack(Stack<T> stack) {
        Stack<T> reversedStack = ImmutableStack.emptyStack();
        while (!stack.isEmpty()) {
            reversedStack = reversedStack.add(stack.head());
            stack = stack.pop();
        }
        return reversedStack;
    }

    @Override
    public Queue<T> deQueue() {
        if (reversedStack.isEmpty()) {
            reversedStack = getReversedStack(stack);
            stack = ImmutableStack.emptyStack();
        }
        if (reversedStack.pop().isEmpty() && stack.isEmpty()) {
            return emptyQueue();
        }
        return new ImmutableQueue<>(stack, reversedStack.pop());
    }

    @Override
    public T head() {
        if (reversedStack.isEmpty()) {
            reversedStack = getReversedStack(stack);
            stack = ImmutableStack.emptyStack();
        }
        return reversedStack.head();
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
            return new ImmutableQueue<>(stack.add(data), ImmutableStack.emptyStack());
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
