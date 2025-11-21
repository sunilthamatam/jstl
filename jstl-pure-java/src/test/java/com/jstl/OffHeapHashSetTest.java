package com.jstl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OffHeapHashSet Tests")
class OffHeapHashSetTest {

    private OffHeapHashSet set;

    @BeforeEach
    void setUp() {
        set = new OffHeapHashSet();
    }

    @AfterEach
    void tearDown() {
        if (set != null) {
            set.close();
        }
    }

    @Test
    @DisplayName("Should create empty set")
    void testCreateEmptySet() {
        assertEquals(0, set.size());
        assertTrue(set.isEmpty());
    }

    @Test
    @DisplayName("Should add elements")
    void testAdd() {
        assertTrue(set.add(100));
        assertTrue(set.add(200));
        assertTrue(set.add(300));

        assertEquals(3, set.size());
        assertFalse(set.isEmpty());
    }

    @Test
    @DisplayName("Should not add duplicate elements")
    void testAddDuplicate() {
        assertTrue(set.add(100));
        assertFalse(set.add(100));

        assertEquals(1, set.size());
    }

    @Test
    @DisplayName("Should check if contains element")
    void testContains() {
        set.add(100);
        set.add(200);

        assertTrue(set.contains(100));
        assertTrue(set.contains(200));
        assertFalse(set.contains(300));
    }

    @Test
    @DisplayName("Should remove elements")
    void testRemove() {
        set.add(100);
        set.add(200);

        assertTrue(set.remove(100));
        assertFalse(set.remove(100)); // Already removed

        assertFalse(set.contains(100));
        assertTrue(set.contains(200));
        assertEquals(1, set.size());
    }

    @Test
    @DisplayName("Should clear all elements")
    void testClear() {
        set.add(100);
        set.add(200);

        set.clear();

        assertEquals(0, set.size());
        assertTrue(set.isEmpty());
        assertFalse(set.contains(100));
    }

    @Test
    @DisplayName("Should handle many elements")
    void testManyElements() {
        int n = 10000;
        for (long i = 0; i < n; i++) {
            assertTrue(set.add(i));
        }

        assertEquals(n, set.size());

        for (long i = 0; i < n; i++) {
            assertTrue(set.contains(i));
        }
        assertFalse(set.contains(n + 1));
    }

    @Test
    @DisplayName("Should handle negative values")
    void testNegativeValues() {
        set.add(-100);
        set.add(-200);

        assertTrue(set.contains(-100));
        assertTrue(set.contains(-200));
    }

    @Test
    @DisplayName("Should handle extreme values")
    void testExtremeValues() {
        set.add(Long.MAX_VALUE);
        set.add(Long.MIN_VALUE);
        set.add(0);

        assertTrue(set.contains(Long.MAX_VALUE));
        assertTrue(set.contains(Long.MIN_VALUE));
        assertTrue(set.contains(0));
    }

    @Test
    @DisplayName("Should handle remove on non-existent element")
    void testRemoveNonExistent() {
        set.add(100);
        assertFalse(set.remove(999));
        assertEquals(1, set.size());
    }

    @Test
    @DisplayName("Should throw when accessing closed set")
    void testAccessAfterClose() {
        set.add(100);
        set.close();

        assertThrows(IllegalStateException.class, () -> set.add(200));
        assertThrows(IllegalStateException.class, () -> set.contains(100));
    }

    @Test
    @DisplayName("Should work with try-with-resources")
    void testTryWithResources() {
        try (OffHeapHashSet tempSet = new OffHeapHashSet()) {
            tempSet.add(100);
            assertEquals(1, tempSet.size());
        }
    }

    @Test
    @DisplayName("Should handle multiple close calls")
    void testMultipleClose() {
        set.close();
        assertDoesNotThrow(() -> set.close());
    }

    @Test
    @DisplayName("Should handle add after remove")
    void testAddAfterRemove() {
        set.add(100);
        set.remove(100);
        assertTrue(set.add(100)); // Should be able to re-add

        assertTrue(set.contains(100));
        assertEquals(1, set.size());
    }

    @Test
    @DisplayName("Should verify no duplicates in large set")
    void testNoDuplicatesLargeSet() {
        int n = 5000;

        // Add elements twice
        for (long i = 0; i < n; i++) {
            set.add(i);
        }
        for (long i = 0; i < n; i++) {
            set.add(i);
        }

        assertEquals(n, set.size());
    }

    @Test
    @DisplayName("Should handle resizing correctly")
    void testResizing() {
        // Add enough elements to trigger multiple resizes
        for (long i = 0; i < 1000; i++) {
            set.add(i);
        }

        // Verify all elements are still accessible
        for (long i = 0; i < 1000; i++) {
            assertTrue(set.contains(i));
        }
    }
}
