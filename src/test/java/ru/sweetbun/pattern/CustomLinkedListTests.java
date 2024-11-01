package ru.sweetbun.pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.sweetbun.pattern.CustomLinkedList;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomLinkedListTests {

    private CustomLinkedList list;

    @BeforeEach
    void setUp() {
        list = new CustomLinkedList();
        list.add(3);
    }

    @Test
    void add() {
        assertTrue(list.add(1));
        assertTrue(list.add(2));
        assertEquals(list.get(2), 2);
    }

    @Test
    void get() {
        assertEquals(3, list.get(0));
    }

    @Test
    void getThrowsExceptionForInvalidIndex() {
        list.add(1);
        Exception exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            list.get(5);
        });
        assertEquals("Index: 5, Size: 2", exception.getMessage());
    }

    @Test
    void remove() {
        assertEquals(3, list.remove(0));
        Exception exception = assertThrows(IndexOutOfBoundsException.class, ()-> {
            list.get(0);
        });
    }

    @Test
    void contains() {
        assertTrue(list.contains(3));
    }

    @Test
    void addAll() {
        List<Integer> toAdd = List.of(13, 22, 1, 34);
        assertTrue(list.addAll(toAdd));
        assertEquals(5, list.size());
        assertEquals(13, list.get(1));
        assertEquals(1, list.get(3));
    }

    @Test
    void iterator() {
        for (int i = 10; i <= 30; i += 10) {
            list.add(i);
        }

        List<Integer> result = new ArrayList<>();
        for (Object el : list) {
            result.add((Integer) el);
        }

        assertEquals(List.of(3, 10, 20, 30), result);
    }
}