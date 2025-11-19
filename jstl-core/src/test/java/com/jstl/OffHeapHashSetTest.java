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
        assertFalse(set.add(100));  // Should return false for duplicate

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
        assertFalse(set.contains(999));
    }

    @Test
    @DisplayName("Should remove elements")
    void testRemove() {
        set.add(100);
        set.add(200);
        set.add(300);

        assertTrue(set.remove(200));
        assertFalse(set.remove(999));  // Non-existent element

        assertEquals(2, set.size());
        assertTrue(set.contains(100));
        assertFalse(set.contains(200));
        assertTrue(set.contains(300));
    }

    @Test
    @DisplayName("Should clear all elements")
    void testClear() {
        set.add(100);
        set.add(200);
        set.add(300);

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

        // Verify some values
        assertTrue(set.contains(0));
        assertTrue(set.contains(500));
        assertTrue(set.contains(9999));
        assertFalse(set.contains(10000));
    }

    @Test
    @DisplayName("Should handle negative values")
    void testNegativeValues() {
        set.add(-100);
        set.add(-200);
        set.add(-300);

        assertTrue(set.contains(-100));
        assertTrue(set.contains(-200));
        assertTrue(set.contains(-300));
        assertEquals(3, set.size());
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
        assertEquals(3, set.size());
    }

    @Test
    @DisplayName("Should handle zero value")
    void testZeroValue() {
        assertTrue(set.add(0));
        assertFalse(set.add(0));  // Duplicate

        assertTrue(set.contains(0));
        assertEquals(1, set.size());
    }

    @Test
    @DisplayName("Should handle remove on non-existent element")
    void testRemoveNonExistent() {
        set.add(100);

        assertFalse(set.remove(999));
        assertEquals(1, set.size());
    }

    @Test
    @DisplayName("Should throw exception when accessing closed set")
    void testAccessAfterClose() {
        set.add(100);
        set.close();

        assertThrows(IllegalStateException.class, () -> set.add(200));
        assertThrows(IllegalStateException.class, () -> set.contains(100));
        assertThrows(IllegalStateException.class, () -> set.size());
        assertThrows(IllegalStateException.class, () -> set.remove(100));
    }

    @Test
    @DisplayName("Should work with try-with-resources")
    void testTryWithResources() {
        try (OffHeapHashSet tempSet = new OffHeapHashSet()) {
            tempSet.add(100);
            tempSet.add(200);
            assertEquals(2, tempSet.size());
        }
        // Set should be closed automatically
    }

    @Test
    @DisplayName("Should handle multiple close calls")
    void testMultipleClose() {
        set.close();
        assertDoesNotThrow(() -> set.close());
    }

    @Test
    @DisplayName("Should handle repeated add operations")
    void testRepeatedAdd() {
        for (int i = 0; i < 100; i++) {
            if (i == 0) {
                assertTrue(set.add(999));
            } else {
                assertFalse(set.add(999));
            }
        }

        assertEquals(1, set.size());
        assertTrue(set.contains(999));
    }

    @Test
    @DisplayName("Should handle collision scenario")
    void testManyElementsCollisionScenario() {
        // Add many elements to test hash collision handling
        for (long i = 0; i < 1000; i++) {
            assertTrue(set.add(i));
        }

        // Verify all elements
        for (long i = 0; i < 1000; i++) {
            assertTrue(set.contains(i));
        }

        assertEquals(1000, set.size());
    }

    @Test
    @DisplayName("Should maintain correct size after operations")
    void testSizeConsistency() {
        assertEquals(0, set.size());

        set.add(100);
        assertEquals(1, set.size());

        set.add(200);
        assertEquals(2, set.size());

        set.add(100);  // Duplicate
        assertEquals(2, set.size());

        set.remove(100);
        assertEquals(1, set.size());

        set.clear();
        assertEquals(0, set.size());
    }

    @Test
    @DisplayName("Should return correct toString")
    void testToString() {
        set.add(100);
        set.add(200);

        String str = set.toString();
        assertTrue(str.contains("OffHeapHashSet"));
        assertTrue(str.contains("size=2"));
    }

    @Test
    @DisplayName("Should handle interleaved operations")
    void testInterleavedOperations() {
        assertTrue(set.add(10));
        assertTrue(set.contains(10));

        assertTrue(set.add(20));
        assertEquals(2, set.size());

        assertTrue(set.remove(10));
        assertFalse(set.contains(10));

        assertTrue(set.add(30));
        assertEquals(2, set.size());

        set.clear();
        assertTrue(set.isEmpty());
    }

    @Test
    @DisplayName("Should handle add after remove")
    void testAddAfterRemove() {
        set.add(100);
        assertEquals(1, set.size());

        set.remove(100);
        assertEquals(0, set.size());
        assertFalse(set.contains(100));

        set.add(100);
        assertEquals(1, set.size());
        assertTrue(set.contains(100));
    }

    @Test
    @DisplayName("Should handle sequential values")
    void testSequentialValues() {
        for (long i = 0; i < 100; i++) {
            assertTrue(set.add(i));
        }

        for (long i = 0; i < 100; i++) {
            assertTrue(set.contains(i));
        }

        assertEquals(100, set.size());
    }

    @Test
    @DisplayName("Should handle sparse values")
    void testSparseValues() {
        set.add(1);
        set.add(1000);
        set.add(1000000);

        assertTrue(set.contains(1));
        assertTrue(set.contains(1000));
        assertTrue(set.contains(1000000));
        assertFalse(set.contains(2));
        assertFalse(set.contains(999));
        assertEquals(3, set.size());
    }

    @Test
    @DisplayName("Should handle all removes")
    void testRemoveAll() {
        set.add(1);
        set.add(2);
        set.add(3);

        assertTrue(set.remove(1));
        assertTrue(set.remove(2));
        assertTrue(set.remove(3));

        assertTrue(set.isEmpty());
        assertEquals(0, set.size());
    }

    @Test
    @DisplayName("Should handle alternating add and remove")
    void testAlternatingAddRemove() {
        for (int i = 0; i < 100; i++) {
            assertTrue(set.add(i));
            if (i > 0) {
                assertTrue(set.remove(i - 1));
            }
        }

        assertEquals(1, set.size());
        assertTrue(set.contains(99));
    }

    @Test
    @DisplayName("Should verify no duplicates in large set")
    void testNoDuplicatesInLargeSet() {
        int n = 5000;

        // Add elements twice
        for (long i = 0; i < n; i++) {
            set.add(i);
        }
        for (long i = 0; i < n; i++) {
            set.add(i);  // Try to add again
        }

        // Should still have exactly n elements
        assertEquals(n, set.size());
    }
}
