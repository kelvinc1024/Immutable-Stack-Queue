public interface Stack<T> {

    Stack<T> add(T t);

    Stack<T> pop();

    T head();

    boolean isEmpty();
}
