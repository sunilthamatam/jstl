package com.jstl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OffHeapHashMap Tests")
class OffHeapHashMapTest {

    private OffHeapHashMap map;

    @BeforeEach
    void setUp() {
        map = new OffHeapHashMap();
    }

    @AfterEach
    void tearDown() {
        if (map != null) {
            map.close();
        }
    }

    @Test
    @DisplayName("Should create empty map")
    void testCreateEmptyMap() {
        assertEquals(0, map.size());
        assertTrue(map.isEmpty());
    }

    @Test
    @DisplayName("Should put and get entries")
    void testPutAndGet() {
        map.put(1, 100);
        map.put(2, 200);
        map.put(3, 300);

        assertEquals(100, map.get(1));
        assertEquals(200, map.get(2));
        assertEquals(300, map.get(3));
        assertEquals(3, map.size());
    }

    @Test
    @DisplayName("Should update existing key")
    void testUpdateValue() {
        map.put(1, 100);
        assertEquals(100, map.get(1));

        map.put(1, 999);
        assertEquals(999, map.get(1));
        assertEquals(1, map.size());
    }

    @Test
    @DisplayName("Should check if key exists")
    void testContainsKey() {
        map.put(1, 100);
        map.put(2, 200);

        assertTrue(map.containsKey(1));
        assertTrue(map.containsKey(2));
        assertFalse(map.containsKey(3));
        assertFalse(map.containsKey(999));
    }

    @Test
    @DisplayName("Should remove entries")
    void testRemove() {
        map.put(1, 100);
        map.put(2, 200);
        map.put(3, 300);

        map.remove(2);

        assertEquals(2, map.size());
        assertTrue(map.containsKey(1));
        assertFalse(map.containsKey(2));
        assertTrue(map.containsKey(3));
    }

    @Test
    @DisplayName("Should clear all entries")
    void testClear() {
        map.put(1, 100);
        map.put(2, 200);
        map.put(3, 300);

        map.clear();

        assertEquals(0, map.size());
        assertTrue(map.isEmpty());
        assertFalse(map.containsKey(1));
    }

    @Test
    @DisplayName("Should return default value for missing key")
    void testGetOrDefault() {
        map.put(1, 100);

        assertEquals(100, map.getOrDefault(1, -1));
        assertEquals(-1, map.getOrDefault(999, -1));
    }

    @Test
    @DisplayName("Should handle many entries")
    void testManyEntries() {
        int n = 10000;
        for (long i = 0; i < n; i++) {
            map.put(i, i * 2);
        }

        assertEquals(n, map.size());

        // Verify some values
        assertEquals(0, map.get(0));
        assertEquals(1000, map.get(500));
        assertEquals(19998, map.get(9999));
    }

    @Test
    @DisplayName("Should handle negative keys and values")
    void testNegativeKeysAndValues() {
        map.put(-1, -100);
        map.put(-2, -200);
        map.put(-3, -300);

        assertEquals(-100, map.get(-1));
        assertEquals(-200, map.get(-2));
        assertEquals(-300, map.get(-3));
    }

    @Test
    @DisplayName("Should handle extreme values")
    void testExtremeValues() {
        map.put(Long.MAX_VALUE, Long.MIN_VALUE);
        map.put(Long.MIN_VALUE, Long.MAX_VALUE);
        map.put(0, 0);

        assertEquals(Long.MIN_VALUE, map.get(Long.MAX_VALUE));
        assertEquals(Long.MAX_VALUE, map.get(Long.MIN_VALUE));
        assertEquals(0, map.get(0));
    }

    @Test
    @DisplayName("Should handle zero key and value")
    void testZeroKeyAndValue() {
        map.put(0, 0);

        assertTrue(map.containsKey(0));
        assertEquals(0, map.get(0));
        assertEquals(1, map.size());
    }

    @Test
    @DisplayName("Should return 0 for non-existent key")
    void testGetNonExistentKey() {
        assertEquals(0, map.get(999));
        assertFalse(map.containsKey(999));
    }

