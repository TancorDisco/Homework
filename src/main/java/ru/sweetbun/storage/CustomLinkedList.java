package ru.sweetbun.storage;

import lombok.AllArgsConstructor;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;

public class CustomLinkedList<T> implements Iterable<T>{

    private Node<T> first;
    private Node<T> last;
    private int size;

    @AllArgsConstructor
    private static class Node<T> {
        private Node<T> prev;
        private T value;
        private Node<T> next;
    }

    public CustomLinkedList() {
        this.first = null;
        this.last = null;
        this.size = 0;
    }

    public boolean add(T value) {
        if (size == 0) {
            first = new Node<>(null, value, null);
            last = first;
        } else {
            final Node<T> prevLast = last;
            last = new Node<>(prevLast, value, null);
            prevLast.next = last;
        }
        size++;
        return true;
    }

    public T get(int index) {
        checkIndex(index);
        return getNode(index).value;
    }

    private Node<T> getNode(int index) {
        Node<T> current;
        if (index < size / 2) {
            current = first;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
        } else {
            current = last;
            for (int i = size - 1; i > index; i--) {
                current = current.prev;
            }
        }
        return current;
    }

    public T remove(int index) {
        checkIndex(index);

        Node<T> node = getNode(index);
        final T value = node.value;
        final Node<T> prev = node.prev;
        final Node<T> next = node.next;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            node.prev = null;
        }

        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }

        node.value = null;
        size--;

        return value;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    public boolean contains(Object o) {
        if (o == null) {
            for (Node<T> node = first; node != null; node = node.next) {
                if (node.value == null) {
                    return true;
                }
            }
        } else {
            for (Node<T> node = first; node != null; node = node.next) {
                if (node.value.equals(o)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean addAll(Collection<? extends T> collection) {
        for (T el : collection) {
            add(el);
        }
        return true;
    }

    @Override
    public Iterator<T> iterator() {
        return new CustomLinkedListIterator();
    }

    private class CustomLinkedListIterator implements CustomIterator<T> {
        private Node<T> current = first;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public T next() {
            T value = current.value;
            current = current.next;
            return value;
        }

        @Override
        public void forEachRemaining(Consumer<? super T> action) {
            while (current != null) {
                action.accept(current.value);
                current = current.next;
            }
        }
    }

    public int size() {
        return size;
    }
}
