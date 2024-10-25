package ru.sweetbun.pattern;

import java.util.Iterator;
import java.util.function.Consumer;

public interface CustomIterator<T> extends Iterator<T> {
    boolean hasNext();
    T next();
    void forEachRemaining(Consumer<? super T> action);
}
