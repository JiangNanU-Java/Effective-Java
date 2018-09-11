package collections.list;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public interface TList<T> extends List<T> {

    @Override
    int size();

    @Override
    boolean isEmpty();

    @Override
    boolean contains(Object o);

    @Override
    Iterator<T> iterator();

    @Override
    Object[] toArray();

    @Override
    <T1> T1[] toArray(T1[] a);

    @Override
    boolean add(T t);

    @Override
    boolean remove(Object o);

    @Override
    boolean containsAll(Collection<?> c);

    @Override
    boolean addAll(Collection<? extends T> c);

    @Override
    boolean addAll(int index, Collection<? extends T> c);

    @Override
    boolean removeAll(Collection<?> c);

    @Override
    boolean retainAll(Collection<?> c);

    @Override
    default void replaceAll(UnaryOperator<T> operator) {

    }

    @Override
    default void sort(Comparator<? super T> c) {

    }

    @Override
    void clear();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();

    @Override
    T get(int index);

    @Override
    T set(int index, T element);

    @Override
    void add(int index, T element);

    @Override
    T remove(int index);

    @Override
    int indexOf(Object o);

    @Override
    int lastIndexOf(Object o);

    @Override
    ListIterator<T> listIterator();

    @Override
    ListIterator<T> listIterator(int index);

    @Override
    List<T> subList(int fromIndex, int toIndex);

    @Override
    default Spliterator<T> spliterator() {
        return null;
    }

    @Override
    default boolean removeIf(Predicate<? super T> filter) {
        return false;
    }

    @Override
    default Stream<T> stream() {
        return null;
    }

    @Override
    default Stream<T> parallelStream() {
        return null;
    }

    @Override
    default void forEach(Consumer<? super T> action) {

    }
}