    @Test
    @DisplayName("Should handle remove on non-existent key")
    void testRemoveNonExistentKey() {
        map.put(1, 100);

        assertDoesNotThrow(() -> map.remove(999));
        assertEquals(1, map.size());
    }

    @Test
    @DisplayName("Should throw exception when accessing closed map")
    void testAccessAfterClose() {
        map.put(1, 100);
        map.close();

        assertThrows(IllegalStateException.class, () -> map.put(2, 200));
        assertThrows(IllegalStateException.class, () -> map.get(1));
        assertThrows(IllegalStateException.class, () -> map.size());
        assertThrows(IllegalStateException.class, () -> map.containsKey(1));
    }

    @Test
    @DisplayName("Should work with try-with-resources")
    void testTryWithResources() {
        try (OffHeapHashMap tempMap = new OffHeapHashMap()) {
            tempMap.put(1, 100);
            tempMap.put(2, 200);
            assertEquals(2, tempMap.size());
        }
        // Map should be closed automatically
    }

    @Test
    @DisplayName("Should handle multiple close calls")
    void testMultipleClose() {
        map.close();
        assertDoesNotThrow(() -> map.close());
    }

    @Test
    @DisplayName("Should handle repeated put operations on same key")
    void testRepeatedPut() {
        for (int i = 0; i < 100; i++) {
            map.put(1, i);
        }

        assertEquals(99, map.get(1));
        assertEquals(1, map.size());
    }

    @Test
    @DisplayName("Should handle collision scenario")
    void testManyKeysCollisionScenario() {
        // Add many entries to test hash collision handling
        for (long i = 0; i < 1000; i++) {
            map.put(i, i * 10);
        }

        // Verify all entries
        for (long i = 0; i < 1000; i++) {
            assertTrue(map.containsKey(i));
            assertEquals(i * 10, map.get(i));
        }
    }

    @Test
    @DisplayName("Should maintain correct size after operations")
    void testSizeConsistency() {
        assertEquals(0, map.size());

        map.put(1, 100);
        assertEquals(1, map.size());

        map.put(2, 200);
        assertEquals(2, map.size());

        map.put(1, 999);  // Update
        assertEquals(2, map.size());

        map.remove(1);
        assertEquals(1, map.size());

        map.clear();
        assertEquals(0, map.size());
    }

    @Test
    @DisplayName("Should return correct toString")
    void testToString() {
        map.put(1, 100);
        map.put(2, 200);

        String str = map.toString();
        assertTrue(str.contains("OffHeapHashMap"));
        assertTrue(str.contains("size=2"));
    }

    @Test
    @DisplayName("Should handle interleaved operations")
    void testInterleavedOperations() {
        map.put(1, 10);
        assertTrue(map.containsKey(1));

        map.put(2, 20);
        assertEquals(20, map.get(2));

        map.remove(1);
        assertFalse(map.containsKey(1));

        map.put(3, 30);
        assertEquals(2, map.size());

        map.clear();
        assertTrue(map.isEmpty());
    }

    @Test
    @DisplayName("Should handle put with same value")
    void testPutSameValue() {
        map.put(1, 100);
        map.put(2, 100);
        map.put(3, 100);

        assertEquals(100, map.get(1));
        assertEquals(100, map.get(2));
        assertEquals(100, map.get(3));
        assertEquals(3, map.size());
    }

    @Test
    @DisplayName("Should handle sequential keys")
    void testSequentialKeys() {
        for (long i = 0; i < 100; i++) {
            map.put(i, i);
        }

        for (long i = 0; i < 100; i++) {
            assertEquals(i, map.get(i));
        }
    }

    @Test
    @DisplayName("Should handle sparse keys")
    void testSparseKeys() {
        map.put(1, 10);
        map.put(1000, 20);
        map.put(1000000, 30);

        assertEquals(10, map.get(1));
        assertEquals(20, map.get(1000));
        assertEquals(30, map.get(1000000));
        assertEquals(3, map.size());
    }
}
