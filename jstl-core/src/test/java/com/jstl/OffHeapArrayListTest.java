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
        assertEquals(100, list.get(0));
        assertEquals(300, list.get(2));
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
        list.add(300);

        list.clear();

        assertEquals(0, list.size());
        assertTrue(list.isEmpty());
    }

    @Test
    @DisplayName("Should reserve capacity")
    void testReserve() {
        list.reserve(1000);

        assertTrue(list.capacity() >= 1000);
        assertEquals(0, list.size());
    }

    @Test
    @DisplayName("Should handle adding many elements")
    void testAddMany() {
        int n = 10000;
        for (long i = 0; i < n; i++) {
            list.add(i);
        }

        assertEquals(n, list.size());

        // Verify some values
        assertEquals(0, list.get(0));
        assertEquals(500, list.get(500));
        assertEquals(9999, list.get(9999));
    }

    @Test
    @DisplayName("Should handle removing from beginning")
    void testRemoveFromBeginning() {
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);

        list.remove(0);

        assertEquals(3, list.size());
        assertEquals(2, list.get(0));
        assertEquals(3, list.get(1));
        assertEquals(4, list.get(2));
    }

    @Test
    @DisplayName("Should handle removing from end")
    void testRemoveFromEnd() {
        list.add(1);
        list.add(2);
        list.add(3);

        list.remove(2);

        assertEquals(2, list.size());
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
    }

    @Test
    @DisplayName("Should grow capacity automatically")
    void testAutoGrow() {
        int initialCapacity = list.capacity();

        // Add elements beyond initial capacity
        for (int i = 0; i < initialCapacity + 100; i++) {
            list.add(i);
        }

        assertTrue(list.capacity() > initialCapacity);
        assertEquals(initialCapacity + 100, list.size());
    }

    @Test
    @DisplayName("Should handle negative values")
    void testNegativeValues() {
        list.add(-100);
        list.add(-200);
        list.add(-300);

        assertEquals(-100, list.get(0));
        assertEquals(-200, list.get(1));
        assertEquals(-300, list.get(2));
    }

    @Test
    @DisplayName("Should handle Long.MAX_VALUE and Long.MIN_VALUE")
    void testExtremeValues() {
        list.add(Long.MAX_VALUE);
        list.add(Long.MIN_VALUE);
        list.add(0);

        assertEquals(Long.MAX_VALUE, list.get(0));
        assertEquals(Long.MIN_VALUE, list.get(1));
        assertEquals(0, list.get(2));
    }

    @Test
    @DisplayName("Should throw exception when accessing closed list")
    void testAccessAfterClose() {
        list.add(100);
        list.close();

        assertThrows(IllegalStateException.class, () -> list.add(200));
        assertThrows(IllegalStateException.class, () -> list.get(0));
        assertThrows(IllegalStateException.class, () -> list.size());
    }

    @Test
    @DisplayName("Should work with try-with-resources")
    void testTryWithResources() {
        try (OffHeapArrayList tempList = new OffHeapArrayList()) {
            tempList.add(100);
            tempList.add(200);
            assertEquals(2, tempList.size());
        }
        // List should be closed automatically
    }

    @Test
    @DisplayName("Should handle multiple close calls")
    void testMultipleClose() {
        list.close();
        assertDoesNotThrow(() -> list.close());
    }

    @Test
    @DisplayName("Should maintain order of elements")
    void testElementOrder() {
        for (long i = 0; i < 100; i++) {
            list.add(i * 10);
        }

        for (int i = 0; i < 100; i++) {
            assertEquals(i * 10L, list.get(i));
        }
    }

    @Test
    @DisplayName("Should handle set on empty positions after reserve")
    void testSetAfterReserve() {
        list.add(1);
        list.add(2);
        list.add(3);

        list.set(1, 999);

        assertEquals(1, list.get(0));
        assertEquals(999, list.get(1));
        assertEquals(3, list.get(2));
    }

    @Test
    @DisplayName("Should have correct capacity after additions")
    void testCapacityGrowth() {
        int prevCapacity = list.capacity();

        for (int i = 0; i < 1000; i++) {
            list.add(i);
        }

        assertTrue(list.capacity() >= list.size());
        assertTrue(list.capacity() >= prevCapacity);
    }

    @Test
    @DisplayName("Should return correct toString for small list")
    void testToString() {
        list.add(1);
        list.add(2);
        list.add(3);

        String str = list.toString();
        assertTrue(str.contains("OffHeapArrayList"));
        assertTrue(str.contains("1"));
        assertTrue(str.contains("2"));
        assertTrue(str.contains("3"));
    }

    @Test
    @DisplayName("Should handle rapid add/remove operations")
    void testRapidAddRemove() {
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }

        for (int i = 0; i < 50; i++) {
            list.remove(0);
        }

        assertEquals(50, list.size());
        assertEquals(50, list.get(0));
    }

    @Test
    @DisplayName("Should handle interleaved operations")
    void testInterleavedOperations() {
        list.add(1);
        assertEquals(1, list.size());

        list.add(2);
        assertEquals(2, list.size());

        list.set(0, 10);
        assertEquals(10, list.get(0));

        list.add(3);
        assertEquals(3, list.size());

        list.remove(1);
        assertEquals(2, list.size());
        assertEquals(10, list.get(0));
        assertEquals(3, list.get(1));
    }
}
