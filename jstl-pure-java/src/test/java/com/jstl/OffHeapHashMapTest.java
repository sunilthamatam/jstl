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
        map.put(1, 999);

        assertEquals(999, map.get(1));
        assertEquals(1, map.size());
    }

    @Test
    @DisplayName("Should check if key exists")
    void testContainsKey() {
        map.put(1, 100);

        assertTrue(map.containsKey(1));
        assertFalse(map.containsKey(99));
    }

    @Test
    @DisplayName("Should remove entries")
    void testRemove() {
        map.put(1, 100);
        map.put(2, 200);

        map.remove(1);

        assertFalse(map.containsKey(1));
        assertTrue(map.containsKey(2));
        assertEquals(1, map.size());
    }

    @Test
    @DisplayName("Should clear all entries")
    void testClear() {
        map.put(1, 100);
        map.put(2, 200);

        map.clear();

        assertEquals(0, map.size());
        assertTrue(map.isEmpty());
        assertFalse(map.containsKey(1));
    }

    @Test
    @DisplayName("Should handle many entries")
    void testManyEntries() {
        int n = 10000;
        for (long i = 0; i < n; i++) {
            map.put(i, i * 2);
        }

        assertEquals(n, map.size());
        assertEquals(0, map.get(0));
        assertEquals(19998, map.get(9999));
    }

    @Test
    @DisplayName("Should handle negative keys and values")
    void testNegativeKeysAndValues() {
        map.put(-1, -100);
        map.put(-2, -200);

        assertEquals(-100, map.get(-1));
        assertEquals(-200, map.get(-2));
    }

    @Test
    @DisplayName("Should handle extreme values")
    void testExtremeValues() {
        map.put(Long.MAX_VALUE, Long.MIN_VALUE);
        map.put(Long.MIN_VALUE, Long.MAX_VALUE);

        assertEquals(Long.MIN_VALUE, map.get(Long.MAX_VALUE));
        assertEquals(Long.MAX_VALUE, map.get(Long.MIN_VALUE));
    }

    @Test
    @DisplayName("Should return 0 for non-existent key")
    void testGetNonExistent() {
        assertEquals(0, map.get(999));
    }

    @Test
    @DisplayName("Should throw when accessing closed map")
    void testAccessAfterClose() {
        map.put(1, 100);
        map.close();

        assertThrows(IllegalStateException.class, () -> map.put(2, 200));
        assertThrows(IllegalStateException.class, () -> map.get(1));
    }

    @Test
    @DisplayName("Should work with try-with-resources")
    void testTryWithResources() {
        try (OffHeapHashMap tempMap = new OffHeapHashMap()) {
            tempMap.put(1, 100);
            assertEquals(1, tempMap.size());
        }
    }

    @Test
    @DisplayName("Should handle multiple close calls")
    void testMultipleClose() {
        map.close();
        assertDoesNotThrow(() -> map.close());
    }

    @Test
    @DisplayName("Should handle rehashing correctly")
    void testRehashing() {
        // Add enough entries to trigger multiple rehashes
        for (long i = 0; i < 1000; i++) {
            map.put(i, i * 10);
        }

        // Verify all entries are still accessible
        for (long i = 0; i < 1000; i++) {
            assertEquals(i * 10, map.get(i));
        }
    }
}
