package com.jstl;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OffHeapCollectionsTest {

    @Test
    void testArrayListBasicOperations() {
        try (OffHeapArrayList list = new OffHeapArrayList()) {
            assertTrue(list.isEmpty());
            list.add(100);
            list.add(200);
            list.add(300);
            assertEquals(3, list.size());
            assertEquals(100, list.get(0));
            assertEquals(200, list.get(1));
            assertEquals(300, list.get(2));
            list.set(1, 999);
            assertEquals(999, list.get(1));
            list.remove(0);
            assertEquals(2, list.size());
            assertEquals(999, list.get(0));
        }
    }

    @Test
    void testHashMapBasicOperations() {
        try (OffHeapHashMap map = new OffHeapHashMap()) {
            assertTrue(map.isEmpty());
            map.put(1, 100);
            map.put(2, 200);
            map.put(3, 300);
            assertEquals(3, map.size());
            assertEquals(100, map.get(1));
            assertTrue(map.containsKey(2));
            assertFalse(map.containsKey(99));
            map.remove(2);
            assertEquals(2, map.size());
            assertFalse(map.containsKey(2));
        }
    }

    @Test
    void testHashSetBasicOperations() {
        try (OffHeapHashSet set = new OffHeapHashSet()) {
            assertTrue(set.isEmpty());
            assertTrue(set.add(10));
            assertTrue(set.add(20));
            assertFalse(set.add(10)); // duplicate
            assertEquals(2, set.size());
            assertTrue(set.contains(10));
            assertFalse(set.contains(99));
            assertTrue(set.remove(10));
            assertFalse(set.remove(10)); // already removed
            assertEquals(1, set.size());
        }
    }

    @Test
    void testClearOperations() {
        try (OffHeapArrayList list = new OffHeapArrayList();
             OffHeapHashMap map = new OffHeapHashMap();
             OffHeapHashSet set = new OffHeapHashSet()) {
            list.add(1); list.add(2);
            map.put(1, 1); map.put(2, 2);
            set.add(1); set.add(2);

            list.clear();
            map.clear();
            set.clear();

            assertTrue(list.isEmpty());
            assertTrue(map.isEmpty());
            assertTrue(set.isEmpty());
        }
    }
}
