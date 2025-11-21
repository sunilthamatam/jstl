package com.jstl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OffHeapArrayList Tests")
class OffHeapArrayListTest {

    private OffHeapArrayList list;

    @BeforeEach
    void setUp() {
        list = new OffHeapArrayList();
    }

    @AfterEach
    void tearDown() {
        if (list != null) {
            list.close();
        }
    }

    @Test
    @DisplayName("Should create empty list")
    void testCreateEmptyList() {
        assertEquals(0, list.size());
        assertTrue(list.isEmpty());
    }

    @Test
    @DisplayName("Should add elements")
    void testAdd() {
        list.add(100);
        list.add(200);
        list.add(300);

        assertEquals(3, list.size());
        assertFalse(list.isEmpty());
    }

    @Test
    @DisplayName("Should get elements at index")
    void testGet() {
        list.add(100);
        list.add(200);
        list.add(300);

        assertEquals(100, list.get(0));
        assertEquals(200, list.get(1));
        assertEquals(300, list.get(2));
    }

    @Test
    @DisplayName("Should set elements at index")
    void testSet() {
        list.add(100);
        list.add(200);
        list.add(300);

        list.set(1, 999);
        assertEquals(999, list.get(1));
    }

    @Test
    @DisplayName("Should remove elements at index")
    void testRemove() {
        list.add(100);
        list.add(200);
        list.add(300);

        list.remove(1);

        assertEquals(2, list.size());
        assertEquals(100, list.get(0));
        assertEquals(300, list.get(1));
    }

    @Test
    @DisplayName("Should clear all elements")
    void testClear() {
        list.add(100);
        list.add(200);

        list.clear();

        assertEquals(0, list.size());
        assertTrue(list.isEmpty());
    }

    @Test
    @DisplayName("Should reserve capacity")
    void testReserve() {
        list.reserve(1000);
        assertTrue(list.capacity() >= 1000);
    }

    @Test
    @DisplayName("Should handle many elements")
    void testManyElements() {
        int n = 10000;
        for (long i = 0; i < n; i++) {
            list.add(i);
        }

        assertEquals(n, list.size());
        assertEquals(0, list.get(0));
        assertEquals(9999, list.get(9999));
    }

    @Test
    @DisplayName("Should handle negative values")
    void testNegativeValues() {
        list.add(-100);
        list.add(-200);

        assertEquals(-100, list.get(0));
        assertEquals(-200, list.get(1));
    }

    @Test
    @DisplayName("Should handle extreme values")
    void testExtremeValues() {
        list.add(Long.MAX_VALUE);
        list.add(Long.MIN_VALUE);

        assertEquals(Long.MAX_VALUE, list.get(0));
        assertEquals(Long.MIN_VALUE, list.get(1));
    }

    @Test
    @DisplayName("Should throw on invalid index get")
    void testInvalidIndexGet() {
        list.add(100);
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(5));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
    }

    @Test
    @DisplayName("Should throw when accessing closed list")
    void testAccessAfterClose() {
        list.add(100);
        list.close();

        assertThrows(IllegalStateException.class, () -> list.add(200));
        assertThrows(IllegalStateException.class, () -> list.get(0));
    }

    @Test
    @DisplayName("Should work with try-with-resources")
    void testTryWithResources() {
        try (OffHeapArrayList tempList = new OffHeapArrayList()) {
            tempList.add(100);
            assertEquals(1, tempList.size());
        }
    }

    @Test
    @DisplayName("Should handle multiple close calls")
    void testMultipleClose() {
        list.close();
        assertDoesNotThrow(() -> list.close());
    }
}
