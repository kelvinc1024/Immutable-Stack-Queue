public class ImmutableStack<T> implements Stack<T> {

    private final T data;
    private final Stack<T> parent;

    private ImmutableStack(T data, Stack<T> parent) {
        this.data = data;
        this.parent = parent;
    }

    @Override
    public Stack<T> add(T data) {
        return new ImmutableStack<>(data, this);
    }

    @Override
    public Stack<T> pop() {
        return parent;
    }

    @Override
    public T head() {
        return data;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public static Stack emptyStack() {
        return new EmptyStack();
    }

    private static class EmptyStack<T> implements Stack<T> {

        @Override
        public Stack<T> add(T data) {
            return new ImmutableStack<>(data, this);
        }

        @Override
        public Stack<T> pop() {
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
